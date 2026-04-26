/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
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
 * <p>Diagnostics from the forked {@code javac} are parsed from the
 * combined stdout/stderr stream. Only lines that match the standard
 * compiler diagnostic format and whose message starts with the
 * {@code [CheckName]} prefix ErrorProne always emits are converted into
 * {@link Violation}s — plain compile errors caused by the project not
 * being built yet are ignored. {@code -proc:none} is passed to keep
 * regular annotation processors (Lombok, Hibernate-Validator, etc.) out
 * of the ErrorProne pass.</p>
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
     * Standard {@code javac} diagnostic format with an ErrorProne
     * {@code [CheckName]} prefix on the message:
     * {@code path:line: warning|error: [Name] body}.
     */
    private static final Pattern DIAGNOSTIC = Pattern.compile(
        "^(.+?):(\\d+): (?:warning|error): \\[([A-Za-z][A-Za-z0-9_]*)] (.+)$"
    );

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
        final Collection<Violation> violations = new LinkedList<>();
        if (sources.isEmpty()) {
            Logger.debug(
                this,
                "No files to check with ErrorProne, all %d are excluded",
                files.size()
            );
        } else {
            Logger.debug(this, "ErrorProne processing %d files", sources.size());
            violations.addAll(this.parse(this.run(sources)));
            Logger.debug(this, "ErrorProne processed %d files", sources.size());
        }
        return violations;
    }

    @Override
    public String name() {
        return "ErrorProne";
    }

    /**
     * Run the forked {@code javac} process with ErrorProne enabled.
     * @param sources Java source files to feed
     * @return Combined stdout/stderr of the process, line by line
     */
    private List<String> run(final List<File> sources) {
        final Result result = new Jaxec(this.command(sources))
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
     * @param sources Java source files to feed
     * @return Argv
     */
    private List<String> command(final List<File> sources) {
        final List<String> command = new ArrayList<>(
            ErrorProneValidator.JVM_FLAGS.size() + 2
        );
        command.add(ErrorProneValidator.javac());
        for (final String flag : ErrorProneValidator.JVM_FLAGS) {
            command.add("-J".concat(flag));
        }
        final File outdir = new File(this.env.tempdir(), "errorprone-classes");
        if (!outdir.exists() && !outdir.mkdirs()) {
            throw new IllegalStateException(
                String.format("Unable to create %s", outdir)
            );
        }
        final List<String> args = new ArrayList<>(sources.size() + 11);
        args.add("-XDcompilePolicy=simple");
        args.add("-XDaddTypeAnnotationsToSymbol=true");
        args.add("--should-stop=ifError=FLOW");
        args.add("-proc:none");
        args.add("-Xplugin:ErrorProne");
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
                    new File(this.env.tempdir(), "errorprone-args.txt"), args
                ).save().getAbsolutePath()
            )
        );
        return command;
    }

    /**
     * Translate diagnostic lines into Qulice violations, keeping only
     * those messages prefixed by an ErrorProne bug-pattern name.
     * @param output Combined stdout/stderr of the forked process
     * @return Violations
     */
    private Collection<Violation> parse(final List<String> output) {
        final Collection<Violation> violations = new LinkedList<>();
        for (final String line : output) {
            final Matcher matcher = ErrorProneValidator.DIAGNOSTIC.matcher(line);
            if (matcher.matches()) {
                final String check = matcher.group(3);
                violations.add(
                    new Violation.Default(
                        this.name(),
                        check,
                        matcher.group(1),
                        matcher.group(2),
                        String.format("[%s] %s", check, matcher.group(4))
                    )
                );
            }
        }
        return violations;
    }

    /**
     * Filters out non-Java and excluded files from further validation.
     * @param files Files to validate
     * @return List of relevant files
     */
    private List<File> relevant(final Collection<File> files) {
        final List<File> sources = new LinkedList<>();
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
                    entries.add(new File(url.getPath()).getAbsolutePath());
                }
            }
            loader = loader.getParent();
        }
        for (final String entry
            : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
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
            try {
                entries.add(
                    new File(source.getLocation().toURI()).getAbsolutePath()
                );
            } catch (final java.net.URISyntaxException | IllegalArgumentException ex) {
                Logger.debug(
                    ErrorProneValidator.class,
                    "Cannot resolve code source for %s: %s", klass, ex.getMessage()
                );
            }
        }
    }
}
