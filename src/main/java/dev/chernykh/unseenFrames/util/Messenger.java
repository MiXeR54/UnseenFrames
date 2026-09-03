package dev.chernykh.unseenFrames.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

/** Sends configurable messages: MiniMessage, an empty string means stay silent. */
public final class Messenger {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private Messenger() {
    }

    public static @Nullable Component render(@Nullable String raw, TagResolver... resolvers) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return MINI_MESSAGE.deserialize(raw, resolvers);
    }

    public static void actionBar(Audience audience, @Nullable String raw, TagResolver... resolvers) {
        Component component = render(raw, resolvers);
        if (component != null) {
            audience.sendActionBar(component);
        }
    }

    public static void chat(Audience audience, @Nullable String raw, TagResolver... resolvers) {
        Component component = render(raw, resolvers);
        if (component != null) {
            audience.sendMessage(component);
        }
    }
}
