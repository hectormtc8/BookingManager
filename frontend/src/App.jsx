import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import AdminRoute from './components/AdminRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Rooms from './pages/Rooms';
import MyBookings from './pages/MyBookings';
import AdminRooms from './pages/AdminRooms';

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/rooms" element={<ProtectedRoute><Rooms /></ProtectedRoute>} />
            <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
            <Route path="/admin/rooms" element={<AdminRoute><AdminRooms /></AdminRoute>} />
            <Route path="*" element={<Navigate to="/rooms" replace />} />
        </Routes>
    );
}