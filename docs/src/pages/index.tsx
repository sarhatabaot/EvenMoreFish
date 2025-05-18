import type {ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Heading from '@theme/Heading';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

import styles from './index.module.css';

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
      </div>
    </header>
  );
}

export default function Home(): ReactNode {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="Description will go into a meta tag in <head />">
      <HomepageHeader />
      <main>
        <HomepageFeatures />

          <section className={styles.keyFeatures}>
              <div className="container">
                  <h2 className="text--center">Key Features</h2>

                  <Tabs>
                      <TabItem value="fish" label="Fish Customization" default>
                          <ul>
                              <li>70+ fish included out of the box</li>
                              <li>Custom items (cod, in-game items, or custom heads)</li>
                              <li>Configurable fish length ranges</li>
                              <li>Biome-specific or region-specific fish (WorldGuard/RedProtect)</li>
                              <li>Run commands when fish are caught or eaten</li>
                              <li>Custom model data support</li>
                          </ul>
                      </TabItem>

                      <TabItem value="competitions" label="Competitions">
                          <ul>
                              <li>6 competition types including largest fish, most fish, and random selection</li>
                              <li>Scheduled or admin-started competitions</li>
                              <li>Configurable bossbar with position-based colors</li>
                              <li>Custom rewards for winners</li>
                          </ul>
                      </TabItem>

                      <TabItem value="shop" label="Fish Shop">
                          <ul>
                              <li>Sell fish for in-game currency</li>
                              <li>Configurable prices per fish/rarity</li>
                              <li>Item security against inventory closures</li>
                              <li>Multi-economy support</li>
                          </ul>
                      </TabItem>
                  </Tabs>
              </div>
          </section>
          <section className={styles.stats}>
              <div className="container text--center">
                  <h2>Trusted by Server Owners</h2>
                  <div className="row">
                      <div className="col col--12">
                          <img
                              src="https://bstats.org/signatures/bukkit/EvenMoreFish.svg"
                              alt="EvenMoreFish usage statistics"
                              className={styles.statsImage}
                          />
                      </div>
                  </div>
              </div>
          </section>
      </main>
    </Layout>
  );
}
