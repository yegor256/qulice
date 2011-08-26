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
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SvnPropertiesCheck.class, ProcessBuilder.class })
public class SvnPropertiesCheckTest {

    private FileSetCheck check;

    private MessageDispatcher dispatcher;

    @Before
    public void mockProcessBuilder() throws Exception {
        PowerMockito.mockStatic(ProcessBuilder.class);
    }

    @Before
    public void prepareCheck() throws Exception {
        this.dispatcher = mock(MessageDispatcher.class);
        final Configuration config = mock(Configuration.class);
        doReturn(new String[]{}).when(config).getAttributeNames();
        doReturn(new Configuration[]{}).when(config).getChildren();
        doReturn(
            new ImmutableSortedMap.Builder<String,String>(Ordering.natural())
            .build()
        ).when(config).getMessages();
        this.check = new SvnPropertiesCheck();
        check.configure(config);
        check.setMessageDispatcher(dispatcher);
    }

    @Test
    public void testSimulatesSvnPropgetRequest() throws Exception {
        final Map<String, String> props = new HashMap<String, String>();
        props.put("svn:keywords", "Id");
        props.put("svn:eol-style", "native");
        final File file = new File("foo.txt");
        for (Map.Entry<String, String> entry : props.entrySet()) {
            final InputStream stream = IOUtils.toInputStream(entry.getValue());
            final Process proc = mock(Process.class);
            doReturn(stream).when(proc).getInputStream();
            final ProcessBuilder builder = PowerMockito.mock(ProcessBuilder.class);
            PowerMockito.doReturn(proc).when(builder).start();
            PowerMockito.whenNew(ProcessBuilder.class)
                .withArguments(
                    eq(SvnPropertiesCheck.SVN),
                    eq(SvnPropertiesCheck.PROPGET),
                    eq(entry.getKey()), eq(file.getPath())
                ).thenReturn(builder);
        }
        this.process(file);
        verify(dispatcher, times(0))
            .fireErrors(anyString(), (java.util.TreeSet) anyObject());
    }

    @Test
    public void testWithMissedSubversionProperties() throws Exception {
        final File file = new File("bar.txt");
        final InputStream stream = IOUtils.toInputStream("");
        final Process proc = mock(Process.class);
        doReturn(stream).when(proc).getInputStream();
        final ProcessBuilder builder = PowerMockito.mock(ProcessBuilder.class);
        PowerMockito.doReturn(proc).when(builder).start();
        PowerMockito.whenNew(ProcessBuilder.class)
            .withArguments(
                eq(SvnPropertiesCheck.SVN), eq(SvnPropertiesCheck.PROPGET),
                anyString(), eq(file.getPath()))
            .thenReturn(builder);
        this.process(file);
        verify(dispatcher)
            .fireErrors(anyString(), (java.util.TreeSet) anyObject());
    }

    @Test
    public void testWithIOExceptionInFile() throws Exception {
        final File file = new File("failed.txt");
        final ProcessBuilder builder = PowerMockito.mock(ProcessBuilder.class);
        PowerMockito.doThrow(new java.io.IOException("oops"))
            .when(builder).start();
        PowerMockito.whenNew(ProcessBuilder.class)
            .withArguments(
                eq(SvnPropertiesCheck.SVN), eq(SvnPropertiesCheck.PROPGET),
                anyString(), eq(file.getPath()))
            .thenReturn(builder);
        this.process(file);
        verify(dispatcher)
            .fireErrors(anyString(), (java.util.TreeSet) anyObject());
    }

    private void process(final File file) {
        this.check.init();
        this.check.beginProcessing("UTF-8");
        this.check.process(file, new ArrayList<String>());
        this.check.finishProcessing();
        this.check.destroy();
    }

}
