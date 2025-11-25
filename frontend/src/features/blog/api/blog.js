import api from '../../../lib/axios';

export const getPosts = (params) => {
    return api.get('/posts', { params });
};

export const getPost = (id) => {
    return api.get(`/posts/${id}`);
};

export const getPostBySlug = (slug) => {
    return api.get(`/posts/slug/${slug}`);
};

export const getCategories = (params) => {
    return api.get('/categories', { params });
};

export const getCategoryBySlug = (slug) => {
    return api.get(`/categories/slug/${slug}`);
};

// Assuming we might have comments later
export const getComments = (postId) => {
    return api.get(`/comments?postId=${postId}`);
};
