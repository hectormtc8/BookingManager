import { useEffect, useState } from 'react';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function AdminRooms() {
    const [rooms, setRooms] = useState([]);
    const [form, setForm] = useState({ name: '', description: '', capacity: '' });
    const [error, setError] = useState('');

    const load = () => api.get('/resources').then(({ data }) => setRooms(data));

    useEffect(() => {
        load();
    }, []);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await api.post('/resources', { ...form, capacity: Number(form.capacity) });
            setForm({ name: '', description: '', capacity: '' });
            load();
        } catch (err) {
            setError(err.response?.data?.message || 'No se pudo crear la sala');
        }
    };

    const deactivate = async (id) => {
        await api.delete(`/resources/${id}`);
        load();
    };

    return (
        <>
            <Navbar />
            <main className="container">
                <h1 className="page-title">Administrar salas</h1>

                <section className="card">
                    <h3 className="card__title">Nueva sala</h3>
                    <form className="form" onSubmit={handleSubmit} style={{ marginTop: '1rem' }}>
                        <input name="name" placeholder="Nombre" value={form.name} onChange={handleChange} required />
                        <input name="description" placeholder="Descripción" value={form.description}
                               onChange={handleChange} />
                        <input name="capacity" type="number" min="1" placeholder="Capacidad" value={form.capacity}
                               onChange={handleChange} required />
                        <button className="btn btn--primary" type="submit">Crear sala</button>
                        {error && <p className="alert alert--error">{error}</p>}
                    </form>
                </section>

                <section className="card">
                    <h3 className="card__title">Salas activas</h3>
                    {rooms.length === 0 && <p className="empty">No hay salas activas.</p>}
                    <ul className="list">
                        {rooms.map((r) => (
                            <li key={r.id} className="row">
                                <div>
                                    <strong>{r.name}</strong>
                                    <p className="card__meta">Capacidad {r.capacity}</p>
                                </div>
                                <button className="btn btn--danger btn--sm" onClick={() => deactivate(r.id)}>
                                    Desactivar
                                </button>
                            </li>
                        ))}
                    </ul>
                </section>
            </main>
        </>
    );
}