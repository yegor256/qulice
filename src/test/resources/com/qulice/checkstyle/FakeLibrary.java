/*
 * Hello.
 */
package foo;

/**
 * Extends a type named Library that comes from nowhere near JNA.
 * @since 1.0
 */
public interface FakeLibrary extends Library {

    /**
     * The code of the last error.
     * @return The code
     */
    int GetLastError();
}
