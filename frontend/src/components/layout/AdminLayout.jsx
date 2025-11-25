import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { LayoutDashboard, Users, FolderTree, FileText, Settings, LogOut, Shield } from 'lucide-react';
import styles from './AdminLayout.module.css';

export const AdminLayout = () => {
    return (
        <div className={styles.container}>
            <aside className={styles.sidebar}>
                <div className={styles.logo}>
                    <h2>Blog Admin</h2>
                </div>
                <nav className={styles.nav}>
                    <Link to="/admin" className={styles.navItem}>
                        <LayoutDashboard size={20} />
                        <span>Dashboard</span>
                    </Link>
                    <Link to="/admin/posts" className={styles.navItem}>
                        <FileText size={20} />
                        <span>Posts</span>
                    </Link>
                    <Link to="/admin/categories" className={styles.navItem}>
                        <FolderTree size={20} />
                        <span>Categories</span>
                    </Link>
                    <Link to="/admin/roles" className={styles.navItem}>
                        <Shield size={20} />
                        <span>Roles</span>
                    </Link>
                    <Link to="/admin/users" className={styles.navItem}>
                        <Users size={20} />
                        <span>Users</span>
                    </Link>
                    <Link to="/admin/settings" className={styles.navItem}>
                        <Settings size={20} />
                        <span>Settings</span>
                    </Link>
                </nav>
                <div className={styles.footer}>
                    <button className={styles.logoutBtn}>
                        <LogOut size={20} />
                        <span>Logout</span>
                    </button>
                </div>
            </aside>
            <main className={styles.main}>
                <header className={styles.header}>
                    <div className={styles.breadcrumbs}>
                        {/* Breadcrumbs placeholder */}
                        Admin / Dashboard
                    </div>
                    <div className={styles.userProfile}>
                        {/* User profile placeholder */}
                        <span>Admin User</span>
                    </div>
                </header>
                <div className={styles.content}>
                    <Outlet />
                </div>
            </main>
        </div>
    );
};
