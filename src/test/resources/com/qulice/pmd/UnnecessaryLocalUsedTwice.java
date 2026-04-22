/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryLocalUsedTwice {

    public String usedTwiceInArguments() {
        final String name = "foo";
        return String.format("%s-%s", name, name);
    }

    public String usedDirectlyAndInLambda() {
        final String prefix = "hello";
        return String.format(
            "%s-%s",
            prefix,
            ((java.util.function.Supplier<String>) () -> String.format("%s!", prefix)).get()
        );
    }
}
