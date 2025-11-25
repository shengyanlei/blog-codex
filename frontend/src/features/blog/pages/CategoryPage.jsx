import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Calendar, User, Clock, ArrowRight, Folder } from 'lucide-react';
import { getCategoryBySlug } from '../api/blog';
import { getPosts } from '../api/blog'; // We might need a method to get posts by category slug or ID
import styles from './CategoryPage.module.css';

export const CategoryPage = () => {
    const { slug } = useParams();

    // Fetch category info
    const { data: categoryResponse, isLoading: isCategoryLoading } = useQuery({
        queryKey: ['category', slug],
        queryFn: () => getCategoryBySlug(slug),
    });

    const category = categoryResponse?.data;

    // Fetch posts for this category
    // Note: backend getPosts might need filter by categoryId. 
    // Since we only have slug here, we might need to get category first to get ID, or update backend to filter by category slug.
    // For now, let's assume we get category first then fetch posts by categoryId.
    const { data: postsResponse, isLoading: isPostsLoading } = useQuery({
        queryKey: ['posts', 'category', category?.id],
        queryFn: () => getPosts({ categoryId: category.id, page: 0, size: 10 }),
        enabled: !!category?.id,
    });

    const posts = postsResponse?.data?.content || [];

    if (isCategoryLoading) return <div className={styles.loading}>Loading category...</div>;
    if (!category) return <div className={styles.error}>Category not found</div>;

    return (
        <div className={styles.container}>
            <header className={styles.header}>
                <div className={styles.categoryIcon}>
                    <Folder size={48} strokeWidth={1} />
                </div>
                <h1 className={styles.title}>{category.name}</h1>
                {category.description && <p className={styles.description}>{category.description}</p>}
            </header>

            <div className={styles.postList}>
                {isPostsLoading ? (
                    <div className={styles.loading}>Loading posts...</div>
                ) : posts.length > 0 ? (
                    posts.map(post => (
                        <article key={post.id} className={styles.postCard}>
                            <Link to={`/post/${post.slug}`} className={styles.postLink}>
                                <h2 className={styles.postTitle}>{post.title}</h2>
                            </Link>
                            <p className={styles.excerpt}>{post.excerpt}</p>
                            <div className={styles.meta}>
                                <span className={styles.metaItem}>
                                    <Calendar size={14} />
                                    {post.publishAt ? new Date(post.publishAt).toLocaleDateString() : 'Draft'}
                                </span>
                                <span className={styles.metaItem}>
                                    <User size={14} /> {post.authorId || 'Admin'}
                                </span>
                                <Link to={`/post/${post.slug}`} className={styles.readMore}>
                                    Read more <ArrowRight size={14} />
                                </Link>
                            </div>
                        </article>
                    ))
                ) : (
                    <div className={styles.empty}>No posts found in this category.</div>
                )}
            </div>
        </div>
    );
};
