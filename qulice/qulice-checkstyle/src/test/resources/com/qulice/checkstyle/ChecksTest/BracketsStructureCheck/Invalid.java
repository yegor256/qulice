/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
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
}
