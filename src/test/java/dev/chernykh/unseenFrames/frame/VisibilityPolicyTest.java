package dev.chernykh.unseenFrames.frame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityPolicyTest {

    @Test
    void hiddenFrameWithItemIsInvisible() {
        assertFalse(VisibilityPolicy.shouldBeVisible(true, true));
        assertFalse(VisibilityPolicy.shouldBeVisible(true, false));
    }

    @Test
    void emptyHiddenFrameIsRevealedOnlyWhenConfigured() {
        assertTrue(VisibilityPolicy.shouldBeVisible(false, true));
        assertFalse(VisibilityPolicy.shouldBeVisible(false, false));
    }
}
