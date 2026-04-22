/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Check if the class/interface javadoc contains properly formatted author
 * and version tags.
 *
 * <p>Correct format is the following (of a class javadoc):
 *
 * <pre>
 * &#47;**
 *  * This is my new class.
 *  *
 *  * &#64;author John Doe (john&#64;example.com)
 *  * &#64;version &#36;Id&#36;
 *  *&#47;
 * public final class Foo {
 *     // ...
 * </pre>
 *
 * <p>"&#36;Id&#36;" will be replaced by a full text automatically
 * by Subversion as explained in their documentation (see link below).
 *
 * @see <a href="http://svnbook.red-bean.com/en/1.4/svn.advanced.props.special.keywords.html">Keywords substitution in Subversion</a>
 * @since 0.3
 */
public final class JavadocTagsCheck extends AbstractCheck {

    /**
     * Map of tag and its pattern.
     */
    private final List<RequiredJavaDocTag> required = new ArrayList<>(1);

    /**
     * List of prohibited javadoc tags.
     */
    private final Collection<String> prohibited =
        Arrays.asList("author", "version");

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
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
    public void init() {
        this.required.add(
            new RequiredJavaDocTag(
                "since",
                Pattern.compile(
                "^\\d+(\\.\\d+){1,2}(\\.[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$"
                ),
                this::log
            )
        );
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final String[] lines = this.getLines();
        final int start = ast.getLineNo();
        final int cstart = JavadocTagsCheck.findCommentStart(lines, start);
        final int cend = JavadocTagsCheck.findCommentEnd(lines, start);
        if (cend > cstart && cstart >= 0) {
            for (final String tag : this.prohibited) {
                this.findProhibited(lines, start, cstart, cend, tag);
            }
            for (final RequiredJavaDocTag tag : this.required) {
                tag.matchTagFormat(lines, cstart, cend);
            }
        } else {
            this.log(0, "Problem finding class/interface comment");
        }
    }

    /**
     * Find a text in lines, by going up.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private static int findTrimmedTextUp(
        final String[] lines,
        final int start,
        final String text
    ) {
        int found = -1;
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                found = pos;
                break;
            }
        }
        return found;
    }

    /**
     * Find javadoc starting comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found starting comment or -1 otherwise.
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocTagsCheck.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find javadoc ending comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private static int findCommentEnd(final String[] lines, final int start) {
        return JavadocTagsCheck.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Check if the tag text matches the format from pattern.
     * @param lines List of all lines.
     * @param start Line number where AST starts.
     * @param cstart Line number where comment starts.
     * @param cend Line number where comment ends.
     * @param tag Name of the tag.
     * @checkstyle ParameterNumber (3 lines)
     */
    private void findProhibited(
        final String[] lines,
        final int start,
        final int cstart,
        final int cend,
        final String tag
    ) {
        final List<Integer> found =
            this.findTagLineNum(lines, cstart, cend, tag);
        if (!found.isEmpty()) {
            this.log(
                start + 1,
                "Prohibited ''@{0}'' tag in class/interface comment",
                tag
            );
        }
    }

    /**
     * Find given tag in comment lines.
     * @param lines Lines to search for the tag.
     * @param start Starting line number.
     * @param end Ending line number.
     * @param tag Name of the tag to look for.
     * @return Line number with found tag or -1 otherwise.
     * @checkstyle ParameterNumber (3 lines)
     */
    private List<Integer> findTagLineNum(
        final String[] lines,
        final int start,
        final int end,
        final String tag
    ) {
        final String prefix = String.format(" * @%s ", tag);
        final List<Integer> found = new ArrayList<>(1);
        for (int pos = start; pos <= end; pos += 1) {
            final String line = lines[pos];
            if (line.contains(String.format("@%s ", tag))) {
                if (!line.trim().startsWith(prefix.trim())) {
                    this.log(
                        start + pos + 1,
                        "Line with ''@{0}'' does not start with a ''{1}''",
                        tag,
                        prefix
                    );
                    break;
                }
                found.add(pos);
            }
        }
        return found;
    }
}
