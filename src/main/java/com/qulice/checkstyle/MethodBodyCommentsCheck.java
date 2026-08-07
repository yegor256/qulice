/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Arrays;

/**
 * Checks method bodies for comments. All comments in method bodies are
 * prohibited.
 *
 * <p>We believe that in-code comments and empty lines are evil. If you
 * need to use
 * a comment inside a method - your code needs refactoring. Either move that
 * comment to a method javadoc block or add a logging mechanism with the same
 * text.
 *
 * @since 0.3
 */
public final class MethodBodyCommentsCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public MethodBodyCommentsCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
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
        final DetailAST start = ast.findFirstToken(TokenTypes.SLIST);
        if (start != null) {
            final String[] lines = Arrays.copyOf(
                this.getLines(), this.getLines().length
            );
            this.maskAnonymous(start, lines);
            this.checkMethod(
                lines,
                start.getLineNo(),
                start.findFirstToken(TokenTypes.RCURLY).getLineNo() - 1
            );
        }
    }

    /**
     * Replace with empty strings the lines that belong to the body of any
     * anonymous class found anywhere in the given subtree.
     * @param node Root of the subtree to scan
     * @param lines Lines to be modified in place
     */
    private void maskAnonymous(final DetailAST node, final String... lines) {
        for (DetailAST child = node.getFirstChild(); child != null;
            child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.LITERAL_NEW) {
                final DetailAST block = child.findFirstToken(
                    TokenTypes.OBJBLOCK
                );
                if (block != null) {
                    Arrays.fill(
                        lines, block.getLineNo(),
                        block.findFirstToken(TokenTypes.RCURLY).getLineNo(),
                        ""
                    );
                }
            }
            this.maskAnonymous(child, lines);
        }
    }

    /**
     * Checks method body for comments.
     * @param lines Array of lines, containing code to check
     * @param start Start line of the method body
     * @param end End line of the method body
     */
    private void checkMethod(final String[] lines, final int start,
        final int end) {
        final boolean oneliner = start == end - 1;
        for (int pos = start; pos < end; ++pos) {
            final String line = lines[pos].trim();
            if (line.startsWith("//") || line.startsWith("/*")) {
                final String comment = line.substring(2).trim();
                if (!comment.startsWith("@checkstyle") && !oneliner
                    && !MethodBodyCommentsCheck.alone(lines, pos)) {
                    this.log(pos + 1, "Comments in method body are prohibited");
                }
            }
        }
    }

    /**
     * Is the comment on the given line the only line inside its
     * immediately enclosing braces?
     *
     * <p>A comment sitting alone between an opening and a closing brace
     * documents an otherwise empty block. Some Checkstyle modules, such
     * as {@code EmptyCatchBlock}, require exactly such a comment, so it
     * must not be reported as a prohibited in-body comment.
     *
     * @param lines All lines of the file being checked
     * @param pos Zero-based index of the comment line
     * @return TRUE if the comment is alone between its enclosing braces
     */
    private static boolean alone(final String[] lines, final int pos) {
        return pos > 0 && pos + 1 < lines.length
            && lines[pos - 1].trim().endsWith("{")
            && lines[pos + 1].trim().startsWith("}");
    }
}
