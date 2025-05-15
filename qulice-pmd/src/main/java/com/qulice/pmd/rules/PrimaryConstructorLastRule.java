/**
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 * Check that primary constructor is placed at the end of constructors list.
 *
 * This rule checks that the constructor with the most parameters (primary constructor)
 * is placed after all other constructors in the class.
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * Rule that checks primary constructor is placed at the end.
 * Primary constructor is the one with the most parameters.
 *
 * @since 0.18
 */
public final class PrimaryConstructorLastRule extends AbstractJavaRule {

    /**
     * Error message for the rule violation.
     */
    private static final String MESSAGE =
            "Primary constructor (with most parameters) should be placed at the end of constructors list";

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        // Only check classes, not interfaces
        if (!node.isInterface()) {
            checkConstructorOrder(node, data);
        }
        return super.visit(node, data);
    }

    /**
     * Check the order of constructors in the class.
     *
     * @param classNode Class declaration node
     * @param data Rule context data
     */
    private void checkConstructorOrder(ASTClassOrInterfaceDeclaration classNode, Object data) {
        List<ASTConstructorDeclaration> constructors =
                classNode.findDescendantsOfType(ASTConstructorDeclaration.class);

        // Filter only direct children constructors (not nested class constructors)
        List<ASTConstructorDeclaration> directConstructors = new ArrayList<>();
        for (ASTConstructorDeclaration constructor : constructors) {
            if (constructor.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) == classNode) {
                directConstructors.add(constructor);
            }
        }

        if (directConstructors.size() <= 1) {
            // No need to check order if there's only one or no constructors
            return;
        }

        // Find the primary constructor (with most parameters)
        ASTConstructorDeclaration primaryConstructor = findPrimaryConstructor(directConstructors);
        if (primaryConstructor == null) {
            return;
        }

        // Check if primary constructor is the last one
        ASTConstructorDeclaration lastConstructor = directConstructors.get(directConstructors.size() - 1);
        if (primaryConstructor != lastConstructor) {
            asCtx(data).addViolation(primaryConstructor, MESSAGE);
        }
    }

    /**
     * Find primary constructor (the one with most parameters).
     * If there are multiple constructors with the same max parameter count,
     * consider the first one as primary.
     *
     * @param constructors List of constructors
     * @return Primary constructor or null if no constructors
     */
    private ASTConstructorDeclaration findPrimaryConstructor(
            List<ASTConstructorDeclaration> constructors) {
        if (constructors.isEmpty()) {
            return null;
        }

        return constructors.stream()
                .max(Comparator.comparingInt(this::getParameterCount))
                .orElse(null);
    }

    /**
     * Get parameter count for a constructor.
     *
     * @param constructor Constructor declaration
     * @return Number of parameters
     */
    private int getParameterCount(ASTConstructorDeclaration constructor) {
        ASTFormalParameters params = constructor.getFirstDescendantOfType(ASTFormalParameters.class);
        return params != null ? params.size() : 0;
    }
}