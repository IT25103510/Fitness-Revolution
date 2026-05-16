const API        = 'http://localhost:8080/api/payments';
const MEMBERS_API = 'http://localhost:8080/api/members';
let selectedPlan = 'MONTHLY', selectedMethod = 'CASH';

document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadPayments();
    loadRefunds();
    loadMembersDropdown();
});

async function loadMembersDropdown() {
    const members = await (await fetch(MEMBERS_API)).json();
    const select  = document.getElementById('fMemberId');

    select.innerHTML = '<option value="">-- Select Member --</option>' +
        members.map(m =>
            `<option value="${m.id}" data-name="${m.name}">${m.id} — ${m.name}</option>`
        ).join('');

    select.onchange = function() {
        const opt = this.options[this.selectedIndex];
        document.getElementById('fMemberName').value = opt.dataset.name || '';
    };

    // Members page එකෙන් redirect වෙලා ආවොත් auto select
    const newMemberId   = sessionStorage.getItem('newMemberId');
    const newMemberName = sessionStorage.getItem('newMemberName');

    if (newMemberId) {
        select.value = newMemberId;
        document.getElementById('fMemberName').value = newMemberName;
        sessionStorage.removeItem('newMemberId');
        sessionStorage.removeItem('newMemberName');
        showAlert('payAlert',
            `Welcome ${newMemberName}! Please complete your payment to activate membership.`,
            'success');
    }
}

async function loadStats() {
    const s = await (await fetch(`${API}/stats`)).json();
    document.getElementById('sTotal').textContent    = s.total;
    document.getElementById('sRevenue').textContent  = 'Rs. ' + Number(s.revenue).toLocaleString();
    document.getElementById('sRefunded').textContent = s.refunded;
    document.getElementById('sOverdue').textContent  = s.overdue;
}

function selectPlan(radio) {
    selectedPlan = radio.value;
    document.querySelectorAll('.plan-card').forEach(c => c.classList.remove('selected'));
    radio.closest('.plan-card').classList.add('selected');
}

function selectMethod(method, el) {
    selectedMethod = method;
    document.querySelectorAll('.method-btn').forEach(b => b.classList.remove('selected'));
    el.classList.add('selected');
}

async function submitPayment() {
    const memberId   = document.getElementById('fMemberId').value.trim();
    const memberName = document.getElementById('fMemberName').value.trim();
    if (!memberId || !memberName) {
        showAlert('payAlert', 'Member select කරන්න!', 'danger'); return;
    }
    const res = await fetch(API, {
        method:  'POST',
        headers: {'Content-Type': 'application/json'},
        body:    JSON.stringify({
            memberId, memberName,
            plan:   selectedPlan,
            method: selectedMethod
        })
    });
    if (res.ok) {
        const data = await res.json();
        showAlert('payAlert', 'Payment successful! Member is now ACTIVE ✅', 'success');
        loadPayments();
        loadStats();
        loadRefunds();
        showInvoice(data.invoice);
    } else {
        showAlert('payAlert', 'Payment failed', 'danger');
    }
}

async function loadPayments() {
    const list = await (await fetch(API)).json();
    const tb   = document.getElementById('payTable');
    if (!list.length) {
        tb.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:2rem">No payments</td></tr>`;
        return;
    }
    tb.innerHTML = [...list].reverse().map(p => `
    <tr>
      <td style="color:var(--green);font-size:0.78rem">${p.paymentId}</td>
      <td>${p.memberName}<br>
        <span style="color:var(--muted);font-size:0.72rem">${p.memberId}</span></td>
      <td>${p.plan}</td>
      <td><span class="badge badge-${p.method.toLowerCase()}">${p.method}</span></td>
      <td style="font-weight:600">Rs.${p.amount.toLocaleString()}</td>
      <td><span class="badge badge-${p.status.toLowerCase()}">${p.status}</span></td>
      <td>${p.status === 'SUCCESS'
        ? `<button class="btn btn-outline btn-sm" onclick="openRefund('${p.paymentId}')">↩</button>`
        : ''}</td>
    </tr>`).join('');
}

async function loadRefunds() {
    const list = await (await fetch(`${API}/refunds`)).json();
    const tb   = document.getElementById('refundTable');
    if (!list.length) {
        tb.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--muted);padding:2rem">No refunds</td></tr>`;
        return;
    }
    tb.innerHTML = list.map(r => `
    <tr>
      <td style="color:#60a5fa;font-size:0.78rem">${r.refundId}</td>
      <td>${r.memberName}</td>
      <td style="color:#ef4444">Rs.${Math.abs(r.amount).toLocaleString()}</td>
      <td style="color:var(--muted);font-size:0.8rem">${r.reason}</td>
      <td style="color:var(--muted);font-size:0.8rem">${r.date}</td>
    </tr>`).join('');
}

function showInvoice(inv) {
    document.getElementById('invId').textContent    = 'Invoice #' + inv.invoiceId;
    document.getElementById('invTotal').textContent = 'Rs. ' + inv.amount.toLocaleString();
    document.getElementById('invBody').innerHTML    = `
    <div class="invoice-row"><span>Member ID</span><span>${inv.memberId}</span></div>
    <div class="invoice-row"><span>Member Name</span><span>${inv.memberName}</span></div>
    <div class="invoice-row"><span>Plan</span><span>${inv.plan}</span></div>
    <div class="invoice-row"><span>Issue Date</span><span>${inv.issueDate}</span></div>
    <div class="invoice-row"><span>Due Date</span><span>${inv.dueDate}</span></div>
    <div class="invoice-row"><span>Status</span>
      <span style="color:var(--green)">✓ PAID</span></div>`;
    document.getElementById('invoiceModal').classList.add('show');
}

function openRefund(paymentId) {
    document.getElementById('refundPayId').value    = paymentId;
    document.getElementById('refundReason').value   = '';
    document.getElementById('refundAlert').innerHTML = '';
    document.getElementById('refundModal').classList.add('show');
}

async function submitRefund() {
    const id     = document.getElementById('refundPayId').value;
    const reason = document.getElementById('refundReason').value.trim();
    if (!reason) { showAlert('refundAlert', 'Reason දාන්න', 'danger'); return; }
    const res = await fetch(`${API}/${id}/refund`, {
        method:  'POST',
        headers: {'Content-Type': 'application/json'},
        body:    JSON.stringify({reason})
    });
    if (res.ok) {
        showAlert('refundAlert', 'Refund done!', 'success');
        setTimeout(() => {
            document.getElementById('refundModal').classList.remove('show');
            loadPayments();
            loadStats();
            loadRefunds();
        }, 1200);
    } else {
        showAlert('refundAlert', 'Refund failed', 'danger');
    }
}

function showAlert(id, msg, type) {
    document.getElementById(id).innerHTML =
        `<div class="alert alert-${type}">${msg}</div>`;
    setTimeout(() => document.getElementById(id).innerHTML = '', 4000);
}