/*
 * Copyright (c) 2011-2024 Qulice.com
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
 * Checks that there is no empty line between a javadoc and it's subject.
 *
 * <p>You can't have empty lines between javadoc block and
 * a class/method/variable. They should stay together, always.
 *
 * @since 0.3
 */
public final class JavadocLocationCheck extends AbstractCheck {

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
        if (!JavadocLocationCheck.isField(ast)) {
            return;
        }
        final String[] lines = this.getLines();
        int current = ast.getLineNo();
        boolean found = false;
        final int start = current;
        --current;
        while (true) {
            if (current <= 0) {
                break;
            }
            final String line = lines[current - 1].trim();
            if (line.endsWith("*/")) {
                found = true;
                break;
            }
            if (!line.isEmpty()) {
                break;
            }
            --current;
        }
        if (found) {
            this.report(start, current);
        }
    }

    /**
     * Report empty lines between current and end line.
     * @param current Current line
     * @param end Final line
     */
    private void report(final int current, final int end) {
        final int diff = current - end;
        if (diff > 1) {
            for (int pos = 1; pos < diff; pos += 1) {
                System.out.printf("report=%d\n", end + pos);
                this.log(
                    end + pos,
                    "Empty line between javadoc and subject"
                );
            }
        }
    }

    /**
     * Returns {@code TRUE} if a specified node is something that should have
     * a Javadoc, which includes classes, interface, class methods, and
     * class variables.
     * @param node Node to check
     * @return Is it a Javadoc-required entity?
     */
    private static boolean isField(final DetailAST node) {
        boolean yes = true;
        if (TokenTypes.VARIABLE_DEF == node.getType()) {
            yes = TokenTypes.OBJBLOCK == node.getParent().getType();
        }
        return yes;
    }
}
