import React from 'react';
import clsx from 'clsx';
import styles from './Table.module.css';

export const Table = ({ columns, data, rowKey = 'id', actions }) => {
    return (
        <div className={styles.tableContainer}>
            <table className={styles.table}>
                <thead>
                    <tr>
                        {columns.map((col) => (
                            <th key={col.key || col.dataIndex} className={styles.th} style={{ width: col.width }}>
                                {col.title}
                            </th>
                        ))}
                        {actions && <th className={styles.th} style={{ width: 100 }}>Actions</th>}
                    </tr>
                </thead>
                <tbody>
                    {data.map((row, rowIndex) => (
                        <tr key={row[rowKey] || rowIndex} className={styles.tr}>
                            {columns.map((col) => (
                                <td key={col.key || col.dataIndex} className={styles.td}>
                                    {col.render ? col.render(row[col.dataIndex], row) : row[col.dataIndex]}
                                </td>
                            ))}
                            {actions && (
                                <td className={styles.td}>
                                    <div className={styles.actions}>
                                        {actions(row)}
                                    </div>
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
            {data.length === 0 && (
                <div className={styles.empty}>
                    No data available
                </div>
            )}
        </div>
    );
};
