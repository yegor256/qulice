/**
 * Copyright (c) 2011-2012, Qulice.com
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

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Check format of PDD puzzles.
 *
 * <p>This is how you should format them:
 *
 * <pre>
 * &#47;**
 *  * This is my new method.
 *  * &#64;todo #123:1hr! I will implement it later, when more information
 *  *  come to light and I have some extra documentation
 *  *&#47;
 * public void func() {
 *     // ...
 * }
 * </pre>
 *
 * <p>Full syntax is explained
 * <a href="http://www.tpc2.com/dev/pdd">here</a>.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @see <a href="http://www.tpc2.com/dev/pdd">Puzzle Driven Development</a>
 * @checkstyle PuzzleFormat (200 lines)
 */
public final class PuzzleFormatCheck extends AbstractFileSetCheck {

    /**
     * Pattern first line of todo tag.
     * @checkstyle LineLength (3 lines)
     */
    private static final Pattern FIRST = Pattern.compile(
        "^\\s+\\* @todo #[\\w\\d:\\-]+!?(:[0-9]+(\\.[0-9]){0,2}hrs?)? [A-Z][^\n]+$"
    );

    /**
     * Pattern for the rest of todo lines.
     */
    private static final Pattern FOLLOWING =
        Pattern.compile("^\\s+\\*  [^ ].+$");

    /**
     * Pattern marking the end of todo text.
     */
    private static final Pattern OTHER =
        Pattern.compile("^\\s+\\*(/?| *@.*)$");

    /**
     * {@inheritDoc}
     */
    @Override
    public void processFiltered(final File file, final List<String> lines) {
        boolean failure = false;
        for (int pos = 0; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            // @checkstyle PuzzleFormat (1 line)
            if (line.contains("@todo")) {
                if (!PuzzleFormatCheck.FIRST.matcher(line).matches()) {
                    // @checkstyle PuzzleFormat (1 line)
                    this.log(pos + 1, "@todo tag has wrong format");
                    failure = true;
                }
                final List<Integer> defects = this.indentDefects(lines, pos);
                if (!defects.isEmpty()) {
                    for (int defect : defects) {
                        this.log(
                            defect + 1,
                            "One space indentation expected in @todo puzzle"
                        );
                        failure = true;
                    }
                }
                if (!this.isInsideJavadoc(lines, pos)) {
                    this.log(
                        pos + 1,
                        // @checkstyle PuzzleFormat (1 line)
                        "@todo puzzles are allowed only in javadoc blocks"
                    );
                }
            }
        }
        if (failure) {
            this.fireErrors(file.getPath());
        }
    }

    /**
     * Check if todo tag is inside a class or method javadoc.
     * @param lines All lines in a file.
     * @param start Line number of todo tag start.
     * @return If the tag is inside the javadoc.
     */
    private boolean isInsideJavadoc(final List<String> lines, final int start) {
        return this.hasMarker(lines, start, -1, "/**")
            && this.hasMarker(lines, start, 1, "*/");
    }

    /**
     * Is there a line with a marker around this start line?
     * @param lines All lines in a file.
     * @param start Line number of todo tag start.
     * @param direction In which direction to go
     * @param marker The marker to find
     * @return The marker is there?
     * @checkstyle ParameterNumber (3 lines)
     */
    private boolean hasMarker(final List<String> lines, final int start,
        final int direction, final String marker) {
        boolean found = false;
        for (int pos = start + direction; pos >= 0; pos += direction) {
            final String line = lines.get(pos);
            if (line.trim().equals(marker)) {
                found = true;
                break;
            }
            if (!line.trim().startsWith("*")) {
                found = false;
                break;
            }
        }
        return found;
    }

    /**
     * Check the rest of todo tag lines, and find defective lines.
     * @param lines All lines in a file.
     * @param start Line number of todo tag start.
     * @return List of line numbers that have wrong format.
     */
    private List<Integer> indentDefects(final List<String> lines,
        final int start) {
        final List<Integer> defects = new ArrayList<Integer>();
        for (int pos = start + 1; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            if (PuzzleFormatCheck.OTHER.matcher(line).matches()) {
                break;
            }
            if (!PuzzleFormatCheck.FOLLOWING.matcher(line).matches()) {
                defects.add(pos);
            }
        }
        return defects;
    }

}
