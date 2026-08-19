/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks if possible to use Diamond operator in generic instances creation.
 *
 * <p>Check is performed for variable declarations. Since parameterized types are invariant
 * in generics, Diamond operator should always be used in variable declarations.</p>
 *
 * <p>For example,</p>
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;Integer&gt;(); // error
 * </pre>
 *
 * <p>will return compilation error (because <code>ArrayList&lt;Integer&gt;</code> is not
 * a subclass of <code>List&lt;Number&gt;</code>).</p>
 *
 * <p>Hence, the only possible way to create a generic instance is copying type arguments from
 * the variable declaration.</p>
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;Number&gt;();
 * </pre>
 *
 * <p>In that case, Diamond Operator should always be used.</p>
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;&gt;();
 * </pre>
 *
 * <p>Exceptions to the rule above are wildcards, with them it's possible
 * to have different type parameters for left and right parts of variable declaration.</p>
 * <pre>
 *     // will compile
 *     private List&lt;? extends Number&gt; numbers = new ArrayList&lt;Integer&gt;();
 *     private List&lt;? super Integer&gt; list = new ArrayList&lt;Number&gt;();
 * </pre>
 *
 * <p>Although, this is not considered as good codestyle,
 * so it's better to use diamond operator here either.</p>
 *
 * @since 0.17
 */
public final class DiamondOperatorCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public DiamondOperatorCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.VARIABLE_DEF};
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
    public void visitToken(final DetailAST node) {
        final DetailAST generic = DiamondOperatorCheck.findFirstChildNodeOfType(
            node.findFirstToken(TokenTypes.TYPE), TokenTypes.TYPE_ARGUMENTS
        );
        final DetailAST assign = node.findFirstToken(TokenTypes.ASSIGN);
        final DetailAST instance;
        if (assign == null || generic == null) {
            instance = null;
        } else {
            instance = assign.getFirstChild().getFirstChild();
        }
        if (instance != null && instance.getType() == TokenTypes.LITERAL_NEW
            && DiamondOperatorCheck.validUsage(instance)) {
            final DetailAST type =
                DiamondOperatorCheck.findFirstChildNodeOfType(
                    instance, TokenTypes.TYPE_ARGUMENTS
                );
            if (type != null && !DiamondOperatorCheck.isDiamondOperatorUsed(type)) {
                log(type, "Use diamond operator");
            }
        }
    }

    private static boolean validUsage(final DetailAST node) {
        return DiamondOperatorCheck.isNotObjectBlock(node)
            && DiamondOperatorCheck.isNotArray(node)
            && !DiamondOperatorCheck.isInitUsingDiamond(node);
    }

    private static boolean isNotArray(final DetailAST node) {
        return node.findFirstToken(TokenTypes.ARRAY_DECLARATOR) == null;
    }

    private static boolean isNotObjectBlock(final DetailAST node) {
        return node.getLastChild().getType() != TokenTypes.OBJBLOCK;
    }

    private static boolean isInitUsingDiamond(final DetailAST node) {
        final DetailAST init = node.findFirstToken(TokenTypes.ELIST);
        boolean typed = false;
        if (init != null) {
            final DetailAST inst = DiamondOperatorCheck.secondChild(init);
            if (inst != null && inst.getType() == TokenTypes.LITERAL_NEW) {
                typed =
                    DiamondOperatorCheck.isDiamondOperatorUsed(
                        inst.findFirstToken(TokenTypes.TYPE_ARGUMENTS)
                    );
            }
        }
        return typed;
    }

    private static DetailAST secondChild(final DetailAST node) {
        DetailAST result = null;
        if (node != null) {
            final DetailAST first = node.getFirstChild();
            if (first != null) {
                result = first.getFirstChild();
            }
        }
        return result;
    }

    private static boolean isDiamondOperatorUsed(final DetailAST node) {
        return node != null && node.getChildCount() == 2
            && node.getFirstChild().getType() == TokenTypes.GENERIC_START
            && node.getLastChild().getType() == TokenTypes.GENERIC_END;
    }

    private static DetailAST findFirstChildNodeOfType(
        final DetailAST node, final int type
    ) {
        DetailAST result = node.findFirstToken(type);
        if (result == null) {
            final DetailAST child = node.getFirstChild();
            if (child != null) {
                result = DiamondOperatorCheck
                    .findFirstChildNodeOfType(child, type);
            }
        }
        return result;
    }
}
