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
 * expression has been already terminated. Moreover, two such closing
 * bracket lines may not follow each other at the very same indentation,
 * since every one of them closes exactly one more nesting level and thus
 * must be shallower than the one above it.
 * All other cases must cause a failure.
 * @since 0.3
 */
public final class CascadeIndentationCheck extends AbstractFileSetCheck {

    /**
     * Default constructor.
     */
    public CascadeIndentationCheck() {
        // nothing to initialize
    }

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
            final String message = CascadeIndentationCheck.violation(
                line, current, previous, closer
            );
            if (!message.isEmpty()) {
                this.log(pos + 1, message);
            }
            previous = current;
            closer = CascadeIndentationCheck.isClosingBracketLine(line);
        }
    }

    private static String violation(final String line, final int current,
        final int previous, final boolean closer) {
        final String message;
        if (current > previous
            && current != previous + 4) {
            message = String.format(
                new Joined(
                    "",
                    "Indentation (%d) must be same or ",
                    "less than previous line (%d), or ",
                    "bigger by exactly 4"
                ).toString(),
                current,
                previous
            );
        } else if (closer && current > previous) {
            message = String.format(
                new Joined(
                    "",
                    "Indentation (%d) must not be greater ",
                    "than the closing bracket line (%d)"
                ).toString(),
                current,
                previous
            );
        } else if (closer && current == previous
            && CascadeIndentationCheck.isClosingBracketLine(line)) {
            message = String.format(
                new Joined(
                    "",
                    "Indentation (%d) of this closing bracket line must be ",
                    "less than the one of the closing bracket line above it"
                ).toString(),
                current
            );
        } else {
            message = "";
        }
        return message;
    }

    private static boolean isClosingBracketLine(final String line) {
        final String trimmed = line.trim();
        boolean result = !trimmed.isEmpty()
            && CascadeIndentationCheck.isClosingBracket(trimmed.charAt(0));
        for (int idx = 0; result && idx < trimmed.length(); idx += 1) {
            result = CascadeIndentationCheck.isAllowedTail(trimmed.charAt(idx));
        }
        return result;
    }

    private static boolean isClosingBracket(final char chr) {
        return chr == ')' || chr == ']' || chr == '}';
    }

    private static boolean isAllowedTail(final char chr) {
        return CascadeIndentationCheck.isClosingBracket(chr)
            || chr == ';' || chr == ','
            || Character.isWhitespace(chr);
    }

    private static boolean inCommentBlock(final String line) {
        final String trimmed = line.trim();
        return !trimmed.isEmpty()
            && (trimmed.charAt(0) == '*'
                || trimmed.startsWith("/*")
                || trimmed.startsWith("*/")
                );
    }

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
