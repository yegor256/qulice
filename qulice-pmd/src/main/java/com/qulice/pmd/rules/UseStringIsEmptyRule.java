/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Rule to prohibit use of String.length() when checking for empty string.
 * String.isEmpty() should be used instead.
 *
 * @since 0.18
 */
public final class UseStringIsEmptyRule extends AbstractJavaRulechainRule {
    public UseStringIsEmptyRule() {
        super(ASTInfixExpression.class);
    }

    @Override
    public Object visit(final ASTInfixExpression expr, final Object data) {
        if (isComparison(expr) && (
            matchesLengthCheck(
                expr.getLeftOperand(),
                expr.getRightOperand()
            )
                || matchesLengthCheck(
                expr.getRightOperand(),
                expr.getLeftOperand()
            )
            )
        ) {
            asCtx(data).addViolation(expr);
        }
        return data;
    }

    private static boolean isComparison(final ASTInfixExpression expr) {
        final boolean result;
        switch (expr.getOperator()) {
            case EQ:
            case NE:
            case GT:
            case LT:
            case GE:
            case LE:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * Checks if length is length() or literal is 0 or 1.
     *
     * @param length The method
     * @param literal The number
     * @return True if matches, false otherwise
     * @checkstyle BooleanExpressionComplexityCheck (20 lines)
     */
    private static boolean matchesLengthCheck(
        final ASTExpression length,
        final ASTExpression literal
    ) {
        boolean result = false;
        if (length != null && literal != null && isZeroOrOneLiteral(literal)
            && length instanceof ASTMethodCall) {
            final ASTMethodCall call = (ASTMethodCall) length;
            result = "length".equals(call.getMethodName())
                && call.getArguments().isEmpty()
                && call.getQualifier() != null
                && isStringExpression(call.getQualifier());
        }
        return result;
    }

    private static boolean isZeroOrOneLiteral(final ASTExpression expr) {
        boolean matches = false;
        if (expr instanceof ASTNumericLiteral lit) {
            final String image = lit.getImage();
            matches = "0".equals(image) || "1".equals(image);
        }
        return matches;
    }

    private static boolean isStringExpression(final ASTExpression expr) {
        final JTypeMirror type = expr.getTypeMirror();
        return type.isClassOrInterface()
            && "java.lang.String".equals(type.toString());
    }
}
