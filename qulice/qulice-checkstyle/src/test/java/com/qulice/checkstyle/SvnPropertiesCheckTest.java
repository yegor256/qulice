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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test case for {@link SvnPropertiesCheck}.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SvnPropertiesCheck.class, ProcessBuilder.class })
public final class SvnPropertiesCheckTest {

    /**
     * The check we're testing.
     */
    private FileSetCheck check;

    /**
     * Dispatcher of log messages.
     */
    private MessageDispatcher dispatcher;

    /**
     * Prepare mocked process builder.
     * @throws Exception If something goes wrong
     */
    @Before
    public void mockProcessBuilder() throws Exception {
        PowerMockito.mockStatic(ProcessBuilder.class);
    }

    /**
     * Prepare check for testing.
     * @throws Exception If something goes wrong
     */
    @Before
    public void prepareCheck() throws Exception {
        this.dispatcher = Mockito.mock(MessageDispatcher.class);
        final Configuration config = Mockito.mock(Configuration.class);
        Mockito.doReturn(new String[]{}).when(config).getAttributeNames();
        Mockito.doReturn(new Configuration[]{}).when(config).getChildren();
        Mockito.doReturn(
            new ImmutableSortedMap.Builder<String, String>(Ordering.natural())
                .build()
        ).when(config).getMessages();
        this.check = new SvnPropertiesCheck();
        this.check.configure(config);
        this.check.setMessageDispatcher(this.dispatcher);
    }

    /**
     * Let's simulate the property reading request.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testSimulatesSvnPropgetRequest() throws Exception {
        final Map<String, String> props = new HashMap<String, String>();
        props.put("svn:keywords", "Id");
        props.put("svn:eol-style", "native");
        final File file = new File("foo.txt");
        for (Map.Entry<String, String> entry : props.entrySet()) {
            final InputStream stream = IOUtils.toInputStream(entry.getValue());
            final Process proc = Mockito.mock(Process.class);
            Mockito.doReturn(stream).when(proc).getInputStream();
            final ProcessBuilder builder =
                PowerMockito.mock(ProcessBuilder.class);
            PowerMockito.doReturn(proc).when(builder).start();
            PowerMockito.whenNew(ProcessBuilder.class)
                .withArguments(
                    Mockito.eq(SvnPropertiesCheck.SVN),
                    Mockito.eq(SvnPropertiesCheck.PROPGET),
                    Mockito.eq(entry.getKey()),
                    Mockito.eq(file.getPath())
                ).thenReturn(builder);
        }
        this.process(file);
        Mockito.verify(this.dispatcher, Mockito.times(0)).fireErrors(
            Mockito.anyString(),
            Mockito.any(java.util.TreeSet.class)
        );
    }

    /**
     * Let's try with properties that are missed.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testWithMissedSubversionProperties() throws Exception {
        final File file = new File("bar.txt");
        final InputStream stream = IOUtils.toInputStream("");
        final Process proc = Mockito.mock(Process.class);
        Mockito.doReturn(stream).when(proc).getInputStream();
        final ProcessBuilder builder = PowerMockito.mock(ProcessBuilder.class);
        PowerMockito.doReturn(proc).when(builder).start();
        PowerMockito.whenNew(ProcessBuilder.class)
            .withArguments(
                Mockito.eq(SvnPropertiesCheck.SVN),
                Mockito.eq(SvnPropertiesCheck.PROPGET),
                Mockito.anyString(),
                Mockito.eq(file.getPath())
            ).thenReturn(builder);
        this.process(file);
        Mockito.verify(this.dispatcher).fireErrors(
            Mockito.anyString(),
            Mockito.any(java.util.TreeSet.class)
        );
    }

    /**
     * What if there is an exception.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testWithIOExceptionInFile() throws Exception {
        final File file = new File("failed.txt");
        final ProcessBuilder builder = PowerMockito.mock(ProcessBuilder.class);
        PowerMockito.doThrow(new java.io.IOException("oops"))
            .when(builder).start();
        PowerMockito.whenNew(ProcessBuilder.class)
            .withArguments(
                Mockito.eq(SvnPropertiesCheck.SVN),
                Mockito.eq(SvnPropertiesCheck.PROPGET),
                Mockito.anyString(),
                Mockito.eq(file.getPath())
            ).thenReturn(builder);
        this.process(file);
        Mockito.verify(this.dispatcher).fireErrors(
            Mockito.anyString(),
            Mockito.any(java.util.TreeSet.class)
        );
    }

    /**
     * Let's process one file.
     * @param file The file to process
     */
    private void process(final File file) {
        this.check.init();
        this.check.beginProcessing("UTF-8");
        this.check.process(file, new ArrayList<String>());
        this.check.finishProcessing();
        this.check.destroy();
    }

}
