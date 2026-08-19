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
public final class NestedClosingBrackets {

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
    public NestedClosingBrackets(final List<String> masks,
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
        ).collect(Collectors.toList());
    }

    /**
     * Join them.
     * @return The text
     */
    public String joined() {
        return String.join(
            ",",
            this.files.stream().map(
                file -> file.trim()
            ).collect(Collectors.toList())
        );
    }
}
