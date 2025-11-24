import { createBrowserRouter } from 'react-router-dom';
import { MainLayout } from '../components/layout/MainLayout';
import { HomePage } from '../features/blog/pages/HomePage';
import { PostDetailPage } from '../features/blog/pages/PostDetailPage';

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
                path: 'posts/:slug',
                element: <PostDetailPage />,
            },
            // Add more routes here
        ],
    },
]);
