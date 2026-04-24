/*
 * LICENSE.
 */
package foo;

/**
 * This is not a real Java class.
 *
 * @since 1.0
 */
public final class Valid {
    /**
     * Compute something.
     * @param text A string with contents. Cannot be null
     * @param size The size to use
     * @return True when empty, false otherwise
     */
    public boolean compute(final String text, final int size) {
        return true;
    }

    /**
     * Multi-line description.
     * @param text Some text, which description
     *  takes a few lines
     * @return The result of computation
     */
    public int something(final String text) {
        return 0;
    }

    /**
     * No params here.
     */
    public void plain() {
    }

    /**
     * With inline tag.
     * @param text A {@code String}
     * @return See {@link java.util.List}
     */
    public Object linked(final String text) {
        return null;
    }
}
