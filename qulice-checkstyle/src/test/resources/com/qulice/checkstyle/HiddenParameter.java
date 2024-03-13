/*
 * Hello.
 */
package foo;

/**
 * Hidden paramater test.
 * @since 1.0
 */
class HiddenParameter {
    private final String test = "";

    /**
     * Some documentation for the function.
     * @param test Test
     */
    void bar(final String test) { // error is here
        System.out.println("Hello: " + test);
    }
}
