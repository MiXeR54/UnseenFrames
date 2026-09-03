package dev.chernykh.unseenFrames.frame;

import dev.chernykh.unseenFrames.config.PluginConfig;
import dev.chernykh.unseenFrames.util.Messenger;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/** Toggles a frame for a player with feedback: sound and action bar. */
public final class FrameToggler {

    private final FrameVisibility visibility;
    private final Supplier<PluginConfig> config;

    public FrameToggler(FrameVisibility visibility, Supplier<PluginConfig> config) {
        this.visibility = visibility;
        this.config = config;
    }

    /**
     * @return true if the frame is now hidden
     */
    public boolean toggle(Player player, ItemFrame frame) {
        boolean hide = !visibility.isEffectivelyHidden(frame);
        visibility.setHidden(frame, hide);

        PluginConfig cfg = config.get();
        playSound(frame, cfg);

        PluginConfig.Messages messages = cfg.messages();
        String message;
        if (!hide) {
            message = messages.revealed();
        } else if (frame.isVisible()) {
            message = messages.hiddenEmpty();
        } else {
            message = messages.hidden();
        }
        Messenger.actionBar(player, message);
        return hide;
    }

    private static void playSound(ItemFrame frame, PluginConfig cfg) {
        Key sound = cfg.sound();
        if (sound == null) {
            return;
        }
        Location location = frame.getLocation();
        frame.getWorld().playSound(location, sound.asString(), SoundCategory.PLAYERS, cfg.soundVolume(), cfg.soundPitch());
    }
}
