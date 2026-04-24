/*
 * Hello.
 */
package foo;

/**
 * Sample with unnecessary semicolons.
 * @since 1.0
 */
public final class ExtraSemicolonInDeclaration {
    /**
     * Dummy field.
     */
    private int dat = 1;

    /**
     * Constructor ending with a stray semicolon.
     */
    public ExtraSemicolonInDeclaration() {
    };

    /**
     * Method ending with a stray semicolon.
     * @return Dummy.
     */
    public int act() {
        return this.dat;
    };
};
