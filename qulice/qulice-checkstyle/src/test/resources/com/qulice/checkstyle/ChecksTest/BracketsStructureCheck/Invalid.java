/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public Invalid(String name,
     String value
     ) {
    }
    public Invalid(
        String name,
        String value) {
    }
    public void print(String format,
       String text
    ) {
    }
    public void print(
        String format,
        String text) {
    }
    // Check static declarations.
    static {
      String.format("File %s not found",
        file);
      String.format(
        "File %s not found",
        file);
      String.format(
        "File %s not found", file);
    }
    // Check instance declarations.
    {
      String.format("File %s not found",
        file);
      String.format(
        "File %s not found",
        file);
      String.format(
        "File %s not found", file);

    }
    // Check method declarations.
    public void main() {
      String.format("File %s not found",
        file);
      String.format(
        "File %s not found",
        file);
      String.format(
        "File %s not found", file);
      new Invalid(x,
        y);
      Invalid d = new Invalid(
        x,
        y);
      Invalid d1 = new Invalid(
        x, y);
    }
    @Test
    public Invalid(String name,
     String value
     ) {
    }
    @Test
    public Invalid(
        String name,
        String value) {
    }
    @Test
    public void print(String format,
       String text
    ) {
    }
    @Test
    public void print(
        String format,
        String text) {
    }
}
