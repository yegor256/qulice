/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The scope of a finished check, as one line of text.
 *
 * <p>Tells the reader what the run has just covered: how many Java files
 * went through the validators and how many rules judged them, validator
 * by validator. A validator that cannot count its rules stays out of the
 * breakdown, instead of claiming zero, and a module that had no file to
 * give them counts no rules at all.</p>
 *
 * @since 1.0
 */
final class Summary {

    /**
     * Files handed to the validators.
     */
    private final Collection<File> files;

    /**
     * Validators that read them.
     */
    private final Collection<ResourceValidator> validators;

    /**
     * Constructor.
     * @param files Files handed to the validators
     * @param validators Validators that read them
     */
    Summary(final Collection<File> files,
        final Collection<ResourceValidator> validators) {
        this.files = files;
        this.validators = validators;
    }

    @Override
    public String toString() {
        final Map<String, Integer> counts = this.counts();
        return String.format(
            "checked %s against %d rules%s",
            this.javas(),
            counts.values().stream().mapToInt(Integer::intValue).sum(),
            Summary.breakdown(counts)
        );
    }

    /**
     * How many rules each validator applies, by validator name, leaving
     * out the ones that count none.
     *
     * <p>A module without a single file to read runs no validator at all,
     * so nobody is asked to count: the counting itself is expensive, since
     * it makes Checkstyle load its configuration and PMD resolve its
     * ruleset.</p>
     *
     * @return Rule counts, in the order the validators ran
     */
    private Map<String, Integer> counts() {
        final Map<String, Integer> counts =
            new LinkedHashMap<>(this.validators.size());
        if (!this.files.isEmpty()) {
            for (final ResourceValidator validator : this.validators) {
                final int rules = validator.rules();
                if (rules > 0) {
                    counts.put(validator.name(), rules);
                }
            }
        }
        return counts;
    }

    /**
     * The Java files among them, counted and named.
     * @return Text, e.g. {@code "42 .java files"}
     */
    private String javas() {
        final long count = this.files.stream().filter(
            file -> file.getName().toLowerCase(Locale.ROOT).endsWith(".java")
        ).count();
        final String text;
        if (count == 1L) {
            text = "1 .java file";
        } else {
            text = String.format("%d .java files", count);
        }
        return text;
    }

    /**
     * Spell the rule counts out, validator by validator.
     * @param counts Rule counts by validator name
     * @return Text in brackets, e.g. {@code " (253 Checkstyle, 132 PMD)"},
     *  or empty when nobody counted anything
     */
    private static String breakdown(final Map<String, Integer> counts) {
        final String text;
        if (counts.isEmpty()) {
            text = "";
        } else {
            text = counts.entrySet().stream().map(
                entry -> String.format("%d %s", entry.getValue(), entry.getKey())
            ).collect(Collectors.joining(", ", " (", ")"));
        }
        return text;
    }
}
