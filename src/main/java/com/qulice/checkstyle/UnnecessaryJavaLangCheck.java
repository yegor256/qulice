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
 * Prohibits the redundant {@code java.lang.} prefix in front of a simple
 * class name.
 *
 * <p>Classes from the {@code java.lang} package are imported implicitly,
 * so qualifying them with their package name adds nothing. A field whose
 * type is written with the {@code java.lang.} prefix should instead be
 * declared as {@code private final String name}, in both code and Javadoc.</p>
 *
 * <p>Unlike a plain line-based regular expression, this check ignores the
 * prefix when it appears inside a string literal or a text block. A string
 * constant that happens to contain the {@code java.lang.} substring is data,
 * not a type reference, and must not be reported. See
 * <a href="https://github.com/yegor256/qulice/issues/1703">#1703</a>.</p>
 *
 * @since 0.24
 */
public final class UnnecessaryJavaLangCheck extends AbstractCheck {

    /**
     * Matches the redundant prefix in front of a class name (a capital
     * letter). Sub-packages such as {@code java.lang.reflect} start with a
     * lower-case letter and are intentionally left alone.
     */
    private static final Pattern PREFIX =
        Pattern.compile("\\bjava\\.lang\\.[A-Z]");

    /**
     * Mutable copy of the file lines, with the contents of string literals
     * and text blocks blanked out so the pattern cannot match inside them.
     */
    private char[][] lines;

    /**
     * Default constructor.
     */
    public UnnecessaryJavaLangCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.STRING_LITERAL,
            TokenTypes.TEXT_BLOCK_CONTENT,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public void beginTree(final DetailAST root) {
        final String[] source = this.getLines();
        this.lines = new char[source.length][];
        for (int pos = 0; pos < source.length; ++pos) {
            this.lines[pos] = source[pos].toCharArray();
        }
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.STRING_LITERAL) {
            this.blank(
                ast.getLineNo(), ast.getColumnNo(), ast.getText().length()
            );
        } else {
            this.blankBlock(ast);
        }
    }

    @Override
    public void finishTree(final DetailAST root) {
        for (int pos = 0; pos < this.lines.length; ++pos) {
            final Matcher matcher =
                UnnecessaryJavaLangCheck.PREFIX.matcher(
                    String.valueOf(this.lines[pos])
                );
            while (matcher.find()) {
                this.log(
                    pos + 1,
                    "Unnecessary java.lang. prefix, use the simple class name"
                );
            }
        }
    }

    private void blankBlock(final DetailAST ast) {
        final String text = ast.getText();
        int span = 0;
        for (int pos = 0; pos < text.length(); ++pos) {
            if (text.charAt(pos) == '\n') {
                ++span;
            }
        }
        final int first = ast.getLineNo();
        for (int line = first; line <= first + span; ++line) {
            final int index = line - 1;
            if (index >= 0 && index < this.lines.length) {
                this.blank(line, 0, this.lines[index].length);
            }
        }
    }

    private void blank(final int line, final int column, final int length) {
        final int index = line - 1;
        if (index >= 0 && index < this.lines.length) {
            final char[] chars = this.lines[index];
            final int from = Math.max(0, Math.min(column, chars.length));
            final int upto = Math.min(column + length, chars.length);
            for (int pos = from; pos < upto; ++pos) {
                chars[pos] = ' ';
            }
        }
    }
}
