import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { User, Shield, Edit } from 'lucide-react';
import { Table } from '../../../components/ui/Table';
import { Modal } from '../../../components/ui/Modal';
import { getUsers, getUserRoles, assignRolesToUser } from '../api/user';
import { getRoles } from '../api/role';
import styles from './UsersPage.module.css';

export const UsersPage = () => {
    const queryClient = useQueryClient();
    const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [selectedRoleIds, setSelectedRoleIds] = useState([]);

    // Fetch users
    const { data: usersResponse, isLoading: isUsersLoading } = useQuery({
        queryKey: ['users'],
        queryFn: () => getUsers(),
    });

    const users = usersResponse?.data || [];

    // Fetch all roles for the selection modal
    const { data: rolesResponse } = useQuery({
        queryKey: ['roles'],
        queryFn: getRoles,
        enabled: isRoleModalOpen,
    });

    const allRoles = rolesResponse?.data || [];

    // Mutation for assigning roles
    const assignRolesMutation = useMutation({
        mutationFn: ({ userId, roleIds }) => assignRolesToUser(userId, roleIds),
        onSuccess: () => {
            queryClient.invalidateQueries(['users']); // Refresh users list (if it shows roles)
            handleCloseRoleModal();
            alert('Roles assigned successfully');
        },
    });

    const handleOpenRoleModal = async (user) => {
        setSelectedUser(user);
        // Ideally we fetch the user's current roles here to pre-select
        // For now, let's assume we can get them or start empty/from user object if available
        // If the user object from list already has roles, use them.
        // Otherwise, we might need to fetch:
        try {
            const userRolesResponse = await getUserRoles(user.id);
            const userRoles = userRolesResponse.data || [];
            setSelectedRoleIds(userRoles.map(r => r.id));
        } catch (e) {
            console.error("Failed to fetch user roles", e);
            setSelectedRoleIds([]);
        }
        setIsRoleModalOpen(true);
    };

    const handleCloseRoleModal = () => {
        setIsRoleModalOpen(false);
        setSelectedUser(null);
        setSelectedRoleIds([]);
    };

    const handleRoleSelectionChange = (roleId) => {
        setSelectedRoleIds(prev => {
            if (prev.includes(roleId)) {
                return prev.filter(id => id !== roleId);
            } else {
                return [...prev, roleId];
            }
        });
    };

    const handleSaveRoles = (e) => {
        e.preventDefault();
        if (selectedUser) {
            assignRolesMutation.mutate({ userId: selectedUser.id, roleIds: selectedRoleIds });
        }
    };

    const columns = [
        {
            title: 'Username',
            dataIndex: 'username',
            key: 'username',
            render: (text, record) => (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <User size={16} className="text-gray-400" />
                    {text}
                    {record.nickname && <span style={{ color: '#6b7280', fontSize: '0.8em' }}>({record.nickname})</span>}
                </div>
            ),
        },
        { title: 'Email', dataIndex: 'email', key: 'email' },
        {
            title: 'Roles',
            dataIndex: 'roles',
            key: 'roles',
            render: (roles) => (
                <div style={{ display: 'flex', gap: '0.25rem', flexWrap: 'wrap' }}>
                    {roles && roles.map(role => (
                        <span key={role.id} className={styles.roleBadge}>{role.name}</span>
                    ))}
                </div>
            )
        },
        { title: 'Status', dataIndex: 'status', key: 'status' },
    ];

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Users</h1>
            </div>

            {isUsersLoading ? (
                <div>Loading...</div>
            ) : (
                <Table
                    columns={columns}
                    data={users}
                    actions={(record) => (
                        <button
                            className={styles.actionBtn}
                            onClick={() => handleOpenRoleModal(record)}
                            title="Assign Roles"
                        >
                            <Shield size={16} />
                        </button>
                    )}
                />
            )}

            <Modal
                isOpen={isRoleModalOpen}
                onClose={handleCloseRoleModal}
                title={`Assign Roles to ${selectedUser?.username}`}
            >
                <form onSubmit={handleSaveRoles} className={styles.form}>
                    <div className={styles.rolesList}>
                        {allRoles.map(role => (
                            <label key={role.id} className={styles.roleOption}>
                                <input
                                    type="checkbox"
                                    checked={selectedRoleIds.includes(role.id)}
                                    onChange={() => handleRoleSelectionChange(role.id)}
                                />
                                <span>{role.name}</span>
                                <span className={styles.roleCode}>({role.code})</span>
                            </label>
                        ))}
                    </div>
                    <div className={styles.formActions}>
                        <button type="button" onClick={handleCloseRoleModal} className={styles.cancelBtn}>
                            Cancel
                        </button>
                        <button type="submit" className={styles.submitBtn}>
                            Save Roles
                        </button>
                    </div>
                </form>
            </Modal>
        </div>
    );
};
