/**
 * Copyright (c) 2011-2014, Qulice.com
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Check for duplicate todo puzzles.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 * @checkstyle PuzzleFormat (200 lines)
 */
public final class PuzzleDuplicateCheck extends AbstractFileSetCheck {

    /**
     * Todo marker.
     */
    private static final String TODO_TAG = "@todo ";

    /**
     * All seen puzzle texts.
     */
    private final transient Collection<String> puzzles =
        new ConcurrentSkipListSet<String>();

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void processFiltered(final File file, final List<String> lines) {
        for (int pos = 0; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            // @checkstyle PuzzleFormat (1 line)
            if (line.contains(PuzzleDuplicateCheck.TODO_TAG)) {
                final StringBuilder text = new StringBuilder();
                final int col = line.indexOf(PuzzleDuplicateCheck.TODO_TAG);
                text.append(
                    StringUtils.substringAfter(
                        StringUtils.substringAfter(
                            line, PuzzleDuplicateCheck.TODO_TAG
                        ),
                        StringUtils.SPACE
                    ).trim()
                );
                text.append(StringUtils.SPACE)
                    .append(this.rest(lines, pos, col));
                if (this.puzzles.contains(text.toString())) {
                    this.log(
                        pos + 1,
                        String.format(
                            "@todo \"%s\" already exists in the project",
                            text.toString()
                        )
                    );
                } else {
                    this.puzzles.add(text.toString());
                }
            }
        }
    }

    /**
     * Get rest of todo tag description.
     * @param lines All lines in a file.
     * @param start Line number of todo tag start.
     * @param col Column at which puzzle text should start.
     * @return Rest of todo description.
     */
    private String rest(final List<String> lines, final int start,
        final int col) {
        final StringBuilder text = new StringBuilder();
        for (int pos = start + 1; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            if ((line.length() <= col) || (line.charAt(col) != ' ')) {
                break;
            }
            text.append(line.substring(col + 1)).append(StringUtils.SPACE);
        }
        return text.toString().trim();
    }

}
