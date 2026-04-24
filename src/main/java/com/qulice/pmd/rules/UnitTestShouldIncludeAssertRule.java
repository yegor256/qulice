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
 * Rule to check that JUnit/TestNG test methods contain at least one
 * assertion. Unlike the PMD built-in
 * {@code UnitTestShouldIncludeAssert} rule (PMD #4272), this
 * implementation descends into lambda bodies, so an assertion placed
 * inside a lambda passed to another method is still recognised.
 * @since 0.26.0
 */
public final class UnitTestShouldIncludeAssertRule
    extends AbstractJavaRulechainRule {

    public UnitTestShouldIncludeAssertRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(final ASTMethodDeclaration method, final Object data) {
        final ASTBlock body = method.getBody();
        if (body != null
            && TestFrameworksUtil.isTestMethod(method)
            && !TestFrameworksUtil.isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class)
                .crossFindBoundaries(true)
                .none(TestFrameworksUtil::isProbableAssertCall)) {
            asCtx(data).addViolation(method);
        }
        return data;
    }
}
