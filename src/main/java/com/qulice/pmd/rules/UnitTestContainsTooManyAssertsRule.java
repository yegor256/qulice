/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Set;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

/**
 * Rule to check that JUnit/TestNG test methods do not contain more than
 * one assertion. Unlike the PMD built-in
 * {@code UnitTestContainsTooManyAsserts} rule, this implementation only
 * counts calls whose simple name is one of the known JUnit/Hamcrest
 * assertion methods, instead of counting any identifier with an
 * {@code assert}, {@code check} or {@code verify} prefix. The PMD
 * default produces false positives for unrelated APIs such as
 * {@code pull.checks()} (jcabi-github) or {@code Mockito.verify(...)},
 * and the {@code assertThrows} method is also excluded because a common
 * idiom wraps {@code assertThrows(...).getMessage()} inside an
 * {@code assertThat} to verify the thrown exception's message in a
 * single logical check.
 * @since 0.26.0
 */
public final class UnitTestContainsTooManyAssertsRule
    extends AbstractJavaRulechainRule {

    /**
     * Method names recognised as JUnit / Hamcrest assertions. The list
     * intentionally excludes {@code assertThrows}, which is treated as
     * an exception-capturing helper, not an assertion.
     */
    private static final Set<String> ASSERTIONS = Set.of(
        "assertThat",
        "assertEquals",
        "assertNotEquals",
        "assertTrue",
        "assertFalse",
        "assertNull",
        "assertNotNull",
        "assertSame",
        "assertNotSame",
        "assertArrayEquals",
        "assertIterableEquals",
        "assertLinesMatch",
        "assertDoesNotThrow",
        "assertTimeout",
        "assertTimeoutPreemptively",
        "assertAll",
        "fail"
    );

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
        return UnitTestContainsTooManyAssertsRule.ASSERTIONS.contains(
            call.getMethodName()
        );
    }
}
