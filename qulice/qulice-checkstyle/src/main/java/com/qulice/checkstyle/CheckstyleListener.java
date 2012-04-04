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
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.qulice.spi.Environment;
import com.ymock.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener of Checkstyle events.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (260 lines)
 */
final class CheckstyleListener implements AuditListener {

    /**
     * Environment.
     */
    private final transient Environment env;

    /**
     * Collection of events collected.
     */
    private final transient List<AuditEvent> all =
        new ArrayList<AuditEvent>();

    /**
     * Public ctor.
     * @param environ The environment
     */
    public CheckstyleListener(final Environment environ) {
        this.env = environ;
    }

    /**
     * Get all events.
     * @return List of events
     */
    public List<AuditEvent> events() {
        return this.all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void auditStarted(final AuditEvent event) {
        // intentionally empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void auditFinished(final AuditEvent event) {
        // intentionally empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileStarted(final AuditEvent event) {
        // intentionally empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileFinished(final AuditEvent event) {
        // intentionally empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addError(final AuditEvent event) {
        this.all.add(event);
        final String check = event.getSourceName();
        Logger.error(
            this,
            "%s[%d]: %s (%s)",
            event.getFileName().substring(
                this.env.basedir().toString().length()
            ),
            event.getLine(),
            event.getMessage(),
            check.substring(check.lastIndexOf('.') + 1)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addException(final AuditEvent event,
        final Throwable throwable) {
        final String check = event.getSourceName();
        Logger.error(
            this,
            "%s[%d]: %s (%s), %[exception]s",
            event.getFileName().substring(
                this.env.basedir().toString().length()
            ),
            event.getLine(),
            event.getMessage(),
            check.substring(check.lastIndexOf('.') + 1),
            throwable
        );
    }

}
