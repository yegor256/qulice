/**
 * Copyright (c) 2011, Qulice.com
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
package com.qulice.findbugs;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.ymock.util.Logger;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.PrintingBugReporter;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;
import java.io.File;

/**
 * Validates source code and compiled binaris with FindBugs.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class FindBugsValidator implements Validator {

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (3 lines)
     */
    @Override
    public void validate(final Environment env) throws ValidationException {
        if (!env.outdir().exists()) {
            Logger.info(
                this,
                "No classes at %s, no FindBugs validation",
                env.outdir()
            );
            return;
        }
        final FindBugs2 findbugs = new FindBugs2();
        findbugs.setProject(this.project(env));
        final BugReporter reporter = new PrintingBugReporter();
        reporter.getProjectStats().getProfiler().start(findbugs.getClass());
        reporter.setPriorityThreshold(Detector.LOW_PRIORITY);
        findbugs.setBugReporter(reporter);
        DetectorFactoryCollection.instance().ensureLoaded();
        findbugs.setDetectorFactoryCollection(
            DetectorFactoryCollection.instance()
        );
        findbugs.setUserPreferences(
            UserPreferences.createDefaultUserPreferences()
        );
        findbugs.setNoClassOk(true);
        findbugs.setScanNestedArchives(true);
        try {
            findbugs.execute();
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(ex);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
        if (findbugs.getBugCount() > 0) {
            throw new ValidationException(
                "%d FindBugs violations (see log above)",
                findbugs.getBugCount()
            );
        }
        Logger.info(this, "No FindBugs violations found");
    }

    /**
     * Create project.
     * @param env Environment
     * @return The project
     */
    private Project project(final Environment env) {
        final Project project = new Project();
        for (File jar : env.classpath()) {
            project.addFile(jar.getPath());
            if (!jar.equals(env.outdir())) {
                project.addAuxClasspathEntry(jar.getPath());
            }
        }
        project.addSourceDir(env.basedir().getPath());
        return project;
    }

}
