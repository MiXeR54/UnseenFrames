package dev.chernykh.unseenFrames.listener;

import dev.chernykh.unseenFrames.Permissions;
import dev.chernykh.unseenFrames.config.PluginConfig;
import dev.chernykh.unseenFrames.frame.FrameToggler;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.function.Supplier;

/** Player gesture: Shift + right-click on a frame with the tool. */
public final class ToggleListener implements Listener {

    private final Supplier<PluginConfig> config;
    private final FrameToggler toggler;

    public ToggleListener(Supplier<PluginConfig> config, FrameToggler toggler) {
        this.config = config;
        this.toggler = toggler;
    }

    /**
     * HIGHEST + ignoreCancelled: region protection cancels the event before we see it.
     * Main hand only: when the frame holds an item the client never sends the off-hand interaction.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame) || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        PluginConfig cfg = config.get();
        if (player.getInventory().getItemInMainHand().getType() != cfg.tool()) {
            return;
        }
        if (cfg.requireSneak() && !player.isSneaking()) {
            return;
        }
        if (!player.hasPermission(Permissions.USE)) {
            return; // behave like vanilla
        }

        // Otherwise the shears would go into an empty frame or the item would rotate.
        event.setCancelled(true);
        toggler.toggle(player, frame);
        if (cfg.toolDamage() > 0) {
            player.damageItemStack(EquipmentSlot.HAND, cfg.toolDamage());
        }
    }
}
