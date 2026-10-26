import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
async function request(path, options = {}) {
  const requestOptions = { ...options }
  if (requestOptions.body) requestOptions.headers = { 'Content-Type': 'application/json', ...(requestOptions.headers || {}) }
  const response = await fetch(`${API}${path}`, requestOptions)
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.error || `Request failed (${response.status})`)
  }
  return response.status === 204 ? null : response.json()
}

const emptyStudent = { registrationNumber: '', name: '', email: '', phone: '', course: '', address: '' }
const emptyRoom = { roomNumber: '', capacity: 1, floor: 0, status: 'AVAILABLE' }

function App() {
  const [page, setPage] = useState('home')
  const [data, setData] = useState({ students: [], rooms: [], allocations: [], fees: [], summary: {} })
  const [message, setMessage] = useState({ type: '', text: '' })
  const [loading, setLoading] = useState(true)

  const load = async () => {
    setLoading(true)
    try {
      const [students, rooms, allocations, fees, summary] = await Promise.all(['/students', '/rooms', '/allocations', '/fees', '/dashboard'].map(request))
      setData({ students, rooms, allocations, fees, summary })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])
  const action = async (work) => {
    try {
      await work()
      await load()
      setMessage({ type: 'success', text: 'Saved successfully.' })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    }
  }

  const openManager = (section = 'dashboard') => {
    setPage(section)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  return <div className="site-shell">
    <header className="site-header">
      <button className="brand" onClick={() => setPage('home')} aria-label="HostelHub home"><Logo /></button>
      <nav className="main-nav">
        <button onClick={() => setPage('home')}>Home</button>
        <a href="#services">Services</a>
        <a href="#about">About us</a>
        <a href="#stories">Stories</a>
      </nav>
      <button className="header-cta" onClick={() => openManager()}>Open dashboard <span>↗</span></button>
    </header>

    {message.text && <div className={`notice ${message.type}`}>{message.text}<button onClick={() => setMessage({ type: '', text: '' })}>×</button></div>}
    {page === 'home' ? <Home summary={data.summary} onOpen={openManager} /> : <Management page={page} setPage={setPage} data={data} action={action} loading={loading} />}
  </div>
}

function Home({ summary, onOpen }) {
  return <main className="landing">
    <section className="hero section-white">
      <div className="hero-copy">
        <p className="eyebrow"><span className="eyebrow-dot" /> A better way to live on campus</p>
        <h1>Make room for<br /><em>better living.</em></h1>
        <p className="hero-text">HostelHub makes student accommodation simple, connected, and stress-free — from finding a room to managing every stay.</p>
        <div className="hero-actions"><button className="yellow-button" onClick={() => onOpen('students')}>Get started <span>↗</span></button><button className="text-button" onClick={() => document.getElementById('services').scrollIntoView({ behavior: 'smooth' })}>See how it works <span>→</span></button></div>
      </div>
      <div className="hero-art">
        <div className="sun-shape" />
        <div className="hero-orb"><div className="orb-window"><span>ROOM<br /><strong>302</strong></span></div><div className="orb-card">✦ <span>Good<br />vibes only</span></div></div>
        <div className="hero-note">Your next<br /><strong>chapter</strong><br />starts here.</div>
      </div>
    </section>

    <section className="stats-strip">
      <div><strong>{summary.students ?? '—'}</strong><span>Students<br />connected</span></div>
      <div><strong>{summary.rooms ?? '—'}</strong><span>Rooms<br />managed</span></div>
      <div><strong>{summary.allocatedBeds ?? '—'}</strong><span>Happy<br />roommates</span></div>
      <div><strong>24/7</strong><span>Support<br />when needed</span></div>
    </section>

    <section id="services" className="section-white services-section">
      <div className="section-heading"><div><p className="eyebrow">Everything in one place</p><h2>Built for your<br /><em>best stay.</em></h2></div><p>From your first room search to your final checkout, HostelHub keeps campus living organized, transparent, and wonderfully human.</p></div>
      <div className="service-grid">
        <ServiceCard tone="cream" icon="⌂" title="Find your space" text="Browse rooms, check availability, and choose a place that feels like home." />
        <ServiceCard tone="yellow" icon="✦" title="Manage with ease" text="Keep student details, allocations, and fees organized in one simple dashboard." />
        <ServiceCard tone="dark" icon="↗" title="Stay connected" text="Everything your hostel team needs to support a thriving student community." />
        <ServiceCard tone="lavender" icon="◎" title="Live confidently" text="Clear records and timely updates mean fewer surprises and more peace of mind." />
      </div>
    </section>

    <section id="about" className="split-section">
      <div className="split-visual"><div className="floating-label">HOSTELHUB<br /><strong>EST. 2024</strong></div><div className="visual-ring"><span>✦</span></div></div>
      <div className="split-copy"><p className="eyebrow">Why HostelHub?</p><h2>More than a room.<br /><em>A place to belong.</em></h2><p>Great student living is about more than four walls. It is the late-night conversations, the shared wins, and knowing the right support is always close by.</p><button className="outline-button" onClick={() => onOpen('dashboard')}>Explore the dashboard <span>↗</span></button></div>
    </section>

    <section id="stories" className="work-section">
      <div className="section-heading light"><div><p className="eyebrow">Made for real life</p><h2>See it in<br /><em>action.</em></h2></div><button className="circle-arrow" onClick={() => onOpen('dashboard')}>↗</button></div>
      <div className="story-grid"><div className="story-card story-one"><span className="story-tag">01 / 03</span><div><h3>One clear view.</h3><p>Know what is happening across your hostel at a glance.</p></div></div><div className="story-card story-two"><span className="story-tag">02 / 03</span><div><h3>Less admin.<br />More living.</h3><p>Simple tools that give your team time back.</p></div></div><div className="story-card story-three"><span className="story-tag">03 / 03</span><div><h3>Community,<br />connected.</h3><p>Support that feels personal, wherever you are.</p></div></div></div>
    </section>

    <section className="quote-section"><span className="quote-mark">“</span><blockquote>HostelHub turned a complicated process into something our students actually enjoy using.</blockquote><p>— A better standard for student living</p></section>
    <section id="contact" className="contact-section"><div><p className="eyebrow">Let’s make it better</p><h2>Ready to make<br /><em>room for more?</em></h2></div><button className="yellow-button" onClick={() => onOpen('students')}>Start managing <span>↗</span></button></section>
  </main>
}

function Logo() {
  return <span className="logo-lockup" aria-hidden="true"><svg className="logo-house" viewBox="0 0 64 64" role="img"><path className="logo-roof" d="M7 25 32 5l25 20v29a6 6 0 0 1-6 6H13a6 6 0 0 1-6-6V25Z" /><path className="logo-letter" d="M19 23h10v8h6v-8h10v27H35v-9h-6v9H19V23Z" /><path className="logo-door" d="M27 50V39c0-4 3-7 7-7s7 3 7 7v11H27Z" /><circle cx="37" cy="44" r="1.6" /></svg><span className="logo-word"><b>Hostel</b><strong>Hub</strong></span></span>
}

function ServiceCard({ tone, icon, title, text }) {
  return <article className={`service-card ${tone}`}><div className="service-icon">{icon}</div><h3>{title}</h3><p>{text}</p><span className="learn-more">Learn more ↗</span></article>
}

function Management({ page, setPage, data, action, loading }) {
  const pages = [['dashboard', 'Overview'], ['students', 'Students'], ['rooms', 'Rooms'], ['allocations', 'Allocations'], ['fees', 'Fees']]
  return <main className="manager"><div className="manager-top"><div><p className="eyebrow">HostelHub workspace</p><h1>{page === 'dashboard' ? 'Good morning, admin.' : pages.find(([key]) => key === page)?.[1]}</h1></div><button className="back-link" onClick={() => setPage('home')}>← Back to website</button></div><div className="manager-nav">{pages.map(([key, label]) => <button className={page === key ? 'active' : ''} onClick={() => setPage(key)} key={key}>{label}</button>)}</div>{loading && <div className="notice">Loading data...</div>}{page === 'dashboard' && <Dashboard summary={data.summary} data={data} />}{page === 'students' && <Students students={data.students} action={action} />}{page === 'rooms' && <Rooms rooms={data.rooms} action={action} />}{page === 'allocations' && <Allocations allocations={data.allocations} students={data.students} rooms={data.rooms} action={action} />}{page === 'fees' && <Fees fees={data.fees} students={data.students} action={action} />}</main>
}

function Dashboard({ summary, data }) { return <><div className="cards">{[['students', 'Students', 'People in your community'], ['rooms', 'Rooms', 'Spaces available'], ['allocatedBeds', 'Allocated beds', 'Currently occupied'], ['pendingFees', 'Pending fees', 'Need attention']].map(([key, label, sub]) => <div className="metric-card" key={key}><span>{label}</span><strong>{summary[key] ?? 0}</strong><small>{sub} <b>↗</b></small></div>)}</div><div className="dashboard-panels"><div className="panel welcome-panel"><p className="eyebrow">Your command center</p><h2>Everything<br /><em>under control.</em></h2><p>Manage your hostel community with confidence. Choose a section above to get started.</p></div><div className="panel quick-panel"><h3>At a glance</h3><div><span>Students with a room</span><strong>{data.allocations.length} <small>active allocations</small></strong></div><div><span>Fee records</span><strong>{data.fees.length} <small>total records</small></strong></div></div></div></> }

function Students({ students, action }) {
  const [form, setForm] = useState(emptyStudent); const [editingId, setEditingId] = useState(null)
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/students/${editingId}` : '/students', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify(form) }).then(() => { setForm(emptyStudent); setEditingId(null) })) }
  return <><div className="page-intro"><p>Community directory</p><span>{students.length} registered students</span></div><div className="layout"><Form title={editingId ? 'Edit student' : 'Add student'} onSubmit={submit}><input placeholder="Registration number" required value={form.registrationNumber} onChange={e => setForm({ ...form, registrationNumber: e.target.value })} /><input placeholder="Full name" required value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /><input placeholder="Email" type="email" required value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} /><input placeholder="Phone" required value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} /><input placeholder="Course" required value={form.course} onChange={e => setForm({ ...form, course: e.target.value })} /><input placeholder="Address" value={form.address} onChange={e => setForm({ ...form, address: e.target.value })} /></Form><Table headers={['Registration', 'Name', 'Email', 'Phone', 'Course', '']} rows={students.map(s => [s.registrationNumber, s.name, s.email, s.phone, s.course, <><button className="small-action" onClick={() => { setForm(s); setEditingId(s.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/students/${s.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Rooms({ rooms, action }) {
  const [form, setForm] = useState(emptyRoom); const [editingId, setEditingId] = useState(null)
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/rooms/${editingId}` : '/rooms', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify({ ...form, capacity: Number(form.capacity), floor: Number(form.floor) }) }).then(() => { setForm(emptyRoom); setEditingId(null) })) }
  return <><div className="page-intro"><p>Spaces & capacity</p><span>{rooms.length} rooms managed</span></div><div className="layout"><Form title={editingId ? 'Edit room' : 'Add room'} onSubmit={submit}><input placeholder="Room number" required value={form.roomNumber} onChange={e => setForm({ ...form, roomNumber: e.target.value })} /><input placeholder="Capacity" type="number" min="1" required value={form.capacity} onChange={e => setForm({ ...form, capacity: e.target.value })} /><input placeholder="Floor" type="number" min="0" value={form.floor} onChange={e => setForm({ ...form, floor: e.target.value })} /><select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}><option>AVAILABLE</option><option>MAINTENANCE</option></select></Form><Table headers={['Room', 'Capacity', 'Floor', 'Status', '']} rows={rooms.map(r => [r.roomNumber, r.capacity, r.floor, r.status, <><button className="small-action" onClick={() => { setForm(r); setEditingId(r.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/rooms/${r.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Allocations({ allocations, students, rooms, action }) {
  const [form, setForm] = useState({ studentId: '', roomId: '' }); const submit = e => { e.preventDefault(); action(() => request('/allocations', { method: 'POST', body: JSON.stringify({ studentId: Number(form.studentId), roomId: Number(form.roomId) }) }).then(() => setForm({ studentId: '', roomId: '' }))) }
  return <><div className="page-intro"><p>Room assignments</p><span>{allocations.length} active allocations</span></div><div className="layout"><Form title="Allocate room" onSubmit={submit}><select required value={form.studentId} onChange={e => setForm({ ...form, studentId: e.target.value })}><option value="">Select student</option>{students.map(s => <option value={s.id} key={s.id}>{s.name} ({s.registrationNumber})</option>)}</select><select required value={form.roomId} onChange={e => setForm({ ...form, roomId: e.target.value })}><option value="">Select room</option>{rooms.map(r => <option value={r.id} key={r.id}>{r.roomNumber} (capacity {r.capacity})</option>)}</select></Form><Table headers={['Student', 'Room', 'Allocated on', '']} rows={allocations.map(a => [a.student.name, a.room.roomNumber, a.allocatedAt, <button className="danger" onClick={() => action(() => request(`/allocations/${a.id}`, { method: 'DELETE' }))}>Remove</button>])} /></div></>
}
function Fees({ fees, students, action }) {
  const empty = { studentId: '', amount: '', dueDate: '', status: 'PENDING', paidDate: null }; const [form, setForm] = useState(empty); const [editingId, setEditingId] = useState(null)
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/fees/${editingId}` : '/fees', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify({ ...form, studentId: Number(form.studentId), amount: Number(form.amount), paidDate: form.paidDate || null }) }).then(() => { setForm(empty); setEditingId(null) })) }
  return <><div className="page-intro"><p>Payments & records</p><span>{fees.length} fee records</span></div><div className="layout"><Form title={editingId ? 'Edit fee record' : 'Add fee record'} onSubmit={submit}><select required value={form.studentId} onChange={e => setForm({ ...form, studentId: e.target.value })}><option value="">Select student</option>{students.map(s => <option value={s.id} key={s.id}>{s.name}</option>)}</select><input placeholder="Amount" type="number" min="0.01" step="0.01" required value={form.amount} onChange={e => setForm({ ...form, amount: e.target.value })} /><input type="date" required value={form.dueDate} onChange={e => setForm({ ...form, dueDate: e.target.value })} /><select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}><option>PENDING</option><option>PAID</option></select></Form><Table headers={['Student', 'Amount', 'Due date', 'Status', '']} rows={fees.map(f => [f.student.name, f.amount, f.dueDate, <span className={`status ${f.status.toLowerCase()}`}>{f.status}</span>, <><button className="small-action" onClick={() => { setForm({ studentId: f.student.id, amount: f.amount, dueDate: f.dueDate, status: f.status, paidDate: f.paidDate }); setEditingId(f.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/fees/${f.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Form({ title, children, onSubmit }) { return <form className="panel form" onSubmit={onSubmit}><h3>{title}</h3>{children}<button className="primary">Save changes <span>↗</span></button></form> }
function Table({ headers, rows }) { return <div className="panel table-wrap"><table><thead><tr>{headers.map((h, i) => <th key={`${h}-${i}`}>{h}</th>)}</tr></thead><tbody>{rows.length ? rows.map((row, i) => <tr key={i}>{row.map((cell, j) => <td key={j}>{cell}</td>)}</tr>) : <tr><td colSpan={headers.length} className="empty-cell">No records yet.</td></tr>}</tbody></table></div> }

createRoot(document.getElementById('root')).render(<App />)
