ALTER TABLE `${table.prefix}fish`
    ADD shortest_length REAL;

ALTER TABLE `${table.prefix}fish`
    ADD shortest_fisher VARCHAR(36) NOT NULL;

ALTER TABLE `${table.prefix}fish_log`
    ADD shortest_length REAL;

CREATE TABLE IF NOT EXISTS `${table.prefix}fish_user_stats` (
    fish_key VARCHAR(513) NOT NULL, --max size for composite key
    user_id INTEGER NOT NULL,
    shortest_length REAL,
    longest_length REAL,
    quantity INTEGER,
    PRIMARY KEY (fish_key)
)

-- also update key for fish table
-- update fish_log name and rarity lengths
-- todo