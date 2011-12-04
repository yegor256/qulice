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
package com.qulice.spi;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.mockito.Mockito;
import org.apache.commons.io.FileUtils;

/**
 * Mocker of {@link Environment}.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class EnvironmentMocker {

    /**
     * The basedir.
     */
    private final File basedir;

    /**
     * Public ctor.
     */
    public EnvironmentMocker() {
        this.basedir = Files.createTempDir();
    }

    /**
     * With this file on board.
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
     * Mock it.
     * @return The instance of {@link Environment}
     */
    public Environment mock() {
        final Environment env = Mockito.mock(Environment.class);
        Mockito.doReturn(this.basedir).when(env).basedir();
        Mockito.doReturn(new File(this.basedir, "target/tempdir"))
            .when(env).tempdir();
        Mockito.doReturn(new File(this.basedir, "target/classes"))
            .when(env).outdir();
        return env;
    }

}
