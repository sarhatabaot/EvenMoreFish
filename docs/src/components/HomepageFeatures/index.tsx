import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Custom Item Support',
    Svg: require('@site/static/img/home/settings.svg').default,
    description: (
      <>
        EvenMoreFish supports the use of custom item plugins like: ItemsAdder, Nexo and more.
      </>
    ),
  },
  {
    title: 'Economies',
    Svg: require('@site/static/img/home/coins-swap.svg').default,
    description: (
      <>
        EvenMoreFish allows the use of multiple economies through a config switch.
      </>
    ),
  },
  {
    title: 'Competitions',
    Svg: require('@site/static/img/home/leaderboard-star.svg').default,
    description: (
      <>
        Special events that allow your players to compete against one another!
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
