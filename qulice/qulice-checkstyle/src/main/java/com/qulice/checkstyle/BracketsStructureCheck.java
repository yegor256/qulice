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
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * Checks opening/closing brackets to be the last symbols on the line. So this
 * will do:<br>
 *       String.format(<br>
 *        "File %s not found",<br>
 *        file<br>
 *      );<br>
 *      String.format(<br>
 *        "File %s not found", file<br>
 *      );<br>
 *      String.format("File %s not found", file);<br>
 * and this won't:<br>
 *      String.format("File %s not found",<br>
 *        file);<br>
 *      String.format(<br>
 *        "File %s not found",<br>
 *        file);<br>
 *      String.format(<br>
 *        "File %s not found", file);<br>
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 */
public final class BracketsStructureCheck extends AbstractFileSetCheck {

    /**
     * Creates new instance of <code>BracketsStructureCheck</code>.
     */
    public BracketsStructureCheck() {
    }

    @Override
    public void processFiltered(final File file, final List<String> list) {
        for (String fullLine : list) {
            // Remove spaces from start and end of the line.
            final String line = fullLine.trim();
            // Count opening brackets.
            final int start = StringUtils.countMatches(line, "(");
            // Count closing brakcets.
            final int end = StringUtils.countMatches(line, ")");
            // Check that corresponding bracket is the last symbol on the line.
            final int length = line.length();
            int index = length;
            if (start > end) {
                index = line.lastIndexOf('(') + 1;
            } else if (start < end) {
                index = line.lastIndexOf(");") + 2;
            }
            if (length != index) {
                this.fireErrors(file.getPath());
            }
        }
    }
}
