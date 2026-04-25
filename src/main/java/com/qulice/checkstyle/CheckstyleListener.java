/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.qulice.spi.Environment;
import com.qulice.spi.Relative;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Listener of Checkstyle events.
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
        final String path = new Relative(
            this.env.basedir(), new File(event.getFileName())
        ).path();
        if (!this.env.exclude("checkstyle", path)
            && !this.skipJavadocPackage(event, path)) {
            this.all.add(event);
        }
    }

    @Override
    public void addException(final AuditEvent event,
        final Throwable throwable) {
        final String check = event.getSourceName();
        Logger.error(
            this,
            "%s[%d]: %s (%s), %[exception]s",
            new Relative(
                this.env.basedir(), new File(event.getFileName())
            ).path(),
            event.getLine(),
            event.getMessage(),
            check.substring(check.lastIndexOf('.') + 1),
            throwable
        );
    }

    /**
     * Get all events.
     * @return List of events
     */
    List<AuditEvent> events() {
        return Collections.unmodifiableList(this.all);
    }

    /**
     * Suppress {@code JavadocPackage} on {@code src/test/java} files when the
     * parallel {@code src/main/java} package already declares
     * {@code package-info.java}. See <a
     * href="https://github.com/yegor256/qulice/issues/865">#865</a>.
     * @param event The audit event being reported
     * @param path Project-relative path of the file under audit
     * @return TRUE if the event should be discarded
     */
    private boolean skipJavadocPackage(final AuditEvent event, final String path) {
        final String marker = "src/test/java/";
        final String unix = path.replace(File.separatorChar, '/');
        final int idx = unix.indexOf(marker);
        boolean skip = false;
        if (event.getSourceName().endsWith(".JavadocPackageCheck") && idx >= 0) {
            final String tail = unix.substring(idx + marker.length());
            final int slash = tail.lastIndexOf('/');
            if (slash > 0) {
                final File main = new File(
                    this.env.basedir(),
                    String.format(
                        "%ssrc/main/java/%s/package-info.java",
                        unix.substring(0, idx),
                        tail.substring(0, slash)
                    )
                );
                skip = main.isFile();
            }
        }
        return skip;
    }
}
