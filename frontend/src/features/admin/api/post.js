import api from '../../../lib/axios';

export const getPosts = (params) => {
    return api.get('/posts', { params });
};

export const getPost = (id) => {
    return api.get(`/posts/${id}`);
};

export const createPost = (data) => {
    return api.post('/posts', data);
};

export const updatePost = (id, data) => {
    return api.put(`/posts/${id}`, data);
};

export const deletePost = (id) => {
    return api.delete(`/posts/${id}`);
};

export const getPostVersions = (id) => {
    return api.get(`/posts/${id}/versions`);
};

export const restorePostVersion = (id, version) => {
    return api.post(`/posts/${id}/versions/${version}/restore`);
};
