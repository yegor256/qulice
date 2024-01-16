/*
 * Copyright (c) 2011-2024 Qulice.com

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
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

/**
 * Validates source code with PMD.
 *
 * @since 0.3
 */
public final class PmdValidator implements ResourceValidator {

    /**
     * Environment to use.
     */
    private final transient Environment env;

    /**
     * Constructor.
     * @param env Environment to use.
     */
    public PmdValidator(final Environment env) {
        this.env = env;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Collection<Violation> validate(final Collection<File> files) {
        final SourceValidator validator = new SourceValidator(this.env);
        final Collection<DataSource> sources = this.getNonExcludedFiles(files);
        final Collection<PmdError> errors = validator.validate(
            sources, this.env.basedir().getPath()
        );
        final Collection<Violation> violations = new LinkedList<>();
        for (final PmdError error : errors) {
            violations.add(
                new Violation.Default(
                    this.name(),
                    error.name(),
                    error.fileName(),
                    error.lines(),
                    error.description()
                )
            );
        }
        return violations;
    }

    @Override
    public String name() {
        return "PMD";
    }

    /**
     * Filters out excluded files from further validation.
     * @param files Files to validate
     * @return Relevant source files
     */
    public Collection<DataSource> getNonExcludedFiles(final Collection<File> files) {
        final Collection<DataSource> sources = new LinkedList<>();
        for (final File file : files) {
            final String name = file.getPath().substring(
                this.env.basedir().toString().length()
            );
            if (!this.env.exclude("pmd", name)) {
                sources.add(new FileDataSource(file));
            }
        }
        return sources;
    }
}
