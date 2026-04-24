/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MojoExecutor} conversion of Properties into Xpp3Dom.
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MojoExecutorTest {

    /**
     * MojoExecutor can render a collection of Properties as nested XML
     * (regression for <a href="https://github.com/yegor256/qulice/issues/318">
     * issue #318</a>).
     */
    @Test
    void rendersCollectionOfPropertiesRecursively() {
        final Properties dep = new Properties();
        dep.put("groupId", "org.apache.xmlgraphics");
        dep.put("artifactId", "batik-ext");
        dep.put("version", "1.7");
        final Properties wrapper = new Properties();
        wrapper.put("dependency", dep);
        final List<Properties> deps = new LinkedList<>();
        deps.add(wrapper);
        final Properties config = new Properties();
        config.put("ignoredDependencies", deps);
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "Dependency details cannot be rendered as toString()",
            xpp.getChild("ignoredDependencies")
                .getChild("dependency")
                .getChild("groupId")
                .getValue(),
            Matchers.equalTo("org.apache.xmlgraphics")
        );
    }

    /**
     * MojoExecutor can render nested Properties having multiple keys
     * (without losing keys beyond the first one).
     */
    @Test
    void preservesAllKeysOfNestedPropertiesInsideCollection() {
        final Properties entry = new Properties();
        entry.put("groupId", "org.example");
        entry.put("artifactId", "sample");
        final List<Properties> items = new LinkedList<>();
        items.add(entry);
        final Properties config = new Properties();
        config.put("items", items);
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "All keys of nested Properties must appear as children",
            xpp.getChild("items").getChildCount(),
            Matchers.equalTo(2)
        );
    }

    /**
     * MojoExecutor can render a collection of strings as repeated elements.
     */
    @Test
    void rendersCollectionOfStrings() {
        final Properties config = new Properties();
        config.put("patterns", Arrays.asList("alpha", "beta"));
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "Collection elements cannot be merged into a single string",
            xpp.getChild("patterns").getChildCount(),
            Matchers.equalTo(2)
        );
    }

    /**
     * MojoExecutor can render a plain String value as a leaf node.
     */
    @Test
    void rendersStringValueAsLeaf() {
        final Properties config = new Properties();
        config.put("encoding", "UTF-8");
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "String value cannot be rendered as a leaf node",
            xpp.getChild("encoding").getValue(),
            Matchers.equalTo("UTF-8")
        );
    }

    /**
     * MojoExecutor can render a String array as repeated child nodes.
     */
    @Test
    void rendersStringArrayAsRepeatedChildren() {
        final Properties config = new Properties();
        config.put("excludes", new String[] {"first", "second", "third"});
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "String array entries cannot be rendered as separate children",
            xpp.getChild("excludes").getChildCount(),
            Matchers.equalTo(3)
        );
    }

    /**
     * MojoExecutor fails fast when the property value has an unsupported type.
     */
    @Test
    void failsOnUnsupportedValueType() {
        final Properties config = new Properties();
        config.put("amount", 42);
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new MojoExecutor(null, null)
                .toXppDom(config, "configuration"),
            "Unsupported value type must not be silently accepted"
        );
    }

    /**
     * MojoExecutor can render arbitrarily nested Properties within a value.
     */
    @Test
    void rendersArbitrarilyNestedProperties() {
        final Properties java = new Properties();
        java.put("version", "17");
        final Properties rules = new Properties();
        rules.put("requireJavaVersion", java);
        final Properties config = new Properties();
        config.put("rules", rules);
        final Xpp3Dom xpp = new MojoExecutor(null, null)
            .toXppDom(config, "configuration");
        MatcherAssert.assertThat(
            "Deeply nested Properties cannot be flattened",
            xpp.getChild("rules")
                .getChild("requireJavaVersion")
                .getChild("version")
                .getValue(),
            Matchers.equalTo("17")
        );
    }
}
