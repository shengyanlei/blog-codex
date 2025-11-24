import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { Calendar, User, Clock, ArrowLeft } from 'lucide-react';
import { Button } from '../../../components/ui/Button';
import styles from './PostDetailPage.module.css';

export const PostDetailPage = () => {
    const { slug } = useParams();

    // Mock data - in real app fetch by slug
    const post = {
        title: '构建高性能 React 应用的最佳实践',
        date: '2023-11-24',
        author: 'Admin',
        readTime: '5 min',
        content: `
      <p>React 是目前最流行的前端库之一，但在构建大型应用时，性能问题往往随之而来。本文将深入探讨 React 性能优化的核心策略。</p>
      <h2>1. 组件渲染控制</h2>
      <p>避免不必要的渲染是优化的第一步。使用 React.memo, useMemo 和 useCallback 可以有效减少子组件的重复渲染。</p>
      <h2>2. 状态管理优化</h2>
      <p>将状态下沉到需要的组件中，或者使用 Zustand 等原子化状态管理库，避免 Context 导致的全局重渲染。</p>
      <h2>3. 代码分割</h2>
      <p>利用 React.lazy 和 Suspense 实现路由懒加载，显著降低首屏体积。</p>
    `
    };

    return (
        <article className={styles.article}>
            <Link to="/">
                <Button variant="ghost" size="sm" className={styles.backButton}>
                    <ArrowLeft size={16} style={{ marginRight: 4 }} /> 返回首页
                </Button>
            </Link>

            <header className={styles.header}>
                <h1 className={styles.title}>{post.title}</h1>
                <div className={styles.meta}>
                    <span className={styles.metaItem}><User size={16} /> {post.author}</span>
                    <span className={styles.metaItem}><Calendar size={16} /> {post.date}</span>
                    <span className={styles.metaItem}><Clock size={16} /> {post.readTime}</span>
                </div>
            </header>

            <div
                className={styles.content}
                dangerouslySetInnerHTML={{ __html: post.content }}
            />
        </article>
    );
};
