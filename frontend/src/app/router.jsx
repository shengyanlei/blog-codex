import { createBrowserRouter } from 'react-router-dom';
import { MainLayout } from '../components/layout/MainLayout';
import { AdminLayout } from '../components/layout/AdminLayout';
import { HomePage } from '../features/blog/pages/HomePage';
import { PostDetailPage } from '../features/blog/pages/PostDetailPage';
import { CategoryPage } from '../features/blog/pages/CategoryPage';
import { DashboardPage } from '../features/admin/pages/DashboardPage';
import { CategoriesPage } from '../features/admin/pages/CategoriesPage';
import { RolesPage } from '../features/admin/pages/RolesPage';
import { UsersPage } from '../features/admin/pages/UsersPage';
import { PostsPage } from '../features/admin/pages/PostsPage';
import { PostEditPage } from '../features/admin/pages/PostEditPage';
import { PostVersionsPage } from '../features/admin/pages/PostVersionsPage';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: 'post/:slug',
                element: <PostDetailPage />,
            },
            {
                path: 'category/:slug',
                element: <CategoryPage />,
            },
        ],
    },
    {
        path: '/admin',
        element: <AdminLayout />,
        children: [
            {
                index: true,
                element: <DashboardPage />,
            },
            {
                path: 'categories',
                element: <CategoriesPage />,
            },
            {
                path: 'roles',
                element: <RolesPage />,
            },
            {
                path: 'users',
                element: <UsersPage />,
            },
            {
                path: 'posts',
                element: <PostsPage />,
            },
            {
                path: 'posts/new',
                element: <PostEditPage />,
            },
            {
                path: 'posts/edit/:id',
                element: <PostEditPage />,
            },
            {
                path: 'posts/:id/versions',
                element: <PostVersionsPage />,
            },
        ],
    },
]);
