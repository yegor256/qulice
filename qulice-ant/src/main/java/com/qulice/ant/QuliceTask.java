/**
 * Copyright (c) 2011-2014, Qulice.com
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
package com.qulice.ant;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Ant Task for Qulice.
 *
 * @author Yuriy Alevohin (alevohin@mail.ru)
 * @version $Id$
 */
public class QuliceTask extends Task {

    /**
     * Sources dirs.
     */
    private transient Path srcdir;

    /**
     * Classes dir (only one dir is supported)
     */
    private transient File classesdir;

    /**
     * Classpath dirs and files.
     */
    private transient Path classpath;

    @Override
    public void execute() throws BuildException {
        super.execute();
        Environment env = environment();
        try {
            final long start = System.nanoTime();
            validate(env);
            Logger.info(
                this,
                "Qulice quality check completed in %[nano]s",
                System.nanoTime() - start
            );
        } catch (ValidationException ex) {
            Logger.info(
                this,
                "Read our quality policy: http://www.qulice.com/quality.html"
            );
            throw new BuildException("Failure", ex);
        }
    }

    /**
     * Create Environment.
     * @return Environment.
     * @throws BuildException If ant task doesn't have mandatory params.
     */
    private Environment environment() throws BuildException {
        if (srcdir == null) {
            throw new BuildException("sourcepath not defined for QuliceTask");
        }
        if (classesdir == null) {
            throw new BuildException("classesdir not defined for QuliceTask");
        }
        if (classpath == null) {
            throw new BuildException("classpath not defined for QuliceTask");
        }
        return new AntEnvironment(
            getProject(),
            srcdir,
            classesdir,
            classpath);
    }

    /**
     * Validate and throws exception if there are any problems.
     * @param env Environment
     * @throws ValidationException If there are any problems.
     */
    private void validate(Environment env) throws ValidationException {
        for (final Validator validator : this.validators()) {
            validator.validate(env);
        }
    }

    /**
     * Create set of Validators.
     * @return Set of Validators.
     */
    private Set<Validator> validators() {
        final Set<Validator> validators = new LinkedHashSet<Validator>();
        validators.add(new com.qulice.checkstyle.CheckstyleValidator());
        validators.add(new com.qulice.pmd.PMDValidator());
        validators.add(new com.qulice.xml.XmlValidator());
        validators.add(new com.qulice.codenarc.CodeNarcValidator());
        validators.add(new com.qulice.findbugs.FindBugsValidator());
        return validators;
    }

    /**
     * Set source dirs
     * @param srcdir Source dirs
     */
    public void setSrcdir(Path srcdir) {
        this.srcdir = srcdir;
    }

    /**
     * Set classes dir
     * @param classesdir Classes dir
     */
    public void setClassesdir(File classesdir) {
        this.classesdir = classesdir;
    }

    /**
     * Set classpath
     * @param classpath classpath
     */
    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }
}
