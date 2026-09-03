package dev.chernykh.unseenFrames.frame;

import dev.chernykh.unseenFrames.config.PluginConfig;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.function.Supplier;

/** Opens the container a hidden frame hangs on, as if the player had clicked the block. */
public final class ContainerPassthrough {

    private final Supplier<PluginConfig> config;

    public ContainerPassthrough(Supplier<PluginConfig> config) {
        this.config = config;
    }

    /**
     * @return true if the container was opened
     */
    public boolean open(Player player, ItemFrame frame) {
        Block support = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());
        if (!(support.getState() instanceof Container container)) {
            return false;
        }
        if (config.get().passthroughCheckProtection() && !allowedByOtherPlugins(player, frame, support)) {
            return false;
        }
        return player.openInventory(container.getInventory()) != null;
    }

    /** Synthetic block click: region protection and lock plugins cancel it when access is denied. */
    private static boolean allowedByOtherPlugins(Player player, ItemFrame frame, Block support) {
        PlayerInteractEvent probe = new PlayerInteractEvent(
                player,
                Action.RIGHT_CLICK_BLOCK,
                player.getInventory().getItemInMainHand(),
                support,
                frame.getFacing(),
                EquipmentSlot.HAND
        );
        probe.callEvent();
        return probe.useInteractedBlock() != Event.Result.DENY;
    }
}
