/**
 * Copyright (c) 2011-2013, Qulice.com
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

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;

/**
 * Validator with Checkstyle.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (260 lines)
 */
public final class CheckstyleValidator implements Validator {

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (3 lines)
     */
    @Override
    public void validate(final Environment env) throws ValidationException {
        final List<File> files = this.files(env);
        if (files.isEmpty()) {
            Logger.info(this, "No files to check with Checkstyle");
            return;
        }
        Checker checker;
        try {
            checker = new Checker();
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to create checker", ex);
        }
        checker.setClassloader(env.classloader());
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        try {
            checker.configure(this.configuration(env));
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to configure checker", ex);
        }
        final CheckstyleListener listener = new CheckstyleListener(env);
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
        final List<AuditEvent> events = listener.events();
        if (!events.isEmpty()) {
            throw new ValidationException(
                "%d Checkstyle violations (see log above)",
                events.size()
            );
        }
        Logger.info(
            this,
            "No Checkstyle violations found in %d files",
            files.size()
        );
    }

    /**
     * Load checkstyle configuration.
     * @param env The environemt
     * @return The configuration just loaded
     * @see #validate()
     */
    private Configuration configuration(final Environment env) {
        final Properties props = new Properties();
        props.setProperty(
            "cache.file",
            new File(env.tempdir(), "checkstyle/checkstyle.cache").getPath()
        );
        props.setProperty("header", this.header(env));
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream("checks.xml")
        );
        Configuration configuration;
        try {
            configuration = ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(props),
                true
            );
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        return configuration;
    }

    /**
     * Create header content, from file.
     * @param env The environment
     * @return The content of header
     * @see #configuration()
     */
    private String header(final Environment env) {
        final String name = env.param("license", "LICENSE.txt");
        final URL url = this.toURL(env, name);
        String content;
        try {
            content = IOUtils.toString(url.openStream()).replaceAll("\\r", "");
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("Failed to read license", ex);
        }
        final StringBuilder builder = new StringBuilder();
        final String eol = System.getProperty("line.separator");
        builder.append("/**").append(eol);
        for (String line : StringUtils.splitPreserveAllTokens(content, eol)) {
            builder.append(" *");
            if (!line.isEmpty()) {
                builder.append(" ").append(line);
            }
            builder.append(eol);
        }
        builder.append(" */").append(eol);
        final String license = builder.toString();
        Logger.info(this, "LICENSE found: %s", url);
        Logger.debug(
            this,
            "LICENSE full text after parsing:\n%s",
            license
        );
        return license;
    }

    /**
     * Convert file name to URL.
     * @param env The environment
     * @param name The name of file
     * @return The URL
     * @see #header(Environment)
     */
    private URL toURL(final Environment env, final String name) {
        URL url;
        if (name.startsWith("file:")) {
            try {
                url = new URL(name);
            } catch (java.net.MalformedURLException ex) {
                throw new IllegalStateException("Invalid URL", ex);
            }
        } else {
            url = env.classloader().getResource(name);
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "'%s' resource is not found in classpath",
                        name
                    )
                );
            }
        }
        return url;
    }

    /**
     * Get full list of files to process.
     * @param env The environmet
     * @return List of files
     * @todo #44:1h Perform refactoring: remove this method, use
     *  Environment.files() instead.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<File> files(final Environment env) {
        final List<File> files = new ArrayList<File>();
        final IOFileFilter filter = new WildcardFileFilter("*.java");
        final String[] dirs = new String[] {
            "src/main/java",
            "src/test/java",
            "src/mock/java",
        };
        for (String dir : dirs) {
            final File sources = new File(env.basedir(), dir);
            if (sources.exists()) {
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

}
