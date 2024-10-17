/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

/* C-style comment */
public final class ProhibitedCStyleComment {

    /** first sentence in a single-line comment should start with a capital letter */
    public void main() {
    }

    /**
     * the first sentence in a multiline comment should start with a capital letter.
     */
    private static String TEXT = "some text";

    /** the first sentence in a multi-line comment must begin with a capital letter, even if the comment begins with two asterisks.
     */
    private static String OTHER_TEXT = "some text";
}

