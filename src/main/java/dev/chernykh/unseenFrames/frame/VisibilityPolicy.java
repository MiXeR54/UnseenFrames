package dev.chernykh.unseenFrames.frame;

/** Pure visibility rule for a frame flagged as hidden. */
public final class VisibilityPolicy {

    private VisibilityPolicy() {
    }

    /**
     * @param hasItem         the frame holds an item
     * @param revealWhenEmpty the reveal-when-empty setting
     * @return whether the (flagged) frame should be visible in vanilla
     */
    public static boolean shouldBeVisible(boolean hasItem, boolean revealWhenEmpty) {
        return !hasItem && revealWhenEmpty;
    }
}
