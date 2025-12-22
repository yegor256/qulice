/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.List;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to check unnecessary local variables.
 *
 * @since 0.4
 */
public final class UnnecessaryLocalRule extends AbstractJavaRulechainRule {
    public UnnecessaryLocalRule() {
        super(ASTVariableDeclarator.class);
    }

    @Override
    public Object visit(
        final ASTVariableDeclarator variable,
        final Object data
    ) {
        if (variable.getInitializer() != null) {
            final String name = variableName(variable);
            if (!name.isEmpty()) {
                asCtx(data).addViolation(variable, name);
            }
        }
        return data;
    }

    private static boolean hasReturnOrArguments(
        final List<ASTExpression> exprs
    ) {
        boolean result = false;
        if (exprs.size() == 1) {
            final ASTExpression use = exprs.getFirst();
            if (use.ancestors(ASTReturnStatement.class).toStream()
                .findAny().isPresent()
                || use.ancestors(ASTArgumentList.class).toStream()
                .findAny().isPresent()
            ) {
                result = true;
            }
        }
        return result;
    }

    private static String variableName(final ASTVariableDeclarator variable) {
        String result = "";
        final ASTBlock block = variable.ancestors(ASTBlock.class).first();
        if (block != null) {
            final String name = variable.getName();
            final List<ASTExpression> uses = block
                .descendants(ASTExpression.class)
                .filter(expr -> name.equals(expr.getImage()))
                .toList();
            if (hasReturnOrArguments(uses)) {
                result = name;
            }
        }
        return result;
    }
}
