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

import java.util.List;
import java.util.Properties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for DuplicateFinderExclude class.
 *
 * @since 0.23.1
 */
final class DuplicateFinderExcludeTest {
    /**
     * Dependency constant.
     */
    private static final String DEPENDENCY = "dependency";

    /**
     * GroupId constant.
     */
    private static final String GROUPID = "groupId";

    /**
     * ArtifactId constant.
     */
    private static final String ARTIFACTID = "artifactId";

    /**
     * Version constant.
     */
    private static final String VERSION = "version";

    /**
     * IgnoredResourcePattern constant.
     */
    private static final String IGNORED_RES = "ignoredResourcePattern";

    /**
     * Class under test.
     */
    private final Exclude<Properties, Properties> exclude =
        new DuplicateFinderExclude(
            "duplicate",
            String.format(
                "%s%s",
                "duplicate:org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5| about.html |",
                "xml-apis:xml-apis:1.0.0| org.w3c.dom.UserDataHandler"
            )
        );

    @Test
    void findDependency() {
        final List<Properties> deps = this.exclude.dependencies();
        MatcherAssert.assertThat(
            "2 should be returned",
            deps.size(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            "'org.eclipse.sisu' should be returned",
            ((Properties) (deps.get(0).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.GROUPID),
            Matchers.equalTo("org.eclipse.sisu")
        );
        MatcherAssert.assertThat(
            "'org.eclipse.sisu.plexus' should be returned",
            ((Properties) (deps.get(0).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.ARTIFACTID),
            Matchers.equalTo("org.eclipse.sisu.plexus")
        );
        MatcherAssert.assertThat(
            "'0.0.0.M5' should be returned",
            ((Properties) (deps.get(0).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.VERSION),
            Matchers.equalTo("0.0.0.M5")
        );
        MatcherAssert.assertThat(
            "'xml-apis' should be returned",
            ((Properties) (deps.get(1).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.GROUPID),
            Matchers.equalTo("xml-apis")
        );
        MatcherAssert.assertThat(
            "'xml-apis' should be returned",
            ((Properties) (deps.get(1).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.ARTIFACTID),
            Matchers.equalTo("xml-apis")
        );
        MatcherAssert.assertThat(
            "'1.0.0' should be returned",
            ((Properties) (deps.get(1).get(DuplicateFinderExcludeTest.DEPENDENCY)))
                .get(DuplicateFinderExcludeTest.VERSION),
            Matchers.equalTo("1.0.0")
        );
    }

    @Test
    void findResources() {
        final List<Properties> res = this.exclude.resources();
        MatcherAssert.assertThat(
            "'about.html' should be returned",
            res.get(0).get(DuplicateFinderExcludeTest.IGNORED_RES),
            Matchers.equalTo("about.html")
        );
        MatcherAssert.assertThat(
            "'org.w3c.dom.UserDataHandler' should be returned",
            res.get(1).get(DuplicateFinderExcludeTest.IGNORED_RES),
            Matchers.equalTo("org.w3c.dom.UserDataHandler")
        );
    }
}
