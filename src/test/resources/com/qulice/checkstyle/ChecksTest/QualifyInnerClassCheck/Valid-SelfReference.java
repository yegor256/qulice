/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class ValidSelfReference {

    private final int value;

    public ValidSelfReference(final int input) {
        this.value = input;
    }

    public ValidSelfReference withExtra(final int extra) {
        return new ValidSelfReference(this.value + extra);
    }
}
