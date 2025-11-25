import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { Save, ArrowLeft, History } from 'lucide-react';
import { getPost, createPost, updatePost } from '../api/post';
import { getCategories } from '../api/category';
import styles from './PostEditPage.module.css';

export const PostEditPage = () => {
    const { id } = useParams();
    const isEditMode = !!id;
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [formData, setFormData] = useState({
        title: '',
        slug: '',
        excerpt: '',
        contentMd: '',
        status: 'DRAFT',
        categoryIds: [],
        metadata: '{}' // JSON string for metadata
    });

    // Fetch post if in edit mode
    useQuery({
        queryKey: ['post', id],
        queryFn: () => getPost(id),
        enabled: isEditMode,
        onSuccess: (data) => {
            const post = data.data;
            setFormData({
                title: post.title,
                slug: post.slug,
                excerpt: post.excerpt || '',
                contentMd: post.contentMd || '',
                status: post.status,
                categoryIds: post.categories ? post.categories.map(c => c.id) : [],
                metadata: post.metadata ? JSON.stringify(post.metadata, null, 2) : '{}'
            });
        }
    });

    // Fetch categories for selection
    const { data: categoriesResponse } = useQuery({
        queryKey: ['categories'],
        queryFn: () => getCategories({ tree: false }), // Assuming we can get a flat list
    });

    // Flatten categories if needed, or just use what we have. 
    // If the API returns a tree, we might need to flatten it here to show in a simple multi-select
    // For now, let's assume the API returns a list or we just map the top level.
    // A proper tree select component would be better but keeping it simple for now.
    const categories = categoriesResponse?.data || [];

    const mutation = useMutation({
        mutationFn: (data) => {
            // Parse metadata back to object
            const payload = {
                ...data,
                metadata: JSON.parse(data.metadata || '{}')
            };
            return isEditMode ? updatePost(id, payload) : createPost(payload);
        },
        onSuccess: () => {
            queryClient.invalidateQueries(['posts']);
            navigate('/admin/posts');
        },
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        mutation.mutate(formData);
    };

    const handleCategoryChange = (e) => {
        const options = e.target.options;
        const selectedValues = [];
        for (let i = 0; i < options.length; i++) {
            if (options[i].selected) {
                selectedValues.push(parseInt(options[i].value));
            }
        }
        setFormData({ ...formData, categoryIds: selectedValues });
    };

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <div className={styles.headerLeft}>
                    <button className={styles.backBtn} onClick={() => navigate('/admin/posts')}>
                        <ArrowLeft size={20} />
                    </button>
                    <h1 className={styles.title}>{isEditMode ? 'Edit Post' : 'Create Post'}</h1>
                </div>
                <div className={styles.headerRight}>
                    {isEditMode && (
                        <button
                            className={styles.historyBtn}
                            onClick={() => navigate(`/admin/posts/${id}/versions`)}
                        >
                            <History size={16} />
                            Versions
                        </button>
                    )}
                    <button className={styles.saveBtn} onClick={handleSubmit} disabled={mutation.isPending}>
                        <Save size={16} />
                        {mutation.isPending ? 'Saving...' : 'Save'}
                    </button>
                </div>
            </div>

            <form className={styles.form}>
                <div className={styles.mainColumn}>
                    <div className={styles.formGroup}>
                        <label>Title</label>
                        <input
                            type="text"
                            value={formData.title}
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                            required
                            className={styles.input}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label>Content (Markdown)</label>
                        <textarea
                            value={formData.contentMd}
                            onChange={(e) => setFormData({ ...formData, contentMd: e.target.value })}
                            className={styles.textarea}
                            rows={20}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label>Excerpt</label>
                        <textarea
                            value={formData.excerpt}
                            onChange={(e) => setFormData({ ...formData, excerpt: e.target.value })}
                            className={styles.excerptInput}
                            rows={3}
                        />
                    </div>
                </div>

                <div className={styles.sideColumn}>
                    <div className={styles.card}>
                        <h3>Publishing</h3>
                        <div className={styles.formGroup}>
                            <label>Status</label>
                            <select
                                value={formData.status}
                                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                                className={styles.select}
                            >
                                <option value="DRAFT">Draft</option>
                                <option value="PUBLISHED">Published</option>
                                <option value="ARCHIVED">Archived</option>
                            </select>
                        </div>
                        <div className={styles.formGroup}>
                            <label>Slug</label>
                            <input
                                type="text"
                                value={formData.slug}
                                onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                                className={styles.input}
                            />
                        </div>
                    </div>

                    <div className={styles.card}>
                        <h3>Categories</h3>
                        <div className={styles.formGroup}>
                            <select
                                multiple
                                value={formData.categoryIds}
                                onChange={handleCategoryChange}
                                className={styles.multiSelect}
                            >
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </option>
                                ))}
                            </select>
                            <small className="text-gray-500">Hold Ctrl/Cmd to select multiple</small>
                        </div>
                    </div>

                    <div className={styles.card}>
                        <h3>Metadata (JSON)</h3>
                        <div className={styles.formGroup}>
                            <textarea
                                value={formData.metadata}
                                onChange={(e) => setFormData({ ...formData, metadata: e.target.value })}
                                className={styles.codeTextarea}
                                rows={5}
                            />
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
};
