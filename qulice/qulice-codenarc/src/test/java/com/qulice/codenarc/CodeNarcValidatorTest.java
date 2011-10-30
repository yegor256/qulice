/**
 * Copyright (c) 2011, Qulice.com
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
package com.qulice.codenarc;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Test case for {@link CodeNarcValidator} class.
 * @author Pavlo Shamrai (pshamrai@gmail.com)
 * @version $Id: CodeNarcValidatorTest.java 45 2011-10-27 19:34:11Z pshamrai@gmail.com $
 */
public final class CodeNarcValidatorTest {

    /**
     * Temporary folder, set by JUnit framework automatically.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    /**
     * The folder to work in, to store sources.
     * @see #prepare()
     */
    private File src;

    /**
     * The environment to work with.
     * @see #prepare()
     */
    private Environment env;

    /**
     * Prepare the folder and the environment.
     * @throws Exception If something wrong happens inside
     */
    @Before
    public void prepare() throws Exception {
        final File basedir = this.temp.newFolder("basedir");
        this.src = new File(basedir, "src");
        this.src.mkdirs();
        this.env = Mockito.mock(Environment.class);
        Mockito.doReturn(basedir).when(this.env).basedir();
        Mockito.doReturn(new File(basedir, "target")).when(this.env).tempdir();
    }

    /**
     * Validate set of files to find violations.
     * @throws Exception If something wrong happens inside.
     */

    @Test(expected = ValidationException.class)
    public void testFailValidation() throws Exception {
        final Validator validator = new CodeNarcValidator();
        final File groovy = new File(this.src, "FailedMain.groovy");
        FileUtils.writeStringToFile(groovy, "class failedMain { int x = 0 }");
        validator.validate(this.env);
    }

    /**
     * Validate set of files without violations.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void testSuccessValidation() throws Exception {
        final Validator validator = new CodeNarcValidator();
        final File groovy = new File(this.src, "SuccessMain.groovy");
        FileUtils.writeStringToFile(groovy, "class SuccessMain { int x = 0 }");
        validator.validate(this.env);
    }

}
