/*
 * Hello.
 */
package foo;

/**
 * Class with a classic switch statement that Checkstyle's
 * {@code UseEnhancedSwitch} suggests rewriting into arrow-switch syntax,
 * which only compiles under Java 14+.
 * @since 1.0
 */
public final class EnhancedSwitch {
    /**
     * Describe the given type using a classic switch.
     * @param type The type
     * @return Human readable name
     */
    public String describe(final int type) {
        final String result;
        switch (type) {
            case 1:
                result = "one";
                break;
            case 2:
                result = "two";
                break;
            default:
                result = "many";
                break;
        }
        return result;
    }
}
