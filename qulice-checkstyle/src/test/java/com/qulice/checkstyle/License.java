/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.Joined;

/**
 * Builder of {@code LICENSE.txt} content.
 * @since 0.4
 */
public final class License {

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
    private String pkg = "foo";

    /**
     * Directory for package-info.java.
     */
    private File directory;

    /**
     * Use this EOL.
     * @param txt What to use as end-of-line character
     * @return This object
     */
    public License withEol(final String txt) {
        this.eol = txt;
        return this;
    }

    /**
     * Use this text (lines).
     * @param lns The lines to use
     * @return This object
     */
    public License withLines(final String... lns) {
        this.lines = new String[lns.length];
        System.arraycopy(lns, 0, this.lines, 0, lns.length);
        return this;
    }

    /**
     * Use this package name.
     * @param name The name of package
     * @return This object
     */
    public License withPackage(final String name) {
        this.pkg = name;
        return this;
    }

    /**
     * Save package-info.java into this folder.
     * @param dir The folder to save to
     * @return This object
     */
    public License savePackageInfo(final File dir) {
        this.directory = dir;
        return this;
    }

    /**
     * Make a file.
     * @return The location of LICENSE.txt
     * @throws IOException If something wrong happens inside
     */
    public File file() throws IOException {
        final File license = File.createTempFile("LICENSE", ".txt");
        FileUtils.forceDeleteOnExit(license);
        FileUtils.writeStringToFile(
            license,
            new IoCheckedText(new Joined(this.eol, this.lines)).asString(),
            StandardCharsets.UTF_8
        );
        if (this.directory != null) {
            this.makePackageInfo(this.directory);
        }
        return license;
    }

    /**
     * Save package-info.java to the directory.
     * @param dir The directory
     * @throws IOException If something wrong happens inside
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    private void makePackageInfo(final File dir) throws IOException {
        final File info = new File(dir, "package-info.java");
        final StringBuilder body = new StringBuilder(100);
        body.append("/*").append(this.eol);
        for (final String line : this.lines) {
            body.append(" *");
            if (!line.isEmpty()) {
                body.append(' ').append(line);
            }
            body.append(this.eol);
        }
        body.append(" */").append(this.eol)
            .append("/**").append(this.eol)
            .append(" * Hm...").append(this.eol)
            .append(" */").append(this.eol)
            .append("package ").append(this.pkg)
            .append(';').append(this.eol);
        FileUtils.writeStringToFile(
            info,
            body.toString(),
            StandardCharsets.UTF_8
        );
    }

}
