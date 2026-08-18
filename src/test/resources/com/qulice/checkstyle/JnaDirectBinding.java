/*
 * Hello.
 */
package foo;

/**
 * Binding to a native library, mapped directly.
 * @since 1.0
 */
public final class JnaDirectBinding implements com.sun.jna.Library {

    /**
     * Open a file.
     * @param name The name of the file
     * @param access The access mask
     * @param mode The sharing mode
     * @param attributes The attributes of the file
     * @return The handle of the file
     */
    public native int CreateFileW(String name, int access, int mode, int attributes);

    /**
     * The code of the last error.
     * @return The code
     */
    public native int GetLastError();
}
