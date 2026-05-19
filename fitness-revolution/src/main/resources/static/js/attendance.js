const API        = 'http://localhost:8080/api/attendance';
const MEMBER_API = 'http://localhost:8080/api/members';

let allAtt     = [];
let allMembers = [];

/* ─────────────────────────────────────────
   INIT
───────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadAll();
    loadToday();
    loadMembersIntoSelect();
});

/* ─────────────────────────────────────────
   LOAD MEMBERS → POPULATE <select>
───────────────────────────────────────── */
async function loadMembersIntoSelect() {
    try {
        const r = await fetch(MEMBER_API);
        allMembers = await r.json();

        var sel = document.getElementById('memberSelect');
        sel.innerHTML = '<option value="">— Select Member —</option>';

        allMembers.forEach(function(m) {
            var opt = document.createElement('option');
            opt.value       = m.id;
            opt.dataset.name = m.name;
            opt.dataset.status = m.status;
            // Format: MBR-0001 — nimal
            opt.textContent = m.id + ' — ' + m.name;
            sel.appendChild(opt);
        });
    } catch(e) {
        console.error('Could not load members', e);
    }
}

/* ─────────────────────────────────────────
   ON SELECT CHANGE
───────────────────────────────────────── */
function onMemberSelect() {
    var sel    = document.getElementById('memberSelect');
    var opt    = sel.options[sel.selectedIndex];
    var id     = sel.value;
    var name   = opt.dataset.name   || '';
    var status = opt.dataset.status || '';

    document.getElementById('fMemberId').value   = id;
    document.getElementById('fMemberName').value = name;

    if (id && (status === 'EXPIRED' || status === 'INACTIVE')) {
        showAlert('attAlert', 'Member is ' + status + ' — check-in may be restricted', 'danger');
    }
}

/* ─────────────────────────────────────────
   CLEAR SELECTION
───────────────────────────────────────── */
function clearMemberSelection() {
    document.getElementById('memberSelect').value = '';
    document.getElementById('fMemberId').value    = '';
    document.getElementById('fMemberName').value  = '';
}

/* ─────────────────────────────────────────
   STATS
───────────────────────────────────────── */
async function loadStats() {
    var s = await (await fetch(API + '/stats')).json();
    document.getElementById('sTotal').textContent     = s.total;
    document.getElementById('sToday').textContent     = s.today;
    document.getElementById('sPresent').textContent   = s.present;
    document.getElementById('sCompleted').textContent = s.completed;
}

/* ─────────────────────────────────────────
   CHECK IN
───────────────────────────────────────── */
async function doCheckIn() {
    var memberId   = document.getElementById('fMemberId').value.trim();
    var memberName = document.getElementById('fMemberName').value.trim();

    if (!memberId || !memberName) {
        showAlert('attAlert', 'Please select a member first', 'danger');
        return;
    }

    var res = await fetch(API + '/checkin', {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ memberId: memberId, memberName: memberName })
    });

    if (res.ok) {
        showAlert('attAlert', memberName + ' checked in successfully!', 'success');
        clearMemberSelection();
        loadAll(); loadToday(); loadStats();
    } else {
        var e = await res.text();
        showAlert('attAlert', e, 'danger');
    }
}

/* ─────────────────────────────────────────
   CHECK OUT (form button)
───────────────────────────────────────── */
async function doCheckOut() {
    var memberId = document.getElementById('fMemberId').value.trim();
    if (!memberId) {
        showAlert('attAlert', 'Please select a member first', 'danger');
        return;
    }
    checkOutMember(memberId);
}

/* ─────────────────────────────────────────
   CHECK OUT (row button + form)
───────────────────────────────────────── */
async function checkOutMember(memberId) {
    var res = await fetch(API + '/checkout/' + memberId, { method: 'PUT' });
    if (res.ok) {
        showAlert('attAlert', 'Checked out successfully!', 'success');
        clearMemberSelection();
        loadAll(); loadToday(); loadStats();
    } else {
        showAlert('attAlert', 'Member is not checked in today', 'danger');
    }
}

/* ─────────────────────────────────────────
   LOAD ALL RECORDS
───────────────────────────────────────── */
async function loadAll() {
    var res = await fetch(API);
    allAtt  = await res.json();
    renderAll(allAtt);
}

/* ─────────────────────────────────────────
   TODAY TABLE
───────────────────────────────────────── */
async function loadToday() {
    var list = await (await fetch(API + '/today')).json();
    var tb   = document.getElementById('todayTable');

    if (!list.length) {
        tb.innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:1.5rem;">No one checked in today</td></tr>';
        return;
    }

    tb.innerHTML = list.map(function(a) {
        var isPresent = a.status === 'PRESENT';
        var btn = isPresent
            ? '<button class="btn btn-outline" style="padding:2px 10px;font-size:.65rem;color:var(--green);border-color:rgba(200,240,38,0.4);" onclick="checkOutMember(\'' + a.memberId + '\')"><i class="ti ti-logout"></i> Check Out</button>'
            : '<span style="color:var(--muted);font-size:.72rem;">Done</span>';

        return '<tr>'
            + '<td><div style="font-weight:600;">' + a.memberName + '</div>'
            + '<span style="color:var(--muted);font-size:.72rem;">' + a.memberId + '</span></td>'
            + '<td style="color:var(--green);">' + a.checkIn + '</td>'
            + '<td style="color:var(--muted);">' + (a.checkOut === '-' ? '—' : a.checkOut) + '</td>'
            + '<td><span class="badge badge-' + a.status.toLowerCase() + '">' + a.status + '</span></td>'
            + '<td>' + btn + '</td>'
            + '</tr>';
    }).join('');
}

/* ─────────────────────────────────────────
   ALL RECORDS TABLE
───────────────────────────────────────── */
function renderAll(list) {
    var tb = document.getElementById('attTable');
    if (!list.length) {
        tb.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:2rem;">No records</td></tr>';
        return;
    }
    tb.innerHTML = list.slice().reverse().map(function(a) {
        return '<tr>'
            + '<td style="color:var(--muted);font-size:.78rem;">' + a.attendanceId + '</td>'
            + '<td><div>' + a.memberName + '</div>'
            + '<span style="color:var(--muted);font-size:.72rem;">' + a.memberId + '</span></td>'
            + '<td style="color:var(--muted);font-size:.8rem;">' + a.date + '</td>'
            + '<td style="color:var(--green);">' + a.checkIn + '</td>'
            + '<td style="color:var(--muted);">' + (a.checkOut === '-' ? '—' : a.checkOut) + '</td>'
            + '<td><span class="badge badge-' + a.status.toLowerCase() + '">' + a.status + '</span></td>'
            + '<td><button class="btn-icon" onclick="deleteAtt(\'' + a.attendanceId + '\')" title="Delete"><i class="ti ti-trash"></i></button></td>'
            + '</tr>';
    }).join('');
}

/* ─────────────────────────────────────────
   FILTER
───────────────────────────────────────── */
function filterAtt() {
    var q = document.getElementById('attSearch').value.toLowerCase();
    renderAll(allAtt.filter(function(a) {
        return a.memberName.toLowerCase().includes(q) || a.memberId.toLowerCase().includes(q);
    }));
}

/* ─────────────────────────────────────────
   DELETE
───────────────────────────────────────── */
async function deleteAtt(id) {
    if (!confirm('Delete this attendance record?')) return;
    await fetch(API + '/' + id, { method: 'DELETE' });
    loadAll(); loadToday(); loadStats();
}

/* ─────────────────────────────────────────
   ALERT
───────────────────────────────────────── */
function showAlert(id, msg, type) {
    var el = document.getElementById(id);
    el.innerHTML = '<div class="alert alert-' + type + '">' + msg + '</div>';
    setTimeout(function() { el.innerHTML = ''; }, 3500);
}