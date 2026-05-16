const API = 'http://localhost:8080/api/trainers';
let allTrainers = [], allSessions = [];

document.addEventListener('DOMContentLoaded', () => {
    loadStats(); loadTrainers(); loadSessions();
    document.getElementById('bDate').valueAsDate = new Date();
});

async function loadStats() {
    const s = await (await fetch(`${API}/stats`)).json();
    document.getElementById('sTotal').textContent  = s.total;
    document.getElementById('sAvail').textContent  = s.available;
    document.getElementById('sSess').textContent   = s.totalSessions;
    document.getElementById('sComp').textContent   = s.completed;
    document.getElementById('sSched').textContent  = s.scheduled;
    document.getElementById('sCanc').textContent   = s.cancelled;
}

async function loadTrainers() {
    allTrainers = await (await fetch(API)).json();
    renderTrainers(allTrainers);
    document.getElementById('bTrainer').innerHTML = allTrainers.filter(t=>t.available)
        .map(t=>`<option value="${t.trainerId}">${t.name} – ${fmt(t.specialization)}</option>`).join('');
}

function renderTrainers(list) {
    const tb = document.getElementById('trainerTable');
    if (!list.length) { tb.innerHTML = `<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:2rem">No trainers</td></tr>`; return; }
    tb.innerHTML = list.map(t => {
        const level = t.experienceYears>=10?'SENIOR':t.experienceYears>=5?'MID':'JUNIOR';
        return `<tr>
      <td><div style="display:flex;align-items:center;gap:0.6rem">
        <div class="avatar">${t.name.charAt(0)}</div>
        <div><div style="color:#fff;font-weight:600">${t.name}</div>
        <div style="color:var(--muted);font-size:0.72rem">${t.trainerId}</div></div>
      </div></td>
      <td style="font-size:0.82rem">${fmt(t.specialization)}</td>
      <td style="text-align:center">${t.experienceYears}y</td>
      <td>Rs.${Number(t.monthlyFee).toLocaleString()}</td>
      <td><span class="badge ${t.available?'badge-available':'badge-unavailable'}">${t.available?'Available':'Unavailable'}</span></td>
      <td><div style="display:flex;gap:4px">
        <button class="btn btn-outline btn-sm" onclick="editTrainer('${t.trainerId}')">✏</button>
        <button class="btn btn-outline btn-sm" onclick="toggleTrainer('${t.trainerId}')">⇄</button>
        <button class="btn btn-outline btn-sm" style="color:#ef4444" onclick="deleteTrainer('${t.trainerId}')">🗑</button>
      </div></td>
    </tr>`;
    }).join('');
}

function filterTrainers() {
    const q = document.getElementById('trainerSearch').value.toLowerCase();
    const a = document.getElementById('filterAvail').value;
    renderTrainers(allTrainers.filter(t =>
        (t.name.toLowerCase().includes(q) || t.specialization.toLowerCase().includes(q)) &&
        (!a || String(t.available)===a)
    ));
}

async function submitTrainer() {
    const id   = document.getElementById('editId').value;
    const body = { name:document.getElementById('fName').value.trim(), email:document.getElementById('fEmail').value.trim(), phone:document.getElementById('fPhone').value.trim(), specialization:document.getElementById('fSpec').value, experienceYears:document.getElementById('fExp').value||'0', monthlyFee:document.getElementById('fFee').value||'0' };
    if (!body.name||!body.email) { showAlert('trainerAlert','Name and Email required','danger'); return; }
    const res = await fetch(id?`${API}/${id}`:API, { method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
    if (res.ok) { showAlert('trainerAlert',id?'Updated!':'Added!','success'); clearTrainerForm(); loadTrainers(); loadStats(); }
    else showAlert('trainerAlert','Error','danger');
}

function editTrainer(id) {
    const t = allTrainers.find(x=>x.trainerId===id);
    if (!t) return;
    document.getElementById('editId').value  = t.trainerId;
    document.getElementById('fName').value   = t.name;
    document.getElementById('fEmail').value  = t.email;
    document.getElementById('fPhone').value  = t.phone;
    document.getElementById('fSpec').value   = t.specialization;
    document.getElementById('fExp').value    = t.experienceYears;
    document.getElementById('fFee').value    = t.monthlyFee;
    document.getElementById('formTitle').textContent = 'Edit Trainer';
    document.getElementById('submitLabel').textContent = '✓ Update Trainer';
    window.scrollTo({top:0,behavior:'smooth'});
}

async function toggleTrainer(id) { await fetch(`${API}/${id}/toggle`,{method:'PUT'}); loadTrainers(); loadStats(); }
async function deleteTrainer(id) { if (!confirm('Delete trainer?')) return; await fetch(`${API}/${id}`,{method:'DELETE'}); loadTrainers(); loadStats(); }

async function bookSession() {
    const trainerId  = document.getElementById('bTrainer').value;
    const memberId   = document.getElementById('bMemberId').value.trim();
    const memberName = document.getElementById('bMemberName').value.trim();
    const date       = document.getElementById('bDate').value;
    const timeSlot   = document.getElementById('bSlot').value;
    const type       = allTrainers.find(t=>t.trainerId===trainerId)?.specialization || 'GENERAL';
    if (!trainerId||!memberId||!memberName||!date) { showAlert('sessionAlert','Fill all fields','danger'); return; }
    const res = await fetch(`${API}/sessions`, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({trainerId,memberId,memberName,date,timeSlot,type}) });
    if (res.ok) { showAlert('sessionAlert','Session booked!','success'); document.getElementById('bMemberId').value=''; document.getElementById('bMemberName').value=''; loadSessions(); loadStats(); }
    else { const e=await res.text(); showAlert('sessionAlert','Error: '+e,'danger'); }
}

async function loadSessions() {
    allSessions = await (await fetch(`${API}/sessions`)).json();
    renderSessions(allSessions);
}

function renderSessions(list) {
    const tb = document.getElementById('sessionTable');
    if (!list.length) { tb.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:2rem">No sessions</td></tr>`; return; }
    tb.innerHTML = [...list].reverse().map(s => `
    <tr>
      <td style="color:#60a5fa;font-size:0.78rem">${s.sessionId}</td>
      <td style="font-size:0.82rem">${s.trainerName}</td>
      <td>${s.memberName}<br><span style="color:var(--muted);font-size:0.72rem">${s.memberId}</span></td>
      <td style="color:var(--muted);font-size:0.8rem">${s.date}</td>
      <td style="font-size:0.78rem">${s.timeSlot}</td>
      <td><span class="badge badge-${s.status.toLowerCase()}">${s.status}</span></td>
      <td><div style="display:flex;gap:4px">
        ${s.status==='SCHEDULED'?`
          <button class="btn btn-outline btn-sm" style="color:var(--green)" onclick="setStatus('${s.sessionId}','COMPLETED')">✓</button>
          <button class="btn btn-outline btn-sm" style="color:#ef4444" onclick="cancelSess('${s.sessionId}')">✕</button>`:''}
      </div></td>
    </tr>`).join('');
}

function showSessions(filter, btn) {
    document.querySelectorAll('.tab-btn').forEach(b=>b.classList.remove('active'));
    btn.classList.add('active');
    renderSessions(filter==='ALL'?allSessions:allSessions.filter(s=>s.status===filter));
}

async function setStatus(id, status) { await fetch(`${API}/sessions/${id}/status`,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({status})}); loadSessions(); loadStats(); }
async function cancelSess(id) { await fetch(`${API}/sessions/${id}/cancel`,{method:'PUT'}); loadSessions(); loadStats(); }

function clearTrainerForm() {
    ['editId','fName','fEmail','fPhone','fExp','fFee'].forEach(id=>document.getElementById(id).value='');
    document.getElementById('fSpec').value='WEIGHT_TRAINING';
    document.getElementById('formTitle').textContent='Add Trainer';
    document.getElementById('submitLabel').textContent='✓ Add Trainer';
    document.getElementById('trainerAlert').innerHTML='';
}

function fmt(s) { return s.replace(/_/g,' ').replace(/\b\w/g,c=>c.toUpperCase()); }
function showAlert(id,msg,type) { document.getElementById(id).innerHTML=`<div class="alert alert-${type}">${msg}</div>`; setTimeout(()=>document.getElementById(id).innerHTML='',3000); }