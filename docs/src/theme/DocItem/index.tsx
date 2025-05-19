// src/theme/DocItem/index.js
import React from 'react';
import DocItem from '@theme-original/DocItem';
import styles from './styles.module.css';

function VersionBadge({ version }) {
    return (
        <span className={styles.badge}>
      Version: {version}
    </span>
    );
}

export default function DocItemWrapper(props) {
    const { frontMatter } = props.content;
    return (
        <>
            {frontMatter.version && (
                <div className={styles.badgeContainer}>
                    <VersionBadge version={frontMatter.version} />
                </div>
            )}
            <DocItem {...props} />
        </>
    );
}