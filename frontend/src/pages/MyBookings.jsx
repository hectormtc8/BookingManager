import { useEffect, useState } from 'react';
import api from '../api/axios';
import Navbar from '../components/Navbar';

const STATUS_LABEL = { CONFIRMED: 'Confirmada', CANCELLED: 'Cancelada' };

const formatDate = (isoDate) =>
    new Date(`${isoDate}T00:00:00`).toLocaleDateString('es-MX', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
    });

export default function MyBookings() {
    const [bookings, setBookings] = useState([]);
    const [error, setError] = useState('');

    const load = () =>
        api.get('/bookings/me')
            .then(({ data }) => setBookings(data))
            .catch((err) => setError(err.response?.data?.message || 'No se pudieron cargar tus reservas'));

    useEffect(() => {
        load();
    }, []);

    const cancel = async (id) => {
        setError('');
        try {
            await api.delete(`/bookings/${id}`);
            load();
        } catch (err) {
            setError(err.response?.data?.message || 'No se pudo cancelar la reserva');
        }
    };

    return (
        <>
            <Navbar />
            <main className="container">
                <h1 className="page-title">Mis reservas</h1>
                {error && <p className="alert alert--error">{error}</p>}
                {bookings.length === 0 && <p className="empty">Aún no tienes reservas.</p>}
                <ul className="list">
                    {bookings.map((b) => (
                        <li key={b.id} className={`card ${b.status === 'CANCELLED' ? 'card--muted' : ''}`}>
                            <div className="card__header">
                                <div>
                                    <h3 className="card__title">{b.resource.name}</h3>
                                    <p className="card__meta">
                                        {formatDate(b.date)} · {b.startTime.slice(0, 5)} – {b.endTime.slice(0, 5)}
                                    </p>
                                </div>
                                <span className={`badge badge--${b.status.toLowerCase()}`}>
                {STATUS_LABEL[b.status]}
              </span>
                            </div>
                            {b.status === 'CONFIRMED' && (
                                <div className="card__actions">
                                    <button className="btn btn--danger btn--sm" onClick={() => cancel(b.id)}>
                                        Cancelar reserva
                                    </button>
                                </div>
                            )}
                        </li>
                    ))}
                </ul>
            </main>
        </>
    );
}