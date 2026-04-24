/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check for text on the first line of a multi-line Javadoc.
 *
 * <p>You can't have a description on the same line as the opening
 * {@code /**}. Either keep the whole Javadoc on a single line, or move
 * the text to a new line under the opening.
 *
 * <p>The following red line will be reported as a violation.
 * <pre>
 * <span style="color:red" >&#47;** Some text</span>
 *  *&#47;
 * public void method() {
 * }
 * </pre>
 *
 * @since 0.24.1
 */
public final class JavadocFirstLineCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
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
        final String[] lines = this.getLines();
        final int start = JavadocFirstLineCheck.findOpeningLine(
            lines, ast.getLineNo() - 1
        );
        if (start >= 0 && JavadocFirstLineCheck.belongsToNode(ast, start)
            && JavadocFirstLineCheck.hasTextAfterOpening(lines[start])
            && !JavadocFirstLineCheck.hasClosingOnSameLine(lines[start])) {
            this.log(start + 1, "No text allowed on the first line of Javadoc");
        }
    }

    /**
     * Find the line that opens the Javadoc right above a node.
     * @param lines All lines of the file
     * @param below Line index of the node (0-based)
     * @return Line index (0-based) of the opening, or -1 if not found
     */
    private static int findOpeningLine(final String[] lines, final int below) {
        int found = -1;
        for (int pos = below - 1; pos >= 0; pos -= 1) {
            final String trimmed = lines[pos].trim();
            if (trimmed.startsWith("/**")) {
                found = pos;
                break;
            }
            if (!trimmed.isEmpty() && !trimmed.startsWith("*")
                && !trimmed.endsWith("*/")) {
                break;
            }
        }
        return found;
    }

    /**
     * Check that the found Javadoc directly precedes the node.
     * @param node Node being inspected
     * @param start Line index (0-based) of the Javadoc opening
     * @return True when no other declaration sits between
     */
    private static boolean belongsToNode(final DetailAST node, final int start) {
        final DetailAST previous = node.getPreviousSibling();
        boolean owns = true;
        if (previous != null) {
            owns = start + 1 > previous.getLineNo();
        }
        return owns;
    }

    /**
     * Check if a line has any text after the opening {@code /**}.
     * @param line The opening line
     * @return True when text sits on the same line
     */
    private static boolean hasTextAfterOpening(final String line) {
        final String trimmed = line.trim();
        final String rest = trimmed.substring("/**".length()).trim();
        return !rest.isEmpty() && !"/".equals(rest);
    }

    /**
     * Check if the Javadoc closes on the same line.
     * @param line The opening line
     * @return True when {@code *&#47;} is on the same line
     */
    private static boolean hasClosingOnSameLine(final String line) {
        return line.trim().endsWith("*/");
    }
}
