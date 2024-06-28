/*
 * Copyright (c) 2011-2024 Qulice.com
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
package com.qulice.maven.transformer;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Convert PLEXUS config to Xpp3Dom.
 *
 * @since 0.23.1
 */
public final class PlexusConfigurationToXpp3Dom implements TransformToXpp3Dom {
    /**
     * PLEXUS configuration.
     */
    private final PlexusConfiguration config;

    /**
     * Ctor.
     * @param config The config to convert.
     */
    public PlexusConfigurationToXpp3Dom(final PlexusConfiguration config) {
        this.config = config;
    }

    @Override
    public Xpp3Dom transform() {
        return this.toXppDom(this.config);
    }

    /**
     * Recursively convert PLEXUS config to Xpp3Dom.
     * @param conf The config to convert.
     * @return The Xpp3Dom document.
     */
    private Xpp3Dom toXppDom(final PlexusConfiguration conf) {
        final Xpp3Dom document = new Xpp3Dom(conf.getName());
        document.setValue(conf.getValue(null));
        for (final String name : conf.getAttributeNames()) {
            try {
                document.setAttribute(name, conf.getAttribute(name));
            } catch (final PlexusConfigurationException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        for (final PlexusConfiguration child : conf.getChildren()) {
            document.addChild(this.toXppDom(child));
        }
        return document;
    }
}
