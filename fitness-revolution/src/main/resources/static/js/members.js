const API = 'http://localhost:8080/api/members';
let allMembers = [];

document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadMembers();
});

async function loadStats() {
    const s = await (await fetch(`${API}/stats`)).json();
    document.getElementById('sTotal').textContent    = s.total;
    document.getElementById('sActive').textContent   = s.active;
    document.getElementById('sExpiring').textContent = s.expiringSoon;
    document.getElementById('sExpired').textContent  = s.expired;
    document.getElementById('sInactive').textContent = s.inactive;
}

async function loadMembers() {
    allMembers = await (await fetch(API)).json();
    renderMembers(allMembers);
}

function renderMembers(list) {
    const tb = document.getElementById('memberTable');
    if (!list.length) {
        tb.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:2rem">No members</td></tr>`;
        return;
    }
    tb.innerHTML = list.map(m => `
    <tr>
      <td>
        <div style="display:flex;align-items:center;gap:0.6rem">
          <div class="avatar">${m.name.charAt(0)}</div>
          <div>
            <div style="color:#fff;font-weight:600">${m.name}</div>
            <div style="color:var(--muted);font-size:0.72rem">${m.id} · ${m.email}</div>
          </div>
        </div>
      </td>
      <td><span class="badge badge-${m.membershipType.toLowerCase()}">${m.membershipType}</span></td>
      <td style="color:var(--muted);font-size:0.8rem">${m.expiryDate}</td>
      <td><span class="badge ${statusClass(m)}">${getStatus(m)}</span></td>
      <td>
        <div style="display:flex;gap:4px">
          <button class="btn btn-outline btn-sm" onclick="editMember('${m.id}')">✏</button>
          <button class="btn btn-outline btn-sm" onclick="renewMember('${m.id}')">↻</button>
          <button class="btn btn-outline btn-sm" onclick="toggleMember('${m.id}')">⇄</button>
          <button class="btn btn-outline btn-sm" style="color:#ef4444" onclick="deleteMember('${m.id}')">🗑</button>
        </div>
      </td>
    </tr>`).join('');
}

function getStatus(m) {
    if (!m.active) return 'Inactive';
    if (new Date(m.expiryDate) < new Date()) return 'Expired';
    if (daysLeft(m) <= 7) return 'Expiring';
    return 'Active';
}

function statusClass(m) {
    const s = getStatus(m);
    if (s === 'Active')   return 'badge-active';
    if (s === 'Expired')  return 'badge-expired';
    if (s === 'Expiring') return 'badge-expiring';
    return 'badge-inactive';
}

function daysLeft(m) {
    return Math.ceil((new Date(m.expiryDate) - new Date()) / 86400000);
}

function filterMembers() {
    const q = document.getElementById('searchBox').value.toLowerCase();
    const s = document.getElementById('filterStatus').value;
    renderMembers(allMembers.filter(m => {
        const matchQ = m.name.toLowerCase().includes(q) ||
            m.email.toLowerCase().includes(q) ||
            m.id.toLowerCase().includes(q);
        const matchS = !s || getStatus(m).toUpperCase().replace(' ', '_') === s;
        return matchQ && matchS;
    }));
}

async function submitMember() {
    const id   = document.getElementById('editId').value;
    const body = {
        name:           document.getElementById('fName').value.trim(),
        email:          document.getElementById('fEmail').value.trim(),
        phone:          document.getElementById('fPhone').value.trim(),
        address:        document.getElementById('fAddress').value.trim(),
        membershipType: document.getElementById('fType').value
    };

    if (!body.name || !body.email) {
        showAlert('formAlert', 'Name and Email required', 'danger'); return;
    }

    const res = await fetch(id ? `${API}/${id}` : API, {
        method:  id ? 'PUT' : 'POST',
        headers: {'Content-Type': 'application/json'},
        body:    JSON.stringify(body)
    });

    if (res.ok) {
        const member = await res.json();
        showAlert('formAlert', id ? 'Updated!' : 'Member added! Redirecting to payment...', 'success');

        if (!id) {
            // after new member add go to the next page
            setTimeout(() => {
                sessionStorage.setItem('newMemberId',   member.id);
                sessionStorage.setItem('newMemberName', member.name);
                window.location.href = 'payments.html';
            }, 1500);
        } else {
            clearForm();
            loadMembers();
            loadStats();
        }
    } else {
        showAlert('formAlert', 'Error occurred', 'danger');
    }
}

function editMember(id) {
    const m = allMembers.find(x => x.id === id);
    if (!m) return;
    document.getElementById('editId').value   = m.id;
    document.getElementById('fName').value    = m.name;
    document.getElementById('fEmail').value   = m.email;
    document.getElementById('fPhone').value   = m.phone;
    document.getElementById('fAddress').value = m.address;
    document.getElementById('fType').value    = m.membershipType;
    document.getElementById('formTitle').textContent   = 'Edit Member';
    document.getElementById('submitLabel').textContent = '✓ Update Member';
    window.scrollTo({top: 0, behavior: 'smooth'});
}

async function renewMember(id) {
    await fetch(`${API}/${id}/renew`, {method: 'PUT'});
    loadMembers();
    loadStats();
}

async function toggleMember(id) {
    await fetch(`${API}/${id}/toggle`, {method: 'PUT'});
    loadMembers();
    loadStats();
}

async function deleteMember(id) {
    if (!confirm('Delete this member?')) return;
    await fetch(`${API}/${id}`, {method: 'DELETE'});
    loadMembers();
    loadStats();
}

function clearForm() {
    ['editId','fName','fEmail','fPhone','fAddress'].forEach(id =>
        document.getElementById(id).value = '');
    document.getElementById('fType').value = 'MONTHLY';
    document.getElementById('formTitle').textContent   = 'Add Member';
    document.getElementById('submitLabel').textContent = '✓ Add Member';
    document.getElementById('formAlert').innerHTML = '';
}

function showAlert(id, msg, type) {
    document.getElementById(id).innerHTML =
        `<div class="alert alert-${type}">${msg}</div>`;
    setTimeout(() => document.getElementById(id).innerHTML = '', 3500);
}