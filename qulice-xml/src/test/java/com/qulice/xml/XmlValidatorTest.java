/**
 * Copyright (c) 2011-2014, Qulice.com
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

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import org.junit.Test;

/**
 * Test case for {@link XmlValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @todo #1 Let's implement a proper validation of XML
 *  document formatting (!). Not just XML validity, but
 *  formatting of the document, including indentation, spaces,
 *  quotation marks used, etc.
 */
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
                "<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://maven.apache.org/xsd/changes-1.0.0.xsd\"></document>"
        );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }

    /**
     * Should pass validation if XML schema file is not accessible.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void passesValidationIfSchemaIsNotAccessible() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(
                "src/main/resources/valid2.xml",
                // @checkstyle LineLength (1 line)
                "<document xmlns=\"http://maven.apache.org/changes/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/changes/1.0.0 http://www.google.com\"></document>"
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
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n xsi:noNamespaceSchemaLocation=\"http://maven.apache.org/xsd/decoration-1.3.0.xsd\" \n name=\"test\">\n</project>"
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
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n xsi:noNamespaceSchemaLocation=\"http://simple.com/test.xsd\">\n</project>"
            ).withFile(
                "src/main/resources/test.xsd",
                // @checkstyle LineLength (1 line)
                "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n<element name=\"project\" type=\"xs:anyType\"/>\n</schema>"
            );
        final Validator validator = new XmlValidator();
        validator.validate(env);
    }
}
