/**
 * Copyright (c) 2011, Qulice.com
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

import com.ymock.util.Logger;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Executor of plugins.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class MojoExecutor {

    /**
     * Plugin manager.
     */
    private MavenPluginManager manager;

    /**
     * Maven session.
     */
    private MavenSession session;

    /**
     * Public ctor.
     * @param mngr The manager
     * @param sesn Maven session
     */
    public MojoExecutor(final MavenPluginManager mngr,
        final MavenSession sesn) {
        this.manager = mngr;
        this.session = sesn;
    }

    /**
     * Find and configure a mojor.
     * @param coords Maven coordinates, e.g. "com.qulice:maven-qulice-plugin:1.0"
     * @param goal Maven plugin goal to execute
     * @param config The configuration to set
     * @throws MojoFailureException If something is wrong inside
     */
    public void execute(final String coords, final String goal,
        final Properties config) throws MojoFailureException {
        final Plugin plugin = new Plugin();
        final String[] sectors = StringUtils.split(coords, ':');
        plugin.setGroupId(sectors[0]);
        plugin.setArtifactId(sectors[1]);
        plugin.setVersion(sectors[2]);
        MojoDescriptor descriptor;
        try {
            descriptor = this.manager.getMojoDescriptor(
                plugin,
                goal,
                this.session.getTopLevelProject().getRemotePluginRepositories(),
                this.session.getRepositorySession()
            );
        } catch (org.apache.maven.plugin.MojoNotFoundException ex) {
            throw new IllegalStateException("Can't find MOJO", ex);
        } catch (org.apache.maven.plugin.PluginResolutionException ex) {
            throw new IllegalStateException("Can't resolve plugin", ex);
        } catch (org.apache.maven.plugin.PluginDescriptorParsingException ex) {
            throw new IllegalStateException("Can't parse descriptor", ex);
        } catch (org.apache.maven.plugin.InvalidPluginDescriptorException ex) {
            throw new IllegalStateException("Invalid plugin descriptor", ex);
        }
        try {
            this.manager.setupPluginRealm(
                descriptor.getPluginDescriptor(),
                this.session,
                this.getClass().getClassLoader(),
                new java.util.ArrayList<String>(),
                this.session.getTopLevelProject().getExtensionDependencyFilter()
            );
        } catch (org.apache.maven.plugin.PluginResolutionException ex) {
            throw new IllegalStateException("Plugin resolution problem", ex);
        } catch (org.apache.maven.plugin.PluginContainerException ex) {
            throw new IllegalStateException("Can't setup realm", ex);
        }
        final Xpp3Dom xpp = Xpp3Dom.mergeXpp3Dom(
            this.toXpp3Dom(config, "configuration"),
            this.toXpp3Dom(descriptor.getMojoConfiguration())
        );
        final MojoExecution execution = new MojoExecution(descriptor, xpp);
        Mojo mojo;
        try {
            mojo = this.manager
                .getConfiguredMojo(Mojo.class, this.session, execution);
        } catch (org.apache.maven.plugin.PluginConfigurationException ex) {
            throw new IllegalStateException("Can't configure MOJO", ex);
        } catch (org.apache.maven.plugin.PluginContainerException ex) {
            throw new IllegalStateException("Plugin container failure", ex);
        }
        Logger.info(
            this,
            "Calling %s:%s...",
            coords,
            goal
        );
        try {
            mojo.execute();
        } catch (org.apache.maven.plugin.MojoExecutionException ex) {
            throw new IllegalArgumentException(ex);
        }
        this.manager.releaseMojo(mojo, execution);
    }

    /**
     * Recuresively convert Properties to Xpp3Dom.
     * @param config The config to convert
     * @param name High-level name of it
     * @return The Xpp3Dom document
     * @see #execute(String,String,Properties)
     */
    private Xpp3Dom toXpp3Dom(final Properties config, final String name) {
        final Xpp3Dom xpp = new Xpp3Dom(name);
        for (Map.Entry entry : config.entrySet()) {
            if (entry.getValue() instanceof String) {
                final Xpp3Dom child = new Xpp3Dom((String) entry.getKey());
                child.setValue(config.getProperty((String) entry.getKey()));
                xpp.addChild(child);
            } else if (entry.getValue() instanceof String[]) {
                final Xpp3Dom child = new Xpp3Dom((String) entry.getKey());
                for (String val : (String[]) entry.getValue()) {
                    final Xpp3Dom row = new Xpp3Dom((String) entry.getKey());
                    row.setValue(val);
                    child.addChild(row);
                }
                xpp.addChild(child);
            } else if (entry.getValue() instanceof Collection) {
                final Xpp3Dom child = new Xpp3Dom((String) entry.getKey());
                for (String val : (Collection<String>) entry.getValue()) {
                    final Xpp3Dom row = new Xpp3Dom((String) entry.getKey());
                    row.setValue(val);
                    child.addChild(row);
                }
                xpp.addChild(child);
            } else if (entry.getValue() instanceof Properties) {
                xpp.addChild(
                    this.toXpp3Dom(
                        (Properties) entry.getValue(),
                        (String) entry.getKey()
                    )
                );
            } else {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid properties value at '%s'",
                        (String) entry.getKey()
                    )
                );
            }
        }
        return xpp;
    }

    /**
     * Recursively convert PLEXUS config to Xpp3Dom.
     * @param config The config to convert
     * @return The Xpp3Dom document
     * @see #execute(String,String,Properties)
     */
    private Xpp3Dom toXpp3Dom(final PlexusConfiguration config) {
        final Xpp3Dom result = new Xpp3Dom(config.getName());
        result.setValue(config.getValue(null));
        for (String name : config.getAttributeNames()) {
            result.setAttribute(name, config.getAttribute(name));
        }
        for (PlexusConfiguration child : config.getChildren()) {
            result.addChild(this.toXpp3Dom(child));
        }
        return result;
    }

}
