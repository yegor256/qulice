/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Checks that a private static constant is mentioned more than once.
 *
 * <p>A {@code private static final} field that the file mentions only
 * once is an indirection that pays for nothing: the reader has to jump
 * to the top of the class in order to learn what the single expression
 * below really says. Such a constant belongs inside its only usage,
 * since a constant starts to pay for itself at the second usage, where
 * it removes a duplicate.</p>
 *
 * <p>Usages are counted as identifiers of the very same text anywhere
 * in the file, the declaration of the constant included, which is why a
 * constant used exactly once is mentioned exactly twice. A name that
 * some other declaration happens to reuse is counted too, which only
 * makes the check more forgiving.</p>
 *
 * <p>Only a constant with a literal initializer is inspected. A
 * constant built by a method call or by {@code new}, like a compiled
 * {@link java.util.regex.Pattern}, stays where it is, since inlining it
 * would move a computation into a method body and could turn a
 * once-per-class cost into a per-call one. The same goes for an array,
 * which is a mutable object no matter how final its field is.</p>
 *
 * <p>A constant that a Javadoc comment refers to, the way {@code {@value
 * #NAME}} and {@code {@link #NAME}} do, is left alone as well, since
 * the documentation is a usage of its own and inlining the constant
 * would break it. So is {@code serialVersionUID}, which belongs to Java
 * serialization rather than to the class that declares it.</p>
 *
 * <p>A constant that is not mentioned anywhere at all belongs to
 * {@link ConstantUsageCheck}, which reports it as unused.</p>
 *
 * @since 0.73.4
 */
public final class SingleUseConstantCheck extends AbstractCheck {

    /**
     * Token types an inlineable initializer may be made of.
     */
    private static final Set<Integer> LITERALS = Set.of(
        TokenTypes.EXPR,
        TokenTypes.NUM_INT,
        TokenTypes.NUM_LONG,
        TokenTypes.NUM_FLOAT,
        TokenTypes.NUM_DOUBLE,
        TokenTypes.STRING_LITERAL,
        TokenTypes.CHAR_LITERAL,
        TokenTypes.TEXT_BLOCK_LITERAL_BEGIN,
        TokenTypes.TEXT_BLOCK_CONTENT,
        TokenTypes.TEXT_BLOCK_LITERAL_END,
        TokenTypes.LITERAL_TRUE,
        TokenTypes.LITERAL_FALSE,
        TokenTypes.LITERAL_NULL,
        TokenTypes.IDENT,
        TokenTypes.DOT,
        TokenTypes.LPAREN,
        TokenTypes.RPAREN,
        TokenTypes.UNARY_MINUS,
        TokenTypes.UNARY_PLUS,
        TokenTypes.PLUS,
        TokenTypes.MINUS,
        TokenTypes.STAR,
        TokenTypes.DIV,
        TokenTypes.MOD
    );

    /**
     * Default constructor.
     */
    public SingleUseConstantCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.VARIABLE_DEF};
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
        final DetailAST name = ast.findFirstToken(TokenTypes.IDENT);
        if (SingleUseConstantCheck.isInlineable(ast)
            && !this.documented(name.getText())
            && SingleUseConstantCheck.mentions(
                SingleUseConstantCheck.root(ast), name.getText()
            ) == 2) {
            this.log(
                name.getLineNo(),
                String.format(
                    "Private constant \"%s\" is used only once, inline it",
                    name.getText()
                )
            );
        }
    }

    private boolean documented(final String name) {
        final Pattern ref = Pattern.compile(
            String.format("#%s\\b", Pattern.quote(name))
        );
        boolean found = false;
        for (final String line : this.getLines()) {
            if (ref.matcher(line).find()) {
                found = true;
                break;
            }
        }
        return found;
    }

    private static boolean isInlineable(final DetailAST node) {
        final DetailAST assign = node.findFirstToken(TokenTypes.ASSIGN);
        return SingleUseConstantCheck.isConstant(node)
            && assign != null
            && SingleUseConstantCheck.isLiteral(assign.getFirstChild());
    }

    private static boolean isConstant(final DetailAST node) {
        return node.getParent().getType() == TokenTypes.OBJBLOCK
            && !"serialVersionUID".equals(
                node.findFirstToken(TokenTypes.IDENT).getText()
            )
            && SingleUseConstantCheck.isShared(node);
    }

    private static boolean isShared(final DetailAST node) {
        final DetailAST mods = node.findFirstToken(TokenTypes.MODIFIERS);
        return mods.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null
            && mods.findFirstToken(TokenTypes.LITERAL_STATIC) != null
            && mods.findFirstToken(TokenTypes.FINAL) != null;
    }

    private static boolean isLiteral(final DetailAST node) {
        boolean literal =
            SingleUseConstantCheck.LITERALS.contains(node.getType());
        DetailAST child = node.getFirstChild();
        while (literal && child != null) {
            literal = SingleUseConstantCheck.isLiteral(child);
            child = child.getNextSibling();
        }
        return literal;
    }

    private static int mentions(final DetailAST node, final String name) {
        int found = 0;
        if (node.getType() == TokenTypes.IDENT
            && node.getText().equals(name)) {
            found = 1;
        }
        DetailAST child = node.getFirstChild();
        while (child != null) {
            found += SingleUseConstantCheck.mentions(child, name);
            child = child.getNextSibling();
        }
        return found;
    }

    private static DetailAST root(final DetailAST node) {
        DetailAST top = node;
        while (top.getParent() != null) {
            top = top.getParent();
        }
        return top;
    }
}
