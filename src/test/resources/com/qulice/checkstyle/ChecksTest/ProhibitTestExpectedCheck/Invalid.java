/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Invalid {
    @Test(expected = IllegalArgumentException.class)
    public void throwsOnBadInput() {
        parse("not-a-number");
    }

    @Test(expected = NullPointerException.class, timeout = 1000)
    public void throwsOnNull() {
        parse(null);
    }
}
