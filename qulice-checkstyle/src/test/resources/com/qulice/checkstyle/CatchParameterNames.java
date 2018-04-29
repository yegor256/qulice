/*
 * Hello.
 */
package foo;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Simple.
 * @since 1.0
 * @checkstyle HiddenField (100 lines)
 */
public final class CatchParameterNames {
    /**
     * Dummy variable.
     */
    private int var;
    /**
     * Invalid exception parameter name.
     */
    void invalidOne() {
        try {
            this.var += 1;
        } catch (final IOException ex_invalid_1) {
            this.var -= 1;
        } catch (final IllegalArgumentException $xxx) {
            this.var -= 1;
        } catch (final TimeoutException _exp) {
            this.var -= 1;
        }
    }

    /**
     * Valid exception parameter name.
     */
    void validOne() {
        try {
            this.var += 1;
        } catch (final IOException ex) {
            this.var -= 1;
        } catch (final IllegalArgumentException exp) {
            this.var -= 1;
        }
    }

}
