/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginContainerException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.reporting.exec.DefaultMavenPluginManagerHelper;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Executor of plugins.
 * @since 0.3
 */
public final class MojoExecutor {

    /**
     * Plugin manager.
     */
    private final MavenPluginManager manager;

    /**
     * Maven session.
     */
    private final MavenSession session;

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
     * Find and configure a mojo.
     * @param coords Maven coordinates,
     *  e.g. "com.qulice:maven-qulice-plugin:1.0"
     * @param goal Maven plugin goal to execute
     * @param config The configuration to set
     * @throws ValidationException If something is wrong inside
     */
    public void execute(final String coords, final String goal,
        final Properties config) throws ValidationException {
        final Plugin plugin = new Plugin();
        final String[] sectors = coords.split(":", -1);
        plugin.setGroupId(sectors[0]);
        plugin.setArtifactId(sectors[1]);
        plugin.setVersion(sectors[2]);
        final MojoDescriptor descriptor = this.descriptor(plugin, goal);
        try {
            new DefaultMavenPluginManagerHelper(this.manager).setupPluginRealm(
                descriptor.getPluginDescriptor(),
                this.session,
                Thread.currentThread().getContextClassLoader(),
                List.of(),
                List.of()
            );
        } catch (final PluginResolutionException ex) {
            throw new IllegalStateException("Plugin resolution problem", ex);
        } catch (final PluginContainerException ex) {
            throw new IllegalStateException("Can't setup realm", ex);
        }
        final MojoExecution execution = new MojoExecution(
            descriptor,
            Xpp3Dom.mergeXpp3Dom(
                this.toXppDom(config, "configuration"),
                this.toXppDom(descriptor.getMojoConfiguration())
            )
        );
        final Mojo mojo = this.mojo(execution);
        try {
            Logger.info(this, "Calling %s:%s...", coords, goal);
            mojo.execute();
        } catch (final MojoExecutionException ex) {
            throw new IllegalArgumentException(ex);
        } catch (final MojoFailureException ex) {
            throw new ValidationException(ex);
        } finally {
            this.manager.releaseMojo(mojo, execution);
        }
    }

    /**
     * Recursively convert Properties to Xpp3Dom.
     * @param config The config to convert
     * @param name High-level name of it
     * @return The Xpp3Dom document
     * @see #execute(String,String,Properties)
     */
    Xpp3Dom toXppDom(final Properties config, final String name) {
        final Xpp3Dom xpp = new Xpp3Dom(name);
        for (final Map.Entry<?, ?> entry : config.entrySet()) {
            xpp.addChild(
                this.toNode(entry.getKey().toString(), entry.getValue())
            );
        }
        return xpp;
    }

    /**
     * Recursively convert a Plexus configuration to Xpp3Dom.
     *
     * <p>Both {@code getValue()} and {@code getAttribute()} are read
     * through their two-argument, default-taking forms on purpose. Two
     * jars on the classpath of a Maven build ship
     * {@link PlexusConfiguration}: {@code plexus-container-default}, where
     * the one-argument forms are declared {@code throws
     * PlexusConfigurationException}, and {@code org.eclipse.sisu.plexus},
     * where they are not. Whichever of the two {@code javac} sees first
     * decides whether a {@code catch} of that exception compiles at all,
     * so calling the one-argument forms makes this class compile or fail
     * depending on the order of the classpath. The two-argument forms
     * throw nothing in either jar, and lose nothing here: the names come
     * from {@code getAttributeNames()}, so every attribute asked for is
     * present.</p>
     *
     * @param config The config to convert
     * @return The Xpp3Dom document
     */
    Xpp3Dom toXppDom(final PlexusConfiguration config) {
        final Xpp3Dom result = new Xpp3Dom(config.getName());
        result.setValue(config.getValue(null));
        for (final String name : config.getAttributeNames()) {
            result.setAttribute(name, config.getAttribute(name, null));
        }
        for (final PlexusConfiguration child : config.getChildren()) {
            result.addChild(this.toXppDom(child));
        }
        return result;
    }

    private MojoDescriptor descriptor(final Plugin plugin, final String goal) {
        try {
            return new DefaultMavenPluginManagerHelper(this.manager)
                .getPluginDescriptor(plugin, this.session)
                .getMojo(goal);
        } catch (final PluginResolutionException ex) {
            throw new IllegalStateException("Can't resolve plugin", ex);
        } catch (final PluginDescriptorParsingException ex) {
            throw new IllegalStateException("Can't parse descriptor", ex);
        } catch (final InvalidPluginDescriptorException ex) {
            throw new IllegalStateException("Invalid plugin descriptor", ex);
        }
    }

    private Mojo mojo(final MojoExecution execution) {
        final Mojo mojo;
        try {
            mojo = this.manager.getConfiguredMojo(
                Mojo.class, this.session, execution
            );
        } catch (final PluginConfigurationException ex) {
            throw new IllegalStateException("Can't configure MOJO", ex);
        } catch (final PluginContainerException ex) {
            throw new IllegalStateException("Plugin container failure", ex);
        }
        return mojo;
    }

    private Xpp3Dom toNode(final String name, final Object value) {
        final Xpp3Dom node;
        if (value instanceof String) {
            node = new Xpp3Dom(name);
            node.setValue(String.class.cast(value));
        } else if (value instanceof String[]) {
            node = new Xpp3Dom(name);
            for (final String val : String[].class.cast(value)) {
                final Xpp3Dom row = new Xpp3Dom(name);
                row.setValue(val);
                node.addChild(row);
            }
        } else if (value instanceof Collection) {
            node = new Xpp3Dom(name);
            for (final Object item : Collection.class.cast(value)) {
                this.appendItem(node, name, item);
            }
        } else if (value instanceof Properties) {
            node = this.toXppDom(Properties.class.cast(value), name);
        } else {
            throw new IllegalArgumentException(
                String.format("Invalid properties value at '%s'", name)
            );
        }
        return node;
    }

    private void appendItem(
        final Xpp3Dom parent, final String name, final Object item
    ) {
        if (item instanceof Properties) {
            final Xpp3Dom converted = this.toXppDom(
                Properties.class.cast(item), name
            );
            final int count = converted.getChildCount();
            for (int idx = 0; idx < count; idx += 1) {
                parent.addChild(converted.getChild(idx));
            }
        } else if (item != null) {
            parent.addChild(this.toNode(name, item));
        }
    }
}
