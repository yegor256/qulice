/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Names of the checks that {@code checks.xml} enables.
 *
 * <p>Every {@code @checkstyle} suppression comment names its target with
 * a regular expression that Checkstyle matches against the fully
 * qualified class name of the check. A name matching none of the enabled
 * checks suppresses nothing at all, which is what
 * {@link UnknownSuppressionCheck} reports. Both the short name of a
 * module and its name with the {@code Check} suffix count as enabled,
 * since either of them matches the class name of the check.</p>
 *
 * @since 1.0
 */
final class ConfiguredChecks {

    /**
     * Names of the enabled checks, read from {@code checks.xml} once.
     */
    private final Unchecked<Collection<String>> names = new Unchecked<>(
        new Sticky<>(ConfiguredChecks::load)
    );

    /**
     * Is any of the enabled checks named by this suppression?
     *
     * <p>The filters capture the name as {@code \w+}, which holds no
     * regular expression metacharacter, so their pattern matching comes
     * down to a search for the name inside the name of a check.</p>
     *
     * @param name Name from a {@code @checkstyle} suppression comment
     * @return True if the name matches at least one enabled check
     */
    boolean covers(final String name) {
        return this.names.value().stream().anyMatch(known -> known.contains(name));
    }

    private static Collection<String> load() throws Exception {
        final Properties props = new Properties();
        props.setProperty("cache.file", "");
        return ConfiguredChecks.modules(
            ConfigurationLoader.loadConfiguration(
                ConfiguredChecks.class.getResource("checks.xml").toString(),
                new PropertiesExpander(props),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            )
        );
    }

    private static Collection<String> modules(final Configuration config) {
        final Collection<String> found = new HashSet<>(0);
        final String name = config.getName();
        final String simple = name.substring(name.lastIndexOf('.') + 1);
        found.add(simple);
        if (!simple.endsWith("Check")) {
            found.add(String.format("%sCheck", simple));
        }
        for (final Configuration child : config.getChildren()) {
            found.addAll(ConfiguredChecks.modules(child));
        }
        return found;
    }
}
