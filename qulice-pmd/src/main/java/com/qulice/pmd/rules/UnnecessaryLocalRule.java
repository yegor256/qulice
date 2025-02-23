/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Rule to check unnecessary local variables.
 *
 * @since 0.4
 */
public final class UnnecessaryLocalRule extends AbstractJavaRule {
    @Override
    public Object visit(final ASTMethodDeclaration meth, final Object data) {
        Object ndata = data;
        if (!meth.isAbstract() && !meth.isNative()) {
            ndata = super.visit(meth, data);
        }
        return ndata;
    }

    @Override
    public Object visit(final ASTReturnStatement rtn, final Object data) {
        final ASTVariableDeclarator name =
            rtn.getFirstChildOfType(ASTVariableDeclarator.class);
        if (name != null) {
            this.usages(rtn, data, name);
        }
        return data;
    }

    @Override
    public Object visit(final ASTArgumentList rtn, final Object data) {
        final List<ASTVariableDeclarator> names =
            rtn.findChildrenOfType(ASTVariableDeclarator.class);
        for (final ASTVariableDeclarator name : names) {
            this.usages(rtn, data, name);
        }
        return data;
    }

    /**
     * Report when number of variable usages is equal to zero.
     * @param node Node to check.
     * @param data Context.
     * @param name Variable name.
     */
    private void usages(final JavaNode node, final Object data,
        final ASTVariableDeclarator name) {
        final Map<NameDeclaration, List<NameOccurrence>> vars = name
            .getScope().getDeclarations();
        for (final Map.Entry<NameDeclaration, List<NameOccurrence>> entry
            : vars.entrySet()) {
            final List<NameOccurrence> usages = entry.getValue();
            if (usages.size() > 1) {
                continue;
            }
            for (final NameOccurrence occ: usages) {
                if (occ.getLocation().equals(name)) {
                    this.asCtx(data).addViolation(
                        node, name.getImage()
                    );
                }
            }
        }
    }
}
