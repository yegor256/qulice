/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
