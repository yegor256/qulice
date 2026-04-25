/*
 * Hello.
 */
package foo;

/**
 * Samples where the if/throw/else pattern is absent.
 * @since 1.0
 */
public final class ValidIfThenThrowElse {

    /**
     * Label to print.
     */
    private final String label;

    /**
     * Constructor.
     * @param name The label
     */
    public ValidIfThenThrowElse(final String name) {
        this.label = name;
    }

    /**
     * Throw without any else is allowed.
     * @param num A number
     */
    public void guard(final int num) {
        if (num < 0) {
            throw new IllegalArgumentException(this.label);
        }
        System.out.println(num);
    }

    /**
     * Else allowed when then branch does not throw.
     * @param num A number
     */
    public void plain(final int num) {
        if (num < 0) {
            System.out.println(this.label);
        } else {
            System.out.println(num);
        }
    }

    /**
     * Throw not at the tail of the then branch: still allowed.
     * @param num A number
     */
    public void tail(final int num) {
        if (num < 0) {
            if (num == -1) {
                throw new IllegalArgumentException(this.label);
            }
            System.out.println("other negative");
        } else {
            System.out.println(num);
        }
    }
}
