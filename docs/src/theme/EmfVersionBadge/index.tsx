import React from 'react';
import clsx from 'clsx';
import type {Props} from "@theme/DocItem";

export default function EmfVersionBadge(props: Props) {
    const frontMatter = props.content.metadata;

    // Return null if no version specified in front matter
    if (!frontMatter.version) return null;
    const variant = frontMatter.version.split('-').pop(); //test this, 2.0.0 (-RC, -BETA, -ALPHA etc) just RC atm, RC = orange
    return (
        <span className={clsx(
            'emf-version-badge', // Reserved class for future customization
            'badge', // Infima base class
            `badge--${frontMatter.versionVariant || 'primary'}` // Infima variant
        )}>
      {frontMatter.versionPrefix || 'v'}{frontMatter.version}
    </span>
    );
}