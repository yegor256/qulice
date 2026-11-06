/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
import java.util.logging.Logger;
public final class Valid {
    private final Logger logger = Logger.getLogger("cache");
    public Object cached() {
        return new Logged(
            new com.github.fabriciofx.cactoos.cache.base.Logged<>(
                this.logger
            ),
            "cache",
            this.logger
        );
    }
}
