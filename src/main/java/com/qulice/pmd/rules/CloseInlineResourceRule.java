/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Set;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Rule to flag {@code AutoCloseable} expressions that are created inline and
 * then consumed by another expression instead of a resource boundary.
 * @since 0.27.7
 */
public final class CloseInlineResourceRule extends AbstractJavaRulechainRule {

    /**
     * Exact resource types allowed by PMD's CloseResource rule.
     */
    private static final Set<String> ALLOWED = Set.of(
        "java.io.ByteArrayOutputStream",
        "java.io.ByteArrayInputStream",
        "java.io.StringWriter",
        "java.io.CharArrayWriter",
        "java.util.stream.Stream",
        "java.util.stream.IntStream",
        "java.util.stream.LongStream",
        "java.util.stream.DoubleStream"
    );

    public CloseInlineResourceRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public Object visit(final ASTConstructorCall call, final Object data) {
        if (CloseInlineResourceRule.leaksInline(call)) {
            this.asCtx(data).addViolation(call);
        }
        return data;
    }

    @Override
    public Object visit(final ASTMethodCall call, final Object data) {
        if (CloseInlineResourceRule.leaksInline(call)
            && CloseInlineResourceRule.hasCloseableArgument(call)) {
            this.asCtx(data).addViolation(call);
        }
        return data;
    }

    private static boolean leaksInline(final ASTExpression expr) {
        boolean result = false;
        if (CloseInlineResourceRule.closeable(expr)
            && !CloseInlineResourceRule.allowed(expr)) {
            result = CloseInlineResourceRule.unmanagedInline(expr);
        }
        return result;
    }

    private static boolean closeable(final ASTExpression expr) {
        return TypeTestUtil.isA(AutoCloseable.class, expr);
    }

    private static boolean allowed(final ASTExpression expr) {
        return CloseInlineResourceRule.ALLOWED.stream()
            .anyMatch(type -> TypeTestUtil.isExactlyA(type, expr));
    }

    private static boolean unmanagedInline(final ASTExpression expr) {
        return CloseInlineResourceRule.inline(expr)
            && !CloseInlineResourceRule.closedDirectly(expr)
            && !CloseInlineResourceRule.returnedDirectly(expr);
    }

    private static boolean hasCloseableArgument(final ASTMethodCall call) {
        return call.getArguments().descendants(ASTExpression.class)
            .any(CloseInlineResourceRule::closeable);
    }

    private static boolean inline(final ASTExpression expr) {
        return expr.ancestors(ASTResource.class).isEmpty()
            && expr.ancestors(ASTVariableDeclarator.class).isEmpty();
    }

    private static boolean closedDirectly(final ASTExpression expr) {
        boolean found = false;
        final Node parent = expr.getParent();
        if (parent instanceof ASTMethodCall) {
            final ASTMethodCall call = (ASTMethodCall) parent;
            found = "close".equals(call.getMethodName())
                && expr.equals(call.getQualifier());
        }
        return found;
    }

    private static boolean returnedDirectly(final ASTExpression expr) {
        return expr.getParent() instanceof ASTReturnStatement;
    }
}
