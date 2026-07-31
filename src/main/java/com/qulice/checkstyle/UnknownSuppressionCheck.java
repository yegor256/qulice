/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks that every {@code @checkstyle} suppression names an enabled check.
 *
 * <p>A comment like {@code @checkstyle LineLength (N lines)} tells the
 * suppression filters to ignore violations of {@code LineLength} nearby.
 * When the name belongs to no enabled check, because it is misspelled or
 * because the check has left {@code checks.xml} since, the comment
 * suppresses nothing and stays in the source as a lie about the code.
 * PMD reports the same mistake in {@code @SuppressWarnings} annotations.
 * The other half, a suppression that names an enabled check but covers no
 * violation of it, is reported by {@link UnusedSuppressions}.
 *
 * @since 1.0
 */
public final class UnknownSuppressionCheck extends AbstractCheck {

    /**
     * The forms of suppression that the filters of {@code checks.xml} honor.
     */
    private static final Pattern TAG = Pattern.compile(
        "@checkstyle (\\w+) (?:\\(\\d+ lines?\\)|disable|enable)"
    );

    /**
     * Checks that {@code checks.xml} enables.
     */
    private final ConfiguredChecks checks = new ConfiguredChecks();

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.COMMENT_CONTENT};
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final String text = ast.getText();
        final Matcher matcher = UnknownSuppressionCheck.TAG.matcher(text);
        while (matcher.find()) {
            final String name = matcher.group(1);
            if (!this.checks.covers(name)) {
                this.log(
                    ast.getLineNo()
                        + UnknownSuppressionCheck.breaks(text, matcher.start()),
                    String.format(
                        "Check \"%s\" is not enabled, this suppression has no effect",
                        name
                    )
                );
            }
        }
    }

    /**
     * How many lines of the comment precede this position?
     * @param text Text of the comment
     * @param position Position of the suppression inside the text
     * @return Number of line breaks before the position
     */
    private static int breaks(final String text, final int position) {
        int count = 0;
        for (int idx = 0; idx < position; ++idx) {
            if (text.charAt(idx) == '\n') {
                ++count;
            }
        }
        return count;
    }
}
