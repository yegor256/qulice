/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MojoExecutor} conversion of Properties into Xpp3Dom.
 * @since 1.0
 */
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
        final Properties config = new Properties();
        config.put("ignoredDependencies", List.of(wrapper));
        MatcherAssert.assertThat(
            "Dependency details cannot be rendered as toString()",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("ignoredDependencies")
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
        final Properties config = new Properties();
        config.put("items", List.of(entry));
        MatcherAssert.assertThat(
            "All keys of nested Properties must appear as children",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("items").getChildCount(),
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
        MatcherAssert.assertThat(
            "Collection elements cannot be merged into a single string",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("patterns").getChildCount(),
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
        MatcherAssert.assertThat(
            "String value cannot be rendered as a leaf node",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("encoding").getValue(),
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
        MatcherAssert.assertThat(
            "String array entries cannot be rendered as separate children",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("excludes").getChildCount(),
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
        MatcherAssert.assertThat(
            "Deeply nested Properties cannot be flattened",
            new MojoExecutor(null, null)
                .toXppDom(config, "configuration")
                .getChild("rules")
                .getChild("requireJavaVersion")
                .getChild("version")
                .getValue(),
            Matchers.equalTo("17")
        );
    }

    /**
     * MojoExecutor can render the attribute of the root node of a Plexus
     * configuration (regression for the {@code getAttribute()} overload
     * that used to compile only with some orderings of the classpath).
     */
    @Test
    void rendersAttributeOfRootPlexusNode() {
        MatcherAssert.assertThat(
            "Attribute of the root node cannot be rendered",
            MojoExecutorTest.rendered().getAttribute("combine.children"),
            Matchers.equalTo("append")
        );
    }

    /**
     * MojoExecutor can render the attribute of a nested node of a Plexus
     * configuration.
     */
    @Test
    void rendersAttributeOfNestedPlexusNode() {
        MatcherAssert.assertThat(
            "Attribute of the child node cannot be rendered",
            MojoExecutorTest.rendered()
                .getChild("dependency")
                .getAttribute("implementation"),
            Matchers.equalTo("org.example.Dep")
        );
    }

    /**
     * MojoExecutor can render the value of a nested node of a Plexus
     * configuration.
     */
    @Test
    void rendersValueOfNestedPlexusNode() {
        MatcherAssert.assertThat(
            "Value of the child node cannot be rendered",
            MojoExecutorTest.rendered().getChild("dependency").getValue(),
            Matchers.equalTo("org.example:sample:1.0")
        );
    }

    private static Xpp3Dom rendered() {
        final Xpp3Dom child = new Xpp3Dom("dependency");
        child.setAttribute("implementation", "org.example.Dep");
        child.setValue("org.example:sample:1.0");
        final Xpp3Dom dom = new Xpp3Dom("configuration");
        dom.setAttribute("combine.children", "append");
        dom.addChild(child);
        return new MojoExecutor(null, null)
            .toXppDom(new XmlPlexusConfiguration(dom));
    }
}
