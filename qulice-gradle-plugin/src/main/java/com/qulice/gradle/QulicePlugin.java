/**
 * Copyright (c) 2011-2015, Qulice.com
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
package com.qulice.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Main class of the Qulice Gradle plugin.
 * @author Dmitri Pisarenko (dp@altruix.co)
 * @version $Id$
 * @since 1.0
 */
public final class QulicePlugin implements Plugin<Project> {
    public void apply(Project project) {
        this.runMavenDependencyAnalysis(project);
        this.runMavenEnforcerPlugin(project);
        this.runJsLintMavenPlugin(project);
        this.runmavenDuplicateFinderPlugin(project);
        this.runCheckstyle(project);
        this.runPMD(project);
        this.runFindBugs(project);
        this.runCobertura(project);
        this.runCodeNarc(project);
    }

    public void runMavenDependencyAnalysis(Project prj) {
        /**
         * @todo #339:30min Implement Maven dependency analysis against a
         *  Gradle project.
         **/
    }
    public void runMavenEnforcerPlugin(Project prj) {
        /**
         * @todo #339:30min Implement running of Maven enforcer plugin against
         *  a Gradle project.
         **/
    }
    public void runJsLintMavenPlugin(Project prj) {
        /**
         * @todo #339:30min Implement running of jslint-maven-plugin against
         *  this Gradle project.
         **/
    }
    public void runmavenDuplicateFinderPlugin(Project prj) {
        /**
         * @todo #339:30min Implement running of maven-duplicate-finder-plugin
         *  against this Gradle project.
         **/
    }
    public void runCheckstyle(Project prj) {
        /**
         * @todo #339:30min Implement running Checkstyle checks on the Gradle
         *  project.
         **/
    }
    public void runPMD(Project prj) {
        /**
         * @todo #339:30min Implement running PMD on this Gradle project.
         **/
    }
    public void runFindBugs(Project prj) {
        /**
         * @todo #339:30min Implement running FindBugs on this Gradle project.
         **/
    }
    public void runCobertura(Project prj) {
        /**
         * @todo #339:30min Implement running Cobertura on Gradle project.
         **/
    }
    public void runCodeNarc(Project prj) {
        /**
         * @todo #339:30min Implement running CodeNarc on Gradle project.
         **/
    }
}
