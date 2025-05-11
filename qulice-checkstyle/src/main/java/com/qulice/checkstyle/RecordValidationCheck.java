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
 */
public class RecordValidationCheck extends AbstractCheck {
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "record.validation";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.RECORD_DEF,
                TokenTypes.RECORD_COMPONENT_DEF
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.RECORD_DEF:
                checkRecordDeclaration(ast);
                break;
            case TokenTypes.RECORD_COMPONENT_DEF:
                checkRecordComponent(ast);
                break;
        }
    }

    private void checkRecordDeclaration(DetailAST ast) {
        // Check if record extends another class
        if (ast.findFirstToken(TokenTypes.EXTENDS_CLAUSE) != null) {
            log(ast.getLineNo(), ast.getColumnNo(), "Records cannot extend other classes");
        }

        // Check if record is final
        DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null && modifiers.findFirstToken(TokenTypes.FINAL) == null) {
            log(modifiers.getLineNo(), modifiers.getColumnNo(), "Records must be final");
        }

        // Check if record has components
        DetailAST components = ast.findFirstToken(TokenTypes.RECORD_COMPONENTS);
        if (components != null && components.findFirstToken(TokenTypes.RECORD_COMPONENT_DEF) == null) {
            log(components.getLineNo(), components.getColumnNo(), "Records must declare at least one component");
        }

        checkRecordInstanceFields(ast);
    }

    private void checkRecordInstanceFields(DetailAST ast) {
        DetailAST objBlockAst = ast.findFirstToken(TokenTypes.OBJBLOCK);

        // Traverse the children of the objBlockAst to find variable definitions
        for (DetailAST child = objBlockAst.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.VARIABLE_DEF) {
                DetailAST modifiersAst = child.findFirstToken(TokenTypes.MODIFIERS);
                DetailAST isStatic = modifiersAst.findFirstToken(TokenTypes.LITERAL_STATIC);

                if (isStatic == null) {
                    log(child.getLineNo(), child.getColumnNo(), "Records cannot have instance fields");
                }
            }
        }
    }

    private void checkRecordComponent(DetailAST ast) {
        // Check if component has a type
        if (ast.findFirstToken(TokenTypes.TYPE) == null) {
            log(ast.getLineNo(), ast.getColumnNo(), "Record component must have a type");
        }

        // Check if component has a name
        if (ast.findFirstToken(TokenTypes.IDENT) == null) {
            log(ast.getLineNo(), ast.getColumnNo(), "Record component must have a name");
        }
    }
}