/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public void one() {
        String sep = System.lineSeparator();
    }

    public void two() {
        String text = String.format("first%nsecond");
    }

    public void three() {
        String text = "plain text without any separators";
    }

    public void four() {
        String escaped = "a backslash \\n is fine";
    }

    public void five() {
        String special = "tabs \t and quotes \" are ok";
    }
}
