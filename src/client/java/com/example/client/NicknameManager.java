package com.example.client;

import com.example.NickgroupsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class NicknameManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("colored_nicknames.json");
    private static ModConfig config = new ModConfig();

    static {
        load();
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    config = GSON.fromJson(reader, ModConfig.class);
                }
            }
            if (config == null) {
                config = new ModConfig();
            }
            if (config.groups == null) {
                config.groups = new ArrayList<>();
            }

            migrateOldConfig();
            ensureDefaultGroup();
        } catch (Exception e) {
            NickgroupsMod.LOGGER.error("Failed to load nickname config", e);
        }
    }

    private static void migrateOldConfig() {
        if (!config.groups.isEmpty()) {
            return;
        }

        boolean migrated = false;
        if (config.ownStyle != null) {
            ModConfig.GroupStyle ownGroup = new ModConfig.GroupStyle();
            ownGroup.name = "Me";
            ownGroup.style = config.ownStyle;
            ownGroup.players.add("Me");
            config.groups.add(ownGroup);
            migrated = true;
        }
        if (config.friendStyles != null && !config.friendStyles.isEmpty()) {
            for (Map.Entry<String, NicknameStyle> entry : config.friendStyles.entrySet()) {
                ModConfig.GroupStyle group = new ModConfig.GroupStyle();
                group.name = entry.getKey();
                group.style = entry.getValue();
                group.players.add(entry.getKey());
                config.groups.add(group);
            }
            migrated = true;
        }
        if (migrated) {
            save();
        }
    }

    private static void ensureDefaultGroup() {
        if (!config.groups.isEmpty()) {
            return;
        }

        ModConfig.GroupStyle defaultGroup = new ModConfig.GroupStyle();
        defaultGroup.name = "VIP";
        defaultGroup.players.add("alexmotu");
        defaultGroup.style.color1 = 0xFF5555;
        defaultGroup.style.mode = "SOLID";
        config.groups.add(defaultGroup);
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            NickgroupsMod.LOGGER.error("Failed to save nickname config", e);
        }
    }

    public static String getProfileName(GameProfile profile) {
        if (profile == null) return null;
        try {
            try {
                return (String) GameProfile.class.getMethod("getName").invoke(profile);
            } catch (NoSuchMethodException e) {
                return (String) GameProfile.class.getMethod("name").invoke(profile);
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static Component getFormat(Player player) {
        if (player == null) return null;
        return getFormat(player.getGameProfile());
    }

    public static Component getFormat(GameProfile profile) {
        String name = getProfileName(profile);
        if (name == null || config.groups == null) {
            return null;
        }

        for (ModConfig.GroupStyle group : config.groups) {
            if (group.style != null && group.style.enabled && group.players != null) {
                for (String playerName : group.players) {
                    if (playerName.equalsIgnoreCase(name)) {
                        return group.style.format(name);
                    }
                }
            }
        }
        return null;
    }

    public static boolean isInAnyGroup(String name) {
        if (name == null || name.trim().isEmpty() || config.groups == null) {
            return false;
        }

        for (ModConfig.GroupStyle group : config.groups) {
            if (group.style != null && group.style.enabled && group.players != null) {
                for (String playerName : group.players) {
                    if (playerName.equalsIgnoreCase(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
