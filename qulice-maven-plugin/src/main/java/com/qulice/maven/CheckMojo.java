/**
 * Copyright (c) 2011-2018, Qulice.com
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
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Check the project and find all possible violations.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.TEST)
public final class CheckMojo extends AbstractQuliceMojo {

    /**
     * Provider of validators.
     */
    private ValidatorsProvider provider =
        new DefaultValidatorsProvider(this.env());

    @Override
    public void doExecute() throws MojoFailureException {
        try {
            this.run();
        } catch (final ValidationException ex) {
            Logger.info(
                this,
                "Read our quality policy: http://www.qulice.com/quality.html"
            );
            throw new MojoFailureException("Failure", ex);
        }
    }

    /**
     * Set provider of validators.
     * @param prov The provider
     */
    public void setValidatorsProvider(final ValidatorsProvider prov) {
        this.provider = prov;
    }

    /**
     * Run them all.
     * @throws ValidationException If any of them fail
     */
    private void run() throws ValidationException {
        final Collection<Violation> results = new LinkedList<>();
        final Collection<File> files = this.env().files("*.*");
        if (!files.isEmpty()) {
            final Collection<ResourceValidator> validators =
                this.provider.externalResource();
            for (final ResourceValidator validator : validators) {
                results.addAll(validator.validate(files));
            }
            for (final Violation result : results) {
                Logger.info(
                    this,
                    "%s: %s[%s]: %s (%s)",
                    result.validator(),
                    StringUtils.removeStart(
                        result.file(),
                        String.format(
                            "%s/", this.session().getExecutionRootDirectory()
                            )
                        ),
                        result.lines(),
                        result.message(),
                        result.name()
                );
            }
        }
        if (!results.isEmpty()) {
            throw new ValidationException(
                String.format("There are %d violations", results.size())
           );
        }
        for (final Validator validator : this.provider.external()) {
            Logger.info(this, "Starting %s validator", validator.name());
            validator.validate(this.env());
            Logger.info(this, "Finishing %s validator", validator.name());
        }
        for (final MavenValidator validator : this.provider.internal()) {
            validator.validate(this.env());
        }
    }

}
