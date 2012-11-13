/**
 * Copyright (c) 2011-2012, Qulice.com
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
package com.qulice.spi;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

/**
 * Mocker of {@link Environment}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class EnvironmentMocker {

    /**
     * The basedir.
     */
    private final transient File basedir;

    /**
     * Files for classpath.
     */
    private final transient Set<File> classpath = new HashSet<File>();

    /**
     * Map of params.
     */
    private final transient ConcurrentMap<String, String> params =
        new ConcurrentHashMap<String, String>();

    /**
     * Public ctor.
     * @throws IOException If some IO problem
     */
    public EnvironmentMocker() throws IOException {
        final File temp = Files.createTempDir();
        FileUtils.forceDeleteOnExit(temp);
        this.basedir = new File(temp, "basedir");
        this.basedir.mkdirs();
    }

    /**
     * With this param and its value.
     * @param name Param name
     * @param value Param value
     * @return This object
     */
    public EnvironmentMocker withParam(final String name, final String value) {
        this.params.put(name, value);
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param content File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public EnvironmentMocker withFile(final String name, final String content)
        throws IOException {
        final File file = new File(this.basedir, name);
        FileUtils.writeStringToFile(file, content);
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param bytes File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public EnvironmentMocker withFile(final String name, final byte[] bytes)
        throws IOException {
        final File file = new File(this.basedir, name);
        FileUtils.writeByteArrayToFile(file, bytes);
        return this;
    }

    /**
     * With default classpath.
     * @return This object
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public EnvironmentMocker withDefaultClasspath() {
        for (String file : System.getProperty("java.class.path")
            .split(System.getProperty("path.separator"))) {
            this.classpath.add(new File(file));
        }
        return this;
    }

    /**
     * Get basedir.
     * @return The basedir
     */
    public File getBasedir() {
        return this.basedir;
    }

    /**
     * Mock it.
     * @return The instance of {@link Environment}
     */
    public Environment mock() {
        final Environment env = Mockito.mock(Environment.class);
        Mockito.doReturn(this.basedir).when(env).basedir();
        final File tempdir = new File(this.basedir, "target/tempdir");
        tempdir.mkdirs();
        Mockito.doReturn(tempdir).when(env).tempdir();
        final File outdir = new File(this.basedir, "target/classes");
        outdir.mkdirs();
        Mockito.doReturn(outdir).when(env).outdir();
        this.classpath.add(outdir);
        Mockito.doReturn(this.classpath).when(env).classpath();
        for (ConcurrentMap.Entry<String, String> entry
            : this.params.entrySet()) {
            Mockito.doReturn(entry.getValue()).when(env)
                .param(Mockito.eq(entry.getKey()), Mockito.anyString());
        }
        return env;
    }

}
