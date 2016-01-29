/**
 * Copyright (c) 2011-2016, Qulice.com
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
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.rule.junit.AbstractJUnitRule;

/**
 * Rule to check old style assertions in JUnit tests.
 * @author Viktor Kuchyn (kuchin.victor@gmail.com)
 * @version $Id$
 */
public final class ProhibitOldStyleAssertionsRule extends AbstractJUnitRule {

    /**
     * Mask of prohibited imports.
     */
    private static final String[] PROHIBITED = {
        "org.junit.Assert.assert",
        "junit.framework.Assert.assert",
    };

    @Override
    public Object visit(final ASTMethodDeclaration method, final Object data) {
        if (this.isJUnitMethod(method, data)
            && this.containsOldStyleAssert(method.getBlock())) {
            this.addViolation(data, method);
        }
        return data;
    }

    @Override
    public Object visit(final ASTImportDeclaration imp, final Object data) {
        if (ProhibitOldStyleAssertionsRule.isArrayMatchesValue(
            imp.getImportedName(), ProhibitOldStyleAssertionsRule.PROHIBITED
        )) {
            this.addViolation(data, imp);
        }
        return super.visit(imp, data);
    }

    /**
     * Checks if string matches at least one element in array.
     * @param value String value to be matched
     * @param array Array of templates
     * @return False if no matches found, true if at least one
     */
    private static boolean isArrayMatchesValue(final String value,
        final String... array) {
        boolean matches = false;
        for (final String element : array) {
            if (value.contains(element)) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    /**
     * Recursively verifies if node contains old style assert statements.
     * @param node Root statement node to search
     * @return True if statement contains old style assertions, false otherwise
     */
    private boolean containsOldStyleAssert(
        final Node node
    ) {
        boolean found = false;
        if (node instanceof ASTStatementExpression
            && ProhibitOldStyleAssertionsRule.isAssertStatement(node)) {
            found = true;
        }
        if (!found) {
            for (int iter = 0; iter < node.jjtGetNumChildren(); iter += 1) {
                final Node child = node.jjtGetChild(iter);
                if (this.containsOldStyleAssert(child)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Tells if the statement is an assert statement or not.
     * @param statement Root node to search assert statements
     * @return True is statement is assert, false otherwise
     */
    private static boolean isAssertStatement(final Node statement) {
        boolean assrt;
        try {
            final ASTPrimaryExpression expression =
                ProhibitOldStyleAssertionsRule.extractExpressionFrom(statement);
            final ASTPrimaryPrefix prefix =
                ProhibitOldStyleAssertionsRule.extractPrefixFrom(expression);
            final String img =
                ProhibitOldStyleAssertionsRule.extractImageFrom(prefix);
            assrt = img.startsWith("assert") || img.startsWith("Assert.assert");
        } catch (final IllegalArgumentException ex) {
            assrt = false;
        }
        return assrt;
    }

    /**
     * Extract primary expression from Node.
     * @param node Expression Statement
     * @return Primary expression.
     * @throws IllegalArgumentException if no primary expression found
     */
    private static ASTPrimaryExpression extractExpressionFrom(final Node node) {
        ASTPrimaryExpression expression = null;
        if (node != null && node.jjtGetNumChildren() > 0
            && node.jjtGetChild(0) instanceof ASTPrimaryExpression) {
            expression = (ASTPrimaryExpression) node.jjtGetChild(0);
        }
        if (expression == null) {
            throw new IllegalArgumentException(
                "No primary expression found in node"
            );
        }
        return expression;
    }

    /**
     * Extract primary prefix from primary expression.
     * @param node Primary Expression
     * @return Primary prefix.
     * @throws IllegalArgumentException if no primary prefix found
     */
    private static ASTPrimaryPrefix extractPrefixFrom(
        final Node node) {
        ASTPrimaryPrefix prefix = null;
        if (node.jjtGetNumChildren() > 0
            && node.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
            prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
        }
        if (prefix == null) {
            throw new IllegalArgumentException(
                "No Primary statement found in node"
            );
        }
        return prefix;
    }

    /**
     * Extract image from primary prefix.
     * @param node Primary prefix
     * @return Image statement
     * @throws IllegalArgumentException if no image found in node
     */
    private static String extractImageFrom(final Node node) {
        String image = null;
        if (node.jjtGetNumChildren() > 0
            && node.jjtGetChild(0) instanceof ASTName) {
            image = ((ASTName) node.jjtGetChild(0)).getImage();
        }
        if (image == null) {
            throw new IllegalArgumentException("No image found in node");
        }
        return image;
    }

}
