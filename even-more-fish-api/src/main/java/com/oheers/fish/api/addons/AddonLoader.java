package com.oheers.fish.api.addons;

import com.oheers.fish.api.plugin.EMFPlugin;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

public abstract class AddonLoader {

    private final EMFPlugin plugin;
    private AddonMetadata cachedMetadata;

    protected AddonLoader(EMFPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin instance cannot be null");
    }


    /**
     * Gets the addon metadata, first trying to load from META-INF/addon-loader.properties,
     * then falling back to loadAddonMetadata() if the file doesn't exist.
     */
    public AddonMetadata getAddonMetadata() {
        if (cachedMetadata == null) {
            // First try to load from properties file
            cachedMetadata = loadMetadataFromProperties();

            // If not found, try the default implementation
            if (cachedMetadata == null) {
                cachedMetadata = loadAddonMetadata();
            }

            // If still null, throw exception
            if (cachedMetadata == null) {
                throw new IllegalStateException("Could not load addon metadata - neither properties file exists nor metadata was provided");
            }
        }
        return cachedMetadata;
    }

    /**
     * Attempts to load metadata from META-INF/addon-loader.properties
     * @return The loaded metadata, or null if the file doesn't exist
     */
    private AddonMetadata loadMetadataFromProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            if (input == null) {
                return null; // File doesn't exist
            }

            Properties props = new Properties();
            props.load(input);

            String name = props.getProperty("name");
            String version = props.getProperty("version");
            List<String> authors = List.of(props.getProperty("authors").split(","));
            String website = props.getProperty("website");
            String description = props.getProperty("description", "");
            List<String> dependencies = List.of(props.getProperty("dependencies").split(","));

            if (name == null || version == null || authors.isEmpty()) {
                plugin.getLogger().warning("MANIFEST.MF is missing required fields (name, version, or author)");
                return null;
            }

            return new AddonMetadata(name, version, authors, description, website, dependencies);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load addon metadata from properties file", e);
            return null;
        }
    }

    /**
     * Default implementation that returns null. Can be overridden by subclasses
     * to provide custom metadata loading behavior.
     */
    protected AddonMetadata loadAddonMetadata() {
        return null;
    }

    public abstract void loadAddons();


    public final void load() {
        if (!canLoad()) {
            plugin.debug("Skipping loading of addon %s - canLoad() returned false".formatted(getAddonMetadata().name()));
            return;
        }

        try {
            validateMetadata();
            loadAddons();
            plugin.debug("Loaded Addon %s %s by %s".formatted(getAddonMetadata().name(), getAddonMetadata().version(), getAddonMetadata().authors()));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load addon %s".formatted(getAddonMetadata().name()), e);
        }
    }

    protected void validateMetadata() {
        AddonMetadata meta = getAddonMetadata();
        if (meta == null) {
            throw new IllegalStateException("Addon metadata cannot be null");
        }
        if (meta.name() == null || meta.name().isBlank()) {
            throw new IllegalStateException("Addon name cannot be null or blank");
        }
    }

    public abstract boolean canLoad();

    public void unload() {
        // Default empty implementation
    }

    public void reload() {
        unload();
        load();
    }

}
