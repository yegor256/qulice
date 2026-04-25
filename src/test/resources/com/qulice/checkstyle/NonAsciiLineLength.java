/*
 * Hello.
 */
package foo;

/**
 * Non-ASCII characters must not be double-counted by LineLengthCheck.
 * @since 1.0
 */
public final class NonAsciiLineLength {

    @Override
    public String toString() {
        final String input = "Hello, товарищ output äÄ üÜ öÖ and ßßßßßßßßßßßßßßßßßßßßßßßßßßßßßßßß";
        return input;
    }
}
