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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of exclude model for DuplicateFinder plugin.
 *
 * @since 0.23.1
 */
public final class DuplicateFinderExclude implements Exclude<Properties, Properties> {
    /**
     * Pattern for parsing ignore list.
     */
    private final Pattern pattern;

    /**
     * Configuration string.
     *  - duplicatefinder:about.html
     *  - duplicatefinder:org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5
     *  - duplicatefinder:org.codehaus.groovy.ast.expr.RegexExpression
     *  - duplicatefinder:org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5
     *  |xml-apis:xml-apis:1.0.0|about.html
     *  - duplicatefinder:org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5
     *  |xml-apis:xml-apis:1.0.0|org.w3c.dom.UserDataHandler
     *  See https://github.com/tpc2/qulice/issues/152#issuecomment-39028953
     *  and https://github.com/teamed/qulice/issues/250 for details
     */
    private final String excludes;

    /**
     * Ctor.
     * @param predicate Predicate for plugin.
     * @param excl String with exclude from config.
     */
    public DuplicateFinderExclude(final String predicate, final String excl) {
        this.pattern = Pattern.compile(
            String.format(
                "(%s:)|(%s)|(?<res>[\\w-\\.&&[^\\|]]+)",
                predicate,
                "(?<gr>[\\w\\.-]+):(?<art>[\\w\\.-]+):(?<ver>[\\w\\.-]+)"
            )
        );
        this.excludes = excl;
    }

    @Override
    public List<Properties> dependencies() {
        final Matcher matcher = this.pattern.matcher(this.excludes);
        final List<Properties> deps = new LinkedList<>();
        while (matcher.find()) {
            final String group = matcher.group("gr");
            final String artifact = matcher.group("art");
            final String version = matcher.group("ver");
            if (!empty(group) && !empty(artifact) && !empty(version)) {
                final Properties main = new Properties();
                final Properties prop = new Properties();
                prop.put("groupId", group);
                prop.put("artifactId", artifact);
                prop.put("version", version);
                main.put("dependency", prop);
                deps.add(main);
            }
        }
        return deps;
    }

    @Override
    public List<Properties> resources() {
        final Matcher matcher = this.pattern.matcher(this.excludes);
        final List<Properties> res = new LinkedList<>();
        while (matcher.find()) {
            final String resource = matcher.group("res");
            if (!empty(resource)) {
                final Properties prop = new Properties();
                prop.put("ignoredResourcePattern", resource);
                res.add(prop);
            }
        }
        return res;
    }

    /**
     * Check string.
     *
     * @param value String for check.
     * @return True for not empty string.
     */
    private static boolean empty(final String value) {
        return value == null || value.isBlank();
    }
}
