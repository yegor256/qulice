package com.qulice.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author Dmitri Pisarenko (dp@altruix.co)
 * @version $Id$
 * @since 1.0
 */
public class QulicePlugin implements Plugin<Project> {
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
