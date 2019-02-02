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

import com.qulice.spi.Environment;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.cactoos.list.ListOf;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test for exclusion filters.
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle LineLengthCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 0.18.15
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.AvoidDuplicateLiterals"
    })
class FindBugsExcludesTest {

    @Test
    public void ifCantCreateTempFile() {
        final Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.tempdir()).thenReturn(new File("/dev/null"));
        Mockito.when(env.excludes("findbugs")).thenReturn(
            new ListOf<>("org.package.Class")
        );
        Assertions.assertThrows(
            IOException.class,
            new FindBugsExcludes(env)::asArguments,
            "Didn't throw an exception for invalid file"
        );
    }

    @Test
    public void noExcludesNoArguments() throws Exception {
        final Environment env = new Environment.Mock()
            .withExcludes("");
        MatcherAssert.assertThat(
            "The arguments were not empty for empty excludes",
            new FindBugsExcludes(env).asArguments(),
            new IsEmptyCollection<>()
        );
    }

    @Test
    public void notEmptyExcludesShouldBeNotEmptyArguments() throws Exception {
        final Environment env = new Environment.Mock()
            .withExcludes("Foo,Bar");
        final Collection<String> arguments = new FindBugsExcludes(env)
            .asArguments();
        MatcherAssert.assertThat(
            "The argument was empty for not empty excludes",
            arguments,
            new IsNot<>(
                new IsEmptyCollection<>()
            )
        );
        MatcherAssert.assertThat(
            "Expected a single argument",
            arguments.size(),
            new IsEqual<>(1)
        );
        new FindBugsExcludesTest.ExcludesXml(
            new SAXReader().read(arguments.iterator().next())
        ).assertXpath("count(/FindBugsFilter)=1");
    }

    @Test
    public void argumentIsReadableXml() throws Exception {
        final Iterator<String> iter = new FindBugsExcludes(
            new Environment.Mock()
            .withExcludes("ClassToExclude,AnotherClass")
        ).asArguments().iterator();
        final ExcludesXml xml = new ExcludesXml(
            new SAXReader().read(iter.next())
        );
        xml.assertXpath("count(/FindBugsFilter)=1");
        xml.assertXpath("count(/FindBugsFilter/Match)=2");
    }

    @Test
    public void noExclude() throws IOException {
        final ExcludesXml xml = new ExcludesXml("");
        xml.assertXpath("count(/FindBugsFilter)=1");
        xml.assertXpath("count(/FindBugsFilter/Match)=0");
    }

    @Test
    public void emptyClassNotEmptyMethodEmptyRule() throws IOException {
        final ExcludesXml xml = new ExcludesXml(":toString");
        xml.assertXpath("count(//Match)=1");
        xml.assertXpath("count(//Match/Method)=1");
        final ExcludesXml same = new ExcludesXml(":toString:");
        same.assertXpath("count(//Match)=1");
        same.assertXpath("count(//Match/Method[@name='toString'])=1");
    }

    @Test
    public void emptyClassEmptyMethodEmptyRule() throws IOException {
        final ExcludesXml xml = new ExcludesXml(":,::,:::");
        xml.assertXpath("count(/FindBugsFilter)=1");
        xml.assertXpath("count(/FindBugsFilter/Match)=0");
        xml.assertXpath("count(//Match)=0");
    }

    @Test
    public void singleClass() throws IOException {
        final ExcludesXml xml = new ExcludesXml("Foo");
        xml.assertXpath("count(/FindBugsFilter/Match)=1");
        xml.assertXpath("count(/FindBugsFilter/Match/Class[@name='Foo'])=1");
        xml.assertXpath("count(/FindBugsFilter/Match[Class[@name='Foo']]/Method)=0");
        xml.assertXpath("count(/FindBugsFilter/Match[Class[@name='Foo']]/Bug)=0");
    }

    @Test
    public void multipleClasses() throws IOException {
        final ExcludesXml xml = new ExcludesXml("Alice:set:FI_EMPTY,Bob:get");
        xml.assertXpath("count(//Match)=2");
        xml.assertXpath("count(//Class)=2");
        xml.assertXpath("count(//Method)=2");
        xml.assertXpath("count(//Bug)=1");
        xml.assertXpath("count(//Match/Class[@name='Alice'])=1");
        xml.assertXpath("count(//Match[Class[@name='Alice']]/Method[@name='set'])=1");
        xml.assertXpath("count(//Match[Class[@name='Alice'] and Method[@name='set']]/Bug[@pattern='FI_EMPTY'])=1");
        xml.assertXpath("count(//Match/Class[@name='Bob'])=1");
        xml.assertXpath("count(//Match[Class[@name='Bob']]/Method[@name='get'])=1");
        xml.assertXpath("count(//Match[Class[@name='Bob']]/Bug)=0");
    }

    @Test
    public void classMethod() throws IOException {
        final ExcludesXml xml = new ExcludesXml("MyClass:myMethod");
        xml.assertXpath("count(/FindBugsFilter/Match[Class[@name='MyClass']]/Method[@name='myMethod'])=1");
    }

    @Test
    public void classMethodRule() throws IOException {
        final ExcludesXml xml = new ExcludesXml("MyClass:myMethod:DLS_DEAD_LOCAL_STORE");
        xml.assertXpath("count(//Match)=1");
        xml.assertXpath("count(//Match/Class[@name='MyClass'])=1");
        xml.assertXpath("count(//Match[Class[@name='MyClass'] and Method[@name='myMethod']]/Bug[@pattern='DLS_DEAD_LOCAL_STORE'])=1");
    }

    @Test
    public void wholeClassOneRule() throws IOException {
        final ExcludesXml xml = new ExcludesXml("MyClass::FI_USELESS");
        xml.assertXpath("count(//Match)=1");
        xml.assertXpath("count(//Match/Class[@name='MyClass'])=1");
        xml.assertXpath("count(//Match/Method)=0");
        xml.assertXpath("count(//Match/Bug[@pattern='FI_USELESS'])=1");
    }

    @Test
    public void anyClassOneRule() throws IOException {
        final ExcludesXml xml = new ExcludesXml("::DM_EXIT");
        xml.assertXpath("count(//Class)=0");
        xml.assertXpath("count(//Method)=0");
        xml.assertXpath("count(//Match)=1");
        xml.assertXpath("count(//Match/*)=1");
        xml.assertXpath("count(//Match/Bug[@pattern='DM_EXIT'])=1");
    }

    @Test
    public void tooManyColonsAreIgnored() throws IOException {
        final ExcludesXml xml = new ExcludesXml("MyClass:myMethod:DM_EXIT:::");
        xml.assertXpath("count(/FindBugsFilter)=1");
        xml.assertXpath("count(//Match)=1");
        xml.assertXpath("count(//Match/Class[@name='MyClass'])=1");
        xml.assertXpath("count(//Match/Method[@name='myMethod'])=1");
        xml.assertXpath("count(//Match/Bug[@pattern='DM_EXIT'])=1");
    }

    /**
     * To simplify the XML assertions.
     */
    static class ExcludesXml {
        /**
         * XML contents.
         */
        private final Document xml;

        /**
         * Ctor.
         * @param xml The XML document that will be tested
         */
        ExcludesXml(final Document xml) {
            this.xml = xml;
        }

        /**
         * Ctor.
         * @param excludes The excludes that the XML will be generated from.
         * @throws IOException
         */
        ExcludesXml(final String excludes) throws IOException {
            this(
                new FindBugsExcludes(
                    new Environment.Mock()
                        .withExcludes(excludes)
                ).asXml()
            );
        }

        /**
         * Checks if the XPATH in the xml is true.
         * @param xpath
         */
        public final void assertXpath(final String xpath) {
            MatcherAssert.assertThat(
                String.format(
                    "Xpath expression '%s' incorrect on xml: '%s'",
                    xpath,
                    this.xml.asXML()
                ),
                this.xml.createXPath(xpath).booleanValueOf(this.xml),
                new IsEqual<>(true)
            );
        }
    }
}

