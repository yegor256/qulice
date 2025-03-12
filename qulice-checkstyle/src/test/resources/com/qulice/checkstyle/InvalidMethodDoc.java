/*
 * Hello
 */
package foo;

/**
 * The {@code InvalidMethodDoc#method} comment isn't javadoc, but it's not a reason
 * to fail {@code MultilineJavadocTagsCheck} validation with an unhandled exception.
 * @foo bar
 */
class InvalidMethodDoc {
    /*
     * Run method.
     */
    void method() {
    }
}
