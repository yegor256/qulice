/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidSemicolon {
    /**
     * Dummy data.
     */
    private int dat = 1;

    /**
     * Method without extra semicolon in the end
     * of try-with-resources head.
     */
    public void view() {
        try (
            Closeable door = new Door();
            Closeable window = new Window();
            Closeable win = new Window()
        ) {
            int data = input.read() + this.dat;
            while (data != -1) {
                System.out.print((char) data);
                data = input.read();
            }
        }
    }
}
