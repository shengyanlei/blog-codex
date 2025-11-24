import React from 'react';
import styles from './Footer.module.css';

export const Footer = () => {
    return (
        <footer className={styles.footer}>
            <div className={styles.container}>
                <p>&copy; {new Date().getFullYear()} Codex Blog. All rights reserved.</p>
                <div className={styles.links}>
                    <a href="#">关于我们</a>
                    <a href="#">隐私政策</a>
                    <a href="#">联系方式</a>
                </div>
            </div>
        </footer>
    );
};
