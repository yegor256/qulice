/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Optional;

/**
 * Checks that String.split is only invoked with regex arguments that the JDK
 * handles via its fastpath.
 *
 * <p>For anything beyond the fastpath, String.split builds a fresh Pattern
 * on every call, which is wasteful in tight loops. Extract the regex into a
 * private static final Pattern field and use Pattern.split(CharSequence)
 * instead.
 *
 * <p>The JDK fastpath accepts only a one-char string whose sole character is
 * not a regex meta character, or a two-char string whose first character is
 * a backslash and whose second character is not an ASCII letter or digit.
 *
 * <p>Examples that are flagged:
 *
 * <pre>
 * "abxxdexxzy".split("xx");
 * "abxxdexxzy".split("xx", 1);
 * "abxxdexxzy".split(".");
 * </pre>
 *
 * <p>Examples that are accepted:
 *
 * <pre>
 * "abxdexzy".split("x");
 * "abxdexzy".split("x", 2);
 * "abxdexzy".split("\n");
 * "ab.ex.zy".split("\\.");
 * </pre>
 *
 * <p>The check only reports calls whose first argument is a string literal:
 * when the regex is a variable the optimization cannot be determined from
 * the AST alone.
 *
 * @since 0.24
 */
public final class SimpleStringSplitCheck extends AbstractCheck {

    /**
     * Regex meta characters the JDK fastpath refuses for a one-char pattern.
     */
    private static final String META = ".$|()[{^?*+\\";

    /**
     * Default constructor.
     */
    public SimpleStringSplitCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.METHOD_CALL};
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
        final Optional<DetailAST> literal =
            SimpleStringSplitCheck.regexLiteral(ast);
        final Optional<String> regex = literal.flatMap(
            node -> SimpleStringSplitCheck.decode(node.getText())
        );
        if (regex.isPresent()
            && !SimpleStringSplitCheck.optimized(regex.get())) {
            this.log(
                literal.get(),
                "String.split regex is not JDK-optimized, use a precompiled java.util.regex.Pattern"
            );
        }
    }

    private static Optional<DetailAST> regexLiteral(final DetailAST call) {
        Optional<DetailAST> result = Optional.empty();
        if (SimpleStringSplitCheck.isSplitCall(call)) {
            result = SimpleStringSplitCheck.firstLiteralArg(call);
        }
        return result;
    }

    private static boolean isSplitCall(final DetailAST call) {
        final DetailAST dot = call.getFirstChild();
        final boolean result;
        if (dot == null || dot.getType() != TokenTypes.DOT) {
            result = false;
        } else {
            final DetailAST name = dot.getLastChild();
            result = name != null && name.getType() == TokenTypes.IDENT
                && "split".equals(name.getText());
        }
        return result;
    }

    private static Optional<DetailAST> firstLiteralArg(final DetailAST call) {
        final DetailAST elist = call.findFirstToken(TokenTypes.ELIST);
        Optional<DetailAST> result = Optional.empty();
        if (elist != null && SimpleStringSplitCheck.isOneOrTwoArgs(elist)) {
            final DetailAST expr = elist.findFirstToken(TokenTypes.EXPR);
            if (expr != null && SimpleStringSplitCheck.isLoneStringLiteral(expr)) {
                result = Optional.of(expr.getFirstChild());
            }
        }
        return result;
    }

    private static boolean isOneOrTwoArgs(final DetailAST elist) {
        final int args = elist.getChildCount(TokenTypes.EXPR);
        return args == 1 || args == 2;
    }

    private static boolean isLoneStringLiteral(final DetailAST expr) {
        return expr.getChildCount() == 1
            && expr.getFirstChild().getType() == TokenTypes.STRING_LITERAL;
    }

    private static boolean optimized(final String regex) {
        final boolean result;
        final int len = regex.length();
        if (len == 1) {
            result = SimpleStringSplitCheck.META.indexOf(regex.charAt(0)) < 0;
        } else if (len == 2 && regex.charAt(0) == '\\') {
            result = !SimpleStringSplitCheck.isAsciiAlphanumeric(regex.charAt(1));
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isAsciiAlphanumeric(final char chr) {
        return SimpleStringSplitCheck.isAsciiDigit(chr)
            || SimpleStringSplitCheck.isAsciiLetter(chr);
    }

    private static boolean isAsciiDigit(final char chr) {
        return chr >= '0' && chr <= '9';
    }

    private static boolean isAsciiLetter(final char chr) {
        return chr >= 'a' && chr <= 'z'
            || chr >= 'A' && chr <= 'Z';
    }

    private static Optional<String> decode(final String text) {
        final String body = text.substring(1, text.length() - 1);
        final StringBuilder out = new StringBuilder(body.length());
        int idx = 0;
        boolean failed = false;
        while (idx < body.length() && !failed) {
            final int advance = SimpleStringSplitCheck.step(body, idx, out);
            if (advance < 0) {
                failed = true;
            } else {
                idx += advance;
            }
        }
        final Optional<String> result;
        if (failed) {
            result = Optional.empty();
        } else {
            result = Optional.of(out.toString());
        }
        return result;
    }

    private static int step(
        final String body, final int idx, final StringBuilder out
    ) {
        final char chr = body.charAt(idx);
        final int advance;
        if (chr == '\\') {
            advance = SimpleStringSplitCheck.handleEscape(body, idx, out);
        } else {
            out.append(chr);
            advance = 1;
        }
        return advance;
    }

    private static int handleEscape(
        final String body, final int idx, final StringBuilder out
    ) {
        final int advance;
        if (idx + 1 >= body.length()) {
            advance = -1;
        } else {
            final int decoded = SimpleStringSplitCheck.escape(
                body.charAt(idx + 1)
            );
            if (decoded < 0) {
                advance = -1;
            } else {
                out.append((char) decoded);
                advance = 2;
            }
        }
        return advance;
    }

    private static int escape(final char chr) {
        return switch (chr) {
            case 'n' -> '\n';
            case 't' -> '\t';
            case 'r' -> '\r';
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 's' -> ' ';
            case '\'' -> '\'';
            case '"' -> '"';
            case '\\' -> '\\';
            default -> -1;
        };
    }
}
