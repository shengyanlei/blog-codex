import React from 'react';
import { Link } from 'react-router-dom';
import { Search, Sun, Moon, User } from 'lucide-react';
import { Button } from '../ui/Button';
import styles from './Header.module.css';

export const Header = () => {
    return (
        <header className={styles.header}>
            <div className={styles.container}>
                <Link to="/" className={styles.logo}>
                    Codex Blog
                </Link>

                <nav className={styles.nav}>
                    <Link to="/" className={styles.link}>首页</Link>
                    <Link to="/category/tech" className={styles.link}>技术</Link>
                    <Link to="/category/life" className={styles.link}>生活</Link>
                </nav>

                <div className={styles.actions}>
                    <div className={styles.searchWrapper}>
                        <Search size={18} className={styles.searchIcon} />
                        <input type="text" placeholder="搜索..." className={styles.searchInput} />
                    </div>

                    <Button variant="ghost" size="sm" aria-label="Toggle theme">
                        <Sun size={20} />
                    </Button>

                    <Link to="/login">
                        <Button variant="primary" size="sm">
                            <User size={16} style={{ marginRight: 4 }} />
                            登录
                        </Button>
                    </Link>
                </div>
            </div>
        </header>
    );
};
