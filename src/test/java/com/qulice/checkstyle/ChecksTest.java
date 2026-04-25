/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.InputSource;

/**
 * Integration test case for all checkstyle checks.
 * @since 0.3
 */
final class ChecksTest {

    /**
     * Test checkstyle for true positive.
     * @param dir Directory where test scripts are located
     * @param name The name of the Invalid*.java file
     * @throws Exception If something goes wrong
     */
    @ParameterizedTest
    @MethodSource("invalids")
    void testCheckstyleTruePositive(final String dir, final String name)
        throws Exception {
        final AuditCollector collector = new AuditCollector();
        this.run(
            dir, String.format("/%s", name),
            new FakeAuditListener(collector)
        );
        final String[] violations = IOUtils.toString(
            Objects.requireNonNull(
                this.getClass().getResourceAsStream(
                    String.format(
                        "%s/violations%s.txt",
                        dir,
                        name.substring(
                            "Invalid".length(),
                            name.length() - ".java".length()
                        )
                    )
                )
            ),
            StandardCharsets.UTF_8
        ).split(String.valueOf('\n'));
        MatcherAssert.assertThat(
            String.format(
                "Expected exactly %d violations from %s/%s (%s)",
                violations.length, dir, name, collector.summary()
            ),
            collector.eventCount() == violations.length
                && Arrays.stream(violations).allMatch(
                    line -> {
                        final String[] sectors = line.split(":");
                        return collector.has(
                            Integer.valueOf(sectors[0]), sectors[1].trim()
                        );
                    }
                ),
            Matchers.is(true)
        );
    }

    /**
     * Test checkstyle for true negative.
     * @param dir Directory where test scripts are located
     * @param name The name of the Valid*.java file
     * @throws Exception If something goes wrong
     */
    @ParameterizedTest
    @MethodSource("valids")
    void testCheckstyleTrueNegative(final String dir, final String name)
        throws Exception {
        final AuditCollector collector = new AuditCollector();
        this.run(
            dir, String.format("/%s", name),
            new FakeAuditListener(collector)
        );
        MatcherAssert.assertThat(
            String.format("Log should be empty for valid file %s/%s", dir, name),
            collector.summary(),
            Matchers.equalTo("")
        );
    }

    /**
     * Check one file.
     * @param dir Directory where test scripts are located
     * @param name The name of the check
     * @param listener The listener
     * @throws Exception If something goes wrong inside
     */
    private void run(
        final String dir, final String name, final AuditListener listener
    ) throws Exception {
        final Checker checker = new Checker();
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        checker.configure(
            ConfigurationLoader.loadConfiguration(
                new InputSource(
                    this.getClass().getResourceAsStream(
                        String.format("%s/config.xml", dir)
                    )
                ),
                new PropertiesExpander(new Properties()),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            )
        );
        final List<File> files = new ArrayList<>(0);
        files.add(
            new File(
                this.getClass().getResource(
                    String.format("%s%s", dir, name)
                ).getFile()
            )
        );
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
    }

    /**
     * Arguments stream of (dir, InvalidXxx.java) pairs for all checks.
     * @return Stream of Arguments
     */
    private static Stream<Arguments> invalids() {
        return ChecksTest.checks().flatMap(dir -> ChecksTest.files(dir, "Invalid"));
    }

    /**
     * Arguments stream of (dir, ValidXxx.java) pairs for all checks.
     * @return Stream of Arguments
     */
    private static Stream<Arguments> valids() {
        return ChecksTest.checks().flatMap(dir -> ChecksTest.files(dir, "Valid"));
    }

    /**
     * Find all files in the given resource directory whose name is
     * either {@code prefix.java} or starts with {@code prefix-} and
     * ends with {@code .java}.
     * @param dir Resource directory (e.g. {@code ChecksTest/FooCheck})
     * @param prefix File prefix (e.g. {@code Invalid} or {@code Valid})
     * @return Stream of (dir, fileName) Arguments
     */
    private static Stream<Arguments> files(
        final String dir, final String prefix
    ) {
        final File directory = new File(
            Objects.requireNonNull(
                ChecksTest.class.getResource(dir),
                String.format("Resource directory not found: %s", dir)
            ).getFile()
        );
        final File[] found = directory.listFiles(
            (parent, child) -> child.endsWith(".java")
                && (
                    child.equals(String.format("%s.java", prefix))
                        || child.startsWith(String.format("%s-", prefix))
                )
        );
        final Stream<Arguments> result;
        if (found == null) {
            result = Stream.empty();
        } else {
            result = Arrays.stream(found)
                .sorted()
                .map(file -> Arguments.of(dir, file.getName()));
        }
        return result;
    }

    /**
     * Returns full list of checks.
     * @return The list
     */
    private static Stream<String> checks() {
        return Stream.of(
            "MethodsOrderCheck",
            "MultilineJavadocTagsCheck",
            "StringLiteralsConcatenationCheck",
            "ProhibitLineSeparatorInStringsCheck",
            "EmptyLinesCheck",
            "EmptyLineBeforeFirstMemberCheck",
            "ImportCohesionCheck",
            "BracketsStructureCheck",
            "NestedSwitchCheck",
            "ProhibitTestExpectedCheck",
            "ProhibitTestMethodNameCheck",
            "MethodDeclarationLengthCheck",
            "CurlyBracketsStructureCheck",
            "JavadocLocationCheck",
            "JavadocParameterOrderCheck",
            "MethodBodyCommentsCheck",
            "RequireThisCheck",
            "ProtectedMethodInFinalClassCheck",
            "NoJavadocForOverriddenMethodsCheck",
            "NonStaticMethodCheck",
            "ConstantUsageCheck",
            "JavadocEmptyLineCheck",
            "JavadocEmptyLineBeforeTagCheck",
            "JavadocFirstLineCheck",
            "JavadocTagsCheck",
            "JavadocTagsDotCheck",
            "JavadocThrowsCheck",
            "ProhibitNonFinalClassesCheck",
            "QualifyInnerClassCheck",
            "CommentCheck",
            "ProhibitUnusedPrivateConstructorCheck",
            "ConstructorsOrderCheck",
            "ConstructorsCodeFreeCheck",
            "RedundantSuperConstructorCheck",
            "StaticAccessViaInstanceCheck",
            "SingleSpaceSeparatorCheck",
            "SimpleStringSplitCheck",
            "ProhibitFieldsInTestClassesCheck"
        ).map(s -> String.format("ChecksTest/%s", s));
    }
}
