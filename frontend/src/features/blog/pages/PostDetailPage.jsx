import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Calendar, User, Clock, ArrowLeft, Eye, ThumbsUp, MessageSquare, Tag } from 'lucide-react';
import { Button } from '../../../components/ui/Button';
import { getPostBySlug } from '../api/blog';
import styles from './PostDetailPage.module.css';

export const PostDetailPage = () => {
    const { slug } = useParams();

    const { data: postResponse, isLoading, error } = useQuery({
        queryKey: ['post', slug],
        queryFn: () => getPostBySlug(slug),
    });

    const post = postResponse?.data;

    if (isLoading) return <div className={styles.loading}>Loading...</div>;
    if (error) return <div className={styles.error}>Post not found</div>;
    if (!post) return <div className={styles.error}>No post data available</div>;

    return (
        <article className={styles.article}>
            <Link to="/">
                <Button variant="ghost" size="sm" className={styles.backButton}>
                    <ArrowLeft size={16} style={{ marginRight: 4 }} /> Back to Home
                </Button>
            </Link>

            <header className={styles.header}>
                <div className={styles.categories}>
                    {/* Categories would go here if backend returned them in detail DTO */}
                </div>
                <h1 className={styles.title}>{post.title}</h1>
                <div className={styles.meta}>
                    <span className={styles.metaItem}><User size={16} /> {post.authorId || 'Admin'}</span>
                    <span className={styles.metaItem}>
                        <Calendar size={16} />
                        {post.publishAt ? new Date(post.publishAt).toLocaleDateString() : 'Draft'}
                    </span>
                    <span className={styles.metaItem}><Clock size={16} /> {post.readTime || '5 min'}</span>
                </div>
                <div className={styles.stats}>
                    <span className={styles.statItem}><Eye size={16} /> {post.viewCount || 0}</span>
                    <span className={styles.statItem}><ThumbsUp size={16} /> {post.likeCount || 0}</span>
                    <span className={styles.statItem}><MessageSquare size={16} /> {post.commentCount || 0}</span>
                </div>
            </header>

            <div
                className={styles.content}
                dangerouslySetInnerHTML={{ __html: post.contentMd }} // Assuming we render MD to HTML or just show raw for now. Ideally use a markdown renderer.
            />

            <footer className={styles.footer}>
                <div className={styles.tags}>
                    {post.tags && post.tags.map(tag => (
                        <span key={tag} className={styles.tag}>
                            <Tag size={14} /> {tag}
                        </span>
                    ))}
                </div>
            </footer>
        </article>
    );
};
