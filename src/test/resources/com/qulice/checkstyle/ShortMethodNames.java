/*
 * Hello.
 */
package foo;

import java.util.Comparator;
import java.util.Locale;

/**
 * Allows short English words as method names.
 * @since 1.0
 */
public final class ShortMethodNames {

    /**
     * The text.
     */
    private final String text;

    /**
     * Ctor.
     * @param txt The text
     */
    public ShortMethodNames(final String txt) {
        this.text = txt;
    }

    /**
     * The text as a string.
     * @return The text
     */
    public String as() {
        return this.text;
    }

    /**
     * The character at the given position.
     * @param position The position
     * @return The character
     */
    public char at(final int position) {
        return this.text.charAt(position);
    }

    /**
     * The text sorted by the given comparator.
     * @param order The comparator
     * @return The text
     */
    public String by(final Comparator<String> order) {
        return this.text;
    }

    /**
     * Move forward.
     * @return The text
     */
    public String go() {
        return this.text;
    }

    /**
     * The identity of this object.
     * @return The text
     */
    public String id() {
        return this.text;
    }

    /**
     * Is the text present in the given line?
     * @param line The line
     * @return True if present
     */
    public boolean in(final String line) {
        return line.contains(this.text);
    }

    /**
     * Is the text empty?
     * @return True if empty
     */
    public boolean is() {
        return this.text.isEmpty();
    }

    /**
     * The text itself.
     * @return The text
     */
    public String it() {
        return this.text;
    }

    /**
     * The text of the given length.
     * @param length The length
     * @return The text
     */
    public String of(final int length) {
        return this.text.substring(0, length);
    }

    /**
     * React on the given event.
     * @param event The event
     * @return The text
     */
    public String on(final String event) {
        return this.text;
    }

    /**
     * The text or the given alternative.
     * @param alternative The alternative
     * @return The text
     */
    public String or(final String alternative) {
        return this.text;
    }

    /**
     * The text converted to the given charset.
     * @param charset The charset
     * @return The text
     */
    public String to(final String charset) {
        return this.text;
    }

    /**
     * The text in upper case.
     * @return The text
     */
    public String up() {
        return this.text.toUpperCase(Locale.ENGLISH);
    }
}
