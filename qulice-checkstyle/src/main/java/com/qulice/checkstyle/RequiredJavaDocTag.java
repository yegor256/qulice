/*
 * Copyright (c) 2011-2024 Qulice.com
 *
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check the required JavaDoc tag in the lines.
 * <p>Correct format is the following (of a class javadoc):
 *
 * <pre>
 * &#47;**
 *  * This is my new class.
 *  *
 *  * &#64;since 0.3
 *  *&#47;
 * public final class Foo {
 *     &#47;**
 *      * This is my other class.
 *      *
 *      *    &#64;since    0.3
 *      *&#47;
 *     public final class Boo {
 *     // ...
 * </pre>
 *
 * <p>"&#36;Id&#36;" will be replaced by a full text automatically
 * by Subversion as explained in their documentation (see link below).
 *
 * @see <a href="http://svnbook.red-bean.com/en/1.4/svn.advanced.props.special.keywords.html">Keywords substitution in Subversion</a>

 * @since 0.23.1
 */
final class RequiredJavaDocTag {
    /**
     * Tag name.
     */
    private final String name;

    /**
     * Pattern for searching a tag in a string.
     */
    private final Pattern tag;

    /**
     * Pattern for checking the contents of a tag in a string.
     */
    private final Pattern content;

    /**
     * Reference to a method for writing a message to the log.
     */
    private final Reporter reporter;

    /**
     * Ctor.
     * @param name Tag name.
     * @param patt Pattern for checking the contents of a tag in a string.
     * @param rep Reference to a method for writing a message to the log.
     */
    RequiredJavaDocTag(final String name, final Pattern patt,
        final Reporter rep) {
        this(
            name,
            Pattern.compile(
                String.format(
                    "(?<name>^ +\\* +@%s)( +)(?<cont>.*)",
                    name
                )
            ),
            patt,
            rep
        );
    }

    /**
     * Ctor.
     * @param cname Tag name.
     * @param ptag Pattern for searching a tag in a string.
     * @param patt Pattern for checking the contents of a tag in a string.
     * @param rep Reference to a method for writing a message to the log.
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    RequiredJavaDocTag(final String cname, final Pattern ptag,
        final Pattern patt, final Reporter rep) {
        this.name = cname;
        this.tag = ptag;
        this.content = patt;
        this.reporter = rep;
    }

    /**
     * Check if the tag text matches the format from pattern.
     * @param lines List of all lines.
     * @param start Line number where comment starts.
     * @param end Line number where comment ends.
     */
    public void matchTagFormat(final String[] lines, final int start,
        final int end) {
        final Map<Integer, String> found = new HashMap<>(1);
        for (int pos = start; pos <= end; pos += 1) {
            final String line = lines[pos];
            final Matcher matcher = this.tag.matcher(line);
            if (RequiredJavaDocTag.tagFound(matcher)) {
                found.put(pos, matcher.group("cont"));
                break;
            }
        }
        if (found.isEmpty()) {
            this.reporter.log(
                start + 1,
                "Missing ''@{0}'' tag in class/interface comment",
                this.name
            );
        } else {
            for (final Map.Entry<Integer, String> item : found.entrySet()) {
                if (!this.content.matcher(item.getValue()).matches()) {
                    this.reporter.log(
                        item.getKey() + 1,
                        "Tag text ''{0}'' does not match the pattern ''{1}''",
                        item.getValue(),
                        this.content.toString()
                    );
                }
            }
        }
    }

    /**
     * Finds the tag name and the following sentences.
     * @param matcher Tag name matcher.
     * @return True if the tag and its clauses are found.
     */
    private static boolean tagFound(final Matcher matcher) {
        return matcher.matches()
            && !RequiredJavaDocTag.empty(matcher.group("name"))
            && !RequiredJavaDocTag.empty(matcher.group("cont"));
    }

    /**
     * Checks for an empty string.
     * @param str Line to check.
     * @return True if str is empty.
     */
    private static boolean empty(final String str) {
        return str == null || str.isBlank();
    }

    /**
     * Logger.
     * @see com.puppycrawl.tools.checkstyle.api.AbstractCheck#log(int, String, Object...)
     * @since 0.23.1
     */
    interface Reporter {
        /**
         * Log a message that has no column information.
         *
         * @param line The line number where the audit event was found.
         * @param msg The message that describes the audit event.
         * @param args The details of the message.
         * @see java.text.MessageFormat
         */
        void log(int line, String msg, Object... args);
    }
}
