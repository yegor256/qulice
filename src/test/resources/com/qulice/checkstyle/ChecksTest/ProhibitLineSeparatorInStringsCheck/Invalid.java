/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public void one() {
        String sep = "\n";
    }

    public void two() {
        String sep = "\r\n";
    }

    public void three() {
        String text = "first\nsecond";
    }

    public void four() {
        String text = "carriage\rreturn";
    }

    public void five() {
        String text = "line1\nline2\nline3";
    }
}
