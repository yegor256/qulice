/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Detects unnecessary semicolons placed after the closing brace
 * of a class, interface, record, method or constructor declaration.
 *
 * <p>Such semicolons form empty declarations that carry no meaning and
 * clutter the code, for example:
 *
 * <pre>
 * class Semicolons {
 *     Semicolons() {
 *     };
 *     void act() {
 *     };
 * };
 * </pre>
 *
 * <p>This check flags every {@code SEMI} token that appears as a
 * direct child of an {@code OBJBLOCK} (a class, interface or record
 * body) or of the root {@code COMPILATION_UNIT}. Enum bodies are
 * excluded because the {@code ;} separator between enum constants and
 * member declarations is mandated by the Java grammar.
 *
 * @since 0.24
 */
public final class ExtraSemicolonCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.OBJBLOCK,
            TokenTypes.COMPILATION_UNIT,
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
        if (ast.getType() == TokenTypes.COMPILATION_UNIT
            || ast.getParent() == null
            || ast.getParent().getType() != TokenTypes.ENUM_DEF) {
            this.reportSemis(ast);
        }
    }

    /**
     * Report every {@code SEMI} token that is a direct child of the node.
     * @param node The parent node whose children are inspected
     */
    private void reportSemis(final DetailAST node) {
        for (DetailAST child = node.getFirstChild(); child != null;
            child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.SEMI) {
                this.log(
                    child.getLineNo(),
                    child.getColumnNo(),
                    "Unnecessary semicolon"
                );
            }
        }
    }
}
