/**
 * Copyright (c) 2011-2015, Qulice.com
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

import com.jcabi.aspects.Tv;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
        final Collection<File> files = env.files("*.*");
        if (files.isEmpty()) {
            Logger.info(this, "No files to check with Checkstyle");
            return;
        }
        final Checker checker;
        checker = new Checker();
        checker.setClassLoader(env.classloader());
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        try {
            checker.configure(this.configuration(env));
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to configure checker", ex);
        }
        final CheckstyleListener listener = new CheckstyleListener(env);
        checker.addListener(listener);
        try {
            checker.process(new LinkedList<File>(files));
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to process files", ex);
        }
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
        final File cacheFile =
                new File(env.tempdir(), "checkstyle/checkstyle.cache");
        if (!cacheFile.getParentFile().mkdirs()) {
            throw new IllegalStateException(
                String.format(
                    "Unable to crate directories needed for %s",
                    cacheFile.getPath()
                )
            );
        }
        final Properties props = new Properties();
        props.setProperty(
            "cache.file",
            cacheFile.getPath()
        );
        props.setProperty("header", this.header(env));
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream("checks.xml")
        );
        final Configuration configuration;
        try {
            configuration = ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(props),
                true
            );
        } catch (final CheckstyleException ex) {
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
        final String content;
        try {
            content = IOUtils.toString(url.openStream())
                .trim().replaceAll("[\\r\\n]+$", "");
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed to read license", ex);
        }
        final StringBuilder builder = new StringBuilder(Tv.HUNDRED);
        final String eol = System.getProperty("line.separator");
        builder.append("/**").append(eol);
        for (final String line
            : StringUtils.splitPreserveAllTokens(content, eol)) {
            builder.append(" *");
            if (!line.trim().isEmpty()) {
                builder.append(' ').append(line.trim());
            }
            builder.append(eol);
        }
        builder.append(" */");
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
        final URL url;
        if (name.startsWith("file:")) {
            try {
                url = new URL(name);
            } catch (final MalformedURLException ex) {
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
}
