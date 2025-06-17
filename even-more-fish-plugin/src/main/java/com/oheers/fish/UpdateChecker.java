package com.oheers.fish;

import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.permissions.AdminPerms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker {

    private final String major = "2";
    private final EvenMoreFish plugin;

    public UpdateChecker(final EvenMoreFish plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("UnstableApiUsage")
    public String getVersion() {
        try {
            URI uri = new URI("https://api.modrinth.com/v2/project/vlh7rLCf/version");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray versions = (JSONArray) new JSONParser().parse(response.body());
            if (versions.isEmpty()) {
                return plugin.getPluginMeta().getVersion();
            }

            JSONObject latestVersion = (JSONObject) versions.get(0);
            return latestVersion.get("version_number").toString();
        } catch (Exception exception) {
            EvenMoreFish.getInstance().getLogger().warning("EvenMoreFish failed to check for updates against the Modrinth website, to check manually go to https://modrinth.com/plugin/evenmorefish/versions");
            return plugin.getPluginMeta().getVersion();
        }
    }
}

public class UpdateNotify implements Listener {

    @EventHandler
    // informs admins with emf.admin permission that the plugin needs updating
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!EvenMoreFish.getInstance().isUpdateAvailable()) {
            return;
        }

        if (event.getPlayer().hasPermission(AdminPerms.UPDATE_NOTIFY)) {
            ConfigMessage.ADMIN_UPDATE_AVAILABLE.getMessage().send(event.getPlayer());
        }

    }
}

