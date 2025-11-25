import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit, Trash2, Shield } from 'lucide-react';
import { Table } from '../../../components/ui/Table';
import { Modal } from '../../../components/ui/Modal';
import { getRoles, createRole, updateRole, deleteRole } from '../api/role';
import styles from './RolesPage.module.css';

export const RolesPage = () => {
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingRole, setEditingRole] = useState(null);
    const [formData, setFormData] = useState({ code: '', name: '', description: '' });

    // Fetch roles
    const { data: rolesResponse, isLoading } = useQuery({
        queryKey: ['roles'],
        queryFn: getRoles,
    });

    const roles = rolesResponse?.data || [];

    // Mutations
    const createMutation = useMutation({
        mutationFn: createRole,
        onSuccess: () => {
            queryClient.invalidateQueries(['roles']);
            handleCloseModal();
        },
    });

    const updateMutation = useMutation({
        mutationFn: (data) => updateRole(editingRole.id, data),
        onSuccess: () => {
            queryClient.invalidateQueries(['roles']);
            handleCloseModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: deleteRole,
        onSuccess: () => {
            queryClient.invalidateQueries(['roles']);
        },
    });

    const handleOpenModal = (role = null) => {
        if (role) {
            setEditingRole(role);
            setFormData({
                code: role.code,
                name: role.name,
                description: role.description || '',
            });
        } else {
            setEditingRole(null);
            setFormData({ code: '', name: '', description: '' });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingRole(null);
        setFormData({ code: '', name: '', description: '' });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingRole) {
            updateMutation.mutate(formData);
        } else {
            createMutation.mutate(formData);
        }
    };

    const handleDelete = (id) => {
        if (window.confirm('Are you sure you want to delete this role?')) {
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
                    <Shield size={16} className="text-gray-400" />
                    {text}
                </div>
            ),
        },
        { title: 'Code', dataIndex: 'code', key: 'code' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
    ];

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Roles</h1>
                <button className={styles.createBtn} onClick={() => handleOpenModal()}>
                    <Plus size={16} />
                    Create Role
                </button>
            </div>

            {isLoading ? (
                <div>Loading...</div>
            ) : (
                <Table
                    columns={columns}
                    data={roles}
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
                title={editingRole ? 'Edit Role' : 'Create Role'}
            >
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label>Code</label>
                        <input
                            type="text"
                            value={formData.code}
                            onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                            required
                            disabled={!!editingRole} // Code is usually immutable
                        />
                    </div>
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
                        <label>Description</label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        />
                    </div>
                    <div className={styles.formActions}>
                        <button type="button" onClick={handleCloseModal} className={styles.cancelBtn}>
                            Cancel
                        </button>
                        <button type="submit" className={styles.submitBtn}>
                            {editingRole ? 'Update' : 'Create'}
                        </button>
                    </div>
                </form>
            </Modal>
        </div>
    );
};
