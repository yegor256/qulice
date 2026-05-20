/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryLocalDestructiveCleanup {

    public String readThenWipe(final Shell shell) {
        final String stdout = shell.exec("cat file");
        shell.exec("rm -rf dir");
        return stdout;
    }

    public interface Shell {
        String exec(String cmd);
    }
}
