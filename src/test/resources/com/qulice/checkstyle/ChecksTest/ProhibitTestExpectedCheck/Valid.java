/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Valid {
    @Test
    public void throwsOnBadInput() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> parse("not-a-number")
        );
    }

    @Test(timeout = 1000)
    public void completesQuickly() {
        parse("42");
    }

    @SomeOther(expected = "foo")
    public void unrelatedAnnotation() {
    }
}
