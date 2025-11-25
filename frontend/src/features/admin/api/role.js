import api from '../../../lib/axios';

export const getRoles = () => {
    return api.get('/roles');
};

export const getRole = (id) => {
    return api.get(`/roles/${id}`);
};

export const createRole = (data) => {
    return api.post('/roles', data);
};

export const updateRole = (id, data) => {
    return api.put(`/roles/${id}`, data);
};

export const deleteRole = (id) => {
    return api.delete(`/roles/${id}`);
};

export const assignPermissions = (roleId, permissionIds) => {
    return api.post(`/roles/${roleId}/permissions`, { permissionIds });
};

export const getRolePermissions = (roleId) => {
    return api.get(`/roles/${roleId}/permissions`); // Assuming backend has this endpoint or returns it with role
};
