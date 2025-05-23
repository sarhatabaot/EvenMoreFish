import React from 'react';
import clsx from 'clsx';

interface EmfVersionBadgeProps {
    frontMatter: {
        version?: string;
        [key: string]: unknown;
    };
}

export default function EmfVersionBadge({frontMatter}: EmfVersionBadgeProps) {
    // Return null if no version specified in front matter
    if (!frontMatter?.version) return null;
    const variant = getVariantFromVersion(frontMatter.version);
    return (
        <span className={clsx(
            'emf-version-badge', // Reserved class for future customization
            'badge', // Infima base class
            `badge--${variant|| 'primary'}` // Infima variant
        )}>
      Version: {frontMatter.version}
    </span>
    );
}

function getVariantFromVersion(version: string): string {
    const channel = version.split('-')[1]; //test this, 2.0.0 (-RC, -BETA, -ALPHA etc) just RC atm, RC = orange
    switch (channel) {
        case 'RC': return 'warning';
        default: return 'secondary';
    }
}