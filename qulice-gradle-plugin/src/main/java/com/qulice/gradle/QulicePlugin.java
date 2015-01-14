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
         * @todo
         **/
    }
    public void runMavenEnforcerPlugin(Project prj) {
        /**
         * @todo
         **/
    }
    public void runJsLintMavenPlugin(Project prj) {
        /**
         * @todo
         **/
    }
    public void runmavenDuplicateFinderPlugin(Project prj) {
        /**
         * @todo
         **/
    }
    public void runCheckstyle(Project prj) {
        /**
         * @todo
         **/
    }
    public void runPMD(Project prj) {
        /**
         * @todo
         **/
    }
    public void runFindBugs(Project prj) {
        /**
         * @todo
         **/
    }
    public void runCobertura(Project prj) {
        /**
         * @todo
         **/
    }
    public void runCodeNarc(Project prj) {
        /**
         * @todo
         **/
    }
}
