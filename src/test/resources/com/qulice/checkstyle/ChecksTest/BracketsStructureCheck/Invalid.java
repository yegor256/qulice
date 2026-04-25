/*
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
    // Check nested method call
    public String nest(String... lines) {
        nest(
          nest(
            "Good"
          )
        );
        nest(
          nest(
            "Bad"
          ));
        nest(
          nest(
            "Good"
          ),
          nest(
             "Bad"
          ));
        nest(
          nest(
            "Good"
          ),
          nest(
            "Good"
          )
        );
    }
    // Check try-with-resources.
    public void tryBad() throws Exception {
        try (final ByteArrayOutputStream baos =
            new ByteArrayOutputStream()) {
            baos.size();
        }
        try (
            final ByteArrayOutputStream baos =
            new ByteArrayOutputStream()) {
            baos.size();
        }
    }
    // Check annotations.
    @RetryOnFailure(attempts = 3, delay = 100,
        unit = TimeUnit.MILLISECONDS)
    public void retry() {
    }
    @Named(value = "foo",
        another = "bar"
    )
    public void named() {
    }
    @Checked(
        value = "one")
    public void checked() {
    }
    // Issue #503: parameters that are expressions on opening bracket line.
    public void issue503() {
        Integer.toString(1
            + 2, 2
        );
        throw new IllegalStateException("Failed"
            + " test" + ex.getMessage(), ex
        );
    }
}
