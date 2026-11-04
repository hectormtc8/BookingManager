import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await login(email, password);
            navigate('/rooms');
        } catch (err) {
            setError(err.response?.data?.message || 'No se pudo iniciar sesión');
        }
    };

    return (
        <div className="auth">
            <div className="auth__card">
                <h2>Bienvenido</h2>
                <p className="auth__subtitle">Inicia sesión para reservar una sala</p>
                <form className="form" onSubmit={handleSubmit}>
                    <input type="email" placeholder="Correo" value={email}
                           onChange={(e) => setEmail(e.target.value)} required />
                    <input type="password" placeholder="Contraseña" value={password}
                           onChange={(e) => setPassword(e.target.value)} required />
                    <button className="btn btn--primary" type="submit">Entrar</button>
                    {error && <p className="alert alert--error">{error}</p>}
                </form>
                <p className="auth__footer">¿No tienes cuenta? <Link to="/register">Regístrate</Link></p>
            </div>
        </div>
    );
}