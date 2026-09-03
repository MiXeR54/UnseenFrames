package dev.chernykh.unseenFrames.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.chernykh.unseenFrames.Permissions;
import dev.chernykh.unseenFrames.UnseenFrames;
import dev.chernykh.unseenFrames.config.PluginConfig;
import dev.chernykh.unseenFrames.frame.FrameToggler;
import dev.chernykh.unseenFrames.frame.FrameVisibility;
import dev.chernykh.unseenFrames.util.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** /unseenframes reload | scan [radius] | toggle */
public final class UnseenFramesCommand {

    private static final int TOGGLE_REACH = 6;
    private static final long SCAN_PERIOD_TICKS = 10;

    private final UnseenFrames plugin;
    private final Supplier<PluginConfig> config;
    private final FrameVisibility visibility;
    private final FrameToggler toggler;

    public UnseenFramesCommand(UnseenFrames plugin, Supplier<PluginConfig> config,
                               FrameVisibility visibility, FrameToggler toggler) {
        this.plugin = plugin;
        this.config = config;
        this.visibility = visibility;
        this.toggler = toggler;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("unseenframes")
                .requires(source -> source.getSender().hasPermission(Permissions.ADMIN))
                .then(Commands.literal("reload").executes(this::reload))
                .then(Commands.literal("scan")
                        .executes(ctx -> scan(ctx, config.get().scanDefaultRadius()))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                                .executes(ctx -> scan(ctx, IntegerArgumentType.getInteger(ctx, "radius")))))
                .then(Commands.literal("toggle").executes(this::toggle))
                .build();
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        plugin.reloadPluginConfig();
        Messenger.chat(ctx.getSource().getSender(), config.get().messages().reloadDone());
        return Command.SINGLE_SUCCESS;
    }

    private int scan(CommandContext<CommandSourceStack> ctx, int requestedRadius) {
        PluginConfig cfg = config.get();
        Player player = player(ctx);
        if (player == null) {
            Messenger.chat(ctx.getSource().getSender(), cfg.messages().playersOnly());
            return 0;
        }
        int radius = Math.min(requestedRadius, cfg.scanMaxRadius());

        List<Location> found = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof ItemFrame frame && visibility.isEffectivelyHidden(frame)) {
                found.add(frame.getBoundingBox().getCenter().toLocation(player.getWorld()));
            }
        }
        Messenger.chat(player, cfg.messages().scanResult(),
                Placeholder.unparsed("count", String.valueOf(found.size())),
                Placeholder.unparsed("radius", String.valueOf(radius)));
        if (found.isEmpty()) {
            return Command.SINGLE_SUCCESS;
        }

        long totalTicks = cfg.scanSeconds() * 20L;
        long[] elapsed = {0};
        player.getScheduler().runAtFixedRate(plugin, task -> {
            for (Location location : found) {
                player.spawnParticle(Particle.END_ROD, location, 3, 0.15, 0.15, 0.15, 0.0);
            }
            elapsed[0] += SCAN_PERIOD_TICKS;
            if (elapsed[0] >= totalTicks) {
                task.cancel();
            }
        }, null, 1, SCAN_PERIOD_TICKS);
        return Command.SINGLE_SUCCESS;
    }

    private int toggle(CommandContext<CommandSourceStack> ctx) {
        PluginConfig cfg = config.get();
        Player player = player(ctx);
        if (player == null) {
            Messenger.chat(ctx.getSource().getSender(), cfg.messages().playersOnly());
            return 0;
        }
        if (!(player.getTargetEntity(TOGGLE_REACH, false) instanceof ItemFrame frame)) {
            Messenger.chat(player, cfg.messages().toggleNoTarget());
            return 0;
        }
        toggler.toggle(player, frame);
        return Command.SINGLE_SUCCESS;
    }

    private static @Nullable Player player(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getExecutor() instanceof Player executor) {
            return executor;
        }
        return ctx.getSource().getSender() instanceof Player sender ? sender : null;
    }
}
