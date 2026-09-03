package dev.chernykh.unseenFrames.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MessengerTest {

    @Test
    void blankMeansSilence() {
        assertNull(Messenger.render(null));
        assertNull(Messenger.render(""));
        assertNull(Messenger.render("   "));
    }

    @Test
    void rendersMiniMessageWithPlaceholders() {
        Component component = Messenger.render("<gray>Found: <count></gray>", Placeholder.unparsed("count", "3"));
        assertEquals("Found: 3", PlainTextComponentSerializer.plainText().serialize(component));
    }
}
