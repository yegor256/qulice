/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

@SuppressWarnings("PMD.UnnecessaryWarningSuppression")
public final class UnnecessaryWarningSuppressionOnItself {

    private final int value;

    public UnnecessaryWarningSuppressionOnItself(final int val) {
        this.value = val;
    }

    public int number() {
        return this.value;
    }

}
