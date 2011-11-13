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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check indents in multi line JavaDoc tags.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 */
public final class MultilineJavadocTagsCheck extends Check {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(final DetailAST ast) {
        final String[] lines = this.getLines();
        final int start = ast.getLineNo();
        final int commentStart = this.findCommentStart(lines, start) + 1;
        final int commentEnd = this.findCommentEnd(lines, start) - 1;
        if ((commentEnd > commentStart) && (commentStart >= 0)) {
            this.checkJavaDoc(lines, commentStart, commentEnd);
        } else {
            this.log(0, "Problem finding method comment");
        }
    }

    /**
     * Checks method's Java Doc for satisfy indentation rules.
     * @param lines Code of the whole class.
     * @param start Start line of the Java Doc.
     * @param end End line of the Java Doc.
     */
    private void checkJavaDoc(final String[] lines, final int start,
        final int end) {
        boolean isTagged = false;
        int tagIndex = -1;
        for (int current = start; current <= end; current += 1) {
            final String line = lines[current];
            if (line.contains("* @")) {
                isTagged = true;
                tagIndex = line.indexOf('@');
            } else {
                if (isTagged) {
                    final int startComment = line.indexOf('*');
                    int startText = startComment + 1;
                    while ((startText < line.length())
                        && (line.charAt(startText) == ' ')) {
                        startText += 1;
                    }
                    if (startText != (tagIndex + 1)) {
                        this.log(
                            current + 1,
                            "Should contain one indentation space"
                        );
                    }
                }
            }
        }
    }

    /**
     * Find javadoc starting comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found starting comment or -1 otherwise.
     */
    private int findCommentStart(final String[] lines, final int start) {
        return this.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find javadoc ending comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private int findCommentEnd(final String[] lines, final int start) {
        return this.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Find a text in lines, by going up.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private int findTrimmedTextUp(final String[] lines,
        final int start, final String text) {
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                return pos;
            }
        }
        return -1;
    }
}
