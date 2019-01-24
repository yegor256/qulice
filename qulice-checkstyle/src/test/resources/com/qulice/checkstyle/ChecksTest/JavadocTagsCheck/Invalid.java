/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

/**
 * This comment doesn't have since tag.
 */
public final class ProhibitedTagsAndMissingSince {
    public void main() {
    }
}

/**
 * @since a1.0
 */
public final class InvalidSince {
}

/**
 * This class have an inner class with no since tag.
 * @since 1.0
 */
public final class InvalidInner {
    /**
     * Inner class without since tag.
     */
    public final class InvalidInnerSince {
    }
}
