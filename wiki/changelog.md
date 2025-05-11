This update adds support for Minecraft 1.21.5.

## Major Changes
- Dropped support for Spigot servers.
- Dropped support for all servers below 1.20.1.
- Added the ability to hunt fish. This needs to be enabled in config.yml.
- The plugin now uses MiniMessage formatting instead of Legacy.
- Rewrote fish, rarity, competition, and bait configurations for better organization.
- The database is now safe to use. If you were using it previously, we recommend starting fresh for the optimal experience.
- All config file versions have been reset to 1.
- All command and subcommand names are now configurable in config.yml.
- Added `/emf journal` for keeping track of caught fish.

## Helpful Changes
- Custom biomes are now supported in the biome requirement on 1.21.3 and above.
- Added `/emf applybaits` to open a GUI for applying baits.
- Added two new competition types: SHORTEST_FISH and SHORTEST_TOTAL.
- If a translation message is missing, the default english version is added to your config.
- You can now "boost" rarities in certain regions.
- Added raw-nbt option for items, you can get an item's raw nbt with `/emf admin rawitem`.
- Added a LuckPerms group requirement type.
- Added support for Nexo and CraftEngine items.
- Reworked the economy configs to allow multiple currencies and multipliers.
- Added the ability to disable catching certain baits.
- The plugin now protects its custom items. All protections are configurable in config.yml.
- Added an option to send caught fish directly to your inventory.
- Bossbar text and progress can now be fully customized.
- Added biome sets for easier biome-based requirements.
- Equipped items are now protected from being accidentally sold.
- Toggle commands (like /emf toggle) now save between relogs.
- You can now disable the plugin's prefix in all messages.

## API Changes
- Maven artifacts are now published to CodeMC.
- Addons are now loaded via an AddonLoader instance.
- RequirementType is now considered an addon.
- RewardType is now considered an addon.
- All deprecated methods have been removed.
- Added EMFFishHuntEvent for the new hunting system.

## Boring Code Changes
- Reworked the Requirement system to match the Reward system.
- Internal rework of the competition code.
- Internal rework of the fish and rarity code.
- Fixed many errors caused by the bait system.
- Replaced the command library with a maintained one.
- Removed internal rounding of numbers, numbers are now only rounded when displayed.
- Baited fishing rod lore is now prefixed with an invisible character to identify the EMF lines.
- Every message is now backed by an Adventure Component.