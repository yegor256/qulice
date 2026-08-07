/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeOps;

/**
 * Rule to flag an interface that is implicitly functional (declares exactly
 * one abstract method) but is not annotated with {@code @FunctionalInterface}.
 *
 * <p>Unlike PMD's built-in {@code ImplicitFunctionalInterface} rule, this
 * variant refuses to fire when the interface extends a super-interface whose
 * type PMD cannot resolve. The built-in rule counts only the abstract methods
 * it can see: when a super-interface is unresolved (for instance because
 * Qulice runs PMD without an auxiliary classpath, so types declared in other
 * files are invisible), the rule misses the abstract methods that
 * super-interface contributes and wrongly reports the interface as functional.
 * Adding the suggested {@code @FunctionalInterface} annotation to such an
 * interface does not compile, because the interface really has more than one
 * abstract method. An interface with a single visible method whose full set of
 * inherited abstract methods cannot be computed is therefore left alone. Once
 * the whole super-interface hierarchy resolves, the check is exactly the same
 * as the built-in one: fire only when the interface has a single abstract
 * method overall.</p>
 *
 * @since 1.0
 */
public final class ImplicitFunctionalInterfaceRule
    extends AbstractJavaRulechainRule {

    /**
     * Default constructor.
     */
    public ImplicitFunctionalInterfaceRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(final ASTClassDeclaration node, final Object data) {
        if (ImplicitFunctionalInterfaceRule.implicit(node)
            && ImplicitFunctionalInterfaceRule.resolved(node.getTypeMirror())
            && TypeOps.findFunctionalInterfaceMethod(
                node.getTypeMirror()
            ) != null) {
            this.asCtx(data).addViolation(node);
        }
        return null;
    }

    /**
     * Tells whether the given type is an interface that could carry a
     * {@code @FunctionalInterface} annotation but does not, ignoring sealed
     * interfaces that cannot be functional at all.
     * @param node The type declaration to inspect
     * @return True if the interface is a candidate for the annotation
     */
    private static boolean implicit(final ASTClassDeclaration node) {
        return node.isRegularInterface()
            && !node.isAnnotationPresent(FunctionalInterface.class)
            && !node.hasModifiers(JModifier.SEALED);
    }

    /**
     * Tells whether every super-interface in the whole hierarchy of the given
     * type has been resolved by PMD. Only when they all resolve can the total
     * number of inherited abstract methods be trusted.
     * @param type The interface type to inspect
     * @return True if the entire super-interface hierarchy is resolved
     */
    private static boolean resolved(final JClassType type) {
        boolean result = true;
        for (final JClassType parent : type.getSuperInterfaces()) {
            if (parent.getSymbol().isUnresolved()
                || !ImplicitFunctionalInterfaceRule.resolved(parent)) {
                result = false;
                break;
            }
        }
        return result;
    }
}
