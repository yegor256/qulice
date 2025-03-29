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

import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for PlexusConfigurationToXpp3Dom class.
 *
 * @since 0.23.1
 */
final class PlexusConfigurationToXpp3DomTest {

    @Test
    void transform() {
        final PlexusConfiguration config = new DefaultPlexusConfiguration("testName");
        config.addChild("kchild1", "vchild1");
        final PlexusConfiguration child = new DefaultPlexusConfiguration("kchild2");
        child.addChild("kchild3", "vchild3");
        config.addChild(child);
        final TransformToXpp3Dom transformer = new PlexusConfigurationToXpp3Dom(config);
        final Xpp3Dom doc = transformer.transform();
        MatcherAssert.assertThat(
            "'testName' should be returned",
            doc.getName(),
            Matchers.equalTo("testName")
        );
        MatcherAssert.assertThat(
            "'kchild1' should be returned",
            doc.getChild(0).getName(),
            Matchers.equalTo("kchild1")
        );
        MatcherAssert.assertThat(
            "'vchild1' should be returned",
            doc.getChild("kchild1").getValue(),
            Matchers.equalTo("vchild1")
        );
        MatcherAssert.assertThat(
            "'kchild2' should be returned",
            doc.getChild(1).getName(),
            Matchers.equalTo("kchild2")
        );
        MatcherAssert.assertThat(
            "'kchild3' should be returned",
            doc.getChild("kchild2").getChild(0).getName(),
            Matchers.equalTo("kchild3")
        );
        MatcherAssert.assertThat(
            "'vchild3' should be returned",
            doc.getChild("kchild2").getChild("kchild3").getValue(),
            Matchers.equalTo("vchild3")
        );
    }
}
