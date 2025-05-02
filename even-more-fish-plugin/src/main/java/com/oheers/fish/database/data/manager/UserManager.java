package com.oheers.fish.database.data.manager;


import com.oheers.fish.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements Listener {
    private final Database database;
    private final Map<UUID, Integer> userCache;

    public UserManager(Database database) {
        this.database = database;
        this.userCache = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onJoin(final @NotNull PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        userCache.putIfAbsent(uuid, database.getUserId(uuid));
    }

    @EventHandler
    public void onLeave(final @NotNull PlayerQuitEvent event) {
        userCache.remove(event.getPlayer().getUniqueId());
    }

    public int getUserId(final UUID uuid) {
        return userCache.get(uuid);
    }
}
