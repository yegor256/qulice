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
package com.qulice.ant;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/**
 * Environment, passed from ant task to validators.
 * @author Yuriy Alevohin (alevohin@mail.ru)
 * @version $Id$
 */
public class AntEnvironment implements Environment {

    /**
     * Ant project.
     */
    private transient final Project project;

    /**
     * Sources dirs.
     */
    private transient final Path srcdir;

    /**
     * Classes dir (only one dir is supported)
     */
    private transient final java.io.File classesdir;

    /**
     * Classpath dirs and files.
     */
    private transient final Path classpath;

    /**
     * Public constructor.
     * @param project - ant project
     * @param srcdir  - sources dirs
     * @param classesdir - classes dir
     * @param classpath - classpath
     */
    public AntEnvironment(
        Project project,
        Path srcdir,
        java.io.File classesdir,
        Path classpath) {
        this.project = project;
        this.srcdir = srcdir;
        this.classesdir = classesdir;
        this.classpath = classpath;
    }

    @Override
    public File basedir() {
        return project.getBaseDir();
    }

    @Override
    public File tempdir() {
        return new File(this.basedir(), "temp");
    }

    @Override
    public File outdir() {
        return classesdir;
    }

    @Override
    public String param(String name, String value) {
        final String property = project.getProperty(name);
        if (property == null) {
            return value;
        } else {
            return property;
        }
    }

    @Override
    public ClassLoader classloader() {
        final List<URL> urls = new LinkedList<URL>();
        for (final String path : this.classpath()) {
            try {
                urls.add(
                    new File(path).toURI().toURL()
                );
            } catch (final MalformedURLException ex) {
                throw new IllegalStateException("Failed to build URL", ex);
            }
        }
        try {
            urls.add(classesdir.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Failed to build URL", ex);
        }
        final URLClassLoader loader = new URLClassLoader(
            urls.toArray(new URL[urls.size()]),
            Thread.currentThread().getContextClassLoader()
        );
        for (final URL url : loader.getURLs()) {
            Logger.debug(this, "Classpath: %s", url);
        }
        return loader;
    }

    @Override
    public Collection<String> classpath() {
        return Arrays.asList(classpath.list());
    }

    @Override
    public Collection<File> files(String pattern) {
        final Collection<File> files = new LinkedList<File>();
        final IOFileFilter filter = new WildcardFileFilter(pattern);
        for (final String dir : srcdir.list()) {
            final File sources = new File(dir);
            if (sources.exists() && sources.isDirectory()) {
                files.addAll(
                    FileUtils.listFiles(
                        sources,
                        filter,
                        DirectoryFileFilter.INSTANCE
                    )
                );
            }
        }
        return files;
    }

    @Override
    public boolean exclude(String check, String name) {
        // @todo 337. Implement exclude and excludes for ant QuliceTask
        return false;
    }

    @Override
    public Collection<String> excludes(String checker) {
        // @todo 337. Implement exclude and excludes for ant QuliceTask
        return Collections.emptyList();
    }
}
