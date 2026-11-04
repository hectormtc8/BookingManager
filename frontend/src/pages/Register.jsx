import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Register() {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await register(fullName, email, password);
            navigate('/rooms');
        } catch (err) {
            setError(err.response?.data?.message || 'No se pudo completar el registro');
        }
    };

    return (
        <div className="auth">
            <div className="auth__card">
                <h2>Crear cuenta</h2>
                <p className="auth__subtitle">Regístrate para empezar a reservar</p>
                <form className="form" onSubmit={handleSubmit}>
                    <input placeholder="Nombre completo" value={fullName}
                           onChange={(e) => setFullName(e.target.value)} required />
                    <input type="email" placeholder="Correo" value={email}
                           onChange={(e) => setEmail(e.target.value)} required />
                    <input type="password" placeholder="Contraseña (mín. 6 caracteres)" value={password}
                           onChange={(e) => setPassword(e.target.value)} required />
                    <button className="btn btn--primary" type="submit">Registrarme</button>
                    {error && <p className="alert alert--error">{error}</p>}
                </form>
                <p className="auth__footer">¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></p>
            </div>
        </div>
    );
}