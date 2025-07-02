/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.qulice.spi.Environment;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Listener of Checkstyle events.
 *
 * @since 0.3
 * @checkstyle ClassDataAbstractionCoupling (260 lines)
 */
final class CheckstyleListener implements AuditListener {

    /**
     * Environment.
     */
    private final Environment env;

    /**
     * Collection of events collected.
     */
    private final List<AuditEvent> all;

    /**
     * Public ctor.
     * @param environ The environment
     */
    CheckstyleListener(final Environment environ) {
        this.all = new LinkedList<>();
        this.env = environ;
    }

    /**
     * Get all events.
     * @return List of events
     */
    public List<AuditEvent> events() {
        return Collections.unmodifiableList(this.all);
    }

    @Override
    public void auditStarted(final AuditEvent event) {
        // intentionally empty
    }

    @Override
    public void auditFinished(final AuditEvent event) {
        // intentionally empty
    }

    @Override
    public void fileStarted(final AuditEvent event) {
        // intentionally empty
    }

    @Override
    public void fileFinished(final AuditEvent event) {
        // intentionally empty
    }

    @Override
    public void addError(final AuditEvent event) {
        final Path basePath = Paths.get(this.env.basedir().toURI());
        final Path filePath = Paths.get(event.getFileName());
        final String name;
        if (filePath.isAbsolute()) {
            name = basePath.relativize(filePath).toString();
        } else {
            name = filePath.toString();
        }

        if (!this.env.exclude("checkstyle", name)) {
            this.all.add(event);
        }
    }

    @Override
    public void addException(final AuditEvent event,
        final Throwable throwable) {
        final String check = event.getSourceName();
        final Path basePath = Paths.get(this.env.basedir().toURI());
        final Path filePath = Paths.get(event.getFileName());
        final String relativeFileName;
        if (filePath.isAbsolute()) {
            relativeFileName = basePath.relativize(filePath).toString();
        } else {
            relativeFileName = filePath.toString(); 
        }
        Logger.error(
            this,
            "%s[%d]: %s (%s), %[exception]s",
            relativeFileName,
            event.getLine(),
            event.getMessage(),
            check.substring(check.lastIndexOf('.') + 1),
            throwable
        );
    }

}
