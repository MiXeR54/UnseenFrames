package dev.chernykh.unseenFrames.listener;

import dev.chernykh.unseenFrames.config.PluginConfig;
import dev.chernykh.unseenFrames.frame.ContainerPassthrough;
import dev.chernykh.unseenFrames.frame.FrameVisibility;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

/** Rotating, placing and removing the item of a hidden frame. */
public final class FrameChangeListener implements Listener {

    private final Plugin plugin;
    private final Supplier<PluginConfig> config;
    private final FrameVisibility visibility;
    private final ContainerPassthrough passthrough;

    public FrameChangeListener(Plugin plugin, Supplier<PluginConfig> config,
                               FrameVisibility visibility, ContainerPassthrough passthrough) {
        this.plugin = plugin;
        this.config = config;
        this.visibility = visibility;
        this.passthrough = passthrough;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFrameChange(PlayerItemFrameChangeEvent event) {
        ItemFrame frame = event.getItemFrame();
        visibility.adoptIfForeign(frame);
        if (!visibility.isHidden(frame)) {
            return;
        }
        switch (event.getAction()) {
            case ROTATE -> {
                PluginConfig cfg = config.get();
                if (!cfg.lockRotation() || event.getPlayer().isSneaking()) {
                    return; // Shift + right-click rotates as in vanilla
                }
                event.setCancelled(true);
                if (cfg.passthroughEnabled()) {
                    passthrough.open(event.getPlayer(), frame);
                }
            }
            // Vanilla applies the item after the event; recompute visibility on the next tick.
            case PLACE, REMOVE -> visibility.applyLater(plugin, frame);
        }
    }
}
