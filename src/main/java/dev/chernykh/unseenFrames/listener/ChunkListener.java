package dev.chernykh.unseenFrames.listener;

import dev.chernykh.unseenFrames.frame.FrameVisibility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

/** On chunk load: fixes flagged frames and adopts foreign invisible ones. */
public final class ChunkListener implements Listener {

    private final FrameVisibility visibility;

    public ChunkListener(FrameVisibility visibility) {
        this.visibility = visibility;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof ItemFrame frame && !visibility.adoptIfForeign(frame)) {
                visibility.apply(frame);
            }
        }
    }
}
