/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
