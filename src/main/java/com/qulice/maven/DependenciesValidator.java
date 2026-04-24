/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.cactoos.text.Joined;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.ContextException;

/**
 * Validator of dependencies.
 * @since 0.3
 * @checkstyle ReturnCountCheck (100 line)
 */
final class DependenciesValidator implements MavenValidator {

    /**
     * Separator between lines.
     */
    private static final String SEP = String.format("%n\t");

    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.outdir().exists() || "pom".equals(env.project().getPackaging())) {
            Logger.info(this, "No dependency analysis in this project");
            return;
        }
        final Collection<String> excludes = env.excludes("dependencies");
        if (excludes.contains(".*")) {
            Logger.info(this, "Dependency analysis suppressed in the project via pom.xml");
            return;
        }
        final Collection<String> unused = Collections2.filter(
            DependenciesValidator.unused(env),
            Predicates.not(new DependenciesValidator.ExcludePredicate(excludes))
        );
        if (!unused.isEmpty()) {
            Logger.warn(
                this,
                "Unused declared dependencies found:%s%s",
                DependenciesValidator.SEP,
                new Joined(DependenciesValidator.SEP, unused).toString()
            );
        }
        final Collection<String> used = Collections2.filter(
            DependenciesValidator.used(env),
            Predicates.not(new DependenciesValidator.ExcludePredicate(excludes))
        );
        if (!used.isEmpty()) {
            Logger.warn(
                this,
                "Used undeclared dependencies found:%s%s",
                DependenciesValidator.SEP,
                new Joined(DependenciesValidator.SEP, used)
            );
        }
        if (!used.isEmpty() || !unused.isEmpty()) {
            Logger.info(
                this,
                "You can suppress this message by <exclude>dependencies:...</exclude> in pom.xml, where <...> is what the dependency name starts with (not a regular expression!)"
            );
        }
        final int failures = used.size() + unused.size();
        if (failures > 0) {
            throw new ValidationException(
                String.format("%d dependency problem(s) found", failures)
            );
        }
        Logger.info(this, "No dependency problems found");
    }

    /**
     * Analyze the project.
     * @param env The environment
     * @return The result of analysis
     */
    private static ProjectDependencyAnalysis analyze(
        final MavenEnvironment env) {
        try {
            return ((ProjectDependencyAnalyzer)
                ((PlexusContainer)
                    env.context().get(PlexusConstants.PLEXUS_KEY)
                ).lookup(ProjectDependencyAnalyzer.class.getName(), "default")
            ).analyze(env.project());
        } catch (final ContextException | ComponentLookupException
            | ProjectDependencyAnalyzerException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Find unused artifacts.
     * @param env Environment
     * @return Collection of unused artifacts
     */
    private static Collection<String> used(final MavenEnvironment env) {
        final ProjectDependencyAnalysis analysis =
            DependenciesValidator.analyze(env);
        final Collection<String> used = new LinkedList<>();
        for (final Object artifact : analysis.getUsedUndeclaredArtifacts()) {
            used.add(artifact.toString());
        }
        return used;
    }

    /**
     * Find unused artifacts.
     *
     * <p>Bytecode analysis cannot detect dependencies used only through
     * annotations with source retention or through inlined compile-time
     * constants. To avoid such false positives, artifacts flagged as
     * unused by {@link ProjectDependencyAnalyzer} are cross-checked
     * against {@code import} statements in the project's source files;
     * any artifact referenced by an import is treated as used.</p>
     *
     * @param env Environment
     * @return Collection of unused artifacts
     */
    private static Collection<String> unused(final MavenEnvironment env) {
        final ProjectDependencyAnalysis analysis =
            DependenciesValidator.analyze(env);
        final Set<String> imports = DependenciesValidator.imports(env);
        final Collection<String> unused = new LinkedList<>();
        for (final Object obj : analysis.getUnusedDeclaredArtifacts()) {
            final Artifact artifact = (Artifact) obj;
            if (!Artifact.SCOPE_COMPILE.equals(artifact.getScope())) {
                continue;
            }
            if (DependenciesValidator.imported(imports, artifact)) {
                Logger.info(
                    DependenciesValidator.class,
                    "Dependency %s is imported in source and treated as used (annotations or inlined constants are invisible to bytecode analysis)",
                    artifact
                );
                continue;
            }
            unused.add(artifact.toString());
        }
        return unused;
    }

    /**
     * Collect fully-qualified imports from all Java source files
     * in the project's compile source roots.
     * @param env Environment
     * @return Set of imported class names and wildcard package imports
     */
    private static Set<String> imports(final MavenEnvironment env) {
        final Set<String> imports = new HashSet<>();
        final Collection<String> roots =
            env.project().getCompileSourceRoots();
        if (roots != null) {
            for (final String root : roots) {
                final Path dir = Paths.get(root);
                if (Files.isDirectory(dir)) {
                    DependenciesValidator.scanJavaFiles(dir, imports);
                }
            }
        }
        return imports;
    }

    /**
     * Walk the given directory and collect imports from every Java file.
     * @param dir Source root directory
     * @param acc Accumulator to populate with imports
     */
    private static void scanJavaFiles(final Path dir, final Set<String> acc) {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> DependenciesValidator.readImports(path, acc));
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Cannot scan source root %s", dir), ex
            );
        }
    }

    /**
     * Read import statements from a single Java source file into the
     * given accumulator.
     * @param file Java source file
     * @param acc Accumulator to populate
     */
    private static void readImports(final Path file, final Set<String> acc) {
        try {
            for (final String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                final String trimmed = line.trim();
                if (!trimmed.startsWith("import ")) {
                    continue;
                }
                final int semi = trimmed.indexOf(';');
                if (semi < 0) {
                    continue;
                }
                String spec =
                    trimmed.substring("import ".length(), semi).trim();
                if (spec.startsWith("static ")) {
                    spec = spec.substring("static ".length()).trim();
                    final int dot = spec.lastIndexOf('.');
                    if (dot > 0) {
                        spec = spec.substring(0, dot);
                    }
                }
                if (!spec.isEmpty()) {
                    acc.add(spec);
                }
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Cannot read source file %s", file), ex
            );
        }
    }

    /**
     * Is the given artifact referenced by any of the collected imports?
     * @param imports Imports collected from project sources
     * @param artifact Artifact whose JAR is inspected
     * @return TRUE if at least one class from the JAR is imported
     */
    private static boolean imported(final Set<String> imports,
        final Artifact artifact) {
        final File file = artifact.getFile();
        boolean found = false;
        if (!imports.isEmpty() && file != null && file.isFile()) {
            try (JarFile jar = new JarFile(file)) {
                final Enumeration<JarEntry> entries = jar.entries();
                while (!found && entries.hasMoreElements()) {
                    found = DependenciesValidator.matches(
                        imports, entries.nextElement().getName()
                    );
                }
            } catch (final IOException ex) {
                Logger.warn(
                    DependenciesValidator.class,
                    "Cannot inspect %s while cross-checking imports: %s",
                    file, ex.getMessage()
                );
            }
        }
        return found;
    }

    /**
     * Does the given JAR entry name represent a class imported by the
     * project sources?
     * @param imports Imports collected from project sources
     * @param entry JAR entry name (e.g. "com/example/Foo.class")
     * @return TRUE if the entry's fully-qualified class name or its
     *  package is imported
     */
    private static boolean matches(final Set<String> imports,
        final String entry) {
        boolean match = false;
        if (entry.endsWith(".class")
            && !"module-info.class".equals(entry)
            && entry.indexOf('$') < 0) {
            final String fqn = entry
                .substring(0, entry.length() - ".class".length())
                .replace('/', '.');
            final int dot = fqn.lastIndexOf('.');
            match = imports.contains(fqn)
                || dot > 0
                && imports.contains(fqn.substring(0, dot).concat(".*"));
        }
        return match;
    }

    /**
     * Predicate for excluded dependencies.
     * @since 0.1
     */
    private static class ExcludePredicate implements Predicate<String> {

        /**
         * List of excludes.
         */
        private final Collection<String> excludes;

        /**
         * Constructor.
         * @param excludes List of excludes
         */
        ExcludePredicate(final Collection<String> excludes) {
            this.excludes = excludes;
        }

        @Override
        public boolean apply(final String name) {
            boolean ignore = false;
            for (final String exclude : this.excludes) {
                if (name.startsWith(exclude)) {
                    ignore = true;
                    break;
                }
            }
            return ignore;
        }
    }
}
