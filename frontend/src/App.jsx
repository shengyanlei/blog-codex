import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import PostList from './pages/PostList';
import MediaLibrary from './pages/MediaLibrary';
import Settings from './pages/Settings';

const ProtectedRoute = ({ children }) => {
  // Mock auth check
  const isAuthenticated = true;
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return <Layout>{children}</Layout>;
};

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route path="/" element={
        <ProtectedRoute>
          <Dashboard />
        </ProtectedRoute>
      } />

      <Route path="/posts" element={
        <ProtectedRoute>
          <PostList />
        </ProtectedRoute>
      } />

      <Route path="/media" element={
        <ProtectedRoute>
          <MediaLibrary />
        </ProtectedRoute>
      } />

      <Route path="/settings" element={
        <ProtectedRoute>
          <Settings />
        </ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
