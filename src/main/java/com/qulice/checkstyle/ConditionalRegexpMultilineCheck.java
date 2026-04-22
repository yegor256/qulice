/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.checks.regexp.RegexpMultilineCheck;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Performs multiline regexp match only if a regexp condition passes.
 * @since 0.5
 */
public final class ConditionalRegexpMultilineCheck extends
    RegexpMultilineCheck {
    /**
     * Condition that has to pass.
     */
    private Pattern condition = Pattern.compile(".");

    @Override
    public void processFiltered(final File file, final FileText lines) {
        boolean found = false;
        for (final String line: lines.toLinesArray()) {
            if (this.condition.matcher(line).find()) {
                found = true;
                break;
            }
        }
        if (found) {
            super.processFiltered(file, lines);
        }
    }

    /**
     * Condition regexp that has to match before checking the core one.
     * @param cond Regexp that has to match in file.
     */
    public void setCondition(final String cond) {
        this.condition = Pattern.compile(cond);
    }
}
