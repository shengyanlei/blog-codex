import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Clock, User } from 'lucide-react';
import styles from './PostCard.module.css';

export const PostCard = ({ post }) => {
    return (
        <article className={styles.card}>
            <div className={styles.imageWrapper}>
                <img src={post.cover} alt={post.title} className={styles.image} />
            </div>
            <div className={styles.content}>
                <div className={styles.meta}>
                    <span className={styles.category}>{post.category}</span>
                    <span className={styles.date}>
                        <Calendar size={14} /> {post.date}
                    </span>
                </div>
                <Link to={`/post/${post.slug}`} className={styles.titleLink}>
                    <h2 className={styles.title}>{post.title}</h2>
                </Link>
                <p className={styles.excerpt}>{post.excerpt}</p>
                <div className={styles.footer}>
                    <div className={styles.author}>
                        <User size={14} /> {post.author}
                    </div>
                    <div className={styles.readTime}>
                        <Clock size={14} /> {post.readTime}
                    </div>
                </div>
            </div>
        </article>
    );
};
