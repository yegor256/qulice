/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code @checkstyle} suppression comments found in one source file.
 *
 * <p>It reads the same three forms that the filters of {@code checks.xml}
 * honor and turns each into a {@link SuppressionTag} with the range it
 * influences:</p>
 *
 * <ul>
 *  <li>the {@code (N lines)} form covers the {@code N} lines that follow
 *  the comment, the way {@code SuppressWithNearbyCommentFilter} and
 *  {@code SuppressWithNearbyTextFilter} read it;</li>
 *  <li>a {@code disable} comment opens a range that the matching
 *  {@code enable} comment closes, or the end of the file does, the way
 *  {@code SuppressWithPlainTextCommentFilter} reads it.</li>
 * </ul>
 *
 * <p>A tag inside a string or character literal is left alone, since it is
 * data rather than a suppression a reader wrote about this file.</p>
 *
 * @since 1.0
 */
final class Suppressions implements Iterable<SuppressionTag> {

    /**
     * The {@code (N lines)} form, with the check name and the line count.
     */
    private static final Pattern NEARBY = Pattern.compile(
        "@checkstyle (\\w+) \\((\\d+) lines?\\)"
    );

    /**
     * The comment that opens a {@code disable}/{@code enable} range.
     */
    private static final Pattern DISABLE = Pattern.compile(
        "@checkstyle (\\w+) disable"
    );

    /**
     * The comment that closes a {@code disable}/{@code enable} range.
     */
    private static final Pattern ENABLE = Pattern.compile(
        "@checkstyle (\\w+) enable"
    );

    /**
     * Text of the source file.
     */
    private final String text;

    /**
     * Constructor.
     * @param text Text of the source file
     */
    Suppressions(final String text) {
        this.text = text;
    }

    @Override
    public Iterator<SuppressionTag> iterator() {
        final List<String> lines = new ArrayList<>(0);
        this.text.lines().forEach(lines::add);
        final Collection<SuppressionTag> tags = new ArrayList<>(0);
        final Map<String, Integer> open = new HashMap<>(0);
        for (int idx = 0; idx < lines.size(); ++idx) {
            final int number = idx + 1;
            final String line = Suppressions.code(lines.get(idx));
            Suppressions.nearby(line, number, tags);
            final Matcher disable = Suppressions.DISABLE.matcher(line);
            while (disable.find()) {
                open.putIfAbsent(disable.group(1), number);
            }
            final Matcher enable = Suppressions.ENABLE.matcher(line);
            while (enable.find()) {
                final Integer start = open.remove(enable.group(1));
                if (start != null) {
                    tags.add(new SuppressionTag(enable.group(1), start, number));
                }
            }
        }
        for (final Map.Entry<String, Integer> entry : open.entrySet()) {
            tags.add(
                new SuppressionTag(entry.getKey(), entry.getValue(), lines.size())
            );
        }
        return tags.iterator();
    }

    /**
     * Collect the {@code (N lines)} suppressions on one line.
     * @param line Text of the line
     * @param number Number of the line
     * @param tags Where to add the suppressions found
     */
    private static void nearby(final String line, final int number,
        final Collection<SuppressionTag> tags) {
        final Matcher matcher = Suppressions.NEARBY.matcher(line);
        while (matcher.find()) {
            tags.add(
                new SuppressionTag(
                    matcher.group(1),
                    number,
                    number + Integer.parseInt(matcher.group(2))
                )
            );
        }
    }

    /**
     * Blank out the string and character literals of a line, so that a
     * {@code @checkstyle} tag inside them is not mistaken for a real
     * suppression comment.
     * @param line Text of the line
     * @return The line with the literals turned into spaces
     */
    private static String code(final String line) {
        final StringBuilder out = new StringBuilder(line.length());
        char quote = 0;
        boolean escape = false;
        for (int idx = 0; idx < line.length(); ++idx) {
            final char chr = line.charAt(idx);
            if (quote == 0 && (chr == '"' || chr == '\'')) {
                quote = chr;
                out.append(chr);
            } else if (quote == 0) {
                out.append(chr);
            } else if (escape) {
                escape = false;
                out.append(' ');
            } else if (chr == '\\') {
                escape = true;
                out.append(' ');
            } else if (chr == quote) {
                quote = 0;
                out.append(chr);
            } else {
                out.append(' ');
            }
        }
        return out.toString();
    }
}
