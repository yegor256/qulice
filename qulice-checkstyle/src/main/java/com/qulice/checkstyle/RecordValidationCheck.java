/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks for proper record declarations.
 * Validates that:
 * 1. Records are properly declared with components
 * 2. Record components are properly formatted
 * 3. Records do not extend other classes
 * 4. Records are final
 * @since 0.24
 */
public final class RecordValidationCheck extends AbstractCheck {
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "record.validation";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.RECORD_DEF,
            TokenTypes.RECORD_COMPONENT_DEF,
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
        switch (ast.getType()) {
            case TokenTypes.RECORD_DEF:
                this.checkRecordDeclaration(ast);
                break;
            case TokenTypes.RECORD_COMPONENT_DEF:
                this.checkRecordComponent(ast);
                break;
            default:
        }
    }

    /**
     * Check if record extends another class.
     * Check if record is final.
     * Check if record has components.
     * @param ast EXPR RECORD_DEF node that needs to be checked
     */
    private void checkRecordDeclaration(final DetailAST ast) {
        if (ast.findFirstToken(TokenTypes.EXTENDS_CLAUSE) != null) {
            this.log(ast.getLineNo(), ast.getColumnNo(), "Records cannot extend other classes");
        }
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null && modifiers.findFirstToken(TokenTypes.FINAL) == null) {
            this.log(modifiers.getLineNo(), modifiers.getColumnNo(), "Records must be final");
        }
        final DetailAST components = ast.findFirstToken(TokenTypes.RECORD_COMPONENTS);
        if (components != null
            && components.findFirstToken(TokenTypes.RECORD_COMPONENT_DEF) == null) {
            this.log(
                components.getLineNo(),
                components.getColumnNo(),
                "Records must declare at least one component"
            );
        }
        this.checkRecordInstanceFields(ast);
    }

    /**
     * Check record does not contain instance field.
     * @param ast EXPR RECORD_DEF node that needs to be checked
     */
    private void checkRecordInstanceFields(final DetailAST ast) {
        final DetailAST block = ast.findFirstToken(TokenTypes.OBJBLOCK);
        DetailAST child = block.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.VARIABLE_DEF) {
                final DetailAST modifiers = child.findFirstToken(TokenTypes.MODIFIERS);
                final DetailAST statics = modifiers.findFirstToken(TokenTypes.LITERAL_STATIC);
                if (statics == null) {
                    this.log(
                        child.getLineNo(),
                        child.getColumnNo(),
                        "Records cannot have instance fields"
                    );
                }
            }
            child = child.getNextSibling();
        }
    }

    /**
     * Check if component has a type.
     * Check if component has a name.
     * @param ast EXPR RECORD_COMPONENT_DEF node that needs to be checked
     */
    private void checkRecordComponent(final DetailAST ast) {
        if (ast.findFirstToken(TokenTypes.TYPE) == null) {
            this.log(ast.getLineNo(), ast.getColumnNo(), "Record component must have a type");
        }
        if (ast.findFirstToken(TokenTypes.IDENT) == null) {
            this.log(ast.getLineNo(), ast.getColumnNo(), "Record component must have a name");
        }
    }
}
