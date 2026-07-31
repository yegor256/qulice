/*
 * Hello.
 */
package foo;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Simple.
 * @since 1.0
 */
public final class CatchParameterNames {

    /**
     * Dummy variable.
     */
    private int counter;

    /**
     * Invalid exception parameter name.
     */
    void invalidOne() {
        try {
            this.counter += 1;
        } catch (final IOException ex_invalid_1) {
            this.counter -= 1;
        } catch (final IllegalArgumentException $xxx) {
            this.counter -= 1;
        } catch (final TimeoutException _exp) {
            this.counter -= 1;
        }
    }

    /**
     * Valid exception parameter name.
     */
    void validOne() {
        try {
            this.counter += 1;
        } catch (final IOException ex) {
            this.counter -= 1;
        } catch (final IllegalArgumentException exp) {
            this.counter -= 1;
        }
    }
}
