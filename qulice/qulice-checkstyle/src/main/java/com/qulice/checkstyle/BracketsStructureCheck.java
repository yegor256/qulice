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
 * Checks opening/closing brackets to be the last symbols on the line.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 * @todo #32:1h! Checks only method calls inside method bodies,
 *  constructors, static initializers, and instance initializers. We should
 *  extend its functionality and enable checking of all other language
 *  constructs.
 */
public final class BracketsStructureCheck extends Check {

    /**
     * Error message.
     */
    private static final String ERROR_MESSAGE = "Brackets structure is broken";

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_NEW,
            TokenTypes.METHOD_CALL,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(final DetailAST ast) {
        int type = TokenTypes.ELIST;
        final int nodeType = ast.getType();
        if ((TokenTypes.CTOR_DEF == nodeType)
            || (TokenTypes.METHOD_DEF == nodeType)) {
            type = TokenTypes.PARAMETERS;
        }
        this.checkMethod(ast, type);
    }

    /**
     * Checks method call statement to satisfy the rule.
     * @param node Tree node, containing method call statement.
     * @param type Type, containing parameters (depends on
     *  <code>node</code> type).
     */
    private void checkMethod(final DetailAST node, final int type) {
        DetailAST opening = node;
        if (TokenTypes.METHOD_CALL != node.getType()) {
            opening = node.findFirstToken(TokenTypes.LPAREN);
        }
        final DetailAST closing = node.findFirstToken(TokenTypes.RPAREN);
        final int startLine = opening.getLineNo();
        final int endLine = closing.getLineNo();
        if (startLine != endLine) {
            final DetailAST elist = node.findFirstToken(type);
            final int parametersLine = elist.getLineNo();
            if (parametersLine == startLine) {
                this.log(parametersLine, this.ERROR_MESSAGE);
            }
            final DetailAST lastParameter = elist.getLastChild();
            final int lastParameterLine = lastParameter.getLineNo();
            if (lastParameterLine == endLine) {
                this.log(lastParameterLine, this.ERROR_MESSAGE);
            }
        }
    }
}
