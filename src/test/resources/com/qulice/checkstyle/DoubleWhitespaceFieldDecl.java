/*
 * Hello.
 */
package foo;

/**
 * Demonstrates a field declaration with multiple whitespace characters
 * between tokens, which must be rejected.
 * @since 1.0
 */
public final class DoubleWhitespaceFieldDecl {

    /**
     * Field with double spaces between the modifiers, the type
     * and the name.
     */
    private  final  String  name = "x";

    /**
     * Greet.
     * @return Greeting
     */
    public String greet() {
        return String.format("Hi %s", this.name);
    }
}
