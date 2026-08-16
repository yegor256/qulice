/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.google.common.base.Splitter;
import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import com.qulice.spi.Relative;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import com.yegor256.Jaxec;
import com.yegor256.Result;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates source code with Google ErrorProne.
 *
 * <p>Runs the {@code javac} executable from the active JDK as a forked
 * process, with ErrorProne wired in as a {@code -Xplugin:ErrorProne} so
 * that every bug pattern fires while {@code javac} type-checks the
 * project's Java sources. The {@code --add-exports} and {@code --add-opens}
 * flags ErrorProne needs to reach internal {@code jdk.compiler} packages
 * are passed to the forked JVM via {@code javac}'s {@code -J} prefix; the
 * JVM hosting Maven and Qulice is unaffected, so consumers do not have to
 * touch their own {@code .mvn/jvm.config} to use this validator.</p>
 *
 * <p>The combined stdout/stderr stream is handed to {@link Diagnostics},
 * which turns every diagnostic it finds there into a {@link Violation} —
 * ErrorProne findings, {@code javac} lint warnings and plain compile
 * errors alike — and passes over the carets, symbol hints and counters
 * the compiler prints around them. {@code -proc:none} is passed to keep
 * regular annotation processors (Lombok, Hibernate-Validator, etc.) out
 * of the ErrorProne pass.</p>
 *
 * <p>Which bug patterns fire is up to {@link Xplugin}, which also takes
 * in the {@code -Xep} flags of the project, read from the
 * {@code qulice.errorprone} parameter.</p>
 *
 * <p>The sources are not fed to one {@code javac} pass but to as many as
 * the project has source roots, which is what {@link Batches} works out;
 * the name of each batch tells the passes apart on disk, both in the
 * argfile they read and in the directory they write classes to.</p>
 *
 * @since 1.0
 */
public final class ErrorProneValidator implements ResourceValidator {

    /**
     * JVM module-access flags ErrorProne requires to reach internal
     * {@code jdk.compiler} APIs. Forwarded to the embedded JVM via
     * {@code javac}'s {@code -J} prefix.
     */
    private static final List<String> JVM_FLAGS = List.of(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"
    );

    /**
     * Name of the parameter through which a project supplies ErrorProne
     * flags of its own, e.g. {@code -Xep:UnusedVariable:OFF}.
     */
    private static final String PARAM = "qulice.errorprone";

    /**
     * Splits a multi-line stdout block into individual lines, on any
     * line terminator (\\n, \\r, \\r\\n, etc.).
     */
    private static final Pattern NEWLINE = Pattern.compile("\\R");

    /**
     * Environment to use.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env Environment to use
     */
    public ErrorProneValidator(final Environment env) {
        this.env = env;
    }

    @Override
    public Collection<Violation> validate(final Collection<File> files) {
        final List<File> sources = this.relevant(files);
        final Collection<Violation> violations = new ArrayList<>(0);
        if (sources.isEmpty()) {
            Logger.debug(
                this,
                "No files to check with ErrorProne, all %d are excluded",
                files.size()
            );
        } else {
            Logger.debug(this, "ErrorProne processing %d files", sources.size());
            final Diagnostics diagnostics = new Diagnostics(
                this.name(), this.env.basedir().getAbsolutePath()
            );
            for (final Map.Entry<String, List<File>> batch
                : new Batches(this.env, sources).split().entrySet()) {
                violations.addAll(
                    diagnostics.violations(
                        this.run(batch.getKey(), batch.getValue())
                    )
                );
            }
            Logger.debug(this, "ErrorProne processed %d files", sources.size());
        }
        return violations;
    }

    @Override
    public String name() {
        return "ErrorProne";
    }

    @Override
    public int rules() {
        return new Xplugin(
            this.env.param(ErrorProneValidator.PARAM, "")
        ).patterns();
    }

    /**
     * Run the forked {@code javac} process with ErrorProne enabled.
     * @param batch Name of the batch being compiled
     * @param sources Java source files to feed
     * @return Combined stdout/stderr of the process, line by line
     */
    private List<String> run(final String batch, final List<File> sources) {
        final Result result = new Jaxec(this.command(batch, sources))
            .withRedirect(true)
            .withCheck(false)
            .exec();
        final String stdout = result.stdout();
        final List<String> lines;
        if (stdout.isEmpty()) {
            lines = List.of();
        } else {
            lines = List.of(ErrorProneValidator.NEWLINE.split(stdout));
        }
        return lines;
    }

    /**
     * Build the {@code javac} command line. To stay below the
     * Windows {@code CreateProcess} 32 KB command-line limit even on
     * projects with thousands of sources or long classpaths, every
     * argument other than the launcher itself and the {@code -J}
     * flags (which {@code javac} forbids inside argfiles) is written
     * to a temporary argfile and passed as {@code @argfile}.
     *
     * <p>{@code -Xlint:-options} turns off the one lint category that
     * would report Qulice to the project instead of the project to the
     * user: {@code options} complains about the {@code -source}/
     * {@code -target} pair {@link Release} deliberately builds in place of
     * {@code --release} (see
     * <a href="https://github.com/yegor256/qulice/issues/1716">#1716</a>),
     * and no change to the project could ever silence it.</p>
     *
     * <p>{@code -encoding} is the project's own
     * {@code project.build.sourceEncoding}, so that a source file holding
     * characters outside ASCII reads the same on every host, instead of
     * through whatever charset the machine running Maven happens to
     * default to.</p>
     *
     * @param batch Name of the batch being compiled
     * @param sources Java source files to feed
     * @return Argv
     */
    private List<String> command(final String batch, final List<File> sources) {
        final List<String> command = new ArrayList<>(
            ErrorProneValidator.JVM_FLAGS.size() + 2
        );
        command.add(ErrorProneValidator.javac());
        for (final String flag : ErrorProneValidator.JVM_FLAGS) {
            command.add("-J".concat(flag));
        }
        final File outdir = new File(
            this.env.tempdir(), String.format("errorprone-classes-%s", batch)
        );
        if (!outdir.exists() && !outdir.mkdirs()) {
            throw new IllegalStateException(
                String.format("Unable to create %s", outdir)
            );
        }
        final List<String> args = new ArrayList<>(sources.size() + 12);
        args.add("-XDcompilePolicy=simple");
        args.add("-XDaddTypeAnnotationsToSymbol=true");
        args.add("--should-stop=ifError=FLOW");
        args.add("-proc:none");
        args.add("-Xlint:-options");
        args.add("-encoding");
        args.add(this.env.encoding().name());
        args.addAll(new Release(this.env, batch).flags());
        args.add(
            new Xplugin(
                this.env.param(ErrorProneValidator.PARAM, "")
            ).argument()
        );
        args.add("-processorpath");
        args.add(ErrorProneValidator.pluginClasspath());
        args.add("-d");
        args.add(outdir.getAbsolutePath());
        final Collection<String> classpath = this.env.classpath();
        if (!classpath.isEmpty()) {
            args.add("-classpath");
            args.add(String.join(File.pathSeparator, classpath));
        }
        for (final File source : sources) {
            args.add(source.getAbsolutePath());
        }
        command.add(
            "@".concat(
                new Argfile(
                    new File(
                        this.env.tempdir(),
                        String.format("errorprone-args-%s.txt", batch)
                    ),
                    args
                ).save().getAbsolutePath()
            )
        );
        return command;
    }

    /**
     * Filters out non-Java and excluded files from further validation.
     * @param files Files to validate
     * @return List of relevant files
     */
    private List<File> relevant(final Collection<File> files) {
        final List<File> sources = new ArrayList<>(files.size());
        for (final File file : files) {
            final String name = new Relative(this.env.basedir(), file).path();
            if (this.env.exclude("errorprone", name)) {
                continue;
            }
            if (!name.endsWith(".java")) {
                continue;
            }
            sources.add(file);
        }
        return sources;
    }

    /**
     * Resolve the {@code javac} executable from the running JDK.
     * @return Absolute path to {@code ${java.home}/bin/javac}
     */
    private static String javac() {
        return new File(
            new File(System.getProperty("java.home"), "bin"),
            "javac"
        ).getAbsolutePath();
    }

    /**
     * Build the {@code -processorpath} value passed to the forked
     * {@code javac}. Combines two sources: every URL on the
     * {@link URLClassLoader} chain starting from the thread context
     * classloader (the qulice plugin's own {@code ClassRealm} plus its
     * URL-based parents, which carries ErrorProne when this code runs
     * inside a Maven plugin execution), and the jar locations of a few
     * classes ErrorProne needs at runtime that Maven's classworlds
     * imports from a non-URL parent realm (notably
     * {@link javax.inject.Inject}, which the
     * {@code ErrorProneInjector} reads to find injectable constructors).
     * Both are required: without the realm URLs there's no
     * {@code error_prone_core}, without the protection-domain lookup
     * there's no {@code javax.inject}.
     * @return Path-separator joined list of jar paths
     */
    private static String pluginClasspath() {
        final Set<String> entries = new LinkedHashSet<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                for (final URL url : ((URLClassLoader) loader).getURLs()) {
                    new Unencoded(url).path().ifPresent(entries::add);
                }
            }
            loader = loader.getParent();
        }
        for (final String entry
            : Splitter.on(File.pathSeparatorChar).split(
                System.getProperty("java.class.path", "")
            )
        ) {
            if (!entry.isEmpty()) {
                entries.add(new File(entry).getAbsolutePath());
            }
        }
        ErrorProneValidator.addCodeSource(entries, javax.inject.Inject.class);
        return String.join(File.pathSeparator, entries);
    }

    /**
     * Append the jar that contains the given class to {@code entries},
     * resolved via {@link Class#getProtectionDomain()}. Silently ignored
     * if the class is loaded from a non-file location (e.g. a JRT module
     * or an exploded directory).
     * @param entries Set to append to
     * @param klass Class whose code source jar should be included
     */
    private static void addCodeSource(
        final Set<String> entries, final Class<?> klass
    ) {
        final java.security.CodeSource source =
            klass.getProtectionDomain().getCodeSource();
        if (source != null && source.getLocation() != null) {
            new Unencoded(source.getLocation()).path().ifPresent(entries::add);
        }
    }
}
