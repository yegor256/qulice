/**
 * Copyright (c) 2011, Qulice.com
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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that there is no empty line between a javadoc and it's subject.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 */
public final class JavadocLocationCheck extends Check {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(final DetailAST ast) {
        if (!this.isField(ast)) {
            return;
        }
        final String[] lines = this.getLines();
        final int current = ast.getLineNo();
        final int commentEnd = this.findCommentEnd(lines, current);
        final int commentMinimum = this.getCommentMinimum(ast);
        if (commentEnd <= commentMinimum) {
            this.log(current, "Problem finding javadoc");
            return;
        }
        final int diff = current - commentEnd;
        if (diff > 1) {
            for (int i = 1; i < diff; i += 1) {
                this.log(
                    commentEnd + i,
                    "Empty line between javadoc and subject"
                );
            }
        }
    }

    /**
     * Returns mimimum line number of the end of the comment.
     * @param node Node to be checked for Java docs.
     * @return Mimimum line number of the end of the comment.
     */
    private int getCommentMinimum(final DetailAST node) {
        int minimum = 0;
        final DetailAST parent = node.getParent();
        if (null != parent) {
            DetailAST previous = node.getPreviousSibling();
            if (null == previous) {
                previous = parent;
            }
            minimum = previous.getLineNo();
        } else if (!this.isFirst(node)) {
            final DetailAST previous = node.getPreviousSibling();
            final DetailAST object =
                previous.findFirstToken(TokenTypes.OBJBLOCK);
            final DetailAST closing = object.getLastChild();
            minimum = closing.getLineNo();
        }
        return minimum;
    }

    /**
     * Checks the specified node: is it first element or not.
     * @param node Node to be checked.
     * @return True if there are no any nodes before this one, else -
     * <code>false</code>.
     */
    private boolean isFirst(final DetailAST node) {
        final DetailAST previous = node.getPreviousSibling();
        return null == previous;
    }

    /**
     * Checks input nodes: if specified node is variable method returns
     * <code>false</code> if node is not a field. Otherwise it returns
     * <code>true</code>.
     * @param node Node to check.
     * @return False if the specified node is a field, otherwise it returns
     * <code>true</code>.
     */
    private boolean isField(final DetailAST node) {
        if (TokenTypes.VARIABLE_DEF != node.getType()) {
            return true;
        }
        final DetailAST parent = node.getParent();
        return TokenTypes.OBJBLOCK == parent.getType();
    }

    /**
     * Find javadoc ending comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private int findCommentEnd(final String[] lines, final int start) {
        return this.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Find a text in lines, by going up.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private int findTrimmedTextUp(final String[] lines,
        final int start, final String text) {
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                return pos + 1;
            }
        }
        return -1;
    }
}
