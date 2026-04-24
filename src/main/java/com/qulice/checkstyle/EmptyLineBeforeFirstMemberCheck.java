/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Pattern;

/**
 * Checks that a class, interface, enum or annotation declaration is followed
 * by an empty line before its first member.
 *
 * <p>Some developers add an empty line after the opening brace of a
 * class, interface or enum for aesthetic reasons, while others consider it
 * wasted vertical space. This check enforces the presence of such a blank
 * line so that the style is consistent across the whole codebase.
 *
 * <p>The following code will be reported as a violation because the first
 * member is not preceded by an empty line:
 * <pre>
 * class Foo {
 *     private int bar;
 * }
 * </pre>
 *
 * <p>Empty type bodies and one-line declarations are not reported.
 *
 * @since 0.24
 */
public final class EmptyLineBeforeFirstMemberCheck extends AbstractCheck {

    /**
     * Pattern matching an empty (whitespace-only) line.
     */
    private static final Pattern BLANK = Pattern.compile("^\\s*$");

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
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
        final DetailAST block = ast.findFirstToken(TokenTypes.OBJBLOCK);
        if (block != null) {
            this.visitBlock(block);
        }
    }

    /**
     * Check the body between curly braces of the given object block.
     * @param block OBJBLOCK node whose body must be inspected.
     */
    private void visitBlock(final DetailAST block) {
        final DetailAST left = block.findFirstToken(TokenTypes.LCURLY);
        final DetailAST right = block.findFirstToken(TokenTypes.RCURLY);
        if (left != null && right != null
            && right.getLineNo() - left.getLineNo() >= 2
            && !EmptyLineBeforeFirstMemberCheck.BLANK
                .matcher(this.getLines()[left.getLineNo()]).find()) {
            this.log(
                left.getLineNo() + 1,
                "Expected empty line before first member"
            );
        }
    }
}
