/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that constant, declared as private field of class is used more than
 * once.
 *
 * @since 0.3
 */
public final class ConstantUsageCheck extends AbstractCheck {

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

    /**
     * Check that constant, declared as private field of class
     * is used more than ones.
     * @param ast Node which contains VARIABLE_DEF
     * @param namenode Node which contains variable name
     */
    private void checkField(final DetailAST ast, final DetailAST namenode) {
        final String name = namenode.getText();
        final int line = namenode.getLineNo();
        DetailAST variable = ast.getNextSibling();
        int counter = 0;
        while (null != variable) {
            switch (variable.getType()) {
                case TokenTypes.VARIABLE_DEF:
                    counter += this.parseVarDef(variable, name);
                    break;
                case TokenTypes.CLASS_DEF:
                    counter += this.parseDef(
                        variable, name, TokenTypes.OBJBLOCK
                    );
                    break;
                default:
                    counter += this.parseDef(variable, name, TokenTypes.SLIST);
                    break;
            }
            variable = variable.getNextSibling();
        }
        if (counter == 0 && ConstantUsageCheck.isPrivate(ast)) {
            this.log(
                line,
                String.format("Private constant \"%s\" is not used", name)
            );
        }
    }

    /**
     * Parses the variable definition and increments the counter
     * if name is found.
     * @param variable DetailAST of variable definition
     * @param name Name of constant we search for
     * @return Zero if not found, 1 otherwise
     */
    private int parseVarDef(final DetailAST variable, final String name) {
        int counter = 0;
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

    /**
     * Returns text representation of the specified node, including it's
     * children.
     * @param node Node, containing text.
     * @return Text representation of the node.
     */
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

    /**
     * Returns <code>true</code> if specified node has parent node of type
     * <code>OBJBLOCK</code>.
     * @param node Node to check.
     * @return True if parent node is <code>OBJBLOCK</code>, else
     *  returns <code>false</code>.
     */
    private static boolean isField(final DetailAST node) {
        final DetailAST parent = node.getParent();
        return TokenTypes.OBJBLOCK == parent.getType();
    }

    /**
     * Returns true if specified node has modifiers of type <code>FINAL</code>.
     * @param node Node to check.
     * @return True if specified node contains modifiers of type
     *  <code>FINAL</code>, else returns <code>false</code>.
     */
    private static boolean isFinal(final DetailAST node) {
        final DetailAST modifiers = node.findFirstToken(TokenTypes.MODIFIERS);
        return modifiers.getChildCount(TokenTypes.FINAL) > 0;
    }

    /**
     * Returns true if specified node has modifiers of type
     * <code>PRIVATE</code>.
     * @param node Node to check.
     * @return True if specified node contains modifiers of type
     *  <code>PRIVATE</code>, else returns <code>false</code>.
     */
    private static boolean isPrivate(final DetailAST node) {
        final DetailAST modifiers = node.findFirstToken(TokenTypes.MODIFIERS);
        return modifiers.getChildCount(TokenTypes.LITERAL_PRIVATE) > 0;
    }

    /**
     * Parses the body of the definition (either method or inner class) and
     * increments counter each time when it founds constant name.
     * @param definition Tree node, containing definition.
     * @param name Constant name to search.
     * @param type Type of definition start.
     * @return Number of found constant usages.
     */
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

    /**
     * Parses the annotation value pair and increments the counter
     * if name is found.
     * @param modifiers DetailAST of variable definition
     * @param name Name of constant we search for
     * @return Zero if not found, 1 otherwise
     */
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
