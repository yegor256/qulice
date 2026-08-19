/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code javac} flags that pin the forked compiler to the Java source
 * level of one batch of the project's sources.
 *
 * <p>Without them the forked {@code javac} runs at the host JDK's default
 * language level (e.g. 21 on a JDK 21 host), so ErrorProne fires
 * syntax-modernising checks such as {@code PatternMatchingInstanceof} even on
 * projects that compile at {@code -source 8}, producing suggestions whose
 * rewrite does not compile under the project's real source level. The level is
 * derived exactly as {@code CheckstyleValidator} derives it:
 * {@code maven.compiler.release} first, then {@code maven.compiler.source},
 * accepting both the modern ({@code "8"}, {@code "17"}) and legacy
 * ({@code "1.8"}) forms. When the project pins a {@code release},
 * {@code --release} is forwarded; otherwise {@code -source}/{@code -target}
 * are, which gates the language features while leaving the API surface
 * untouched so that projects using newer library APIs under a plain
 * {@code -source} build do not gain spurious "cannot find symbol" errors. When
 * neither property is set the level is unknown and nothing is added, leaving
 * the host default in place. See
 * <a href="https://github.com/yegor256/qulice/issues/1716">#1716</a>.</p>
 *
 * <p>Maven does not oblige the main sources and the test sources to share a
 * level: the {@code testCompile} goal reads {@code maven.compiler.testRelease},
 * {@code maven.compiler.testSource} and {@code maven.compiler.testTarget}, so a
 * project may compile its tests higher than its main code — as
 * {@code hone-maven-plugin} does, staying usable from Java 8 while its own
 * tests are written in Java 17. A test batch therefore derives its level from
 * those properties first, and only when the project pins none of them does it
 * fall back to the main ones, which keeps the fallback whole: a project that
 * sets nothing test-specific is compiled exactly as before. See
 * <a href="https://github.com/yegor256/qulice/issues/1746">#1746</a>.</p>
 *
 * <p>Which batches those are is not a matter of guessing at their names:
 * {@link Batches} gives every declared test source root a batch of its own,
 * named after the root, so the only batch that does not hold test sources is
 * {@link Batches#MAIN}.</p>
 *
 * @since 1.0
 */
final class Release {

    /**
     * Environment to read the Maven properties from.
     */
    private final Environment env;

    /**
     * Name of the batch being compiled.
     */
    private final String batch;

    /**
     * Constructor.
     * @param env Environment to read the Maven properties from
     * @param batch Name of the batch being compiled
     */
    Release(final Environment env, final String batch) {
        this.env = env;
        this.batch = batch;
    }

    /**
     * The source-level flags to append to the {@code javac} command line.
     * @return Source-level flags, possibly empty
     */
    List<String> flags() {
        List<String> flags = new ArrayList<>(0);
        if (!Batches.MAIN.equals(this.batch)) {
            flags = this.pinned(
                "maven.compiler.testRelease",
                "maven.compiler.testSource",
                "maven.compiler.testTarget"
            );
        }
        if (flags.isEmpty()) {
            flags = this.pinned(
                "maven.compiler.release",
                "maven.compiler.source",
                "maven.compiler.target"
            );
        }
        return flags;
    }

    private List<String> pinned(
        final String release, final String source, final String target
    ) {
        final List<String> flags = new ArrayList<>(4);
        final int rel = Release.major(this.env.param(release, ""));
        final int level = Release.major(this.env.param(source, ""));
        if (rel >= 0) {
            flags.add("--release");
            flags.add(String.valueOf(rel));
        } else if (level >= 0) {
            flags.add("-source");
            flags.add(String.valueOf(level));
            flags.add("-target");
            flags.add(String.valueOf(this.target(target, level)));
        }
        return flags;
    }

    private int target(final String property, final int fallback) {
        int level = Release.major(this.env.param(property, ""));
        if (level < 0) {
            level = fallback;
        }
        return level;
    }

    private static int major(final String value) {
        int result = -1;
        if (value != null) {
            String txt = value.trim();
            if (txt.startsWith("1.")) {
                txt = txt.substring(2);
            }
            try {
                result = Integer.parseInt(txt);
            } catch (final NumberFormatException ex) {
                result = -1;
            }
        }
        return result;
    }
}
