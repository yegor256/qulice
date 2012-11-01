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
package com.qulice.pmd;

import com.qulice.spi.Environment;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.FileDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Contains methods to work with source files.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class Files {

    /**
     * Get full list of files to process.
     * @param environment The environment.
     * @return Collection of files.
     * @todo #44:1h Perform refactoring: remove this method, use
     *  Environment.files() instead.
     */
    private Collection<File> getFiles(final Environment environment) {
        final Collection<File> files = new LinkedList<File>();
        final IOFileFilter filter = new WildcardFileFilter("*.java");
        final String[] paths = new String[] {
            "src/main/java",
            "src/test/java",
            "src/mock/java",
        };
        for (String path : paths) {
            final File dir = new File(environment.basedir(), path);
            if (dir.exists()) {
                final Collection<File> sources = FileUtils.listFiles(
                    dir,
                    filter,
                    DirectoryFileFilter.INSTANCE
                );
                files.addAll(sources);
            }
        }
        return files;
    }

    /**
     * Get full list of files to process.
     * @param environment The environment.
     * @return Collection of data sources.
     * @see #validate()
     */
    public Collection<DataSource> getSources(final Environment environment) {
        final Collection<DataSource> sources = new LinkedList<DataSource>();
        for (File file : this.getFiles(environment)) {
            sources.add(new FileDataSource(file));
        }
        return sources;
    }
}
