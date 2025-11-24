import React from 'react';
import { PostCard } from '../components/PostCard';
import styles from './HomePage.module.css';

const MOCK_POSTS = [
    {
        id: 1,
        title: '构建高性能 React 应用的最佳实践',
        slug: 'best-practices-react-performance',
        excerpt: '深入探讨 React 性能优化的核心策略，包括组件渲染控制、状态管理优化以及代码分割技巧。',
        cover: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&auto=format&fit=crop&q=60',
        date: '2023-11-24',
        author: 'Admin',
        category: 'Tech',
        readTime: '5 min'
    },
    {
        id: 2,
        title: '现代 CSS 布局指南：Grid 与 Flexbox',
        slug: 'modern-css-layout-guide',
        excerpt: '全面解析 CSS Grid 和 Flexbox 的使用场景与技巧，助你轻松应对各种复杂的网页布局需求。',
        cover: 'https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=800&auto=format&fit=crop&q=60',
        date: '2023-11-20',
        author: 'Designer',
        category: 'Design',
        readTime: '8 min'
    },
    {
        id: 3,
        title: 'Spring Boot 3.0 新特性解析',
        slug: 'spring-boot-3-new-features',
        excerpt: 'Spring Boot 3.0 带来了哪些激动人心的新特性？本文将带你一探究竟，了解 AOT 编译、Observability 等关键更新。',
        cover: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&auto=format&fit=crop&q=60',
        date: '2023-11-15',
        author: 'BackendDev',
        category: 'Backend',
        readTime: '10 min'
    }
];

export const HomePage = () => {
    return (
        <div className={styles.container}>
            <section className={styles.hero}>
                <h1 className={styles.heroTitle}>探索技术的边界</h1>
                <p className={styles.heroSubtitle}>分享编程心得，记录生活点滴。</p>
            </section>

            <section className={styles.postList}>
                {MOCK_POSTS.map(post => (
                    <PostCard key={post.id} post={post} />
                ))}
            </section>
        </div>
    );
};
