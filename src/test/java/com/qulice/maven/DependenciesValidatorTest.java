/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DependenciesValidator} class.
 * @since 0.3
 */
final class DependenciesValidatorTest {

    /**
     * Plexus role.
     */
    private static final String ROLE =
        ProjectDependencyAnalyzer.class.getName();

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
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesIfNoDependencyProblemsFound() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWith(new ProjectDependencyAnalysis())
            )
        );
    }

    /**
     * DependencyValidator can catch dependency problems.
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
        Assertions.assertThrows(
            ValidationException.class,
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWith(
                    new ProjectDependencyAnalysis(
                        Collections.emptySet(), unused, Collections.emptySet()
                    )
                )
            )
        );
    }

    /**
     * DependencyValidator can ignore runtime scope dependencies.
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
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWith(
                    new ProjectDependencyAnalysis(
                        Collections.emptySet(), Collections.emptySet(), unused
                    )
                )
            )
        );
    }

    /**
     * DependencyValidator can exclude used undeclared dependencies.
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
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                new MavenEnvironment.Wrap(
                    new Environment.Mock().withExcludes(
                        Joiner.on(':').join(
                            artifact.getGroupId(), artifact.getArtifactId()
                        )
                    ),
                    DependenciesValidatorTest.envWith(
                        new ProjectDependencyAnalysis(
                            Collections.emptySet(), used, Collections.emptySet()
                        )
                    )
                )
            )
        );
    }

    /**
     * DependencyValidator can exclude unused declared dependencies.
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
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                new MavenEnvironment.Wrap(
                    new Environment.Mock().withExcludes(
                        Joiner.on(':').join(
                            artifact.getGroupId(), artifact.getArtifactId()
                        )
                    ),
                    DependenciesValidatorTest.envWith(
                        new ProjectDependencyAnalysis(
                            Collections.emptySet(), Collections.emptySet(), unused
                        )
                    )
                )
            )
        );
    }

    /**
     * DependencyValidator cannot fail the build when the "unused declared"
     * dependency is actually referenced by an {@code import} in source, which
     * is the typical shape of false positives caused by annotations with
     * source retention or inlined compile-time constants (see issue #782).
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsImportedDependencyAsUsed(@TempDir final Path dir)
        throws Exception {
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/Subject.java",
            String.join(
                String.valueOf('\n'),
                "package com.example;",
                "import com.fake.Marker;",
                "@Marker",
                "public class Subject {}",
                ""
            )
        );
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWithUnused(
                    src,
                    DependenciesValidatorTest.jar(
                        dir, "fake.jar", "com/fake/Marker.class"
                    ),
                    "com.fake:fake"
                )
            )
        );
    }

    /**
     * Static imports must also count as evidence that a dependency is used,
     * since inlined constants are referenced via {@code import static}.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsStaticImportAsUsage(@TempDir final Path dir) throws Exception {
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/UsesConst.java",
            String.join(
                String.valueOf('\n'),
                "package com.example;",
                "import static com.consts.Constants.VALUE;",
                "public class UsesConst { int x = VALUE; }",
                ""
            )
        );
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWithUnused(
                    src,
                    DependenciesValidatorTest.jar(
                        dir, "consts.jar", "com/consts/Constants.class"
                    ),
                    "com.consts:consts"
                )
            )
        );
    }

    /**
     * Wildcard imports must cover any class inside the imported package.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void treatsWildcardImportAsUsage(@TempDir final Path dir) throws Exception {
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/UsesWild.java",
            String.join(
                String.valueOf('\n'),
                "package com.example;",
                "import com.wild.*;",
                "public class UsesWild {}",
                ""
            )
        );
        Assertions.assertDoesNotThrow(
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWithUnused(
                    src,
                    DependenciesValidatorTest.jar(
                        dir, "wild.jar", "com/wild/Thing.class"
                    ),
                    "com.wild:wild"
                )
            )
        );
    }

    /**
     * Without any matching import, an "unused declared" compile-scope
     * dependency must still fail the build even when sources exist.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void stillFailsWithoutMatchingImport(@TempDir final Path dir)
        throws Exception {
        final Path src = DependenciesValidatorTest.sourceRoot(dir);
        DependenciesValidatorTest.writeJava(
            src, "com/example/Other.java",
            String.join(
                String.valueOf('\n'),
                "package com.example;",
                "import java.util.List;",
                "public class Other {}",
                ""
            )
        );
        Assertions.assertThrows(
            ValidationException.class,
            () -> new DependenciesValidator().validate(
                DependenciesValidatorTest.envWithUnused(
                    src,
                    DependenciesValidatorTest.jar(
                        dir, "alone.jar", "com/alone/Class.class"
                    ),
                    "com.alone:alone"
                )
            ),
            "a declared dependency that is neither referenced in bytecode nor imported in source must not pass validation"
        );
    }

    private static MavenEnvironment envWith(
        final ProjectDependencyAnalysis analysis
    ) throws Exception {
        return new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            "default",
            new FakeProjectDependencyAnalyzer(analysis)
        ).mock();
    }

    private static MavenEnvironment envWithUnused(final Path src,
        final File jar, final String coord) throws Exception {
        final List<String> parts = Splitter.on(':').splitToList(coord);
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(parts.get(0));
        artifact.setArtifactId(parts.get(1));
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("1.0.0");
        artifact.setType(DependenciesValidatorTest.TYPE);
        artifact.setFile(jar);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final MavenEnvironment env = DependenciesValidatorTest.envWith(
            new ProjectDependencyAnalysis(
                Collections.emptySet(), Collections.emptySet(), unused
            )
        );
        env.project().addCompileSourceRoot(src.toString());
        return env;
    }

    private static File jar(final Path dir, final String name,
        final String... entries) throws Exception {
        final File jar = dir.resolve(name).toFile();
        try (
            JarOutputStream out = new JarOutputStream(
                Files.newOutputStream(jar.toPath())
            )
        ) {
            for (final String entry : entries) {
                out.putNextEntry(new JarEntry(entry));
                out.write(new byte[]{0});
                out.closeEntry();
            }
        }
        return jar;
    }

    private static Path sourceRoot(final Path dir) throws Exception {
        final Path src = dir.resolve("src").resolve("main").resolve("java");
        Files.createDirectories(src);
        return src;
    }

    private static void writeJava(final Path src, final String path,
        final String content) throws Exception {
        final Path target = src.resolve(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content, StandardCharsets.UTF_8);
    }
}
