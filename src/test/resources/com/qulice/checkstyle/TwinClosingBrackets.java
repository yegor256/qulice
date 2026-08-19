/*
 * Hello.
 */
package foo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple.
 * @since 1.0
 */
public final class TwinClosingBrackets {

    /**
     * The globs.
     */
    private final List<String> globs;

    /**
     * The files.
     */
    private final List<String> files;

    /**
     * Ctor.
     * @param masks Globs
     * @param names Files
     */
    public TwinClosingBrackets(final List<String> masks,
        final List<String> names) {
        this.globs = masks;
        this.files = names;
    }

    /**
     * Filter them.
     * @return The files
     */
    public Collection<String> filtered() {
        return this.files.stream().filter(
            file -> this.globs.stream().anyMatch(
                glob -> file.contains(glob)
            )
            )
            .collect(Collectors.toList());
    }
}
