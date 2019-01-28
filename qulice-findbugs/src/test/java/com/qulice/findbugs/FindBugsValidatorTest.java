/*
 * Copyright (c) 2011-2019, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.findbugs;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link FindBugsValidator}.
 * @since 0.3
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class FindBugsValidatorTest {

    /**
     * Having a class with the same name as interface (Remote).
     * violates findbugs rule NM_SAME_SIMPLE_NAME_AS_INTERFACE
     * Class name
     */
    public static final String REMOTE_FILE = "target/classes/Remote.class";

    /**
     * Having a class with the same name as interface (Remote)
     * violates findbugs rule NM_SAME_SIMPLE_NAME_AS_INTERFACE.
     * Source code
     * @checkstyle LineLengthCheck (2 lines)
     */
    public static final String REMOTE_SOURCE = "class Remote implements java.rmi.Remote { }";

    /**
     * FindbugsValidator can pass correct files with no exceptions.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void passesCorrectFilesWithNoExceptions() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile("src/main/java/Main.java", "class Main { int x = 0; }")
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * FindbugsValidator can report incorrectly added throws.
     * @throws Exception If something wrong happens inside
     */
    @Ignore
    @Test(expected = ValidationException.class)
    public void reportsIncorrectlyAddedThrows() throws Exception {
        final byte[] bytecode = new BytecodeMocker()
            .withSource(
                Joiner.on("\n").join(
                    "package test;",
                    "final class Main {",
                    "public void foo() throws InterruptedException {",
                    "System.out.println(\"test\");",
                    "}",
                    "}"
                )
            )
            .mock();
        final Environment env = new Environment.Mock()
            .withFile("target/classes/Main.class", bytecode)
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * FindbugsValidator can ignore correct throws.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void ignoresCorrectlyAddedThrows() throws Exception {
        final byte[] bytecode = new BytecodeMocker()
            .withSource(
                Joiner.on("\n").join(
                    "package test;",
                    "final class Main {",
                    "public void foo() throws InterruptedException {",
                    "Thread.sleep(1);",
                    "}",
                    "}"
                )
            )
            .mock();
        final Environment env = new Environment.Mock()
            .withFile("target/classes/Main.class", bytecode)
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * FindbugsValidator throw exception for invalid file.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void throwsExceptionOnViolation() throws Exception {
        final byte[] bytecode = new BytecodeMocker()
            .withSource("class Foo { public Foo clone() { return this; } }")
            .mock();
        final Environment env = new Environment.Mock()
            .withFile("target/classes/Foo.class", bytecode)
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * FindbugsValidator can exclude classes from check.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void excludesIncorrectClassFormCheck() throws Exception {
        final byte[] bytecode = new BytecodeMocker()
            .withSource("class Foo { public Foo clone() { return this; } }")
            .mock();
        final Environment env = new Environment.Mock()
            .withFile("target/classes/Foo.class", bytecode)
            .withExcludes(FindBugsValidator.EXCLUDE, "Foo")
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * FindbugsValidator can exclude several classes from check.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void excludesSeveralIncorrectClassFromCheck() throws Exception {
        final byte[] bytecode = new BytecodeMocker()
            .withSource("class Foo { public Foo clone() { return this; } }")
            .mock();
        final byte[] another = new BytecodeMocker()
            .withSource("class Bar { public Bar clone() { return this; } }")
            .mock();
        final Environment env = new Environment.Mock()
            .withFile("target/classes/Foo.class", bytecode)
            .withFile("target/classes/Bar.class", another)
            .withExcludes(FindBugsValidator.EXCLUDE, "Foo,Bar")
            .withDefaultClasspath();
        new FindBugsValidator().validate(env);
    }

    /**
     * Test of findbugs-filter
     * Having a class with the same name as interface (Remote)
     * violates findbugs rule NM_SAME_SIMPLE_NAME_AS_INTERFACE
     * so we disable the rule by providing the filter file.
     * @throws Exception If something wrong
     */
    @Test
    public void excludesFileFilter() throws Exception {
        final File tmp = File.createTempFile(
            FindBugsValidator.EXCLUDE_FILTER, ".xml"
        );
        final Document document = DocumentHelper.createDocument();
        final Element match = document
            .addElement("FindBugsFilter")
            .addElement("Match");
        match.addElement("Bug").addAttribute(
            "pattern",
            "NM_SAME_SIMPLE_NAME_AS_INTERFACE"
        );
        match.addElement("Class").addAttribute(
            "name",
            "Remote"
        );
        new XMLWriter(
            new FileOutputStream(tmp)
        ).write(document);
        final Environment env = new Environment.Mock()
            .withFile(
                FindBugsValidatorTest.REMOTE_FILE,
                new BytecodeMocker()
                    .withSource(FindBugsValidatorTest.REMOTE_SOURCE)
                    .mock()
            )
            .withDefaultClasspath()
            .withExcludes(
                FindBugsValidator.EXCLUDE_FILTER,
                tmp.getAbsolutePath()
            );
        new FindBugsValidator().validate(env);
    }

    /**
     * Test of findbugs-filter exception.
     * There can only be one file filter
     * @throws Exception Illegal config
     */
    @Test(expected = IllegalStateException.class)
    public void haveTwoFileFiltersIsIncorrect() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                FindBugsValidatorTest.REMOTE_FILE,
                new BytecodeMocker()
                    .withSource(FindBugsValidatorTest.REMOTE_SOURCE)
                    .mock()
            )
            .withDefaultClasspath()
            .withExcludes(
                FindBugsValidator.EXCLUDE_FILTER,
                "file.xml,file2.xml"
            );
        new FindBugsValidator().validate(env);
    }

    /**
     * Either file filter or class filters exception is allowed.
     * but not both together
     * @throws Exception Illegal config
     */
    @Test(expected = IllegalStateException.class)
    public void mixClassesAndFileFiltersIsIncorrect() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                FindBugsValidatorTest.REMOTE_FILE,
                new BytecodeMocker()
                    .withSource(FindBugsValidatorTest.REMOTE_SOURCE)
                    .mock()
            )
            .withDefaultClasspath()
            .withExcludes(
                FindBugsValidator.EXCLUDE_FILTER,
                "file.xml"
            ).withExcludes(
                FindBugsValidator.EXCLUDE,
                "org.company.Class"
            );
        new FindBugsValidator().validate(env);
    }

}
