import { useEffect, useState } from 'react';
import api from '../api/axios';
import Navbar from '../components/Navbar';

function RoomCard({ room }) {
    const [date, setDate] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [message, setMessage] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        try {
            await api.post('/bookings', { resourceId: room.id, date, startTime, endTime });
            setMessage({ type: 'ok', text: 'Reserva confirmada' });
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.message || 'No se pudo reservar' });
        }
    };

    return (
        <li className="card">
            <div className="card__header">
                <div>
                    <h3 className="card__title">{room.name}</h3>
                    <p className="card__meta">{room.description}</p>
                </div>
                <span className="badge badge--info">Capacidad {room.capacity}</span>
            </div>
            <form className="form-row" onSubmit={handleSubmit}>
                <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
                <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />
                <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} required />
                <button className="btn btn--primary" type="submit">Reservar</button>
            </form>
            {message && (
                <p className={`alert alert--${message.type === 'ok' ? 'success' : 'error'}`}>{message.text}</p>
            )}
        </li>
    );
}

export default function Rooms() {
    const [rooms, setRooms] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get('/resources')
            .then(({ data }) => setRooms(data))
            .catch((err) => setError(err.response?.data?.message || 'No se pudieron cargar las salas'));
    }, []);

    return (
        <>
            <Navbar />
            <main className="container">
                <h1 className="page-title">Salas disponibles</h1>
                {error && <p className="alert alert--error">{error}</p>}
                {rooms.length === 0 && !error && <p className="empty">Todavía no hay salas disponibles.</p>}
                <ul className="list">
                    {rooms.map((room) => <RoomCard key={room.id} room={room} />)}
                </ul>
            </main>
        </>
    );
}