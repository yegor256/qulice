/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

/**
 * Rule to check that JUnit/TestNG test methods contain at least one
 * assertion. Unlike the PMD built-in
 * {@code UnitTestShouldIncludeAssert} rule (PMD #4272), this
 * implementation descends into lambda bodies, so an assertion placed
 * inside a lambda passed to another method is still recognised.
 *
 * <p>It also treats the cactoos-matchers convention as an assertion: a
 * no-argument {@code affirm()} call on an {@code Assertion} object, as in
 * {@code new Assertion<>(...).affirm()}. PMD's own
 * {@link TestFrameworksUtil#isProbableAssertCall(ASTMethodCall)} knows only
 * about JUnit, TestNG, Hamcrest and AssertJ, so without this addition such
 * tests are wrongly reported as missing an assertion (issue #1698).</p>
 *
 * @since 0.26.0
 */
public final class UnitTestShouldIncludeAssertRule
    extends AbstractJavaRulechainRule {

    /**
     * Default constructor.
     */
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
                .none(UnitTestShouldIncludeAssertRule::isAssertion)) {
            asCtx(data).addViolation(method);
        }
        return data;
    }

    /**
     * Does this call look like a test assertion?
     * @param call The method call to inspect
     * @return True if it is a recognised assertion
     */
    private static boolean isAssertion(final ASTMethodCall call) {
        return TestFrameworksUtil.isProbableAssertCall(call)
            || UnitTestShouldIncludeAssertRule.isAffirmCall(call);
    }

    /**
     * Is this a no-argument {@code affirm()} call on an {@code Assertion}
     * object, as used by cactoos-matchers?
     * @param call The method call to inspect
     * @return True if it is a cactoos-matchers assertion
     */
    private static boolean isAffirmCall(final ASTMethodCall call) {
        final ASTExpression qualifier = call.getQualifier();
        return "affirm".equals(call.getMethodName())
            && call.getArguments().isEmpty()
            && qualifier != null
            && UnitTestShouldIncludeAssertRule.isAssertion(qualifier);
    }

    /**
     * Is the given expression of a type named {@code Assertion}? The
     * comparison is by simple name so that it still holds when
     * cactoos-matchers is absent from the classpath and the type therefore
     * cannot be fully resolved.
     * @param expr The expression to inspect
     * @return True if its type's simple name is {@code Assertion}
     */
    private static boolean isAssertion(final ASTExpression expr) {
        final JTypeDeclSymbol symbol = expr.getTypeMirror().getSymbol();
        return symbol != null
            && "Assertion".equals(symbol.getSimpleName());
    }
}
