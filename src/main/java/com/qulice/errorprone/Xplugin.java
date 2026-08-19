/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.google.errorprone.ErrorProneOptions;
import com.google.errorprone.scanner.BuiltInCheckerSuppliers;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The {@code -Xplugin} argument that wires ErrorProne into a forked
 * {@code javac}, together with the bug patterns it leaves out.
 *
 * <p>Qulice switches a few patterns off for every project it checks, and
 * the project appends {@code -Xep} flags of its own after them, so that a
 * bug pattern which contradicts the project's domain has somewhere to go
 * other than a {@code @SuppressWarnings} on every affected class. See
 * <a href="https://github.com/yegor256/qulice/issues/1734">#1734</a>.</p>
 *
 * @since 1.0
 */
public final class Xplugin {

    /**
     * The bug patterns Qulice switches off for every project it checks.
     *
     * <p>{@code InvalidBlockTag} is disabled because it rejects the
     * {@code @checkstyle} block tags this codebase writes in Javadoc.</p>
     *
     * <p>{@code OperatorPrecedence} is disabled because it contradicts
     * Checkstyle's {@code UnnecessaryParentheses}: a boolean expression
     * that mixes {@code &&} and {@code ||} can satisfy only one of them at
     * a time. {@code OperatorPrecedence} demands grouping parentheses
     * around the {@code &&} operands (for example
     * {@code (a && b) || (c && d)}), while {@code UnnecessaryParentheses}
     * flags those very parentheses as redundant, since {@code &&} already
     * binds tighter than {@code ||}. Keeping {@code UnnecessaryParentheses}
     * as the single arbiter lets the terse, paren-free form pass both
     * checks. See
     * <a href="https://github.com/yegor256/qulice/issues/1705">#1705</a>.</p>
     *
     * <p>{@code UnicodeInCode} is disabled because it judges the project's
     * domain instead of its quality: it rejects every non-ASCII character
     * outside comments and literals, so a project whose own notation is
     * not ASCII — such as
     * <a href="https://github.com/objectionary/eo">objectionary/eo</a>,
     * where {@code Phi.Φ} and {@code φTerm()} are public API — would have
     * to rename its API in order to pass.</p>
     */
    private static final List<String> DISABLED = List.of(
        "-Xep:InvalidBlockTag:OFF",
        "-Xep:OperatorPrecedence:OFF",
        "-Xep:UnicodeInCode:OFF"
    );

    /**
     * Splits the flags a project supplies, on commas or whitespace.
     */
    private static final Pattern SEPARATOR = Pattern.compile("[\\s,]+");

    /**
     * The flags of the project, as it wrote them.
     */
    private final String extra;

    /**
     * Constructor.
     * @param flags ErrorProne flags of the project, if any, separated by
     *  whitespace or commas
     */
    public Xplugin(final String flags) {
        this.extra = flags;
    }

    /**
     * Build the {@code javac} argument.
     *
     * <p>The flags of the project come last, so the project has the final
     * say on every pattern, the ones Qulice disables included: ErrorProne
     * reads the flags left to right and the last one naming a pattern
     * wins. A project can therefore both switch a pattern off, with
     * {@code -Xep:UnusedVariable:OFF}, and switch one back on, with
     * {@code -Xep:OperatorPrecedence:ERROR}.</p>
     *
     * @return The whole {@code -Xplugin:ErrorProne ...} argument
     */
    public String argument() {
        return String.format(
            "-Xplugin:ErrorProne %s", String.join(" ", this.flags())
        );
    }

    /**
     * How many bug patterns fire with these flags.
     *
     * <p>ErrorProne answers this itself: the count starts at the patterns
     * it enables by default and then the very flags the forked
     * {@code javac} receives are applied to it, so a pattern Qulice
     * switches off leaves the count and one the project switches back on
     * rejoins it.</p>
     *
     * @return The number of bug patterns that judge every file
     */
    public int patterns() {
        return BuiltInCheckerSuppliers.defaultChecks()
            .applyOverrides(ErrorProneOptions.processArgs(this.flags()))
            .getEnabledChecks()
            .size();
    }

    private List<String> flags() {
        final List<String> flags = new ArrayList<>(
            Xplugin.DISABLED.size() + 4
        );
        flags.addAll(Xplugin.DISABLED);
        flags.addAll(this.extras());
        return flags;
    }

    private List<String> extras() {
        return Xplugin.SEPARATOR.splitAsStream(this.extra.trim())
            .filter(flag -> !flag.isEmpty())
            .map(Xplugin::checked)
            .toList();
    }

    private static String checked(final String flag) {
        if (!flag.startsWith("-Xep")) {
            throw new IllegalArgumentException(
                String.format(
                    "Only ErrorProne flags belong here, all of them starting with '-Xep', while '%s' does not",
                    flag
                )
            );
        }
        return flag;
    }
}
