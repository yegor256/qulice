/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

/**
 * Check pom.xml with XPath validation queries.
 *
 * <p>Restrictions:
 *
 * <ol>
 * <li>Each xpath component should contains namespace prefix pom:</li>
 * <li>Each xpath query should end with /text()</li>
 * </ol>
 *
 * @since 0.6
 */
public final class PomXpathValidator implements MavenValidator {

    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        PomXpathValidator.validate(PomXpathValidator.pom(env), env.asserts());
    }

    /**
     * Validate pom against xpath queries.
     * @param pom POM
     * @param xpaths Xpath queries
     * @throws ValidationException validation exception
     */
    private static void validate(final XML pom, final Iterable<String> xpaths)
        throws ValidationException {
        for (final String xpath : xpaths) {
            if (pom.xpath(xpath).isEmpty()) {
                throw new ValidationException(
                    "pom.xml don't match the xpath query [%s]", xpath
                );
            }
        }
    }

    /**
     * Parse pom.xml.
     *
     * @param env Environment
     * @return XML instance
     */
    private static XML pom(final Environment env) {
        try {
            return new XMLDocument(
                FileUtils.readFileToString(
                    new File(
                        String.format(
                            "%s%s%s",
                            env.basedir(),
                            File.separator,
                            "pom.xml"
                        )
                    ),
                    StandardCharsets.UTF_8
                )
            ).registerNs("pom", "http://maven.apache.org/POM/4.0.0");
        } catch (final IOException exc) {
            throw new IllegalArgumentException(exc);
        }
    }
}
