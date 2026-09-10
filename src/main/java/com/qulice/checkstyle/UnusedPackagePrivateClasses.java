/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.qulice.spi.Environment;
import com.qulice.spi.Ignored;
import com.qulice.spi.Relative;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * The package-private classes that nothing in their package uses.
 *
 * <p>A top-level type declared without the {@code public} modifier is
 * visible to its own package and to nobody else. When no other type of
 * that package mentions it, no code in the project can reach it: the
 * class is dead and the only sensible thing to do with it is to delete
 * it. Neither Checkstyle nor PMD says a word about it, since both of
 * them look at one file at a time, while the answer lies in the other
 * files of the same package.</p>
 *
 * <p>The files are parsed here, and not through a Checkstyle module,
 * because Checkstyle takes an unchanged file from its cache on a second
 * run and never shows it to a module. A cross-file question answered
 * from a partial set of files gets a wrong answer: a class whose only
 * user was cached away would be reported as unused.</p>
 *
 * <p>A test class is left alone, whatever its name and however lonely:
 * JUnit finds it by itself and no other class of the package is supposed
 * to mention it. Both {@code src/main/java} and {@code src/test/java}
 * hold files of the same package, so a class used only by a test of its
 * own package is used, not dead. A file an {@code <exclude>} covers is
 * never reported either, although what it mentions still counts as a
 * usage.</p>
 *
 * <p>One arrangement stays out of reach: a package split between two
 * Maven modules, where the class lives in one of them and its only user
 * in the other. Qulice sees a single module at a time, so such a class
 * looks unused and has to be excluded by hand.</p>
 *
 * @since 1.0
 */
final class UnusedPackagePrivateClasses {

    /**
     * The tokens that declare a type.
     */
    private static final Collection<Integer> KINDS = Set.of(
        TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF,
        TokenTypes.RECORD_DEF, TokenTypes.ANNOTATION_DEF
    );

    /**
     * Environment to use.
     */
    private final Environment env;

    /**
     * Constructor.
     *
     * @param env Environment to use
     */
    UnusedPackagePrivateClasses(final Environment env) {
        this.env = env;
    }

    /**
     * Find the package-private classes that nothing in their package uses.
     *
     * @param files All files of the project, of any kind
     * @return Violations, one per unused class
     */
    Collection<Violation> validate(final Collection<File> files) {
        final Mentions mentions = new Mentions();
        for (final File file : files) {
            for (final TopLevelType type : this.types(file)) {
                mentions.add(type);
                if (this.suspect(type)) {
                    mentions.suspect(type);
                }
            }
        }
        return mentions.unused();
    }

    private Collection<TopLevelType> types(final File file) {
        final Collection<TopLevelType> found = new ArrayList<>(0);
        final String path = new Relative(this.env.basedir(), file).path();
        if (path.endsWith(".java") && !new Ignored(path).yes()) {
            found.addAll(UnusedPackagePrivateClasses.declared(file));
        }
        return found;
    }

    private boolean suspect(final TopLevelType type) {
        return type.hidden()
            && !type.test()
            && !this.env.exclude(
                "checkstyle", new Relative(this.env.basedir(), type.file()).path()
            );
    }

    private static Collection<TopLevelType> declared(final File file) {
        final Collection<TopLevelType> found = new ArrayList<>(0);
        try {
            final DetailAST unit = JavaParser.parseFile(
                file, JavaParser.Options.WITHOUT_COMMENTS
            );
            final String pack = UnusedPackagePrivateClasses.pack(unit);
            DetailAST node = unit.getFirstChild();
            while (node != null) {
                if (UnusedPackagePrivateClasses.KINDS.contains(node.getType())) {
                    found.add(new TopLevelType(file, pack, node));
                }
                node = node.getNextSibling();
            }
        } catch (final IOException | CheckstyleException ex) {
            Logger.debug(
                UnusedPackagePrivateClasses.class,
                "Failed to parse %s, its classes are left alone: %[exception]s",
                file, ex
            );
        }
        return found;
    }

    private static String pack(final DetailAST unit) {
        final DetailAST node = unit.findFirstToken(TokenTypes.PACKAGE_DEF);
        final String pack;
        if (node == null) {
            pack = "";
        } else {
            pack = FullIdent.createFullIdent(
                node.getLastChild().getPreviousSibling()
            ).getText();
        }
        return pack;
    }
}
