import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, RotateCcw, FileClock } from 'lucide-react';
import { Table } from '../../../components/ui/Table';
import { getPostVersions, restorePostVersion } from '../api/post';
import styles from './PostVersionsPage.module.css';

export const PostVersionsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const { data: versionsResponse, isLoading } = useQuery({
        queryKey: ['postVersions', id],
        queryFn: () => getPostVersions(id),
    });

    const versions = versionsResponse?.data || [];

    const restoreMutation = useMutation({
        mutationFn: (version) => restorePostVersion(id, version),
        onSuccess: () => {
            queryClient.invalidateQueries(['post', id]);
            queryClient.invalidateQueries(['postVersions', id]);
            alert('Version restored successfully');
            navigate(`/admin/posts/edit/${id}`);
        },
    });

    const handleRestore = (version) => {
        if (window.confirm(`Are you sure you want to restore version ${version}? Current content will be overwritten.`)) {
            restoreMutation.mutate(version);
        }
    };

    const columns = [
        { title: 'Version', dataIndex: 'version', key: 'version', width: 100 },
        {
            title: 'Created At',
            dataIndex: 'createdAt',
            key: 'createdAt',
            render: (date) => new Date(date).toLocaleString()
        },
        { title: 'Created By', dataIndex: 'createdBy', key: 'createdBy' }, // Placeholder
        { title: 'Reason', dataIndex: 'changeReason', key: 'changeReason' },
    ];

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <div className={styles.headerLeft}>
                    <button className={styles.backBtn} onClick={() => navigate(`/admin/posts/edit/${id}`)}>
                        <ArrowLeft size={20} />
                    </button>
                    <h1 className={styles.title}>Version History</h1>
                </div>
            </div>

            {isLoading ? (
                <div>Loading...</div>
            ) : (
                <Table
                    columns={columns}
                    data={versions}
                    actions={(record) => (
                        <button
                            className={styles.restoreBtn}
                            onClick={() => handleRestore(record.version)}
                            title="Restore this version"
                        >
                            <RotateCcw size={16} />
                            Restore
                        </button>
                    )}
                />
            )}
        </div>
    );
};
