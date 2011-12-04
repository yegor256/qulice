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

import com.qulice.spi.Environment;
import com.qulice.spi.EnvironmentMocker;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Builder of {@code LICENSE.txt} content.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class LicenseMocker {

    /**
     * The text.
     */
    private String[] lines;

    /**
     * EOL.
     */
    private String eol;

    /**
     * Package name.
     */
    private String pkgName = "foo";

    /**
     * Directory for package-info.java.
     */
    private File packageInfoDir;

    /**
     * Use this EOL.
     * @param txt What to use as end-of-line character
     * @return This object
     */
    public LicenseMocker withEol(final String txt) {
        this.eol = txt;
        return this;
    }

    /**
     * Use this text (lines).
     * @param lns The lines to use
     * @return This object
     */
    public LicenseMocker withLines(final String[] lns) {
        this.lines = lns;
        return this;
    }

    /**
     * Save package-info.java into this folder.
     * @param dir The folder to save to
     * @return This object
     */
    public LicenseMocker savePackageInfo(final File dir) {
        this.packageInfoDir = dir;
        return this;
    }

    /**
     * Mock it.
     * @return The location of LICENSE.txt
     * @throws Exception If something wrong happens inside
     */
    public File mock() throws Exception {
        final File license = File.createTempFile("LICENSE", ".txt");
        FileUtils.forceDeleteOnExit(license);
        FileUtils.writeStringToFile(
            license,
            StringUtils.join(this.lines, this.eol)
        );
        if (this.packageInfoDir != null) {
            this.makePackageInfo(this.packageInfoDir);
        }
        return license;
    }

    /**
     * Save package-info.java to the directory.
     * @param dir The directory
     * @throws Exception If something wrong happens inside
     */
    private void makePackageInfo(final File dir) throws Exception {
        final File info = new File(dir, "package-info.java");
        final StringBuilder body = new StringBuilder();
        body.append("/**").append(this.eol);
        for (String line : this.lines) {
            body.append(" *");
            if (!line.isEmpty()) {
                body.append(" ").append(line);
            }
            body.append(this.eol);
        }
        body.append(" */").append(this.eol)
            .append("/**").append(this.eol)
            .append(" * Hm...").append(this.eol)
            .append(" * @version $Id $").append(this.eol)
            .append(" * @author John Doe (j@qulice.com)").append(this.eol)
            .append(" */").append(this.eol)
            .append("package ").append(this.pkgName)
            .append(";").append(this.eol);
        FileUtils.writeStringToFile(info, body.toString());
        System.out.println(body.toString());
    }

}
