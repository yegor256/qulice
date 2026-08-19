/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to check that a method does not take too many parameters. Unlike the
 * PMD built-in {@code ExcessiveParameterList} rule, which exempts private
 * constructors only, this implementation skips every constructor: a value
 * object with many immutable attributes gets a parameter count that tracks
 * its field count rather than its complexity, and the class has nowhere
 * else to accept those attributes. The rule stays active for methods,
 * where a long parameter list is a design smell worth reporting.
 * Skipping constructors here instead of through a
 * {@code violationSuppressXPath} keeps a redundant
 * {@code @SuppressWarnings("PMD.ExcessiveParameterList")} visible to
 * {@code UnnecessaryWarningSuppression}, which PMD credits to the
 * annotation whenever the annotation suppressor runs first.
 * @since 1.0
 */
public final class ExcessiveParameterListRule
    extends AbstractJavaRulechainRule {

    /**
     * The number of parameters at or above which a method is reported.
     */
    private static final int THRESHOLD = 10;

    /**
     * Default constructor.
     */
    public ExcessiveParameterListRule() {
        super(ASTFormalParameters.class);
    }

    @Override
    public Object visit(final ASTFormalParameters params, final Object data) {
        final int count = params.size();
        if (count >= ExcessiveParameterListRule.THRESHOLD
            && !(params.getParent() instanceof ASTConstructorDeclaration)) {
            this.asCtx(data).addViolation(
                params, count, ExcessiveParameterListRule.THRESHOLD
            );
        }
        return data;
    }
}
