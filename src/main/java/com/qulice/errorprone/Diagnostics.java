/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Violation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * What a forked {@code javac} printed, read as violations.
 *
 * <p>Every diagnostic counts, not just the ones ErrorProne labels with
 * its {@code [CheckName]} prefix: a plain {@code cannot find symbol} is
 * the loudest signal of all, because {@code javac} stops before the
 * analysis phase once it sees an error and ErrorProne then reports
 * nothing at all. Lines that carry no source position, such as
 * {@code error: warnings found and -Werror specified}, are blamed on the
 * project itself.</p>
 *
 * @since 1.0
 */
final class Diagnostics {

    /**
     * Standard {@code javac} diagnostic format,
     * {@code path:line: warning|error: body}, where the body optionally
     * opens with the {@code [Name]} prefix that ErrorProne puts on its
     * findings and {@code javac} puts on its lint categories.
     */
    private static final Pattern POSITIONED = Pattern.compile(
        "^(.+?):(\\d+): (?:warning|error): (?:\\[([A-Za-z][A-Za-z0-9_]*)] )?(.+)$"
    );

    /**
     * A {@code javac} diagnostic that names no source position.
     */
    private static final Pattern GLOBAL = Pattern.compile(
        "^(?:warning|error): (?:\\[([A-Za-z][A-Za-z0-9_]*)] )?(.+)$"
    );

    /**
     * Check name for a diagnostic that carries no bracketed name of its own.
     */
    private static final String JAVAC = "javac";

    /**
     * Name of the validator to attribute the violations to.
     */
    private final String validator;

    /**
     * File to blame for the diagnostics that name no source position.
     */
    private final String fallback;

    /**
     * Constructor.
     * @param validator Name of the validator reporting these diagnostics
     * @param fallback File to blame when a diagnostic names no position
     */
    Diagnostics(final String validator, final String fallback) {
        this.validator = validator;
        this.fallback = fallback;
    }

    /**
     * Read the diagnostics out of the compiler's output.
     * @param output Combined stdout/stderr of the forked process
     * @return Violations, one per diagnostic line
     */
    Collection<Violation> violations(final List<String> output) {
        final Collection<Violation> violations = new ArrayList<>(0);
        for (final String line : output) {
            final Matcher positioned = Diagnostics.POSITIONED.matcher(line);
            final Matcher global = Diagnostics.GLOBAL.matcher(line);
            if (positioned.matches()) {
                violations.add(
                    this.violation(
                        positioned.group(1),
                        positioned.group(2),
                        Diagnostics.check(positioned.group(3)),
                        positioned.group(4)
                    )
                );
            } else if (global.matches()) {
                violations.add(
                    this.violation(
                        this.fallback,
                        "0",
                        Diagnostics.check(global.group(1)),
                        global.group(2)
                    )
                );
            }
        }
        return violations;
    }

    /**
     * Turn one parsed diagnostic into a violation.
     * @param file Source file the compiler pointed at
     * @param lines Line number inside that file
     * @param check Name of the check or lint category
     * @param message Diagnostic body, without the bracketed name
     * @return The violation
     */
    private Violation violation(final String file, final String lines,
        final String check, final String message) {
        return new Violation.Default(
            this.validator,
            check,
            file,
            lines,
            String.format("[%s] %s", check, message)
        );
    }

    /**
     * Name of the check a diagnostic belongs to, falling back to
     * {@code javac} for the diagnostics the compiler leaves unlabelled.
     * @param name Bracketed name the diagnostic carried, or {@code null}
     * @return Name to report the violation under
     */
    private static String check(final String name) {
        return Optional.ofNullable(name).orElse(Diagnostics.JAVAC);
    }
}
