/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ConstantUsedBeforeDeclaration {

    /**
     * Public constant that uses the private one declared below.
     */
    public static final String OP_ONE = ConstantUsedBeforeDeclaration.OP_ZERO + "blah1";

    /**
     * Another public constant that uses the private one declared below.
     */
    public static final String OP_TWO = ConstantUsedBeforeDeclaration.OP_ZERO + "blah2";

    /**
     * Private constant, used only by constants declared before it.
     */
    private static final String OP_ZERO = "Something private";
}
