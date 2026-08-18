/*
 * Hello.
 */
package foo;

/**
 * Suppresses the method name check by its short name.
 * @since 1.0
 */
public interface SuppressedMethodName {

    /**
     * The code of the last error.
     * @return The code
     * @checkstyle MethodName (2 lines)
     */
    int GetLastError();
}
