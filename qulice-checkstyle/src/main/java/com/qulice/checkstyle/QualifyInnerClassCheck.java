/*
 * Copyright (c) 2011-2022 Qulice.com
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

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks if inner classes are properly accessed using their qualified name
 * with the outer class.
 *
 * @since 0.18
 * @todo #738:30min Static inner classes should be qualified with outer class
 *  Implement QualifyInnerClassCheck so it follows what defined in
 *  QualifyInnerClassCheck test and add this check to checks.xml and CheckTest.
 */
public final class QualifyInnerClassCheck extends AbstractCheck {
    // FIXME: do we need to clear it in the end?
    private Set<String> nestedClasses = new HashSet<>();
    private boolean rootClassVisited = false;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.CLASS_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.LITERAL_NEW
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
        if (ast.getType() == TokenTypes.CLASS_DEF
            || ast.getType() == TokenTypes.ENUM_DEF
            || ast.getType() == TokenTypes.INTERFACE_DEF) {
            if (!this.rootClassVisited) {
                // this is first time we see a class, so let's assume
                // that all other classes are (possibly indirectly) nested
                // and manually scan for them
                this.rootClassVisited = true;
                this.scanClass(ast);
            }
        }
        if (ast.getType() == TokenTypes.LITERAL_NEW) {
            this.visitNewExpression(ast);
        }
    }

    /**
     * Checks if class to be instantiated is nested and unqualified
     * @param expr EXPR LITERAL_NEW node that needs to be checked
     */
    private void visitNewExpression(final DetailAST expr) {
        DetailAST child = expr.getFirstChild();
        if (child.getType() == TokenTypes.DOT) {
            // new Foo.Bar

            // FIXME: check this case
        } else if (child.getType() == TokenTypes.IDENT) {
            // new Foo
            if (this.nestedClasses.contains(child.getText())) {
                this.log(child, "Static inner class should be qualified with outer class");
            }
        } else {
            throw new IllegalStateException("unsupported input " + child.getType());
        }
    }


    /**
     * Scans class for all nested sub-classes
     * 
     * @param node Class-like AST node that needs to be checked
     */
    // FIXME: checkstyle discourages manual traversing of AST,
    // but exactly this is happening here.
    private void scanClass(final DetailAST node) {
        this.nestedClasses.add(getClassName(node));
        DetailAST content = node.findFirstToken(TokenTypes.OBJBLOCK);
        if (content == null) {
            return;
        }
        for (DetailAST child = content.getFirstChild(); child != null; child  = child.getNextSibling()) {
            if (child.getType() == TokenTypes.CLASS_DEF
             || child.getType() == TokenTypes.ENUM_DEF
             || child.getType() == TokenTypes.INTERFACE_DEF) {
                scanClass(child);
            }
        }
    }

    /**
     * @param clazz Class-like AST node
     * @return class name
     */
    private static String getClassName(final DetailAST clazz) {
        for (DetailAST child = clazz.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.IDENT) {
                return child.getText();
            }
        }
        throw new IllegalStateException("unexpected input: can not find class name");
    }
}
