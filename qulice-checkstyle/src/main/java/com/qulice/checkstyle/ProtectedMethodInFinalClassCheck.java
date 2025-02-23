/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import java.util.List;

/**
 * Checks that final class doesn't contain protected methods unless they are
 * overriding protected methods from superclass.
 *
 * @since 0.6
 */
public final class ProtectedMethodInFinalClassCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            final DetailAST modifiers = ast.findFirstToken(
                TokenTypes.MODIFIERS
            );
            if (modifiers.findFirstToken(TokenTypes.FINAL) != null) {
                this.checkMethods(ast);
            }
        }
    }

    /**
     * Checks methods in current class have no protected modifier.
     * @param ast DetailAST of CLASS_DEF
     */
    private void checkMethods(final DetailAST ast) {
        final DetailAST objblock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        for (final DetailAST method
            : ProtectedMethodInFinalClassCheck.findAllChildren(
                objblock, TokenTypes.METHOD_DEF
            )
        ) {
            if (method
                .findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_PROTECTED) != null) {
                if (AnnotationUtil.containsAnnotation(method, "Override")) {
                    this.log(
                        method.getLineNo(),
                        "Protected method is overriding default scoped method"
                    );
                } else {
                    this.log(
                        method.getLineNo(),
                        "Final class should not contain protected methods"
                    );
                }
            }
        }
    }

    /**
     * Search for all children of given type.
     * @param base Parent node to start from
     * @param type Node type
     * @return Iterable
     */
    private static Iterable<DetailAST> findAllChildren(final DetailAST base,
        final int type) {
        final List<DetailAST> children = Lists.newArrayList();
        DetailAST child = base.getFirstChild();
        while (child != null) {
            if (child.getType() == type) {
                children.add(child);
            }
            child = child.getNextSibling();
        }
        return children;
    }
}
