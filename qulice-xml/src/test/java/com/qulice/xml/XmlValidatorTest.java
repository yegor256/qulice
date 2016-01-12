/**
 * Copyright (c) 2011-2015, Qulice.com
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
package com.qulice.xml;

import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XMLDocument;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link XmlValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlValidatorTest {

    /**
     * Should fail validation in case of wrong XML.
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failsValidationOnWrongFile() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile("src/main/resources/invalid.xml", "<a></a>");
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    /**
     * Should pass validation in case of correct XML.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void passesValidationOnCorrectFile() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/valid.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://maven.apache.org/xsd/changes-1.0.0.xsd\">\n    <body/>\n</document>\n"
        );
        final Validator validator = new XmlValidator(true);
        validator.validate(env);
    }

    /**
     * Should fail validation for XML without schema specified.
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failValidationWithoutSchema() throws Exception {
        new XmlValidator(true).validate(new Environment.Mock()
            .withFile(
                "src/main/resources/noschema.xml",
                // @checkstyle LineLength (1 line)
                "<Configuration><Appenders><Console name=\"CONSOLE\" target=\"SYSTEM_OUT\"><PatternLayout pattern=\"[%p] %t %c: %m%n\"/></Console></Appenders></Configuration>"
            )
        );
    }

    /**
     * XmlValidator can inform about missing end of line at the and of file.
     * @throws Exception In case of error.
     */
    @Test
    public void informsAboutMissingEOLAtEOF() throws Exception {
        final Environment env = new Environment.Mock().withFile(
            "src/main/resources/valid5.xml",
            // @checkstyle LineLength (1 line)
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project xmlns=\"http://maven.apache.org/DECORATION/1.3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/DECORATION/1.3.0 http://maven.apache.org/xsd/decoration-1.3.0.xsd\" name=\"xockets-hadoop-transport\">\n</project>"
        );
        final Validator validator = new XmlValidator(true);
        String message = "";
        try {
            validator.validate(env);
        } catch (final ValidationException ex) {
            message = ex.getMessage();
        }
        MatcherAssert.assertThat(
            message,
            Matchers.allOf(
                Matchers.containsString("--- before"),
                Matchers.containsString("+++ after"),
                Matchers.containsString("</project>\n+")
            )
        );
    }

    /**
     * Should fail validation on incorrectly formatted file.
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failsValidationOnIncorrectlyFormattedFile() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/almost-valid.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://maven.apache.org/xsd/changes-1.0.0.xsd\"><body/></document>"
        );
        final Validator validator = new XmlValidator(true);
        validator.validate(env);
    }

    /**
     * Should pass validation if XML schema file is not accessible.
     * @throws Exception If something wrong happens inside.
     * @todo #246 XmlValidator should be able to log IO problems (for example,
     *  inability to connect to a server) and ignore them (see ticket #243).\
     *  However, {@link com.jcabi.xml.StrictXML} class outright throws an
     *  IllegalArgumentException in such cases. Let's find a way to detect
     *  whether a failure was caused by such IO errorsand fix this test.
     */
    @Test
    @org.junit.Ignore
    public void passesValidationIfSchemaIsNotAccessible() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/valid2.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://www.google.com\"></document>"
        );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    /**
     * Should fail validation in case of noNamespaceSchemaLocation attribute.
     * specified on xml instance, while targetNamespace attribute exists in
     * schema
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failValidationWithNoSchemaLocationAttr() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/valid3.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n xsi:noNamespaceSchemaLocation=\"http://maven.apache.org/xsd/decoration-1.3.0.xsd\" \n name=\"test\">\n</project>"
        );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    /**
     * Should pass validation if noNamespaceSchemaLocation attribute specified.
     * @throws Exception If something wrong happens inside.
     * @checkstyle IndentationCheck (15 lines)
     */
    @Test
    public void passesValidationIfNoSchemaLocationSpecified() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/valid4.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://simple.com/test.xsd\">\n</project>\n"
            ).withFile(
                "src/main/resources/test.xsd",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n<element name=\"project\" type=\"xs:anyType\"/>\n</schema>"
            );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    /**
     * Should pass validation for valid XML when multiple schemas are specified.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void passesValidationWithMultipleSchemas() throws Exception {
        // @checkstyle MultipleStringLiterals (50 lines)
        // @checkstyle LineLength (10 lines)
        final String xml = new StringBuilder()
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ")
            .append("<beans xmlns=\"http://www.springframework.org/schema/beans\" ")
            .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
            .append("xmlns:util=\"http://www.springframework.org/schema/util\" ")
            .append("xsi:schemaLocation=\"")
            .append("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd ")
            .append("http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd\">")
            .append("<bean id=\"bean\" class=\"bean\"></bean>")
            .append("<util:constant static-field=\"blah\"/>")
            .append("</beans>")
            .toString();
        final Environment env = new Environment.Mock()
            .withFile("src/main/resources/valid-multi.xml", xml);
        final Validator validator = new XmlValidator(false);
        validator.validate(env);
    }

    /**
     * Should fail validation for invalid XML if multiple schemas are specified.
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failsValidationWithMultipleSchemas() throws Exception {
        // @checkstyle LineLength (10 lines)
        final String xml = new StringBuilder()
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ")
            .append("<beans xmlns=\"http://www.springframework.org/schema/beans\" ")
            .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
            .append("xmlns:util=\"http://www.springframework.org/schema/util\" ")
            .append("xsi:schemaLocation=\"")
            .append("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd ")
            .append("http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd\">")
            .append("<bean noSuchAttribute=\"fail\"/>")
            .append("<util:shouldFail/>")
            .append("</beans>")
            .toString();
        final Environment env = new Environment.Mock()
            .withFile("src/main/resources/invalid-multi.xml", xml);
        final Validator validator = new XmlValidator(false);
        validator.validate(env);
    }

    /**
     * Should pass validation if comments are before parent tag.
     * @throws Exception If something wrong happens inside.
     * @checkstyle IndentationCheck (15 lines)
     */
    @Test
    public void passesValidationIfCommentsAreBeforeParentTag()
        throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/comments.xml",
                // @checkstyle LineLength (1 line)
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- comment -->\n<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://maven.apache.org/xsd/changes-1.0.0.xsd\">\n  <body/>\n</document>\n"
            );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    @Test
    public void passesValidationForClasspathSchema() throws Exception {
        final Environment env = new Environment.Mock().withFile(
            "test-classpath-schema.xml",
            new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<foo xmlns=\"http://qulice.com/test/schema\" ")
                .append("xmlns:xsi=\"")
                .append("http://www.w3.org/2001/XMLSchema-instance")
                .append("\" ")
                .append("xsi:schemaLocation=\"")
                .append("http://qulice.com/test/schema ")
                .append("test-classpath-schema.xsd")
                .append("\">")
                .append("<bar>333</bar>")
                .append("<baz>444</baz>")
                .append("</foo>").toString()
        );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsValidation() throws Exception {
        new StrictXML(
            new XMLDocument("<a></a>")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsValidationForInvalidURI() throws Exception {
        new StrictXML(
            new XMLDocument("<a xsi:schemaLocation=\"http://hello world\"></a>")
        );
    }
}
