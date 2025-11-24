import React from 'react';
import { BarChart3, MessageSquare, Users, FileText } from 'lucide-react';

const StatCard = ({ title, value, icon: Icon, color }) => (
    <div className="card p-6 flex items-center gap-4">
        <div className={`p-3 rounded-lg ${color}`}>
            <Icon size={24} className="text-white" />
        </div>
        <div>
            <p className="text-sm text-gray-500">{title}</p>
            <h3 className="text-2xl font-bold">{value}</h3>
        </div>
    </div>
);

const Dashboard = () => {
    return (
        <div>
            <div className="mb-8">
                <h1 className="text-2xl font-bold mb-2">Dashboard</h1>
                <p className="text-gray-500">Welcome back, here's what's happening today.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <StatCard title="Total Posts" value="128" icon={FileText} color="bg-blue-500" />
                <StatCard title="Comments" value="1,204" icon={MessageSquare} color="bg-green-500" />
                <StatCard title="Total Views" value="45.2k" icon={BarChart3} color="bg-purple-500" />
                <StatCard title="Subscribers" value="892" icon={Users} color="bg-orange-500" />
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="card p-6">
                    <h3 className="font-bold mb-4">Recent Activity</h3>
                    <div className="space-y-4">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="flex items-center gap-4 pb-4 border-b border-gray-100 last:border-0 last:pb-0">
                                <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
                                    📝
                                </div>
                                <div>
                                    <p className="font-medium">New post published</p>
                                    <p className="text-sm text-gray-500">2 hours ago</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="card p-6">
                    <h3 className="font-bold mb-4">Quick Actions</h3>
                    <div className="grid grid-cols-2 gap-4">
                        <button className="p-4 border border-dashed border-gray-300 rounded-lg hover:bg-gray-50 hover:border-primary transition-colors text-center">
                            <span className="block text-2xl mb-2">✍️</span>
                            <span className="font-medium">Write Post</span>
                        </button>
                        <button className="p-4 border border-dashed border-gray-300 rounded-lg hover:bg-gray-50 hover:border-primary transition-colors text-center">
                            <span className="block text-2xl mb-2">📤</span>
                            <span className="font-medium">Upload Media</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
