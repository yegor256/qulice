/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Arrays;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to check plain assertions in JUnit tests.
 *
 * @since 0.17
 */
public final class ProhibitPlainJunitAssertionsRule
    extends AbstractJavaRulechainRule {
    /**
     * Mask of prohibited imports.
     */
    private static final String[] PROHIBITED = {
        "org.junit.Assert.assert",
        "junit.framework.Assert.assert",
    };

    public ProhibitPlainJunitAssertionsRule() {
        super(
            ASTMethodDeclaration.class,
            ASTImportDeclaration.class
        );
    }

    @Override
    public Object visit(final ASTImportDeclaration imp, final Object data) {
        final String name = imp.getImportedName();
        if (Arrays.stream(ProhibitPlainJunitAssertionsRule.PROHIBITED)
            .anyMatch(name::contains)
        ) {
            asCtx(data).addViolation(imp);
        }
        return data;
    }

    @Override
    public Object visit(final ASTMethodDeclaration method, final Object data) {
        if (ProhibitPlainJunitAssertionsRule.isJUnitTest(method)) {
            method.descendants(ASTMethodCall.class)
                .filter(ProhibitPlainJunitAssertionsRule::isPlainJunitAssert)
                .toStream()
                .findAny()
                .ifPresent(call -> asCtx(data).addViolation(method));
        }
        return data;
    }

    private static boolean isJUnitTest(final ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations()
            .toStream()
            .map(ASTAnnotation::getSimpleName)
            .anyMatch(
                name -> "Test".equals(name)
                    || "org.junit.Test".equals(name)
                    || "org.junit.jupiter.api.Test".equals(name)
            );
    }

    private static boolean isPlainJunitAssert(final ASTMethodCall call) {
        final String name = call.getMethodName();
        final ASTExpression qualifier = call.getQualifier();
        return name.startsWith("assert") && qualifier != null
            && "Assert".contentEquals(qualifier.getText());
    }
}
