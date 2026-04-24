/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public Valid() {
    }
    @Test
    public Valid() {
    }
    public Valid(String name, String value) {
    }
    public Valid(String name, String value) {
    }
    public void print(){
    }
    @Test
    public void print(){
    }
    public void print(String format, String[] text) {
    }
    public void print(String format, String text ) {
    }
    // Check static declarations.
    static {
       String.format(
        "File %s not found",
        file
      );
      String.format(
        "File %s not found", file
      );
      String.format("File %s not found", file);
      String.format("File %s not found(", file);
    }
    // Check instance declarations.
    {
       String.format(
        "File %s not found",
        file
      );
      String.format(
        "File %s not found", file
      );
      String.format("File %s not found", file);
      String.format("File %s not found(", file);
    }
    // Check method declarations.
    public void main() {
       String.format(
        "File %s not found",
        file
      );
      String.format(
        "File %s not found", file
      );
      String.format("File %s not found", file);
      String.format("File %s not found(", file);
      new Invalid(
          x,
          y,
          new String[] {"some text", "another one"}
      );
      Invalid d = new Invalid(
        x, y
      );
      Invalid d1 = new Invalid(x, y);
      Invalid d2 = new Invalid("File %s not found(", file);
      Valid object = new
              Valid();
    }
    // Check annotations.
    @Override
    public void plain() {
    }
    @SuppressWarnings("foo")
    public void single() {
    }
    @RetryOnFailure(attempts = 3, delay = 100, unit = TimeUnit.MILLISECONDS)
    public void retry() {
    }
    @RetryOnFailure(
        attempts = 3, delay = 100, unit = TimeUnit.MILLISECONDS
    )
    public void retryLong() {
    }
    @Named(
        value = "foo",
        another = "bar"
    )
    public void named() {
    }
    // Check try-with-resources.
    public void tryGood() throws Exception {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.size();
        }
        try (
            final ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            baos.size();
        }
        try (
            final ByteArrayOutputStream baos =
                new ByteArrayOutputStream()
        ) {
            baos.size();
        }
    }
}
