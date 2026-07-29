/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Rule to prohibit use of String.length() when checking for empty string.
 * String.isEmpty() should be used instead.
 *
 * <p>Only comparisons that have an exact {@code isEmpty()} equivalent are
 * flagged. With {@code length()} on the left, those are {@code == 0},
 * {@code != 0}, {@code > 0}, {@code <= 0}, {@code < 1} and {@code >= 1};
 * the same six are recognised when the operands are swapped. Comparisons
 * against {@code 1} such as {@code length() == 1} or {@code length() > 1}
 * describe single-character strings and have no {@code isEmpty()}
 * counterpart, so they are left alone.
 *
 * @since 0.18
 */
public final class UseStringIsEmptyRule extends AbstractJavaRulechainRule {

    public UseStringIsEmptyRule() {
        super(ASTInfixExpression.class);
    }

    @Override
    public Object visit(final ASTInfixExpression expr, final Object data) {
        final ASTExpression left = expr.getLeftOperand();
        final ASTExpression right = expr.getRightOperand();
        if (isEmptyEquivalent(expr.getOperator(), left, right)
            || isEmptyEquivalent(mirror(expr.getOperator()), right, left)
        ) {
            asCtx(data).addViolation(expr);
        }
        return data;
    }

    /**
     * Is this an {@code isEmpty()}-equivalent comparison, assuming the
     * {@code length()} call is the left operand and the literal is the right?
     * @param operator The comparison operator
     * @param length The candidate {@code String.length()} call
     * @param literal The candidate numeric literal
     * @return True if the comparison can be rewritten with {@code isEmpty()}
     */
    private static boolean isEmptyEquivalent(
        final BinaryOp operator,
        final ASTExpression length,
        final ASTExpression literal
    ) {
        return isStringLength(length) && isWhitelisted(operator, literal);
    }

    /**
     * Is the pair of operator and literal one of the six allowed
     * {@code isEmpty()} equivalents ({@code == 0}, {@code != 0}, {@code > 0},
     * {@code <= 0}, {@code < 1}, {@code >= 1})?
     * @param operator The comparison operator
     * @param literal The candidate numeric literal
     * @return True if the pairing matches the whitelist
     */
    private static boolean isWhitelisted(
        final BinaryOp operator,
        final ASTExpression literal
    ) {
        boolean result = false;
        if (literal instanceof ASTNumericLiteral lit) {
            final String image = lit.getImage();
            result = switch (operator) {
                case EQ, NE, GT, LE -> "0".equals(image);
                case LT, GE -> "1".equals(image);
                default -> false;
            };
        }
        return result;
    }

    /**
     * Mirror an operator so a {@code literal OP length()} comparison can be
     * evaluated as if {@code length()} were on the left.
     * @param operator The operator as written
     * @return The operator with its operands swapped
     */
    private static BinaryOp mirror(final BinaryOp operator) {
        return switch (operator) {
            case GT -> BinaryOp.LT;
            case LT -> BinaryOp.GT;
            case GE -> BinaryOp.LE;
            case LE -> BinaryOp.GE;
            default -> operator;
        };
    }

    /**
     * Is the expression a no-argument {@code length()} call on a
     * {@link String}?
     * @param expr The expression to check
     * @return True if it is a {@code String.length()} call
     */
    private static boolean isStringLength(final ASTExpression expr) {
        boolean result = false;
        if (expr instanceof ASTMethodCall call && call.getQualifier() != null) {
            result = "length".equals(call.getMethodName())
                && call.getArguments().isEmpty()
                && isStringExpression(call.getQualifier());
        }
        return result;
    }

    /**
     * Checks if the expression is of {@link String} type.
     * @param expr The expression to check
     * @return True if expression has {@link String} type
     */
    private static boolean isStringExpression(final ASTExpression expr) {
        final JTypeMirror type = expr.getTypeMirror();
        return type.isClassOrInterface()
            && String.class.getName().equals(type.toString());
    }
}
