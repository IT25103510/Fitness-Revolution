const API = 'http://localhost:8080/api/members';
let allMembers = [];
let allMembershipTypes = [];

// Modal state
let modalMemberId    = null;
let modalSelectedType = null;

document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadMembers();
    loadMembershipTypes();
});

// ── Membership Types ──────────────────────────────────

const DEFAULT_MEMBERSHIP_TYPES = [
    { code: 'MONTHLY',  name: 'Monthly',  price: 3000,  durationMonths: 1  },
    { code: 'YEARLY',   name: 'Yearly',   price: 25000, durationMonths: 12 },
    { code: 'STUDENT',  name: 'Student',  price: 1500,  durationMonths: 6  },
    { code: 'FAMILY',   name: 'Family',   price: 20000, durationMonths: 12 },
];

function formatDuration(months) {
    if (months === 1)  return '1 Month';
    if (months === 6)  return '6 Months';
    if (months === 12) return '1 Year';
    return months + ' Months';
}

async function loadMembershipTypes() {
    try {
        const res = await fetch('http://localhost:8080/api/membership-types/active');
        if (!res.ok) throw new Error();
        const types = await res.json();
        if (!types || types.length === 0) throw new Error();
        allMembershipTypes = types;
    } catch(e) {
        allMembershipTypes = DEFAULT_MEMBERSHIP_TYPES;
    }
}

// ── Membership Type Modal ─────────────────────────────

function openTypeModal(memberId) {
    const m = allMembers.find(x => x.id === memberId);
    if (!m) return;

    modalMemberId     = memberId;
    modalSelectedType = m.membershipType || null;

    document.getElementById('modalSub').textContent =
        'Member: ' + m.name + '  ·  Current: ' + (m.membershipType || 'None');

    const grid = document.getElementById('modalTypeGrid');
    if (!allMembershipTypes.length) {
        grid.innerHTML = '<div style="color:var(--muted);font-size:0.85rem;grid-column:1/-1;text-align:center;padding:1rem">No membership types found. Add types in Memberships page first.</div>';
    } else {
        grid.innerHTML = allMembershipTypes.map(function(t) {
            var sel = t.code === modalSelectedType ? ' selected' : '';
            return '<div class="modal-type-card' + sel + '" id="mtc-' + t.code + '" onclick="selectModalType(\'' + t.code + '\')">' +
                '<div class="mtc-name">' + t.name + '</div>' +
                '<div class="mtc-price">Rs. ' + Number(t.price).toLocaleString() + '</div>' +
                '<div class="mtc-dur">&#9201; ' + formatDuration(t.durationMonths) + '</div>' +
                '</div>';
        }).join('');
    }

    document.getElementById('typeModal').classList.add('open');
}

function selectModalType(code) {
    modalSelectedType = code;
    document.querySelectorAll('.modal-type-card').forEach(function(c) { c.classList.remove('selected'); });
    var card = document.getElementById('mtc-' + code);
    if (card) card.classList.add('selected');
}

async function saveTypeFromModal() {
    if (!modalMemberId) return;
    if (!modalSelectedType) { alert('Please select a membership type.'); return; }

    const m = allMembers.find(x => x.id === modalMemberId);
    if (!m) return;

    const body = {
        name:           m.name,
        email:          m.email,
        phone:          m.phone   || '',
        address:        m.address || '',
        membershipType: modalSelectedType
    };

    try {
        const res = await fetch(API + '/' + modalMemberId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (res.ok) {
            closeTypeModalDirect();
            // Member info save කරලා memberships page ට redirect
            sessionStorage.setItem('assignMemberId',   modalMemberId);
            sessionStorage.setItem('assignMemberName', m.name);
            sessionStorage.setItem('assignMemberType', modalSelectedType);
            window.location.href = 'memberships.html';
        } else {
            alert('Failed to update membership type. Please try again.');
        }
    } catch(e) {
        alert('Network error. Is the server running?');
    }
}

function closeTypeModal(event) {
    if (event.target === document.getElementById('typeModal')) closeTypeModalDirect();
}

function closeTypeModalDirect() {
    document.getElementById('typeModal').classList.remove('open');
    modalMemberId     = null;
    modalSelectedType = null;
}

// ── Stats & Members ───────────────────────────────────

async function loadStats() {
    try {
        const s = await (await fetch(API + '/stats')).json();
        document.getElementById('sTotal').textContent    = s.total;
        document.getElementById('sActive').textContent   = s.active;
        document.getElementById('sExpiring').textContent = s.expiringSoon;
        document.getElementById('sExpired').textContent  = s.expired;
        document.getElementById('sInactive').textContent = s.inactive;
    } catch(e) {}
}

async function loadMembers() {
    try {
        allMembers = await (await fetch(API)).json();
        renderMembers(allMembers);
    } catch(e) {
        document.getElementById('memberTable').innerHTML =
            '<tr><td colspan="5" style="text-align:center;color:var(--red);padding:2rem">Failed to load members. Is the server running?</td></tr>';
    }
}

function renderMembers(list) {
    const tb = document.getElementById('memberTable');
    if (!list.length) {
        tb.innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:2rem">No members found</td></tr>';
        return;
    }
    tb.innerHTML = list.map(m =>
        '<tr>' +
        '<td><div style="display:flex;align-items:center;gap:0.6rem">' +
        '<div class="avatar">' + m.name.charAt(0) + '</div>' +
        '<div><div style="color:#fff;font-weight:600">' + m.name + '</div>' +
        '<div style="color:var(--muted);font-size:0.72rem">' + m.id + ' &middot; ' + m.email + '</div></div>' +
        '</div></td>' +
        '<td><span class="badge badge-' + (m.membershipType ? m.membershipType.toLowerCase() : 'inactive') + '" ' +
        'style="cursor:pointer" title="Click to change type" onclick="openTypeModal(\'' + m.id + '\')">' +
        (m.membershipType || '—') + '</span></td>' +
        '<td style="color:var(--muted);font-size:0.8rem">' + (m.expiryDate || '—') + '</td>' +
        '<td><span class="badge ' + statusClass(m) + '">' + getStatus(m) + '</span></td>' +
        '<td><div style="display:flex;gap:4px">' +
        '<button class="btn btn-outline btn-sm" title="Edit member" onclick="editMember(\'' + m.id + '\')">&#9998;</button>' +
        '<button class="btn btn-outline btn-sm" title="Change membership type" onclick="openTypeModal(\'' + m.id + '\')">&#127991;</button>' +
        '<button class="btn btn-outline btn-sm" title="Renew" onclick="renewMember(\'' + m.id + '\')">&#8635;</button>' +
        '<button class="btn btn-outline btn-sm" title="Toggle active" onclick="toggleMember(\'' + m.id + '\')">&#8644;</button>' +
        '<button class="btn btn-outline btn-sm" title="Delete" style="color:#ef4444" onclick="deleteMember(\'' + m.id + '\')">&#128465;</button>' +
        '</div></td>' +
        '</tr>'
    ).join('');
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

// ── Add / Edit Member Form ────────────────────────────

async function submitMember() {
    const id = document.getElementById('editId').value;
    const body = {
        name:    document.getElementById('fName').value.trim(),
        email:   document.getElementById('fEmail').value.trim(),
        phone:   document.getElementById('fPhone').value.trim(),
        address: document.getElementById('fAddress').value.trim(),
    };

    if (!body.name || !body.email) {
        showAlert('formAlert', 'Name and Email are required.', 'error'); return;
    }

    // New member: assign first available type as default; user can change via modal
    if (!id) {
        body.membershipType = (allMembershipTypes[0] || DEFAULT_MEMBERSHIP_TYPES[0]).code;
    }

    const res = await fetch(id ? API + '/' + id : API, {
        method:  id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify(body)
    });

    if (res.ok) {
        const member = await res.json();
        if (!id) {
            // Redirect to memberships page so admin can assign a type
            sessionStorage.setItem('assignMemberId',   member.id);
            sessionStorage.setItem('assignMemberName', member.name);
            window.location.href = 'memberships.html';
        } else {
            showAlert('formAlert', 'Member updated!', 'success');
            clearForm();
            await loadMembers();
            await loadStats();
        }
    } else {
        showAlert('formAlert', 'Error occurred. Please try again.', 'error');
    }
}

function editMember(id) {
    const m = allMembers.find(x => x.id === id);
    if (!m) return;
    document.getElementById('editId').value   = m.id;
    document.getElementById('fName').value    = m.name;
    document.getElementById('fEmail').value   = m.email;
    document.getElementById('fPhone').value   = m.phone   || '';
    document.getElementById('fAddress').value = m.address || '';
    document.getElementById('formTitle').textContent   = 'EDIT MEMBER';
    document.getElementById('submitLabel').textContent = '✓ UPDATE MEMBER';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function renewMember(id) {
    await fetch(API + '/' + id + '/renew', { method: 'PUT' });
    loadMembers(); loadStats();
}

async function toggleMember(id) {
    await fetch(API + '/' + id + '/toggle', { method: 'PUT' });
    loadMembers(); loadStats();
}

async function deleteMember(id) {
    if (!confirm('Delete this member? This cannot be undone.')) return;
    await fetch(API + '/' + id, { method: 'DELETE' });
    loadMembers(); loadStats();
}

function clearForm() {
    ['editId','fName','fEmail','fPhone','fAddress'].forEach(id =>
        document.getElementById(id).value = '');
    document.getElementById('formTitle').textContent   = 'ADD MEMBER';
    document.getElementById('submitLabel').textContent = '✓ ADD MEMBER';
    document.getElementById('formAlert').innerHTML = '';
}

function showAlert(id, msg, type) {
    document.getElementById(id).innerHTML =
        '<div class="alert alert-' + type + '">' + msg + '</div>';
    setTimeout(() => {
        const el = document.getElementById(id);
        if (el) el.innerHTML = '';
    }, 4000);
}