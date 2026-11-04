import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const { fullName, isAdmin, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <span className="navbar__brand">BookingManager</span>
            <NavLink to="/rooms">Salas</NavLink>
            <NavLink to="/my-bookings">Mis reservas</NavLink>
            {isAdmin && <NavLink to="/admin/rooms">Administrar salas</NavLink>}
            <div className="navbar__user">
                <span>{fullName}</span>
                <button className="btn btn--ghost btn--sm" onClick={handleLogout}>Cerrar sesión</button>
            </div>
        </nav>
    );
}