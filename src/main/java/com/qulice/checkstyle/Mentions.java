/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Violation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What the types of a package mention, and which of them nobody mentions.
 *
 * <p>The types of a project arrive here one file at a time, since the
 * question of {@link UnusedPackagePrivateClasses} spans the whole project
 * while an abstract syntax tree costs almost a hundred times more memory
 * than the file it comes from. Only names are kept: the name a type
 * mentions, together with the package where it mentions it, and the
 * violation to report for a type that may turn out to be unused.</p>
 *
 * <p>A type mentioning its own name is not recorded, so a class that
 * only refers to itself, through a recursive factory or a constant of
 * its own type, stays a suspect: such a reference keeps nothing
 * alive.</p>
 *
 * @since 1.0
 */
final class Mentions {

    /**
     * The names mentioned by a type other than the one they name, each
     * of them prefixed with the package where the mention was seen.
     */
    private final Collection<String> named;

    /**
     * The violation to report for each type that may be unused, keyed
     * the same way, by package and name.
     */
    private final Map<String, Violation> suspects;

    /**
     * Ctor.
     */
    Mentions() {
        this.named = new HashSet<>(0);
        this.suspects = new LinkedHashMap<>(0);
    }

    /**
     * Remember the names this type mentions.
     *
     * @param type The type, of any visibility
     */
    void add(final TopLevelType type) {
        final String name = type.name();
        for (final String ident : type.mentioned()) {
            if (!ident.equals(name)) {
                this.named.add(Mentions.key(type.pack(), ident));
            }
        }
    }

    /**
     * Remember that this type will be unused, unless somebody mentions it.
     *
     * @param type The type, package-private and not a test
     */
    void suspect(final TopLevelType type) {
        this.suspects.put(
            Mentions.key(type.pack(), type.name()),
            new Violation.Default(
                "Checkstyle",
                "UnusedPackagePrivateClassCheck",
                type.file().getPath(),
                String.valueOf(type.line()),
                String.format(
                    "This package-private class \"%s\" is not used anywhere in its package, delete it or make it public",
                    type.name()
                )
            )
        );
    }

    /**
     * The violations of the suspects that no type of their package mentions.
     *
     * @return Violations, one per unused class
     */
    Collection<Violation> unused() {
        final Collection<Violation> found = new ArrayList<>(0);
        for (final Map.Entry<String, Violation> entry : this.suspects.entrySet()) {
            if (!this.named.contains(entry.getKey())) {
                found.add(entry.getValue());
            }
        }
        return found;
    }

    private static String key(final String pack, final String name) {
        return String.format("%s:%s", pack, name);
    }
}
