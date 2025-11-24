import React from 'react';
import { Edit, Trash2, Eye } from 'lucide-react';

const PostList = () => {
    const posts = [
        { id: 1, title: 'Getting Started with React', status: 'Published', date: '2023-11-20', views: 120 },
        { id: 2, title: 'Understanding Hooks', status: 'Draft', date: '2023-11-22', views: 0 },
        { id: 3, title: 'Advanced State Management', status: 'Published', date: '2023-11-23', views: 45 },
    ];

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-2xl font-bold mb-2">Articles</h1>
                    <p className="text-gray-500">Manage your blog posts</p>
                </div>
                <button className="btn btn-primary">
                    + New Post
                </button>
            </div>

            <div className="card">
                <table className="table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Date</th>
                            <th>Views</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {posts.map((post) => (
                            <tr key={post.id}>
                                <td className="font-medium">{post.title}</td>
                                <td>
                                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${post.status === 'Published' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                                        }`}>
                                        {post.status}
                                    </span>
                                </td>
                                <td className="text-gray-500">{post.date}</td>
                                <td className="text-gray-500">{post.views}</td>
                                <td>
                                    <div className="flex items-center gap-2">
                                        <button className="p-1 text-gray-500 hover:text-blue-600">
                                            <Edit size={16} />
                                        </button>
                                        <button className="p-1 text-gray-500 hover:text-red-600">
                                            <Trash2 size={16} />
                                        </button>
                                        <button className="p-1 text-gray-500 hover:text-gray-900">
                                            <Eye size={16} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default PostList;
