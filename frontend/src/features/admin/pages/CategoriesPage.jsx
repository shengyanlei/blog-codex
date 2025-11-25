import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit, Trash2, Folder, ChevronRight, ChevronDown } from 'lucide-react';
import { Table } from '../../../components/ui/Table';
import { Modal } from '../../../components/ui/Modal';
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api/category';
import styles from './CategoriesPage.module.css';

export const CategoriesPage = () => {
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCategory, setEditingCategory] = useState(null);
    const [formData, setFormData] = useState({ name: '', slug: '', description: '', parentId: null, displayOrder: 0 });

    // Fetch categories
    const { data: categoriesResponse, isLoading } = useQuery({
        queryKey: ['categories'],
        queryFn: () => getCategories({ tree: true }),
    });

    const categories = categoriesResponse?.data || [];

    // Mutations
    const createMutation = useMutation({
        mutationFn: createCategory,
        onSuccess: () => {
            queryClient.invalidateQueries(['categories']);
            handleCloseModal();
        },
    });

    const updateMutation = useMutation({
        mutationFn: (data) => updateCategory(editingCategory.id, data),
        onSuccess: () => {
            queryClient.invalidateQueries(['categories']);
            handleCloseModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: deleteCategory,
        onSuccess: () => {
            queryClient.invalidateQueries(['categories']);
        },
    });

    const handleOpenModal = (category = null) => {
        if (category) {
            setEditingCategory(category);
            setFormData({
                name: category.name,
                slug: category.slug,
                description: category.description || '',
                parentId: category.parentId,
                displayOrder: category.displayOrder || 0,
            });
        } else {
            setEditingCategory(null);
            setFormData({ name: '', slug: '', description: '', parentId: null, displayOrder: 0 });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingCategory(null);
        setFormData({ name: '', slug: '', description: '', parentId: null, displayOrder: 0 });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingCategory) {
            updateMutation.mutate(formData);
        } else {
            createMutation.mutate(formData);
        }
    };

    const handleDelete = (id) => {
        if (window.confirm('Are you sure you want to delete this category?')) {
            deleteMutation.mutate(id);
        }
    };

    const columns = [
        {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Folder size={16} className="text-gray-400" />
                    {text}
                </div>
            ),
        },
        { title: 'Slug', dataIndex: 'slug', key: 'slug' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
        { title: 'Order', dataIndex: 'displayOrder', key: 'displayOrder', width: 80 },
    ];

    // Flatten tree for table (simplified for now, ideally use a tree table component)
    // For this MVP, we'll just show the root categories or a flat list if the backend supported it directly
    // Since backend returns tree when tree=true, we might need to flatten it or use a recursive component
    // For simplicity in this step, let's assume we fetch flat list for table view or just show roots

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Categories</h1>
                <button className={styles.createBtn} onClick={() => handleOpenModal()}>
                    <Plus size={16} />
                    Create Category
                </button>
            </div>

            {isLoading ? (
                <div>Loading...</div>
            ) : (
                <Table
                    columns={columns}
                    data={categories}
                    actions={(record) => (
                        <>
                            <button className={styles.actionBtn} onClick={() => handleOpenModal(record)}>
                                <Edit size={16} />
                            </button>
                            <button className={styles.actionBtn} onClick={() => handleDelete(record.id)}>
                                <Trash2 size={16} />
                            </button>
                        </>
                    )}
                />
            )}

            <Modal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                title={editingCategory ? 'Edit Category' : 'Create Category'}
            >
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label>Name</label>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Slug</label>
                        <input
                            type="text"
                            value={formData.slug}
                            onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                            required
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Description</label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Display Order</label>
                        <input
                            type="number"
                            value={formData.displayOrder}
                            onChange={(e) => setFormData({ ...formData, displayOrder: parseInt(e.target.value) })}
                        />
                    </div>
                    <div className={styles.formActions}>
                        <button type="button" onClick={handleCloseModal} className={styles.cancelBtn}>
                            Cancel
                        </button>
                        <button type="submit" className={styles.submitBtn}>
                            {editingCategory ? 'Update' : 'Create'}
                        </button>
                    </div>
                </form>
            </Modal>
        </div>
    );
};
