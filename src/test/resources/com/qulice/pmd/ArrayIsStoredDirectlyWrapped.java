/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArrayIsStoredDirectlyWrapped {
    private final List<String> args;
    private final List<String> others;
    private final List<String> more;

    public ArrayIsStoredDirectlyWrapped(final String[] args, final String[] others, final String... more) {
        this.args = Arrays.asList(args);
        this.others = new ArrayList<>(Arrays.asList(others));
        this.more = Arrays.asList(more);
    }

    public List<String> list() {
        return this.args;
    }

    public List<String> another() {
        return this.others;
    }

    public List<String> last() {
        return this.more;
    }
}
