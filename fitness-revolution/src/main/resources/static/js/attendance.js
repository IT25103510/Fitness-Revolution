const API = 'http://localhost:8080/api/attendance';
let allAtt = [];

document.addEventListener('DOMContentLoaded', () => { loadStats(); loadAll(); loadToday(); });

async function loadStats() {
    const s = await (await fetch(`${API}/stats`)).json();
    document.getElementById('sTotal').textContent     = s.total;
    document.getElementById('sToday').textContent     = s.today;
    document.getElementById('sPresent').textContent   = s.present;
    document.getElementById('sCompleted').textContent = s.completed;
}

async function doCheckIn() {
    const memberId   = document.getElementById('fMemberId').value.trim();
    const memberName = document.getElementById('fMemberName').value.trim();
    if (!memberId || !memberName) { showAlert('attAlert', 'Fill Member ID and Name', 'danger'); return; }
    const res = await fetch(`${API}/checkin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memberId, memberName })
    });
    if (res.ok) {
        showAlert('attAlert', 'Checked in successfully!', 'success');
        document.getElementById('fMemberId').value = '';
        document.getElementById('fMemberName').value = '';
        loadAll(); loadToday(); loadStats();
    } else {
        const e = await res.text();
        showAlert('attAlert', e, 'danger');
    }
}

async function doCheckOut() {
    const memberId = document.getElementById('fMemberId').value.trim();
    if (!memberId) { showAlert('attAlert', 'Enter Member ID', 'danger'); return; }
    await checkOutMember(memberId);
}

// Called from both the manual form and the row-level Check Out button
async function checkOutMember(memberId) {
    const res = await fetch(`${API}/checkout/${memberId}`, { method: 'PUT' });
    if (res.ok) {
        showAlert('attAlert', 'Checked out successfully!', 'success');
        document.getElementById('fMemberId').value = '';
        document.getElementById('fMemberName').value = '';
        loadAll(); loadToday(); loadStats();
    } else {
        showAlert('attAlert', 'Member is not checked in today', 'danger');
    }
}

async function loadAll() {
    const res = await fetch(API);
    allAtt = await res.json();
    renderAll(allAtt);
}

async function loadToday() {
    const list = await (await fetch(`${API}/today`)).json();
    const tb = document.getElementById('todayTable');
    if (!list.length) {
        tb.innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:1.5rem">No one checked in today</td></tr>';
        return;
    }
    tb.innerHTML = list.map(a => {
        const isPresent = a.status === 'PRESENT';
        const checkoutBtn = isPresent
            ? '<button class="btn btn-outline btn-sm" style="padding:2px 10px;font-size:0.65rem;color:var(--green);border-color:rgba(200,240,38,0.4)" onclick="checkOutMember(\'' + a.memberId + '\')"><i class="ti ti-logout"></i> Check Out</button>'
            : '<span style="color:var(--muted);font-size:0.72rem">Done</span>';
        return '<tr>'
            + '<td><div style="font-weight:600">' + a.memberName + '</div><span style="color:var(--muted);font-size:0.72rem">' + a.memberId + '</span></td>'
            + '<td style="color:var(--green)">' + a.checkIn + '</td>'
            + '<td style="color:var(--muted)">' + (a.checkOut === '-' ? '—' : a.checkOut) + '</td>'
            + '<td><span class="badge badge-' + a.status.toLowerCase() + '">' + a.status + '</span></td>'
            + '<td>' + checkoutBtn + '</td>'
            + '</tr>';
    }).join('');
}

function renderAll(list) {
    const tb = document.getElementById('attTable');
    if (!list.length) {
        tb.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:2rem">No records</td></tr>';
        return;
    }
    tb.innerHTML = [...list].reverse().map(a =>
        '<tr>'
        + '<td style="color:var(--muted);font-size:0.78rem">' + a.attendanceId + '</td>'
        + '<td><div>' + a.memberName + '</div><span style="color:var(--muted);font-size:0.72rem">' + a.memberId + '</span></td>'
        + '<td style="color:var(--muted);font-size:0.8rem">' + a.date + '</td>'
        + '<td style="color:var(--green)">' + a.checkIn + '</td>'
        + '<td style="color:var(--muted)">' + (a.checkOut === '-' ? '—' : a.checkOut) + '</td>'
        + '<td><span class="badge badge-' + a.status.toLowerCase() + '">' + a.status + '</span></td>'
        + '<td><button class="btn-icon" onclick="deleteAtt(\'' + a.attendanceId + '\')" title="Delete"><i class="ti ti-trash"></i></button></td>'
        + '</tr>'
    ).join('');
}

function filterAtt() {
    const q = document.getElementById('attSearch').value.toLowerCase();
    renderAll(allAtt.filter(a =>
        a.memberName.toLowerCase().includes(q) || a.memberId.toLowerCase().includes(q)
    ));
}

async function deleteAtt(id) {
    if (!confirm('Delete this attendance record?')) return;
    await fetch(`${API}/${id}`, { method: 'DELETE' });
    loadAll(); loadToday(); loadStats();
}

function showAlert(id, msg, type) {
    document.getElementById(id).innerHTML = '<div class="alert alert-' + type + '">' + msg + '</div>';
    setTimeout(() => document.getElementById(id).innerHTML = '', 3000);
}