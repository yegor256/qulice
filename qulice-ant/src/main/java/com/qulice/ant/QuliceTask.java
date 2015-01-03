package com.qulice.ant;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Created with IntelliJ IDEA.
 * User: alevohins
 * Date: 03.01.15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class QuliceTask extends Task {

    private transient Path sourcepath;
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

    private Environment environment() throws BuildException {
        if (sourcepath == null) {
            throw new BuildException("sourcepath not defined for QuliceTask");
        }
        if (classpath == null) {
            throw new BuildException("classpath not defined for QuliceTask");
        }
        return new AntEnvironment(
            getProject(),
            sourcepath,
            classpath);
    }

    private void validate(Environment env) throws ValidationException {
        for (final Validator validator : this.validators()) {
            validator.validate(env);
        }
    }

    private Set<Validator> validators() {
        final Set<Validator> validators = new LinkedHashSet<Validator>();
        validators.add(new com.qulice.checkstyle.CheckstyleValidator());
        validators.add(new com.qulice.pmd.PMDValidator());
        validators.add(new com.qulice.xml.XmlValidator());
        validators.add(new com.qulice.codenarc.CodeNarcValidator());
        validators.add(new com.qulice.findbugs.FindBugsValidator());
        return validators;
    }

    public void setSourcepath(Path sourcepath) {
        this.sourcepath = sourcepath;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }
}
