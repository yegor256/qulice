/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * <p>For example,
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;Integer&gt;(); // error
 * </pre>
 * will return compilation error (because <code>ArrayList&lt;Integer&gt;</code> is not
 * a subclass of <code>List&lt;Number&gt;</code>).
 * </p>
 * <p>Hence, the only possible way to create a generic instance is copying type arguments from
 * the variable declaration.
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;Number&gt;();
 * </pre>
 * In that case, Diamond Operator should always be used.
 * <pre>
 *     private List&lt;Number&gt; numbers = new ArrayList&lt;&gt;();
 * </pre>
 * </p>
 * <p>Exceptions to the rule above are wildcards, with them it's possible
 * to have different type parameters for left and right parts of variable declaration.
 * <pre>
 *     // will compile
 *     private List&lt;? extends Number&gt; numbers = new ArrayList&lt;Integer&gt;();
 *     private List&lt;? super Integer&gt; list = new ArrayList&lt;Number&gt;();
 *</pre>
 * Although, this is not considered as good codestyle,
 * so it's better to use diamond operator here either.
 * </p>
 *
 * @since 0.17
 */
public final class DiamondOperatorCheck extends AbstractCheck {

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
        final DetailAST generic = DiamondOperatorCheck
            .findFirstChildNodeOfType(
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

    /**
     * Checks if diamond is not required.
     *
     * @param node Node
     * @return True if not array
     */
    private static boolean validUsage(final DetailAST node) {
        return DiamondOperatorCheck.isNotObjectBlock(node)
            && DiamondOperatorCheck.isNotArray(node)
            && !DiamondOperatorCheck.isInitUsingDiamond(node);
    }

    /**
     * Checks if node is not array.
     *
     * @param node Node
     * @return True if not array
     */
    private static boolean isNotArray(final DetailAST node) {
        return node.findFirstToken(TokenTypes.ARRAY_DECLARATOR) == null;
    }

    /**
     * Checks if node is object block.
     *
     * @param node Node
     * @return True if not object block
     */
    private static boolean isNotObjectBlock(final DetailAST node) {
        return node.getLastChild().getType() != TokenTypes.OBJBLOCK;
    }

    /**
     * Checks if node has initialization with diamond operator.
     *
     * @param node Node
     * @return True if not object block
     */
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

    /**
     * Checks if node has initialization with diamond operator.
     *
     * @param node Node
     * @return True if not object block
     */
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

    /**
     * Checks if node contains empty set of type parameters and
     * comprises angle brackets only (<>).
     * @param node Node of type arguments
     * @return True if node contains angle brackets only
     */
    private static boolean isDiamondOperatorUsed(final DetailAST node) {
        return node != null && node.getChildCount() == 2
            && node.getFirstChild().getType() == TokenTypes.GENERIC_START
            && node.getLastChild().getType() == TokenTypes.GENERIC_END;
    }

    /**
     * Returns the first child node of a specified type.
     *
     * @param node AST subtree to process.
     * @param type Type of token
     * @return Child node of specified type OR NULL!
     */
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
