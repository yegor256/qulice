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

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.ymock.util.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Check for required svn properties in java files.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class SvnPropertiesCheck extends AbstractFileSetCheck {

    /**
     * Svn executable command.
     */
    public static final String SVN = "svn";

    /**
     * Svn "propget" command.
     */
    public static final String PROPGET = "propget";

    /**
     * List of required values.
     */
    private final Map<String, String> required = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        this.required.put("svn:keywords", "Id");
        this.required.put("svn:eol-style", "native");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processFiltered(final File file, final List<String> lines) {
        boolean failed = false;
        for (Map.Entry<String, String> entry : this.required.entrySet()) {
            final String value = this.read(file, entry.getKey());
            if (value == null || value.isEmpty()) {
                this.log(
                    0,
                    String.format(
                        "Svn property '%s' is not set",
                        entry.getKey()
                    )
                );
                failed = true;
            }
            if (!entry.getValue().equals(value)) {
                this.log(
                    0,
                    String.format(
                        "Wrong svn property: %s='%s', should be: '%s'",
                        entry.getKey(),
                        value,
                        entry.getValue()
                    )
                );
                failed = true;
            }
        }
        if (failed) {
            this.fireErrors(file.getPath());
        }
    }

    /**
     * Read SVN property on the file.
     * @param file The file
     * @param name SVN property name
     * @return The value of the property
     */
    private String read(final File file, final String name) {
        BufferedReader reader = null;
        String value = null;
        try {
            final ProcessBuilder builder = new ProcessBuilder(
                this.SVN,
                this.PROPGET,
                name,
                file.getPath()
            );
            builder.redirectErrorStream(true);
            final Process proc = builder.start();
            reader = new BufferedReader(
                new InputStreamReader(proc.getInputStream())
            );
            value = reader.readLine();
            if (value == null) {
                value = "";
            }
        } catch (java.io.IOException ex) {
            this.log(
                0,
                Logger.format("Failed to execute 'svn': %[exception]s", ex)
            );
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (java.io.IOException ex) {
                    this.log(
                        0,
                        Logger.format(
                            "Failed to close 'svn' stream: %[exception]s",
                            ex
                        )
                    );
                }
            }
        }
        return value;
    }

}
