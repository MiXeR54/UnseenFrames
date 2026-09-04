package dev.chernykh.unseenFrames;

import dev.chernykh.unseenFrames.command.UnseenFramesCommand;
import dev.chernykh.unseenFrames.config.PluginConfig;
import dev.chernykh.unseenFrames.frame.ContainerPassthrough;
import dev.chernykh.unseenFrames.frame.FrameToggler;
import dev.chernykh.unseenFrames.frame.FrameVisibility;
import dev.chernykh.unseenFrames.listener.ChunkListener;
import dev.chernykh.unseenFrames.listener.FrameChangeListener;
import dev.chernykh.unseenFrames.listener.ToggleListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class UnseenFrames extends JavaPlugin {

    /** Service ID on <a href="https://bstats.org/plugin/bukkit/UnseenFrames/33847">bstats.org</a>; 0 disables metrics. */
    private static final int BSTATS_SERVICE_ID = 33847;

    private final boolean folia = detectFolia();

    private volatile PluginConfig config;
    private FrameVisibility visibility;
    private @Nullable Metrics metrics;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = PluginConfig.load(getConfig(), getLogger());

        visibility = new FrameVisibility(new NamespacedKey(this, "hidden"), this::config);
        FrameToggler toggler = new FrameToggler(visibility, this::config);
        ContainerPassthrough passthrough = new ContainerPassthrough(this::config);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ToggleListener(this::config, toggler), this);
        pm.registerEvents(new FrameChangeListener(this, this::config, visibility, passthrough), this);
        pm.registerEvents(new ChunkListener(visibility), this);

        UnseenFramesCommand command = new UnseenFramesCommand(this, this::config, visibility, toggler);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(command.build(), "Invisible item frames: reload, scan, toggle", List.of("uf")));

        setupMetrics();
    }

    @Override
    public void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
    }

    public PluginConfig config() {
        return config;
    }

    /** Re-reads config.yml and brings already-loaded frames in line with the new rules. */
    public void reloadPluginConfig() {
        reloadConfig();
        config = PluginConfig.load(getConfig(), getLogger());
        if (folia) {
            getLogger().info("Folia: loaded frames will be updated when their chunks load next");
            return;
        }
        int changed = 0;
        for (World world : getServer().getWorlds()) {
            for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
                if (visibility.adoptIfForeign(frame) || visibility.apply(frame)) {
                    changed++;
                }
            }
        }
        if (changed > 0) {
            getLogger().info("Resynced frames: " + changed);
        }
    }

    private void setupMetrics() {
        if (BSTATS_SERVICE_ID <= 0) {
            getLogger().info("bStats: service ID not set, metrics disabled");
            return;
        }
        Metrics m = new Metrics(this, BSTATS_SERVICE_ID);
        m.addCustomChart(new SimplePie("container_passthrough", () -> String.valueOf(config.passthroughEnabled())));
        m.addCustomChart(new SimplePie("reveal_when_empty", () -> String.valueOf(config.revealWhenEmpty())));
        m.addCustomChart(new SimplePie("adopt_invisible_frames", () -> String.valueOf(config.adoptInvisibleFrames())));
        m.addCustomChart(new SimplePie("tool", () -> config.tool().key().asString()));
        metrics = m;
    }

    private static boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
