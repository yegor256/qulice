package com.qulice.ant;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.CollectionUtils;

/**
 * Created with IntelliJ IDEA.
 * User: alevohins
 * Date: 03.01.15
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */

public class AntEnvironment implements Environment {

    private transient final Project project;
    private transient final Path sourcepath;
    private transient final Path classpath;

    public AntEnvironment(
        Project project,
        Path sourcepath,
        Path classpath) {
        this.project = project;
        this.sourcepath = sourcepath;
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
        return new File(this.basedir(), "classes");
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
        final URLClassLoader loader = new URLClassLoader(
            urls.toArray(new URL[urls.size()]),
//            Thread.currentThread().getContextClassLoader()
            project.getCoreLoader() == null ?
                Thread.currentThread().getContextClassLoader() :
                project.getCoreLoader()
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
        for (final String dir : sourcepath.list()) {
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
