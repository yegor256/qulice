/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that method and constructor declarations do not span multiple
 * lines when the entire signature can fit on one line within the limit.
 *
 * <p>This is how a correct declaration looks like:
 *
 * <pre>
 * public Foo(final int max) throws IOException {
 *     ...
 * }
 * </pre>
 *
 * <p>And this is what will be reported:
 *
 * <pre>
 * public Foo(final int max)
 *     throws IOException {
 *     ...
 * }
 * </pre>
 *
 * <p>The reason is explained in <a
 * href="https://www.yegor256.com/2014/04/27/typical-mistakes-in-java-code.html#indentation">this
 * article</a>: one should put as much as possible on one line within the
 * configured limit (80 by default). See <a
 * href="https://github.com/yegor256/qulice/issues/647">#647</a>.
 *
 * @since 0.24
 */
public final class MethodDeclarationLengthCheck extends AbstractCheck {

    /**
     * Maximum allowed length of the joined declaration, in characters.
     */
    private int max = 80;

    /**
     * Configure the maximum allowed length.
     * @param value New value
     */
    public void setMax(final int value) {
        this.max = value;
    }

    @Override
    public int[] getDefaultTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF};
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST start = MethodDeclarationLengthCheck.head(ast);
        final DetailAST end = MethodDeclarationLengthCheck.tail(ast);
        if (start != null && end != null
            && start.getLineNo() < end.getLineNo()) {
            this.verify(start, end);
        }
    }

    /**
     * Checks whether the joined declaration fits on one line.
     * @param start First non-annotation token of the declaration
     * @param end Token that terminates the declaration (SLIST or SEMI)
     */
    private void verify(final DetailAST start, final DetailAST end) {
        final String[] lines = this.getLines();
        final int first = start.getLineNo();
        final int last = end.getLineNo();
        final int col = start.getColumnNo();
        final StringBuilder joined = new StringBuilder(
            lines[first - 1].substring(col).trim()
        );
        for (int idx = first; idx < last; idx += 1) {
            final String trimmed = lines[idx].trim();
            if (!trimmed.isEmpty()) {
                joined.append(' ').append(trimmed);
            }
        }
        if (col + joined.length() <= this.max) {
            this.log(
                first,
                "Method declaration can be placed on a single line"
            );
        }
    }

    /**
     * Returns the first child of the method/constructor that is not an
     * annotation (i.e., the first keyword or type of the signature).
     * @param def METHOD_DEF or CTOR_DEF node
     * @return First non-annotation child token, or null if absent
     */
    private static DetailAST head(final DetailAST def) {
        final DetailAST modifiers = def.findFirstToken(TokenTypes.MODIFIERS);
        DetailAST child = modifiers.getFirstChild();
        while (child != null && child.getType() == TokenTypes.ANNOTATION) {
            child = child.getNextSibling();
        }
        final DetailAST result;
        if (child == null) {
            DetailAST fallback = def.findFirstToken(TokenTypes.TYPE);
            if (fallback == null) {
                fallback = def.findFirstToken(TokenTypes.IDENT);
            }
            result = fallback;
        } else {
            result = child;
        }
        return result;
    }

    /**
     * Returns the token that closes the declaration: the opening brace
     * of the body, or the terminating semicolon for abstract methods.
     * @param def METHOD_DEF or CTOR_DEF node
     * @return SLIST or SEMI child, or null if neither is present
     */
    private static DetailAST tail(final DetailAST def) {
        DetailAST end = def.findFirstToken(TokenTypes.SLIST);
        if (end == null) {
            end = def.findFirstToken(TokenTypes.SEMI);
        }
        return end;
    }
}
