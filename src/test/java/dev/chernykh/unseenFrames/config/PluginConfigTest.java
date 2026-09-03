package dev.chernykh.unseenFrames.config;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginConfigTest {

    private static final Logger LOG = Logger.getLogger("test");

    private static PluginConfig load(String yaml) {
        return PluginConfig.load(YamlConfiguration.loadConfiguration(new StringReader(yaml)), LOG);
    }

    @Test
    void emptyConfigUsesDefaults() {
        PluginConfig cfg = load("");

        assertEquals(Material.SHEARS, cfg.tool());
        assertTrue(cfg.requireSneak());
        assertEquals(1, cfg.toolDamage());
        assertTrue(cfg.revealWhenEmpty());
        assertTrue(cfg.lockRotation());
        assertTrue(cfg.passthroughEnabled());
        assertTrue(cfg.passthroughCheckProtection());
        assertTrue(cfg.adoptInvisibleFrames());
        assertEquals(PluginConfig.DEFAULT_SOUND, cfg.sound());
        assertEquals(16, cfg.scanDefaultRadius());
        assertEquals(64, cfg.scanMaxRadius());
        assertEquals(6, cfg.scanSeconds());
        assertFalse(cfg.messages().hidden().isBlank());
    }

    @Test
    void readsValues() {
        PluginConfig cfg = load("""
                tool: minecraft:stick
                require-sneak: false
                tool-damage: 3
                reveal-when-empty: false
                lock-rotation: false
                container-passthrough:
                  enabled: false
                  check-protection: false
                adopt-invisible-frames: false
                sound:
                  key: custom:frames.toggle
                  volume: 0.5
                  pitch: 1.5
                scan:
                  default-radius: 8
                  max-radius: 32
                  seconds: 3
                messages:
                  hidden: ""
                  revealed: "<green>ok</green>"
                """);

        assertEquals(Material.STICK, cfg.tool());
        assertFalse(cfg.requireSneak());
        assertEquals(3, cfg.toolDamage());
        assertFalse(cfg.revealWhenEmpty());
        assertFalse(cfg.lockRotation());
        assertFalse(cfg.passthroughEnabled());
        assertFalse(cfg.passthroughCheckProtection());
        assertFalse(cfg.adoptInvisibleFrames());
        assertEquals(Key.key("custom", "frames.toggle"), cfg.sound());
        assertEquals(0.5f, cfg.soundVolume());
        assertEquals(1.5f, cfg.soundPitch());
        assertEquals(8, cfg.scanDefaultRadius());
        assertEquals(32, cfg.scanMaxRadius());
        assertEquals(3, cfg.scanSeconds());
        assertEquals("", cfg.messages().hidden());
        assertEquals("<green>ok</green>", cfg.messages().revealed());
    }

    @Test
    void unknownToolFallsBackToShears() {
        assertEquals(Material.SHEARS, load("tool: minecraft:definitely_not_an_item").tool());
        assertEquals(Material.SHEARS, load("tool: ''").tool());
    }

    @Test
    void soundKeyHandling() {
        assertNull(load("sound:\n  key: ''").sound(), "empty key = no sound");
        assertEquals(PluginConfig.DEFAULT_SOUND, load("sound:\n  key: 'not a key!'").sound(), "invalid key = default sound");
        assertEquals(Key.key("minecraft", "entity.sheep.shear"), load("sound:\n  key: entity.sheep.shear").sound());
    }

    @Test
    void negativeNumbersAreClamped() {
        PluginConfig cfg = load("""
                tool-damage: -5
                scan:
                  default-radius: 0
                  max-radius: -1
                  seconds: 0
                """);
        assertEquals(0, cfg.toolDamage());
        assertEquals(1, cfg.scanDefaultRadius());
        assertEquals(1, cfg.scanMaxRadius());
        assertEquals(1, cfg.scanSeconds());
    }
}
