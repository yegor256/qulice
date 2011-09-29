/**
 * Copyright (c) 2011, Qulice.com
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
 * Check if indentation on every level has fixed number of spaces.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class CascadeIndentationCheck extends AbstractFileSetCheck {

    /**
     * How many spaces are allowed?
     */
    private static final Integer DELTA = 4;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processFiltered(final File file, final List<String> lines) {
        boolean failure = false;
        int previous = 0;
        for (int pos = 0; pos < lines.size(); pos += 1) {
            final String line = lines.get(pos);
            final int current = this.indentation(line);
            if (current == 0) {
                continue;
            }
            final int diff = current - previous;
            if (diff > 0 && diff != this.DELTA) {
                this.log(
                    pos + 1,
                    String.format(
                        "Should be indented by %d spaces (%d instead)",
                        previous + this.DELTA,
                        current
                    )
                );
                failure = true;
                continue;
            }
            previous = current;
        }
        if (failure) {
            this.fireErrors(file.getPath());
        }
    }

    /**
     * Calculate indentation of this string (how many spaces are on the left).
     * @param line The line
     * @return Number of spaces found
     */
    private int indentation(final String line) {
        int pos;
        for (pos = 0; pos < line.length(); pos += 1) {
            if (line.charAt(pos) == '*') {
                pos -= 1;
                break;
            }
            if (line.charAt(pos) != ' ') {
                break;
            }
        }
        return pos;
    }

}
