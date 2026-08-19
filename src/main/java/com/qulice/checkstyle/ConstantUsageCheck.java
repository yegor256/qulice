/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that constant, declared as private field of class is used more than
 * once.
 * @since 0.3
 */
public final class ConstantUsageCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public ConstantUsageCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.VARIABLE_DEF,
        };
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
        if (ConstantUsageCheck.isField(ast)
            && ConstantUsageCheck.isFinal(ast)) {
            final DetailAST namenode = ast.findFirstToken(TokenTypes.IDENT);
            if (!"serialVersionUID".equals(this.getText(namenode))) {
                this.checkField(ast, namenode);
            }
        }
    }

    private void checkField(final DetailAST ast, final DetailAST namenode) {
        final String name = namenode.getText();
        final DetailAST objblock = ast.getParent();
        int counter = 0;
        final DetailAST classdef = objblock.getParent();
        if (classdef != null) {
            final DetailAST mods =
                classdef.findFirstToken(TokenTypes.MODIFIERS);
            if (mods != null) {
                counter += this.parseAnnotation(mods, name);
            }
        }
        DetailAST variable = objblock.getFirstChild();
        while (null != variable) {
            if (!variable.equals(ast)) {
                counter += switch (variable.getType()) {
                    case TokenTypes.VARIABLE_DEF -> this.parseVarDef(variable, name);
                    case TokenTypes.CLASS_DEF -> this.parseDef(
                        variable, name, TokenTypes.OBJBLOCK
                    );
                    default -> this.parseDef(variable, name, TokenTypes.SLIST);
                };
            }
            variable = variable.getNextSibling();
        }
        if (counter == 0 && ConstantUsageCheck.isPrivate(ast)) {
            this.log(
                namenode.getLineNo(),
                String.format("Private constant \"%s\" is not used", name)
            );
        }
    }

    private int parseVarDef(final DetailAST variable, final String name) {
        int counter = 0;
        final DetailAST modifiers =
            variable.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            counter += this.parseAnnotation(modifiers, name);
        }
        final DetailAST assign =
            variable.findFirstToken(TokenTypes.ASSIGN);
        if (assign != null) {
            DetailAST expression =
                assign.findFirstToken(TokenTypes.EXPR);
            if (expression == null) {
                expression = assign.findFirstToken(
                    TokenTypes.ARRAY_INIT
                );
            }
            final String text = this.getText(expression);
            if (text.contains(name)) {
                ++counter;
            }
        }
        return counter;
    }

    private String getText(final DetailAST node) {
        final String ret;
        if (node == null) {
            ret = "";
        } else if (0 == node.getChildCount()) {
            ret = node.getText();
        } else {
            final StringBuilder result = new StringBuilder();
            DetailAST child = node.getFirstChild();
            while (null != child) {
                final String text = this.getText(child);
                result.append(text);
                if (".".equals(node.getText())
                    && child.getNextSibling() != null) {
                    result.append(node.getText());
                }
                child = child.getNextSibling();
            }
            ret = result.toString();
        }
        return ret;
    }

    private static boolean isField(final DetailAST node) {
        return TokenTypes.OBJBLOCK == node.getParent().getType();
    }

    private static boolean isFinal(final DetailAST node) {
        return node.findFirstToken(TokenTypes.MODIFIERS)
            .getChildCount(TokenTypes.FINAL) > 0;
    }

    private static boolean isPrivate(final DetailAST node) {
        return node.findFirstToken(TokenTypes.MODIFIERS)
            .getChildCount(TokenTypes.LITERAL_PRIVATE) > 0;
    }

    private int parseDef(final DetailAST definition, final String name,
        final int type) {
        int counter = 0;
        final DetailAST modifiers =
            definition.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            counter += this.parseAnnotation(modifiers, name);
        }
        final DetailAST opening = definition.findFirstToken(type);
        if (null != opening) {
            final DetailAST closing = opening.findFirstToken(TokenTypes.RCURLY);
            final int start = opening.getLineNo();
            final int end = closing.getLineNo() - 1;
            final String[] lines = this.getLines();
            for (int pos = start; pos < end; pos += 1) {
                if (lines[pos].contains(name)) {
                    counter += 1;
                }
            }
        }
        return counter;
    }

    private int parseAnnotation(final DetailAST modifiers, final String name) {
        int counter = 0;
        final DetailAST variable =
            modifiers.findFirstToken(TokenTypes.ANNOTATION);
        if (variable != null) {
            final String txt = this.getText(variable);
            if (txt.contains(name)) {
                ++counter;
            }
        }
        return counter;
    }
}
