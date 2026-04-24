/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

/**
 * Rule to check that JUnit/TestNG test methods do not contain more than
 * one assertion. Unlike the PMD built-in
 * {@code UnitTestContainsTooManyAsserts} rule, this implementation does
 * not count calls to {@code assertThrows} as assertions, because a
 * common idiom wraps {@code assertThrows(...).getMessage()} inside an
 * {@code assertThat} to verify the thrown exception's message in a
 * single logical check.
 * @since 0.26.0
 */
public final class UnitTestContainsTooManyAssertsRule
    extends AbstractJavaRulechainRule {

    public UnitTestContainsTooManyAssertsRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(final ASTMethodDeclaration method, final Object data) {
        final ASTBlock body = method.getBody();
        if (body != null
            && TestFrameworksUtil.isTestMethod(method)
            && body.descendants(ASTMethodCall.class)
                .filter(UnitTestContainsTooManyAssertsRule::isCountedAssert)
                .count() > 1) {
            this.asCtx(data).addViolation(method);
        }
        return data;
    }

    private static boolean isCountedAssert(final ASTMethodCall call) {
        return TestFrameworksUtil.isProbableAssertCall(call)
            && !"assertThrows".equals(call.getMethodName());
    }
}
