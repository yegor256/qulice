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
package com.qulice.findbugs;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.ymock.util.Logger;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Validates source code and compiled binaris with FindBugs.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class FindBugsValidator implements Validator {

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (3 lines)
     */
    @Override
    public void validate(final Environment env) throws ValidationException {
        if (env.outdir().exists()) {
            this.check(this.findbugs(env));
        } else {
            Logger.info(
                this,
                "No classes at %s, no FindBugs validation",
                env.outdir()
            );
        }
    }

    /**
     * Start findbugs and return its output.
     * @param env Environment
     * @return Output of findbugs
     */
    private String findbugs(final Environment env) {
        final List<String> args = new LinkedList<String>();
        args.add("java");
        args.addAll(this.options(env));
        args.add(Wrap.class.getName());
        args.add(env.basedir().getPath());
        args.add(env.outdir().getPath());
        args.add(StringUtils.join(env.classpath(), ","));
        final String command = StringUtils.join(args, " ");
        Logger.debug(this, "#restart(): running \"%s\"", command);
        final ProcessBuilder builder = new ProcessBuilder(args);
        String report;
        try {
            final Process process = builder.start();
            if (process.waitFor() != 0) {
                Logger.warn(
                    this,
                    "Failed to execute FindBugs:\n%s",
                    command,
                    IOUtils.toString(process.getErrorStream())
                );
            }
            report = IOUtils.toString(process.getInputStream());
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        Logger.debug(this, "#restart(): returned:\n%s", report);
        return report;
    }

    /**
     * Java options.
     * @param env Environment
     * @return Options
     */
    private List<String> options(final Environment env) {
        final File jar = this.jar(Wrap.class);
        final List<String> opts = new LinkedList<String>();
        if (!jar.isDirectory()) {
            opts.add("-jar");
            opts.add(jar.getPath());
        }
        opts.add("-classpath");
        opts.add(
            StringUtils.join(
                CollectionUtils.union(
                    Arrays.asList(
                        new File[] {
                            jar,
                            this.jar(edu.umd.cs.findbugs.FindBugs2.class),
                        }
                    ),
                    env.classpath()
                ),
                System.getProperty("path.separator")
            )
        );
        return opts;
    }

    /**
     * Get address of our JAR or directory.
     * @param resource Name of resource
     * @return The file
     */
    private File jar(final Class resource) {
        final String name = resource.getName()
            .replace(".", System.getProperty("file.separator"));
        final URL res = this.getClass().getResource(
            String.format("/%s.class", name)
        );
        if (res == null) {
            throw new IllegalStateException(
                String.format(
                    "can't find JAR for %s",
                    name
                )
            );
        }
        final String path = res.getFile().replaceAll("\\!.*$", "");
        File file;
        if ("jar".equals(FilenameUtils.getExtension(path))) {
            file = new File(URI.create(path).getPath());
        } else {
            file = new File(path).getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile();
        }
        Logger.debug(this, "#jar(%s): found at %s", resource.getName(), file);
        return file;
    }

    /**
     * Check report for errors.
     * @param report The report
     * @throws ValidationException If it contains errors
     * @checkstyle RedundantThrows (3 lines)
     */
    private void check(final String report) throws ValidationException {
        int total = 0;
        for (String line : report.split("\n")) {
            if (line.matches("[a-zA-Z ]+: .*")) {
                Logger.warn(this, "FindBugs: %s", line);
                ++total;
            }
        }
        if (total > 0) {
            throw new ValidationException(
                "%d FindBugs violations (see log above)",
                total
            );
        }
    }

}
