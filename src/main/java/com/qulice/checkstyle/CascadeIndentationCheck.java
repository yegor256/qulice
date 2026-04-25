/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;
import java.io.File;
import org.cactoos.text.Joined;

/**
 * Make sure each line indentation is either:
 * <ul>
 * <li>the same as previous one or less
 * <li>bigger than previous by exactly 4
 * </ul>
 * Also, if the previous non-empty line consists only of closing brackets
 * (and optional trailing semicolon or comma), the current line indentation
 * must not be greater than that of the closing bracket line, since the
 * expression has been already terminated.
 * All other cases must cause a failure.
 * @since 0.3
 */
public final class CascadeIndentationCheck extends AbstractFileSetCheck {

    /**
     * Exact indentation increase difference.
     */
    private static final int LINE_INDENT_DIFF = 4;

    @Override
    public void processFiltered(final File file, final FileText lines) {
        int previous = 0;
        boolean closer = false;
        for (int pos = 0; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            final int current = CascadeIndentationCheck.indentation(line);
            if (CascadeIndentationCheck.inCommentBlock(line)
                || line.isEmpty()) {
                continue;
            }
            if (current > previous
                && current != previous
                + CascadeIndentationCheck.LINE_INDENT_DIFF) {
                this.log(
                    pos + 1,
                    String.format(
                        new Joined(
                            "",
                            "Indentation (%d) must be same or ",
                            "less than previous line (%d), or ",
                            "bigger by exactly 4"
                        ).toString(),
                        current,
                        previous
                    )
                );
            } else if (closer && current > previous) {
                this.log(
                    pos + 1,
                    String.format(
                        new Joined(
                            "",
                            "Indentation (%d) must not be greater ",
                            "than the closing bracket line (%d)"
                        ).toString(),
                        current,
                        previous
                    )
                );
            }
            previous = current;
            closer = CascadeIndentationCheck.isClosingBracketLine(line);
        }
    }

    /**
     * Tells whether the line consists only of closing brackets, optionally
     * followed by a comma or a semicolon and surrounding whitespace.
     * @param line Input line
     * @return True if the line is a standalone closing bracket line
     */
    private static boolean isClosingBracketLine(final String line) {
        final String trimmed = line.trim();
        boolean result = !trimmed.isEmpty()
            && CascadeIndentationCheck.isClosingBracket(trimmed.charAt(0));
        for (int idx = 0; result && idx < trimmed.length(); idx += 1) {
            result = CascadeIndentationCheck.isAllowedTail(trimmed.charAt(idx));
        }
        return result;
    }

    /**
     * Tells whether the character is a closing bracket.
     * @param chr Character
     * @return True if it is one of ')', ']', '}'
     */
    private static boolean isClosingBracket(final char chr) {
        return chr == ')' || chr == ']' || chr == '}';
    }

    /**
     * Tells whether a character is allowed inside a standalone
     * closing-bracket line (closing bracket, comma, semicolon or
     * whitespace).
     * @param chr Character
     * @return True if the character is allowed
     */
    private static boolean isAllowedTail(final char chr) {
        return CascadeIndentationCheck.isClosingBracket(chr)
            || chr == ';' || chr == ','
            || Character.isWhitespace(chr);
    }

    /**
     * Checks if the line belongs to a comment block.
     * @param line Input
     * @return True if the line belongs to a comment block
     */
    private static boolean inCommentBlock(final String line) {
        final String trimmed = line.trim();
        return !trimmed.isEmpty()
            && (trimmed.charAt(0) == '*'
                || trimmed.startsWith("/*")
                || trimmed.startsWith("*/")
                );
    }

    /**
     * Calculates indentation of a line.
     * @param line Input line
     * @return Indentation of the given line
     */
    private static int indentation(final String line) {
        int result = 0;
        for (int pos = 0; pos < line.length(); pos += 1) {
            if (!Character.isWhitespace(line.charAt(pos))) {
                break;
            }
            result += 1;
        }
        return result;
    }
}
