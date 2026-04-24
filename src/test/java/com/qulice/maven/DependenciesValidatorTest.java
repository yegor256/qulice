/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DependenciesValidator} class.
 *
 * @since 0.3
 */
@SuppressWarnings("PMD.TooManyMethods")
final class DependenciesValidatorTest {

    /**
     * Plexus role.
     */
    private static final String ROLE =
        ProjectDependencyAnalyzer.class.getName();

    /**
     * Plexus hint.
     */
    private static final String HINT = "default";

    /**
     * Compile scope.
     */
    private static final String SCOPE = "compile";

    /**
     * Jar type.
     */
    private static final String TYPE = "jar";

    /**
     * DependencyValidator can pass on when no violations are found.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesIfNoDependencyProblemsFound() throws Exception {
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis();
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * DependencyValidator can catch dependency problems.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void catchesDependencyProblemsAndThrowsException() throws Exception {
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), unused, Collections.emptySet()
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        Assertions.assertThrows(
            ValidationException.class,
            () -> new DependenciesValidator().validate(env)
        );
    }

    /**
     * DependencyValidator can ignore runtime scope dependencies.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void ignoresRuntimeScope() throws Exception {
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope("runtime");
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), Collections.emptySet(), unused
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * DependencyValidator can exclude used undeclared dependencies.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUsedUndeclaredDependencies() throws Exception {
        final Set<Artifact> used = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        used.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), used, Collections.emptySet()
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(
            new MavenEnvironment.Wrap(
                new Environment.Mock().withExcludes(
                    Joiner.on(':').join(
                        artifact.getGroupId(), artifact.getArtifactId()
                    )
                ), env
            )
        );
    }

    /**
     * DependencyValidator can exclude unused declared dependencies.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUnusedDeclaredDependencies() throws Exception {
        final Set<Artifact> unused = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("othergroup");
        artifact.setArtifactId("otherartifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("1.2.3");
        artifact.setType(DependenciesValidatorTest.TYPE);
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), Collections.emptySet(), unused
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(
            new MavenEnvironment.Wrap(
                new Environment.Mock().withExcludes(
                    Joiner.on(':').join(
                        artifact.getGroupId(), artifact.getArtifactId()
                    )
                ), env
            )
        );
    }

    /**
     * DependencyValidator cannot fail the build when the "unused declared"
     * dependency is actually referenced by an {@code import} in source, which
     * is the typical shape of false positives caused by annotations with
     * source retention or inlined compile-time constants (see issue #782).
     *
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsImportedDependencyAsUsed(@TempDir final Path dir)
        throws Exception {
        final File jar = DependenciesValidatorTest.jar(
            dir, "fake.jar", "com/fake/Marker.class"
        );
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/Subject.java",
            "package com.example;\nimport com.fake.Marker;\n@Marker\npublic class Subject {}\n"
        );
        final MavenEnvironment env = DependenciesValidatorTest.envWithUnused(
            src, jar, "com.fake:fake"
        );
        new DependenciesValidator().validate(env);
    }

    /**
     * Static imports must also count as evidence that a dependency is used,
     * since inlined constants are referenced via {@code import static}.
     *
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsStaticImportAsUsage(@TempDir final Path dir) throws Exception {
        final File jar = DependenciesValidatorTest.jar(
            dir, "consts.jar", "com/consts/Constants.class"
        );
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/UsesConst.java",
            "package com.example;\nimport static com.consts.Constants.VALUE;\npublic class UsesConst { int x = VALUE; }\n"
        );
        final MavenEnvironment env = DependenciesValidatorTest.envWithUnused(
            src, jar, "com.consts:consts"
        );
        new DependenciesValidator().validate(env);
    }

    /**
     * Wildcard imports must cover any class inside the imported package.
     *
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsWildcardImportAsUsage(@TempDir final Path dir) throws Exception {
        final File jar = DependenciesValidatorTest.jar(
            dir, "wild.jar", "com/wild/Thing.class"
        );
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/UsesWild.java",
            "package com.example;\nimport com.wild.*;\npublic class UsesWild {}\n"
        );
        final MavenEnvironment env = DependenciesValidatorTest.envWithUnused(
            src, jar, "com.wild:wild"
        );
        new DependenciesValidator().validate(env);
    }

    /**
     * Without any matching import, an "unused declared" compile-scope
     * dependency must still fail the build even when sources exist.
     *
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void stillFailsWithoutMatchingImport(@TempDir final Path dir)
        throws Exception {
        final File jar = DependenciesValidatorTest.jar(
            dir, "alone.jar", "com/alone/Class.class"
        );
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/Other.java",
            "package com.example;\nimport java.util.List;\npublic class Other {}\n"
        );
        final MavenEnvironment env = DependenciesValidatorTest.envWithUnused(
            src, jar, "com.alone:alone"
        );
        Assertions.assertThrows(
            ValidationException.class,
            () -> new DependenciesValidator().validate(env),
            "a declared dependency that is neither referenced in bytecode nor imported in source must not pass validation"
        );
    }

    /**
     * Build a MavenEnvironment where exactly one artifact is reported as
     * "unused declared" and the given source root is part of the project.
     * @param src Directory containing Java sources
     * @param jar JAR file to attach to the artifact
     * @param coord Artifact coordinate in the form {@code groupId:artifactId}
     * @return Wired environment
     * @throws Exception If something wrong happens inside
     */
    private static MavenEnvironment envWithUnused(final Path src,
        final File jar, final String coord) throws Exception {
        final String[] parts = coord.split(":");
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(parts[0]);
        artifact.setArtifactId(parts[1]);
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("1.0.0");
        artifact.setType(DependenciesValidatorTest.TYPE);
        artifact.setFile(jar);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis = new ProjectDependencyAnalysis(
            Collections.emptySet(), Collections.emptySet(), unused
        );
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            new FakeProjectDependencyAnalyzer(analysis)
        ).mock();
        env.project().addCompileSourceRoot(src.toString());
        return env;
    }

    /**
     * Create a JAR file at the given path containing the given class entries.
     * @param dir Parent directory
     * @param name JAR file name
     * @param entries Names of class entries to include
     * @return The created JAR file
     * @throws Exception If something wrong happens inside
     */
    private static File jar(final Path dir, final String name,
        final String... entries) throws Exception {
        final File jar = dir.resolve(name).toFile();
        try (OutputStream fos = Files.newOutputStream(jar.toPath());
            JarOutputStream out = new JarOutputStream(fos)) {
            for (final String entry : entries) {
                out.putNextEntry(new JarEntry(entry));
                out.write(new byte[]{0});
                out.closeEntry();
            }
        }
        return jar;
    }

    /**
     * Create a compile source root directory under the given temp dir.
     * @param dir Temporary directory
     * @return Created source root
     * @throws Exception If something wrong happens inside
     */
    private static Path sourceRoot(final Path dir) throws Exception {
        final Path src = dir.resolve("src").resolve("main").resolve("java");
        Files.createDirectories(src);
        return src;
    }

    /**
     * Write the given Java source file under the source root.
     * @param src Source root
     * @param path Relative path for the source file
     * @param content File content
     * @throws Exception If something wrong happens inside
     */
    private static void writeJava(final Path src, final String path,
        final String content) throws Exception {
        final Path target = src.resolve(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content, StandardCharsets.UTF_8);
    }

    /**
     * FakeProjectDependencyAnalyzer.
     *
     * A mock to ProjectDependencyAnalyzer.
     *
     * @since 0.24.1
     */
    private static final class FakeProjectDependencyAnalyzer
        implements ProjectDependencyAnalyzer {
        /**
         * ProjectDependencyAnalysis.
         */
        private final ProjectDependencyAnalysis analysis;

        FakeProjectDependencyAnalyzer(
            final ProjectDependencyAnalysis alysis
        ) {
            this.analysis = alysis;
        }

        @Override
        public ProjectDependencyAnalysis analyze(
            final MavenProject project,
            final Collection<String> collection
        ) throws ProjectDependencyAnalyzerException {
            return this.analysis;
        }
    }
}
