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
package com.qulice.maven;

import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Plugin;

/**
 * Check that the project has not SNAPSHOT dependencies if its own
 * status is stable.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class SnapshotsValidator implements MavenValidator {

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        final String version = env.project().getVersion();
        if (!this.isSnapshot(version)) {
            int errors = 0;
            for (Extension ext : env.project().getBuildExtensions()) {
                if (this.isSnapshot(ext.getVersion())) {
                    Logger.warn(
                        this,
                        "%s build extension is SNAPSHOT",
                        ext
                    );
                    ++errors;
                }
            }
            for (Plugin plugin : env.project().getBuildPlugins()) {
                if (this.isSnapshot(plugin.getVersion())) {
                    Logger.warn(
                        this,
                        "%s build plugin is SNAPSHOT",
                        plugin
                    );
                    ++errors;
                }
            }
            for (Dependency dep : env.project().getDependencies()) {
                if (this.isSnapshot(dep.getVersion())) {
                    Logger.warn(
                        this,
                        "%s dependency is SNAPSHOT",
                        dep
                    );
                    ++errors;
                }
            }
            if (errors > 0) {
                Logger.warn(
                    this,
                    // @checkstyle LineLength (1 line)
                    "The version of the project is not SNAPSHOT; there shouldn't not be any SNAPSHOT dependencies (%d found)",
                    errors
                );
                throw new ValidationException(
                    "%d dependencies are in SNAPSHOT state",
                    errors
                );
            }
        }
    }

    /**
     * Whether this version is a snapshot?
     * @param version The version
     * @return TRUE if yes
     */
    private boolean isSnapshot(final String version) {
        return version.endsWith("-SNAPSHOT");
    }

}
