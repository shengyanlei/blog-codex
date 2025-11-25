import api from '../../../lib/axios';

export const getUsers = (params) => {
    return api.get('/users', { params });
};

export const getUser = (id) => {
    return api.get(`/users/${id}`);
};

// Usually user creation is done via registration, but admin might be able to create users
// export const createUser = (data) => api.post('/users', data);

export const updateUser = (id, data) => {
    return api.put(`/users/${id}`, data);
};

export const getUserRoles = (userId) => {
    return api.get(`/roles/users/${userId}/roles`);
};

export const assignRolesToUser = (userId, roleIds) => {
    return api.post(`/roles/users/${userId}/roles`, { roleIds });
};
