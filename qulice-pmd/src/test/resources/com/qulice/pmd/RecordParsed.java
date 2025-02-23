/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public record RecordParsed(
        Object symbol,
        int line,
        String msg
) {
}
