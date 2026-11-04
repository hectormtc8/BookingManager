import { createContext, useContext, useState } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [session, setSession] = useState(() => ({
        token: localStorage.getItem('token'),
        role: localStorage.getItem('role'),
        fullName: localStorage.getItem('fullName'),
    }));

    const saveSession = ({ token, role, fullName }) => {
        localStorage.setItem('token', token);
        localStorage.setItem('role', role);
        localStorage.setItem('fullName', fullName);
        setSession({ token, role, fullName });
    };

    const login = async (email, password) => {
        const { data } = await api.post('/auth/login', { email, password });
        saveSession(data);
    };

    const register = async (fullName, email, password) => {
        const { data } = await api.post('/auth/register', { fullName, email, password });
        saveSession(data);
    };

    const logout = () => {
        localStorage.clear();
        setSession({ token: null, role: null, fullName: null });
    };

    const value = {
        ...session,
        isAuthenticated: !!session.token,
        isAdmin: session.role === 'ADMIN',
        login,
        register,
        logout,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);