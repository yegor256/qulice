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

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Convert properties config to Xpp3Dom.
 *
 * @since 0.23.1
 */
public final class PropertiesToXpp3Dom implements TransformToXpp3Dom {
    /**
     * Configuration properties.
     */
    private final Properties config;

    /**
     * The name of the parent configuration element.
     */
    private final Xpp3Dom parent;

    /**
     * Ctor.
     * @param conf Configuration properties.
     * @param cfgname The name of the parent configuration element.
     */
    public PropertiesToXpp3Dom(final Properties conf, final String cfgname) {
        this(conf, new Xpp3Dom(cfgname));
    }

    /**
     * Ctor.
     * @param conf Configuration properties.
     * @param parnt Parent element.
     */
    public PropertiesToXpp3Dom(final Properties conf, final Xpp3Dom parnt) {
        this.config = conf;
        this.parent = parnt;
    }

    @Override
    public Xpp3Dom transform() {
        return PropertiesToXpp3Dom.toXppDom(this.config, this.parent);
    }

    /**
     * Recuresively convert Properties to Xpp3Dom.
     * @param config The config to convert
     * @param parent Parent Xpp3Dom element.
     * @return The Xpp3Dom document
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Xpp3Dom toXppDom(final Properties config, final Xpp3Dom parent) {
        for (final Map.Entry<?, ?> entry : config.entrySet()) {
            if (entry.getValue() instanceof String) {
                final Xpp3Dom child = new Xpp3Dom(entry.getKey().toString());
                child.setValue(config.getProperty(entry.getKey().toString()));
                parent.addChild(child);
            } else if (entry.getValue() instanceof String[]) {
                stringArrayToXppDom(parent, entry);
            } else if (entry.getValue() instanceof Collection) {
                collectionToXppDom(parent, entry);
            } else if (entry.getValue() instanceof Properties) {
                parent.addChild(
                    PropertiesToXpp3Dom.toXppDom(
                        Properties.class.cast(entry.getValue()),
                        new Xpp3Dom(entry.getKey().toString())
                    )
                );
            } else {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid properties value at '%s'",
                        entry.getKey().toString()
                    )
                );
            }
        }
        return parent;
    }

    /**
     * Converts the array of strings to Xpp3Dom.
     * @param parent Parent Xpp3Dom element.
     * @param cfgitem Current configuration item.
     */
    private static void stringArrayToXppDom(final Xpp3Dom parent, final Map.Entry<?, ?> cfgitem) {
        final Xpp3Dom child = new Xpp3Dom(cfgitem.getKey().toString());
        for (final String val : String[].class.cast(cfgitem.getValue())) {
            final Xpp3Dom row = new Xpp3Dom(cfgitem.getKey().toString());
            row.setValue(val);
            child.addChild(row);
        }
        parent.addChild(child);
    }

    /**
     * Converts the collection to Xpp3Dom.
     * @param parent Parent Xpp3Dom element.
     * @param cfgitem Current configuration item.
     */
    private static void collectionToXppDom(final Xpp3Dom parent, final Map.Entry<?, ?> cfgitem) {
        final Xpp3Dom child = new Xpp3Dom(cfgitem.getKey().toString());
        for (final Object val : Collection.class.cast(cfgitem.getValue())) {
            if (val instanceof Properties) {
                PropertiesToXpp3Dom.toXppDom(Properties.class.cast(val), child);
            } else {
                if (val != null) {
                    final Xpp3Dom row = new Xpp3Dom(cfgitem.getKey().toString());
                    row.setValue(val.toString());
                    child.addChild(row);
                }
            }
        }
        parent.addChild(child);
    }
}
