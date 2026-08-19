/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to check that a class does not declare too many fields. Like the PMD
 * built-in {@code TooManyFields} rule, this implementation counts the
 * fields declared in the body of the class, skipping the static and the
 * final ones, since those are constants rather than state. Unlike the
 * built-in rule, it skips a class that extends
 * {@code org.apache.maven.plugin.AbstractMojo}: a Maven Mojo declares one
 * field per {@code @Parameter} it exposes to users of the plugin goal,
 * because the Maven Plugin API requires each configurable parameter as a
 * directly annotated field on the Mojo, so that count tracks the number of
 * options the goal accepts rather than the cohesion of the class.
 * Skipping them here instead of through a
 * {@code violationSuppressXPath} keeps a redundant
 * {@code @SuppressWarnings("PMD.TooManyFields")} visible to
 * {@code UnnecessaryWarningSuppression}, which PMD credits to the
 * annotation whenever the annotation suppressor runs first.
 * @since 1.0
 */
public final class TooManyFieldsRule extends AbstractJavaRulechainRule {

    /**
     * The largest number of non-static non-final fields a class may declare.
     */
    private static final int MAX = 15;

    /**
     * The base class of every Maven Mojo.
     */
    private static final NamedSupertype MOJO = new NamedSupertype(
        "org.apache.maven.plugin.AbstractMojo"
    );

    /**
     * Default constructor.
     */
    public TooManyFieldsRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(final ASTClassDeclaration type, final Object data) {
        if (!TooManyFieldsRule.MOJO.matches(type.getSuperClassTypeNode())
            && TooManyFieldsRule.state(type) > TooManyFieldsRule.MAX) {
            this.asCtx(data).addViolation(type);
        }
        return data;
    }

    /**
     * How many fields does this class use to keep its state?
     * @param type Class to inspect
     * @return Number of non-static non-final fields in the class body
     */
    private static long state(final ASTClassDeclaration type) {
        return type.getDeclarations(ASTFieldDeclaration.class)
            .filter(TooManyFieldsRule::mutable)
            .count();
    }

    /**
     * Is this field a part of the state of its class?
     * @param field Field to inspect
     * @return TRUE if the field is neither static nor final
     */
    private static boolean mutable(final ASTFieldDeclaration field) {
        return !field.hasModifiers(JModifier.FINAL)
            && !field.hasModifiers(JModifier.STATIC);
    }
}
