package dev.chernykh.unseenFrames.frame;

import dev.chernykh.unseenFrames.config.PluginConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

/**
 * Core of the plugin: the "hidden" flag in the entity's PDC and vanilla invisibility derived from it.
 * Frames without the flag are left alone, except for adoption ({@link #adoptIfForeign}).
 */
public final class FrameVisibility {

    private final NamespacedKey hiddenKey;
    private final Supplier<PluginConfig> config;

    public FrameVisibility(NamespacedKey hiddenKey, Supplier<PluginConfig> config) {
        this.hiddenKey = hiddenKey;
        this.config = config;
    }

    public NamespacedKey hiddenKey() {
        return hiddenKey;
    }

    /** The frame is flagged as hidden by this plugin. */
    public boolean isHidden(ItemFrame frame) {
        return frame.getPersistentDataContainer().has(hiddenKey);
    }

    /** Hidden by our flag or invisible by any other means. */
    public boolean isEffectivelyHidden(ItemFrame frame) {
        return isHidden(frame) || !frame.isVisible();
    }

    /** Sets or clears the flag and applies visibility right away. */
    public void setHidden(ItemFrame frame, boolean hidden) {
        PersistentDataContainer pdc = frame.getPersistentDataContainer();
        if (hidden) {
            pdc.set(hiddenKey, PersistentDataType.BOOLEAN, true);
            apply(frame);
        } else {
            pdc.remove(hiddenKey);
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
        }
    }

    /**
     * Recomputes vanilla invisibility from the flag and the item.
     *
     * @return whether the frame's state changed
     */
    public boolean apply(ItemFrame frame) {
        if (!isHidden(frame)) {
            return false;
        }
        boolean hasItem = !frame.getItem().isEmpty();
        boolean visible = VisibilityPolicy.shouldBeVisible(hasItem, config.get().revealWhenEmpty());
        if (frame.isVisible() == visible) {
            return false;
        }
        frame.setVisible(visible);
        return true;
    }

    /** Applies visibility on the next tick, after vanilla has changed the item in the frame. */
    public void applyLater(Plugin plugin, ItemFrame frame) {
        frame.getScheduler().run(plugin, task -> apply(frame), null);
    }

    /**
     * Adopts a frame that somebody else made invisible (a command, an older plugin).
     *
     * @return true if the frame received the flag
     */
    public boolean adoptIfForeign(ItemFrame frame) {
        if (frame.isVisible() || isHidden(frame) || !config.get().adoptInvisibleFrames()) {
            return false;
        }
        setHidden(frame, true);
        return true;
    }
}
