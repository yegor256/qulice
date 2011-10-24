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
package com.qulice.checkstyle;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import com.puppycrawl.tools.checkstyle.api.MessageDispatcher;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;

/**
 * Test case for {@link BracketsStructureCheck}.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id: BracketsStructureCheckTest.java 71 2011-10-14 20:41:40Z guard $
 */
public final class BracketsStructureCheckTest {

    /**
     * The check we're testing.
     */
    private FileSetCheck check;
    /**
     * Message dispatcher to catch logging mechanism.
     */
    private MessageDispatcher dispatcher;

    /**
     * Prepare the check before testing.
     * @throws Exception If something goes wrong
     */
    @Before
    public void prepareCheck() throws Exception {
        this.dispatcher = Mockito.mock(MessageDispatcher.class);
        final Configuration config = Mockito.mock(Configuration.class);
        Mockito.doReturn(new String[]{}).when(config).getAttributeNames();
        Mockito.doReturn(new Configuration[]{}).when(config).getChildren();
        final ImmutableSortedMap.Builder builder =
            new ImmutableSortedMap.Builder<String, String>(Ordering.natural());
        final ImmutableSortedMap map = builder.build();
        final Stubber stubber = Mockito.doReturn(map);
        stubber.when(config).getMessages();
        this.check = new BracketsStructureCheck();
        this.check.configure(config);
        this.check.setMessageDispatcher(this.dispatcher);
    }

    /**
     * Checks with correct lines.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testWithCorrectLines() throws Exception {
        final String[] correctStrings = {
            "String.format(\n\"File %s not found\",\nfile\n);",
            "String.format(\n\"File %s not found\", file\n);",
            "String.format(\"File %s not found\", file);",
        };
        for (String line : correctStrings) {
            this.process(line);
            Mockito.verify(this.dispatcher, Mockito.times(0)).fireErrors(
                Mockito.anyString(),
                Mockito.any(java.util.TreeSet.class));
        }
    }

    /**
     * Checks with wrong lines.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testWithWrongLines() throws Exception {
        final String[] wrongStrings = {
            "String.format(\"File %s not found\",\nfile);",
            "String.format(\n\"File %s not found\",\nfile);",
            "String.format(\n\"File %s not found\", file);",
        };
        for (String item : wrongStrings) {
            this.process(item);
            Mockito.verify(
                this.dispatcher,
                Mockito.times(1)).fireErrors(
                    Mockito.anyString(),
                    Mockito.any(java.util.TreeSet.class));
        }
    }

    /**
     * Process one text block.
     * @param text The text
     */
    private void process(final String text) {
        this.check.init();
        this.check.beginProcessing("UTF-8");
        this.check.process(
            Mockito.mock(File.class),
            Arrays.asList(StringUtils.split(text, "\n")));
        this.check.finishProcessing();
        this.check.destroy();
    }
}
