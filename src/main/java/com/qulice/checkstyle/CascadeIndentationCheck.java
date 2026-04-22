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
 * All other cases must cause a failure.
 *
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
            }
            previous = current;
        }
    }

    /**
     * Checks if the line belongs to a comment block.
     * @param line Input.
     * @return True if the line belongs to a comment block.
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
     * @return Indentation of the given line.
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
