package com.oheers.fish.update;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateChecker {
    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/vlh7rLCf/version";

    private final String currentVersion;
    private final HttpClient httpClient;
    private final Logger logger;

    public UpdateChecker(@NotNull String currentVersion,
                         @NotNull Logger logger) {
        this.currentVersion = currentVersion;
        this.httpClient = HttpClient.newHttpClient();
        this.logger = logger;
    }

    public UpdateChecker(@NotNull String currentVersion,
                         @NotNull HttpClient httpClient,
                         @NotNull Logger logger) {
        this.currentVersion = currentVersion;
        this.httpClient = httpClient;
        this.logger = logger;
    }

    /**
     * Fetches the latest version from Modrinth.
     * Falls back to current version on failure.
     */
    public String getVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODRINTH_API_URL))
                    .header("User-Agent", "EvenMoreFish/" + currentVersion)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode());
            }

            return parseVersionFromJson(response.body());
        } catch (Exception e) {
            logger.warning("Update check failed: " + e.getMessage());
            logger.info("Check for updates manually: https://modrinth.com/plugin/evenmorefish/versions");
            return currentVersion;
        }
    }

    /**
     * Parses the version number from the JSON array response.
     */
    private @NotNull String parseVersionFromJson(String json) throws Exception {
        JSONArray versions = (JSONArray) new JSONParser().parse(json);
        if (versions.isEmpty()) return currentVersion;

        JSONObject latest = (JSONObject) versions.get(0);
        Object versionNumber = latest.get("version_number");
        return versionNumber != null ? versionNumber.toString() : currentVersion;
    }

    /**
     * Asynchronously checks if a newer version is available.
     */
    @Contract(" -> new")
    public @NotNull CompletableFuture<Boolean> checkUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            ComparableVersion latest = new ComparableVersion(getVersion());
            ComparableVersion local = new ComparableVersion(currentVersion);
            return latest.compareTo(local) > 0;
        });
    }
}
