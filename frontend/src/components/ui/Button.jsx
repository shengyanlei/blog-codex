import React from 'react';
import clsx from 'clsx';
import styles from './Button.module.css';

export const Button = ({
    children,
    variant = 'primary',
    size = 'md',
    className,
    ...props
}) => {
    return (
        <button
            className={clsx(
                styles.button,
                styles[variant],
                styles[size],
                className
            )}
            {...props}
        >
            {children}
        </button>
    );
};
