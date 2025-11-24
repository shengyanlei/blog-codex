import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, FileText, Image, Users, Settings, LogOut } from 'lucide-react';
import clsx from 'clsx';

const Sidebar = () => {
    const navItems = [
        { icon: LayoutDashboard, label: '仪表盘', path: '/' },
        { icon: FileText, label: '文章管理', path: '/posts' },
        { icon: Image, label: '媒体资源', path: '/media' },
        { icon: Users, label: '用户管理', path: '/users' },
        { icon: Settings, label: '系统配置', path: '/settings' },
    ];

    return (
        <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
            <div className="p-6 border-b border-gray-100">
                <h1 className="text-xl font-bold text-primary flex items-center gap-2">
                    <span className="text-2xl">⚡</span> Codex Blog
                </h1>
            </div>

            <nav className="flex-1 p-4 space-y-1">
                {navItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        className={({ isActive }) =>
                            clsx(
                                'flex items-center gap-3 px-4 py-3 rounded-lg transition-colors',
                                isActive
                                    ? 'bg-blue-50 text-primary font-medium'
                                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                            )
                        }
                    >
                        <item.icon size={20} />
                        {item.label}
                    </NavLink>
                ))}
            </nav>

            <div className="p-4 border-t border-gray-100">
                <button className="flex items-center gap-3 px-4 py-3 w-full text-left text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                    <LogOut size={20} />
                    退出登录
                </button>
            </div>
        </aside>
    );
};

export default Sidebar;
