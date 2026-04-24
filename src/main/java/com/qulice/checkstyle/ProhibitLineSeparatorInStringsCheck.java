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
 * Prohibits hard-coded line separator escape sequences inside string literals.
 *
 * <p>The following constructs are prohibited, because {@code \n} and
 * {@code \r} are OS dependent line separators:
 *
 * <pre>
 * String a = "first\nsecond";
 * System.out.println("line\r\n");
 * </pre>
 *
 * <p>These strings should be rewritten using
 * {@link System#lineSeparator()} or {@link String#format(String, Object[])}
 * with the {@code %n} directive, for example:
 *
 * <pre>
 * String a = "first" + System.lineSeparator() + "second";
 * System.out.println(String.format("line%n"));
 * </pre>
 *
 * @since 0.24
 */
public final class ProhibitLineSeparatorInStringsCheck extends AbstractCheck {

    /**
     * Matches one or more backslashes followed by {@code n} or {@code r};
     * only runs with an odd backslash count denote an actual escape sequence.
     */
    private static final Pattern ESCAPE = Pattern.compile("\\\\+[rn]");

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.STRING_LITERAL};
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
        if (ProhibitLineSeparatorInStringsCheck.hasLineSeparator(ast.getText())) {
            this.log(
                ast,
                "OS-dependent line separator in string literal, use System.lineSeparator() or String.format(\"%n\")"
            );
        }
    }

    /**
     * Checks if the literal source text contains an unescaped
     * {@code \n} or {@code \r} escape sequence.
     * @param text Raw source text of the string literal, including quotes
     *  (e.g. {@code "\n"} or {@code "a\\nb"})
     * @return True if the literal embeds a line separator escape
     */
    private static boolean hasLineSeparator(final String text) {
        final Matcher matcher =
            ProhibitLineSeparatorInStringsCheck.ESCAPE.matcher(text);
        boolean found = false;
        while (matcher.find()) {
            if ((matcher.end() - matcher.start() - 1) % 2 == 1) {
                found = true;
                break;
            }
        }
        return found;
    }
}
