import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit, Trash2, FileText, Eye, ThumbsUp, MessageSquare } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { Table } from '../../../components/ui/Table';
import { getPosts, deletePost } from '../api/post';
import styles from './PostsPage.module.css';

export const PostsPage = () => {
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // Fetch posts
    const { data: postsResponse, isLoading } = useQuery({
        queryKey: ['posts'],
        queryFn: () => getPosts({ page: 0, size: 20 }), // Pagination to be implemented
    });

    const posts = postsResponse?.data?.content || []; // Assuming Spring Data Page response

    const deleteMutation = useMutation({
        mutationFn: deletePost,
        onSuccess: () => {
            queryClient.invalidateQueries(['posts']);
        },
    });

    const handleDelete = (id) => {
        if (window.confirm('Are you sure you want to delete this post?')) {
            deleteMutation.mutate(id);
        }
    };

    const columns = [
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            render: (text, record) => (
                <div className={styles.titleCell}>
                    <FileText size={16} className="text-gray-400" />
                    <span className={styles.postTitle}>{text}</span>
                    <span className={styles.statusBadge}>{record.status}</span>
                </div>
            ),
        },
        {
            title: 'Stats',
            dataIndex: 'stats',
            key: 'stats',
            render: (_, record) => (
                <div className={styles.statsCell}>
                    <span title="Views"><Eye size={14} /> {record.viewCount || 0}</span>
                    <span title="Likes"><ThumbsUp size={14} /> {record.likeCount || 0}</span>
                    <span title="Comments"><MessageSquare size={14} /> {record.commentCount || 0}</span>
                </div>
            )
        },
        {
            title: 'Author',
            dataIndex: 'authorId',
            key: 'author',
            render: (authorId) => <span>User #{authorId}</span> // Placeholder until we have author details
        },
        {
            title: 'Published',
            dataIndex: 'publishAt',
            key: 'publishAt',
            render: (date) => date ? new Date(date).toLocaleDateString() : '-'
        },
    ];

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Posts</h1>
                <Link to="/admin/posts/new" className={styles.createBtn}>
                    <Plus size={16} />
                    Create Post
                </Link>
            </div>

            {isLoading ? (
                <div>Loading...</div>
            ) : (
                <Table
                    columns={columns}
                    data={posts}
                    actions={(record) => (
                        <>
                            <button
                                className={styles.actionBtn}
                                onClick={() => navigate(`/admin/posts/edit/${record.id}`)}
                            >
                                <Edit size={16} />
                            </button>
                            <button className={styles.actionBtn} onClick={() => handleDelete(record.id)}>
                                <Trash2 size={16} />
                            </button>
                        </>
                    )}
                />
            )}
        </div>
    );
};
