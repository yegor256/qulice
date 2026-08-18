/*
 * Hello.
 */
package foo;

import com.sun.jna.Library;

/**
 * Binding to a native library, mapped as an interface.
 * @since 1.0
 */
public interface JnaMappedLibrary extends Library {

    /**
     * Open a file.
     * @param name The name of the file
     * @param access The access mask
     * @param mode The sharing mode
     * @param attributes The attributes of the file
     * @return The handle of the file
     */
    int CreateFileW(String name, int access, int mode, int attributes);

    /**
     * The code of the last error.
     * @return The code
     */
    int GetLastError();
}
