document.addEventListener('DOMContentLoaded', () => {
    loadMemberReport();
    loadPaymentReport();
    loadTrainerReport();
    loadAttReport();
});

async function loadMemberReport() {
    const s = await (await fetch('http://localhost:8080/api/members/stats')).json();
    document.getElementById('memberReport').innerHTML = `
    <div class="invoice-row"><span>Total Members</span><span style="color:var(--green)">${s.total}</span></div>
    <div class="invoice-row"><span>Active</span><span style="color:#22c55e">${s.active}</span></div>
    <div class="invoice-row"><span>Expiring Soon</span><span style="color:#f59e0b">${s.expiringSoon}</span></div>
    <div class="invoice-row"><span>Expired</span><span style="color:#ef4444">${s.expired}</span></div>
    <div class="invoice-row"><span>Inactive</span><span style="color:#9ca3af">${s.inactive}</span></div>`;
}

async function loadPaymentReport() {
    const s = await (await fetch('http://localhost:8080/api/payments/stats')).json();
    document.getElementById('paymentReport').innerHTML = `
    <div class="invoice-row"><span>Total Payments</span><span style="color:var(--green)">${s.total}</span></div>
    <div class="invoice-row"><span>Total Revenue</span><span style="color:#22c55e">Rs. ${Number(s.revenue).toLocaleString()}</span></div>
    <div class="invoice-row"><span>Refunds</span><span style="color:#60a5fa">${s.refunded}</span></div>
    <div class="invoice-row"><span>Overdue Invoices</span><span style="color:#ef4444">${s.overdue}</span></div>`;
}

async function loadTrainerReport() {
    const s = await (await fetch('http://localhost:8080/api/trainers/stats')).json();
    document.getElementById('trainerReport').innerHTML = `
    <div class="invoice-row"><span>Total Trainers</span><span style="color:var(--green)">${s.total}</span></div>
    <div class="invoice-row"><span>Available</span><span style="color:#22c55e">${s.available}</span></div>
    <div class="invoice-row"><span>Total Sessions</span><span style="color:#60a5fa">${s.totalSessions}</span></div>
    <div class="invoice-row"><span>Completed</span><span style="color:#22c55e">${s.completed}</span></div>
    <div class="invoice-row"><span>Scheduled</span><span style="color:#f59e0b">${s.scheduled}</span></div>
    <div class="invoice-row"><span>Cancelled</span><span style="color:#ef4444">${s.cancelled}</span></div>`;
}

async function loadAttReport() {
    const s = await (await fetch('http://localhost:8080/api/attendance/stats')).json();
    document.getElementById('attReport').innerHTML = `
    <div class="invoice-row"><span>Total Records</span><span style="color:var(--green)">${s.total}</span></div>
    <div class="invoice-row"><span>Today's Visits</span><span style="color:#60a5fa">${s.today}</span></div>
    <div class="invoice-row"><span>Currently Present</span><span style="color:#f59e0b">${s.present}</span></div>
    <div class="invoice-row"><span>Completed Sessions</span><span style="color:#22c55e">${s.completed}</span></div>`;
}