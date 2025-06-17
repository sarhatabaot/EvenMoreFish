package com.oheers.fish;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.oheers.fish.api.EMFAPI;
import com.oheers.fish.api.addons.AddonManager;
import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.api.plugin.EMFPlugin;
import com.oheers.fish.api.requirement.RequirementType;
import com.oheers.fish.api.reward.RewardType;
import com.oheers.fish.baits.BaitManager;
import com.oheers.fish.commands.AdminCommand;
import com.oheers.fish.commands.MainCommand;
import com.oheers.fish.competition.AutoRunner;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.CompetitionQueue;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.events.FishEatEvent;
import com.oheers.fish.events.FishInteractEvent;
import com.oheers.fish.events.McMMOTreasureEvent;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.fishing.rods.RodManager;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.plugin.*;
import de.themoep.inventorygui.InventoryGui;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.oheers.fish.FishUtils.classExists;

public class EvenMoreFish extends EMFPlugin {
    private final Random random = new Random();
    private final boolean isPaper = classExists("com.destroystokyo.paper.PaperConfig")
        || classExists("io.papermc.paper.configuration.Configuration");

    private ItemStack customNBTRod;
    // Do some fish in some rarities have the comp-check-exempt: true.
    private boolean raritiesCompCheckExempt = false;
    private CompetitionQueue competitionQueue;
    private Logger logger;
    private int metricFishCaught = 0;
    private int metricBaitsUsed = 0;
    private int metricBaitsApplied = 0;
    private boolean firstLoad = false;

    // this is for pre-deciding a rarity and running particles if it will be chosen
    // it's a work-in-progress solution and probably won't stick.
    private Map<UUID, Rarity> decidedRarities;
    private boolean isUpdateAvailable;

    private DependencyManager dependencyManager;
    private ConfigurationManager configurationManager;
    private PluginDataManager pluginDataManager;
    private IntegrationManager integrationManager;
    private EventManager eventManager;

    private static EvenMoreFish instance;
    private static TaskScheduler scheduler;
    private EMFAPI api;


    public static @NotNull EvenMoreFish getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void onLoad() {
        CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
                .missingExecutorImplementationMessage("You are not able to use this command!");
        CommandAPI.onLoad(config);
    }

    @Override
    public void onEnable() {
        // Don't enable if the server is not using Paper.
        if (!isPaper) {
            getLogger().severe("Spigot detected! EvenMoreFish no longer runs on Spigot, we recommend updating to Paper instead. https://papermc.io/downloads/paper");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!NBT.preloadApi()) {
            throw new RuntimeException("NBT-API wasn't initialized properly, disabling the plugin");
        }

        instance = this;

        CommandAPI.onEnable();

        // If EMF folder does not exist, this is the first load.
        firstLoad = !getDataFolder().exists();

        scheduler = UniversalScheduler.getScheduler(this);

        this.api = new EMFAPI();

        this.decidedRarities = new HashMap<>();

        this.logger = getLogger();

        this.dependencyManager = new DependencyManager(this);
        this.dependencyManager.checkDependencies(); // need to test, order may be important, if it is, we introduce multiple stages with events

        this.configurationManager = new ConfigurationManager(this);
        this.configurationManager.loadConfigurations(); //need to test, order may be important

        this.integrationManager = new IntegrationManager(this);
        this.integrationManager.loadAddons();

        // could not set up economy.
        if (!Economy.getInstance().isEnabled()) {
            getLogger().warning("EvenMoreFish won't be hooking into economy. If this wasn't by choice in config.yml, please install Economy handling plugins.");
        }

        FishManager.getInstance().load();
        BaitManager.getInstance().load();

        // Always load this after FishManager and BaitManager
        RodManager.getInstance().load();

        competitionQueue = new CompetitionQueue();
        competitionQueue.load();

        // check for updates on the modrinth page
        checkUpdate().thenAccept(available ->
            isUpdateAvailable = available
        );

        if (!MainConfig.getInstance().debugSession()) {
            metrics();
        }

        AutoRunner.init();

        this.pluginDataManager = new PluginDataManager(this);
        this.eventManager = new EventManager(this);
        this.eventManager.registerCoreListeners();
        this.eventManager.registerOptionalListeners();

        registerCommands();

        logger.log(Level.INFO, "EvenMoreFish by Oheers : Enabled");

        // Set this to false as the plugin is now loaded.
        firstLoad = false;
    }

    @Override
    public void onDisable() {
        // If the server is not using Paper, the plugin won't have enabled in the first place.
        if (!isPaper) {
            return;
        }

        CommandAPI.onDisable();

        terminateGuis();
        // Don't use the scheduler here because it will throw errors on disable
        if (this.pluginDataManager != null) {
            this.pluginDataManager.shutdown();
        }

        // Ends the current competition in case the plugin is being disabled when the server will continue running
        Competition active = Competition.getCurrentlyActive();
        if (active != null) {
            active.end(false);
        }

        RewardType.unregisterAll();
        RequirementType.unregisterAll();


        // Make sure this is in the reverse order of loading.
        RodManager.getInstance().unload();
        BaitManager.getInstance().unload();
        FishManager.getInstance().unload();

        logger.log(Level.INFO, "EvenMoreFish by Oheers : Disabled");
    }


    @Override
    public boolean isDebugSession() {
        return MainConfig.getInstance().debugSession();
    }

    private void metrics() {
        Metrics metrics = new Metrics(this, 11054);

        metrics.addCustomChart(new SingleLineChart("fish_caught", () -> {
            int returning = metricFishCaught;
            metricFishCaught = 0;
            return returning;
        }));

        metrics.addCustomChart(new SingleLineChart("baits_applied", () -> {
            int returning = metricBaitsApplied;
            metricBaitsApplied = 0;
            return returning;
        }));

        metrics.addCustomChart(new SingleLineChart("baits_used", () -> {
            int returning = metricBaitsUsed;
            metricBaitsUsed = 0;
            return returning;
        }));

        metrics.addCustomChart(new SimplePie("database", () -> MainConfig.getInstance().databaseEnabled() ? "true" : "false"));

        metrics.addCustomChart(new SimplePie("paper-adapter", () -> "true"));
    }

    // gets called on server shutdown to simulate all players closing their Guis
    private void terminateGuis() {
        getServer().getOnlinePlayers().forEach(player -> {
            InventoryGui inventoryGui = InventoryGui.getOpen(player);
            if (inventoryGui != null) {
                inventoryGui.close();
            }
        });
    }

    @Override
    public void reload(@Nullable CommandSender sender) {

        // If EMF folder does not exist, assume first load again.
        firstLoad = !getDataFolder().exists();

        terminateGuis();

        this.configurationManager.reloadConfigurations();

        FishManager.getInstance().reload();
        BaitManager.getInstance().reload();
        RodManager.getInstance().reload();

        HandlerList.unregisterAll(FishEatEvent.getInstance());
        HandlerList.unregisterAll(FishInteractEvent.getInstance());
        HandlerList.unregisterAll(McMMOTreasureEvent.getInstance());

        this.eventManager.registerOptionalListeners();

        competitionQueue.load();

        if (sender != null) {
            ConfigMessage.RELOAD_SUCCESS.getMessage().send(sender);
        }

        firstLoad = false;
    }

    private void registerCommands() {
        new MainCommand().getCommand().register(this);

        // Shortcut command for /emf admin
        if (MainConfig.getInstance().isAdminShortcutCommandEnabled()) {
            new AdminCommand(
                    MainConfig.getInstance().getAdminShortcutCommandName()
            ).getCommand().register(this);
        }
    }

    // Checks for updates, surprisingly
    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Boolean> checkUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            ComparableVersion modrinthVersion = new ComparableVersion(new UpdateChecker(this).getVersion());
            ComparableVersion serverVersion = new ComparableVersion(getPluginMeta().getVersion());
            return modrinthVersion.compareTo(serverVersion) > 0;
        });
    }

    public Random getRandom() {
        return random;
    }

    public ItemStack getCustomNBTRod() {
        return customNBTRod;
    }

    public boolean isRaritiesCompCheckExempt() {
        return raritiesCompCheckExempt;
    }

    public void setRaritiesCompCheckExempt(boolean bool) {
        this.raritiesCompCheckExempt = bool;
    }

    public CompetitionQueue getCompetitionQueue() {
        return competitionQueue;
    }

    public int getMetricFishCaught() {
        return metricFishCaught;
    }

    public void incrementMetricFishCaught(int value) {
        this.metricFishCaught = (metricFishCaught + value);
    }

    public int getMetricBaitsUsed() {
        return metricBaitsUsed;
    }

    public void incrementMetricBaitsUsed(int value) {
        this.metricBaitsUsed = (metricBaitsUsed + value);
    }

    public int getMetricBaitsApplied() {
        return metricBaitsApplied;
    }

    public void incrementMetricBaitsApplied(int value) {
        this.metricBaitsApplied = (metricBaitsApplied + value);
    }

    public Map<UUID, Rarity> getDecidedRarities() {
        return decidedRarities;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public EMFAPI getApi() {
        return api;
    }

    public List<Player> getVisibleOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
//        return MainConfig.getInstance().shouldRespectVanish() ? VanishChecker.getVisibleOnlinePlayers() : new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    // FISH TOGGLE METHODS

    public void performFishToggle(@NotNull Player player) {
        NamespacedKey key = new NamespacedKey(this, "fish-disabled");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        // If custom fishing is disabled
        if (isCustomFishingDisabled(player)) {
            // Set fish-disabled to false
            pdc.set(key, PersistentDataType.BOOLEAN, false);
            ConfigMessage.TOGGLE_ON.getMessage().send(player);
        } else {
            // Set fish-disabled to true
            pdc.set(key, PersistentDataType.BOOLEAN, true);
            ConfigMessage.TOGGLE_OFF.getMessage().send(player);
        }
    }

    public boolean isCustomFishingDisabled(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, "fish-disabled");
        return pdc.getOrDefault(key, PersistentDataType.BOOLEAN, false);
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public PluginDataManager getPluginDataManager() {
        return pluginDataManager;
    }

    public AddonManager getAddonManager() {
        return integrationManager.getAddonManager();
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
