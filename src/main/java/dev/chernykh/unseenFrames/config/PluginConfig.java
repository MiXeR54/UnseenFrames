package dev.chernykh.unseenFrames.config;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Immutable snapshot of config.yml. After {@code /unseenframes reload} a new instance is created;
 * plugin components receive it through {@code Supplier<PluginConfig>}.
 */
public record PluginConfig(
        Material tool,
        boolean requireSneak,
        int toolDamage,
        boolean revealWhenEmpty,
        boolean lockRotation,
        boolean passthroughEnabled,
        boolean passthroughCheckProtection,
        boolean adoptInvisibleFrames,
        @Nullable Key sound,
        float soundVolume,
        float soundPitch,
        int scanDefaultRadius,
        int scanMaxRadius,
        int scanSeconds,
        Messages messages
) {

    public static final Material DEFAULT_TOOL = Material.SHEARS;
    public static final Key DEFAULT_SOUND = Key.key("minecraft", "item.shears.snip");

    /** Raw MiniMessage strings. An empty string means "do not show". */
    public record Messages(
            String hidden,
            String hiddenEmpty,
            String revealed,
            String scanResult,
            String toggleNoTarget,
            String reloadDone,
            String playersOnly
    ) {
    }

    public static PluginConfig load(ConfigurationSection c, Logger log) {
        return new PluginConfig(
                parseTool(c.getString("tool", "minecraft:shears"), log),
                c.getBoolean("require-sneak", true),
                Math.max(0, c.getInt("tool-damage", 1)),
                c.getBoolean("reveal-when-empty", true),
                c.getBoolean("lock-rotation", true),
                c.getBoolean("container-passthrough.enabled", true),
                c.getBoolean("container-passthrough.check-protection", true),
                c.getBoolean("adopt-invisible-frames", true),
                parseSound(c.getString("sound.key", DEFAULT_SOUND.asString()), log),
                (float) c.getDouble("sound.volume", 1.0),
                (float) c.getDouble("sound.pitch", 1.0),
                Math.max(1, c.getInt("scan.default-radius", 16)),
                Math.max(1, c.getInt("scan.max-radius", 64)),
                Math.max(1, c.getInt("scan.seconds", 6)),
                new Messages(
                        c.getString("messages.hidden", "<gray>Frame hidden</gray>"),
                        c.getString("messages.hidden-empty", "<gray>Frame will hide once an item is placed inside</gray>"),
                        c.getString("messages.revealed", "<gray>Frame is visible again</gray>"),
                        c.getString("messages.scan-result", "<gray>Hidden frames within <radius> blocks: <white><count></white></gray>"),
                        c.getString("messages.toggle-no-target", "<red>Look at an item frame</red>"),
                        c.getString("messages.reload-done", "<green>UnseenFrames configuration reloaded</green>"),
                        c.getString("messages.players-only", "<red>This command can only be used by players</red>")
                )
        );
    }

    static Material parseTool(@Nullable String raw, Logger log) {
        Material material = raw == null ? null : Material.matchMaterial(raw.trim());
        if (material == null) {
            log.warning("config.yml: unknown item tool=\"" + raw + "\", falling back to " + DEFAULT_TOOL.key());
            return DEFAULT_TOOL;
        }
        return material;
    }

    static @Nullable Key parseSound(@Nullable String raw, Logger log) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String key = raw.trim();
        if (!Key.parseable(key)) {
            log.warning("config.yml: invalid sound key sound.key=\"" + raw + "\", falling back to " + DEFAULT_SOUND.asString());
            return DEFAULT_SOUND;
        }
        return Key.key(key);
    }
}
