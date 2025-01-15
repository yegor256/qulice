/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
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

import java.io.Writer;
import java.util.List;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Renderer implementation which keeps track of all pmd-generated report.
 *
 * @since 1.0
 */
@SuppressWarnings("deprecation")
final class PmdRenderer extends AbstractPropertySource implements Renderer {
    /**
     * This variable is union of all observed reports.
     */
    private final Report accumulator = new Report();

    @Override
    public String getName() {
        return "qulice";
    }

    @Override
    public void setName(final String name) {
        throw new UnsupportedOperationException("Unimplemented method 'setName'");
    }

    @Override
    public String getDescription() {
        return "not implemented yet";
    }

    @Override
    public String defaultFileExtension() {
        throw new UnsupportedOperationException("Unimplemented defaultFileExtension");
    }

    @Override
    public void setDescription(final String description) {
        throw new UnsupportedOperationException("Unimplemented setDescription");
    }

    @Override
    public boolean isShowSuppressedViolations() {
        throw new UnsupportedOperationException("Unimplemented isShowSuppressedViolations");
    }

    @Override
    public void setShowSuppressedViolations(final boolean show) {
        throw new UnsupportedOperationException("Unimplemented setShowSuppressedViolations");
    }

    @Override
    public void setUseShortNames(final List<String> list) {
        // ignore it
    }

    @Override
    public Writer getWriter() {
        throw new UnsupportedOperationException("Unimplemented getWriter");
    }

    @Override
    public void setWriter(final Writer writer) {
        throw new UnsupportedOperationException("Unimplemented setWriter");
    }

    @Override
    public void start() {
        // ignore it
    }

    @Override
    public void startFileAnalysis(final DataSource source) {
        // ignore it
    }

    @Override
    public void renderFileReport(final Report report) {
        this.accumulator.merge(report);
    }

    @Override
    public void end() {
        // ignore it
    }

    @Override
    public void flush() {
        // ignore it
    }

    @Override
    public void setReportFile(final String report) {
        // ignore it
    }

    @Override
    public String getPropertySourceType() {
        throw new UnsupportedOperationException("Unimplemented method 'getPropertySourceType'");
    }

    /**
     * Merges all collected errors into the provided target.
     * @param target A Report instance which is updated
     */
    void exportTo(final Report target) {
        target.merge(this.accumulator);
    }
}
