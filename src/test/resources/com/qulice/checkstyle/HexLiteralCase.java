/*
 * Hello.
 */
package foo;

/**
 * Hexadecimal literals spell their letters in lower case.
 * @since 1.0
 */
public final class HexLiteralCase {

    /**
     * Magic bytes that open a PNG file.
     * @return Header of eight bytes
     */
    public byte[] header() {
        return new byte[] {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
        };
    }
}
