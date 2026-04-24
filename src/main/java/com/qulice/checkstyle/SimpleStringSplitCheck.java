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

    /**
     * Returns the STRING_LITERAL node that is the first argument of a
     * split call with one or two arguments, if any.
     * @param call METHOD_CALL AST node
     * @return Node of the literal regex, or empty when the call is not a
     *  split with a literal first argument
     */
    private static Optional<DetailAST> regexLiteral(final DetailAST call) {
        Optional<DetailAST> result = Optional.empty();
        if (SimpleStringSplitCheck.isSplitCall(call)) {
            result = SimpleStringSplitCheck.firstLiteralArg(call);
        }
        return result;
    }

    /**
     * Tells whether the method call invokes a method named "split".
     * @param call METHOD_CALL AST node
     * @return True when the call is of the form receiver.split(...)
     */
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

    /**
     * Finds the first STRING_LITERAL argument when the call has one or two
     * arguments.
     * @param call METHOD_CALL AST node
     * @return STRING_LITERAL node, or empty when the shape does not match
     */
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

    /**
     * Tells whether the given ELIST holds one or two EXPR children.
     * @param elist ELIST AST node
     * @return True if ELIST has 1 or 2 EXPR children
     */
    private static boolean isOneOrTwoArgs(final DetailAST elist) {
        final int args = elist.getChildCount(TokenTypes.EXPR);
        return args == 1 || args == 2;
    }

    /**
     * Tells whether the given EXPR is a lone STRING_LITERAL.
     * @param expr EXPR AST node
     * @return True if EXPR has a single STRING_LITERAL child
     */
    private static boolean isLoneStringLiteral(final DetailAST expr) {
        return expr.getChildCount() == 1
            && expr.getFirstChild().getType() == TokenTypes.STRING_LITERAL;
    }

    /**
     * Tells whether the given regex string would hit the JDK fastpath.
     * @param regex Runtime regex string
     * @return True if JDK fastpath applies
     */
    private static boolean optimized(final String regex) {
        final boolean result;
        if (regex.length() == 1) {
            result = SimpleStringSplitCheck.META.indexOf(regex.charAt(0)) < 0;
        } else if (regex.length() == 2 && regex.charAt(0) == '\\') {
            result = !SimpleStringSplitCheck.isAsciiAlphanumeric(regex.charAt(1));
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Tells whether the given character is an ASCII letter or digit.
     * @param chr Character to test
     * @return True if ASCII letter or digit
     */
    private static boolean isAsciiAlphanumeric(final char chr) {
        return SimpleStringSplitCheck.isAsciiDigit(chr)
            || SimpleStringSplitCheck.isAsciiLetter(chr);
    }

    /**
     * Tells whether the given character is an ASCII digit 0-9.
     * @param chr Character to test
     * @return True if ASCII digit
     */
    private static boolean isAsciiDigit(final char chr) {
        return chr >= '0' && chr <= '9';
    }

    /**
     * Tells whether the given character is an ASCII letter a-z or A-Z.
     * @param chr Character to test
     * @return True if ASCII letter
     */
    private static boolean isAsciiLetter(final char chr) {
        return chr >= 'a' && chr <= 'z'
            || chr >= 'A' && chr <= 'Z';
    }

    /**
     * Decodes a Java string literal (with surrounding double quotes) to the
     * runtime string it denotes. Returns empty when the literal contains
     * escape sequences the check does not understand, so the check can stay
     * silent rather than guess.
     * @param text Raw token text including the surrounding double quotes
     * @return Decoded runtime string, or empty on unsupported escapes
     */
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

    /**
     * Advances one step through the string literal body.
     * @param body Literal body without surrounding quotes
     * @param idx Current index
     * @param out Receiver for the decoded char
     * @return Number of source chars consumed, or -1 on unsupported escape
     */
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

    /**
     * Handles a backslash escape starting at {@code idx} in {@code body}.
     * @param body Literal body without surrounding quotes
     * @param idx Index of the backslash
     * @param out Receiver for the decoded char
     * @return Number of source chars consumed, or -1 on unsupported escape
     */
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

    /**
     * Translates a single Java escape letter to its runtime character.
     * @param chr Character after the backslash
     * @return Runtime character code, or -1 for unsupported escapes
     */
    private static int escape(final char chr) {
        final int result;
        switch (chr) {
            case 'n':
                result = '\n';
                break;
            case 't':
                result = '\t';
                break;
            case 'r':
                result = '\r';
                break;
            case 'b':
                result = '\b';
                break;
            case 'f':
                result = '\f';
                break;
            case 's':
                result = ' ';
                break;
            case '\'':
                result = '\'';
                break;
            case '"':
                result = '"';
                break;
            case '\\':
                result = '\\';
                break;
            default:
                result = -1;
                break;
        }
        return result;
    }
}
