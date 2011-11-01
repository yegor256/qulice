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
import org.apache.commons.lang.StringUtils;

/**
 * Checks opening/closing brackets to be the last symbols on the line. So this
 * will do:<br>
 *       String.format(<br>
 *        "File %s not found",<br>
 *        file<br>
 *      );<br>
 *      String.format(<br>
 *        "File %s not found", file<br>
 *      );<br>
 *      String.format("File %s not found", file);<br>
 * and this won't:<br>
 *      String.format("File %s not found",<br>
 *        file);<br>
 *      String.format(<br>
 *        "File %s not found",<br>
 *        file);<br>
 *      String.format(<br>
 *        "File %s not found", file);<br>
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 */
public final class BracketsStructureCheck extends Check {
    /**
     * Opening bracket.
     */
    private static final String OPENING_BRACKET = "(";
    /**
     * Closing bracket.
     */
    private static final String CLOSING_BRACKET = ")";
    /**
     * Closing bracket with semicolon.
     */
    private static final String CLOSING_BRACKET_1 = ");";
    /**
     * Error message.
     */
    private static final String ERROR_MESSAGE = "Brackets structure is broken";

    /**
     * Creates new instance of <code>BracketsStructureCheck</code>.
     */
    public BracketsStructureCheck() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.OBJBLOCK,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(final DetailAST ast) {
        DetailAST declaration = ast.getFirstChild();
        // Find all methods, constructors, static initializers.
        while (null != declaration) {
            if (TokenTypes.VARIABLE_DEF == declaration.getType()) {
                continue;
            }
            // Retrieve body.
            final DetailAST opening =
                declaration.findFirstToken(TokenTypes.SLIST);
            if (opening != null) {
                final DetailAST closing =
                    opening.findFirstToken(TokenTypes.RCURLY);
                final int firstLine = opening.getLineNo();
                final int lastLine = closing.getLineNo();
                final String[] lines = this.getLines();
                for (int i = firstLine; i < lastLine; i += 1) {
                    // Check line.
                    this.checkLine(lines[i], i + 1);
                }
            }
            declaration = declaration.getNextSibling();
        }
    }

    /**
     * Checks input string to validate check rule.
     * @param input String to be checked.
     * @param number Line number of the input string.
     */
    private void checkLine(final String input, final int number) {
        final String line = input.trim();
        final int opened = StringUtils.countMatches(line, this.OPENING_BRACKET);
        final int closed = StringUtils.countMatches(line, this.CLOSING_BRACKET);
        if (opened > closed) {
            if (!line.endsWith(this.OPENING_BRACKET)) {
                this.log(number, this.ERROR_MESSAGE);
            }
        } else if (closed > opened) {
            if (
                !line.equals(this.CLOSING_BRACKET)
                    && !line.equals(this.CLOSING_BRACKET_1)
                ) {
                this.log(number, this.ERROR_MESSAGE);
            }
        }
    }
}
