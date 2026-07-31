/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Set;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to check that a class does not expose too many methods. Unlike the
 * PMD built-in {@code TooManyMethods} rule, this implementation counts
 * only public and protected methods, because private and package-private
 * ones are implementation detail and say nothing about how large the
 * contract of the class is. Methods annotated with {@code @Override} are
 * skipped too, since a supertype dictates them and the class has no say
 * in how many of them there are. Test classes are skipped altogether, since
 * one assertion per test method inflates their method count beyond any
 * useful threshold. Skipping them here instead of through a
 * {@code violationSuppressXPath} keeps a redundant
 * {@code @SuppressWarnings("PMD.TooManyMethods")} visible to
 * {@code UnnecessaryWarningSuppression}, which PMD credits to the
 * annotation whenever the annotation suppressor runs first.
 * @since 1.0
 */
public final class TooManyMethodsRule extends AbstractJavaRulechainRule {

    /**
     * The largest number of public and protected methods a class may have.
     */
    private static final int MAX = 10;

    /**
     * Simple-name suffixes that mark a type as a test.
     */
    private static final Set<String> SUFFIXES = Set.of(
        "Test",
        "Tests",
        "IT",
        "TestCase",
        "ITCase"
    );

    /**
     * Annotations that mark a type as a test even when its name follows
     * another convention. Names ending with {@code Test} are recognised
     * separately, which covers {@code @Test}, {@code @ParameterizedTest},
     * {@code @RepeatedTest} and their third-party equivalents.
     */
    private static final Set<String> ANNOTATIONS = Set.of(
        "TestFactory",
        "TestTemplate",
        "Theory",
        "Nested"
    );

    /**
     * Default constructor.
     */
    public TooManyMethodsRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(final ASTClassDeclaration type, final Object data) {
        if (!TooManyMethodsRule.tested(type)
            && TooManyMethodsRule.exposed(type) > TooManyMethodsRule.MAX) {
            this.asCtx(data).addViolation(type);
        }
        return data;
    }

    private static int exposed(final ASTClassDeclaration type) {
        return type.getDeclarations(ASTMethodDeclaration.class)
            .filter(TooManyMethodsRule::visible)
            .count();
    }

    private static boolean visible(final ASTMethodDeclaration method) {
        return method.getVisibility()
            .isAtLeast(ModifierOwner.Visibility.V_PROTECTED)
            && !method.isAnnotationPresent(Override.class);
    }

    private static boolean tested(final ASTClassDeclaration type) {
        return type.ancestorsOrSelf()
            .filterIs(ASTTypeDeclaration.class)
            .any(TooManyMethodsRule::marked);
    }

    private static boolean marked(final ASTTypeDeclaration type) {
        return TooManyMethodsRule.named(type.getSimpleName())
            || type.descendants(ASTAnnotation.class).any(
                TooManyMethodsRule::testing
            );
    }

    private static boolean named(final String simple) {
        return TooManyMethodsRule.SUFFIXES.stream().anyMatch(simple::endsWith);
    }

    private static boolean testing(final ASTAnnotation annotation) {
        return annotation.getSimpleName().endsWith("Test")
            || TooManyMethodsRule.ANNOTATIONS.contains(
                annotation.getSimpleName()
            );
    }
}
