import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
async function request(path, options = {}) {
  const requestOptions = { ...options }
  if (requestOptions.body) requestOptions.headers = { 'Content-Type': 'application/json', ...(requestOptions.headers || {}) }
  const response = await fetch(`${API}${path}`, requestOptions)
  if (!response.ok) { const body = await response.json().catch(() => ({})); throw new Error(body.error || `Request failed (${response.status})`) }
  return response.status === 204 ? null : response.json()
}
const emptyStudent = { registrationNumber: '', name: '', email: '', phone: '', course: '', address: '' }
const emptyRoom = { roomNumber: '', capacity: 1, floor: 0, status: 'AVAILABLE' }
function App() {
  const [page, setPage] = useState('dashboard')
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
      throw error
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load().catch(() => {}) }, [])
  const action = async (work) => { try { await work(); await load(); setMessage({ type: 'success', text: 'Saved successfully.' }) } catch (error) { setMessage({ type: 'error', text: error.message }) } }
  return <div className="shell">
    <header><h1>HostelHub</h1><span>Hostel Management System</span></header>
    <nav>{[['dashboard', 'Dashboard'], ['students', 'Students'], ['rooms', 'Rooms'], ['allocations', 'Allocations'], ['fees', 'Fees']].map(([key, label]) => <button className={page === key ? 'active' : ''} onClick={() => setPage(key)} key={key}>{label}</button>)}</nav>
    <main>{loading && <div className="notice">Loading data...</div>}{message.text && <div className={`notice ${message.type}`}>{message.text}<button onClick={() => setMessage({ type: '', text: '' })}>×</button></div>}
      {page === 'dashboard' && <Dashboard summary={data.summary} />}
      {page === 'students' && <Students students={data.students} action={action} />}
      {page === 'rooms' && <Rooms rooms={data.rooms} action={action} />}
      {page === 'allocations' && <Allocations allocations={data.allocations} students={data.students} rooms={data.rooms} action={action} />}
      {page === 'fees' && <Fees fees={data.fees} students={data.students} action={action} />}
    </main>
  </div>
}
function Dashboard({ summary }) { return <><h2>Dashboard</h2><div className="cards">{[['students', 'Students'], ['rooms', 'Rooms'], ['allocatedBeds', 'Allocated beds'], ['pendingFees', 'Pending fees']].map(([key, label]) => <div className="card" key={key}><small>{label}</small><strong>{summary[key] ?? 0}</strong></div>)}</div><div className="panel"><h3>Welcome</h3><p>Use the navigation to manage students, rooms, room allocations, and fee records. All changes are persisted through the Spring Boot API.</p></div></> }
function Students({ students, action }) {
  const [form, setForm] = useState(emptyStudent)
  const [editingId, setEditingId] = useState(null)
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/students/${editingId}` : '/students', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify(form) }).then(() => { setForm(emptyStudent); setEditingId(null) })) }
  return <><h2>Students</h2><div className="layout"><Form title={editingId ? 'Edit student' : 'Add student'} onSubmit={submit}><input placeholder="Registration number" required value={form.registrationNumber} onChange={e => setForm({ ...form, registrationNumber: e.target.value })} /><input placeholder="Full name" required value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /><input placeholder="Email" type="email" required value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} /><input placeholder="Phone" required value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} /><input placeholder="Course" required value={form.course} onChange={e => setForm({ ...form, course: e.target.value })} /><input placeholder="Address" value={form.address} onChange={e => setForm({ ...form, address: e.target.value })} /></Form><Table headers={['Registration', 'Name', 'Email', 'Phone', 'Course', '']} rows={students.map(s => [s.registrationNumber, s.name, s.email, s.phone, s.course, <><button onClick={() => { setForm(s); setEditingId(s.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/students/${s.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Rooms({ rooms, action }) {
  const [form, setForm] = useState(emptyRoom)
  const [editingId, setEditingId] = useState(null)
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/rooms/${editingId}` : '/rooms', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify({ ...form, capacity: Number(form.capacity), floor: Number(form.floor) }) }).then(() => { setForm(emptyRoom); setEditingId(null) })) }
  return <><h2>Rooms</h2><div className="layout"><Form title={editingId ? 'Edit room' : 'Add room'} onSubmit={submit}><input placeholder="Room number" required value={form.roomNumber} onChange={e => setForm({ ...form, roomNumber: e.target.value })} /><input placeholder="Capacity" type="number" min="1" required value={form.capacity} onChange={e => setForm({ ...form, capacity: e.target.value })} /><input placeholder="Floor" type="number" min="0" value={form.floor} onChange={e => setForm({ ...form, floor: e.target.value })} /><select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}><option>AVAILABLE</option><option>MAINTENANCE</option></select></Form><Table headers={['Room', 'Capacity', 'Floor', 'Status', '']} rows={rooms.map(r => [r.roomNumber, r.capacity, r.floor, r.status, <><button onClick={() => { setForm(r); setEditingId(r.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/rooms/${r.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Allocations({ allocations, students, rooms, action }) {
  const [form, setForm] = useState({ studentId: '', roomId: '' })
  const submit = e => { e.preventDefault(); action(() => request('/allocations', { method: 'POST', body: JSON.stringify({ studentId: Number(form.studentId), roomId: Number(form.roomId) }) }).then(() => setForm({ studentId: '', roomId: '' }))) }
  return <><h2>Room allocations</h2><div className="layout"><Form title="Allocate room" onSubmit={submit}><select required value={form.studentId} onChange={e => setForm({ ...form, studentId: e.target.value })}><option value="">Select student</option>{students.map(s => <option value={s.id} key={s.id}>{s.name} ({s.registrationNumber})</option>)}</select><select required value={form.roomId} onChange={e => setForm({ ...form, roomId: e.target.value })}><option value="">Select room</option>{rooms.map(r => <option value={r.id} key={r.id}>{r.roomNumber} (capacity {r.capacity})</option>)}</select></Form><Table headers={['Student', 'Room', 'Allocated on', '']} rows={allocations.map(a => [a.student.name, a.room.roomNumber, a.allocatedAt, <button className="danger" onClick={() => action(() => request(`/allocations/${a.id}`, { method: 'DELETE' }))}>Remove</button>])} /></div></>
}
function Fees({ fees, students, action }) {
  const [form, setForm] = useState({ studentId: '', amount: '', dueDate: '', status: 'PENDING', paidDate: null })
  const [editingId, setEditingId] = useState(null)
  const empty = { studentId: '', amount: '', dueDate: '', status: 'PENDING', paidDate: null }
  const submit = e => { e.preventDefault(); action(() => request(editingId ? `/fees/${editingId}` : '/fees', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify({ ...form, studentId: Number(form.studentId), amount: Number(form.amount), paidDate: form.paidDate || null }) }).then(() => { setForm(empty); setEditingId(null) })) }
  return <><h2>Fee records</h2><div className="layout"><Form title={editingId ? 'Edit fee record' : 'Add fee record'} onSubmit={submit}><select required value={form.studentId} onChange={e => setForm({ ...form, studentId: e.target.value })}><option value="">Select student</option>{students.map(s => <option value={s.id} key={s.id}>{s.name}</option>)}</select><input placeholder="Amount" type="number" min="0.01" step="0.01" required value={form.amount} onChange={e => setForm({ ...form, amount: e.target.value })} /><input type="date" required value={form.dueDate} onChange={e => setForm({ ...form, dueDate: e.target.value })} /><select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}><option>PENDING</option><option>PAID</option></select></Form><Table headers={['Student', 'Amount', 'Due date', 'Status', '']} rows={fees.map(f => [f.student.name, f.amount, f.dueDate, f.status, <><button onClick={() => { setForm({ studentId: f.student.id, amount: f.amount, dueDate: f.dueDate, status: f.status, paidDate: f.paidDate }); setEditingId(f.id) }}>Edit</button> <button className="danger" onClick={() => action(() => request(`/fees/${f.id}`, { method: 'DELETE' }))}>Delete</button></>])} /></div></>
}
function Form({ title, children, onSubmit }) { return <form className="panel form" onSubmit={onSubmit}><h3>{title}</h3>{children}<button className="primary">Save</button></form> }
function Table({ headers, rows }) { return <div className="panel table-wrap"><table><thead><tr>{headers.map(h => <th key={h}>{h}</th>)}</tr></thead><tbody>{rows.length ? rows.map((row, i) => <tr key={i}>{row.map((cell, j) => <td key={j}>{cell}</td>)}</tr>) : <tr><td colSpan={headers.length}>No records yet.</td></tr>}</tbody></table></div> }
createRoot(document.getElementById('root')).render(<App />)
