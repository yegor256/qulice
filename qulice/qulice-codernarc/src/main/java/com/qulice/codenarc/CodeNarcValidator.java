package com.qulice.codenarc;

import java.util.ArrayList;
import java.util.List;

import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.DirectorySourceAnalyzer;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.report.HtmlReportWriter;
import org.codenarc.results.Results;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;

/**
 * Validates groovy source code with CodeNarc.
 *
 * @author Pavlo Shamrai (pshamrai@gmail.com)
 * @version $Id: 
 * 
 */

public class CodeNarcValidator implements Validator{
    
	private static final String CODE_NARC_REPORT = "CodeNarc Report";
	private static final String RULESETS_BASIC_XML = "rulesets/basic.xml";
	private static final String INCLUDES = "**/*.groovy";
	protected String ruleSetFiles;
    protected String baseDir;
    protected String includes;
    protected String excludes;
    protected String title;
    protected List reports;
 
	public CodeNarcValidator() {
		super();
		reports = new ArrayList();
	}


	public CodeNarcValidator(String ruleSetFiles, String baseDir,
			String includes, String excludes, String title, List reports) {
		super();
		this.ruleSetFiles = ruleSetFiles;
		this.baseDir = baseDir;
		this.includes = includes;
		this.excludes = excludes;
		this.title = title;
		this.reports = reports;
	}


	@Override
	public void validate(Environment env) throws ValidationException {
	
		setDefaults(env);
		
		FilesystemSourceAnalyzer sourceAnalyzer = new FilesystemSourceAnalyzer();
		sourceAnalyzer.setBaseDirectory(baseDir);
		sourceAnalyzer.setIncludes(includes);
		sourceAnalyzer.setExcludes(excludes);
		
		
		CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
		codeNarcRunner.setSourceAnalyzer(sourceAnalyzer);
		codeNarcRunner.setRuleSetFiles(ruleSetFiles);
		codeNarcRunner.setReportWriters(reports);
		Results results = codeNarcRunner.execute();
		
		List violations = results.getViolations();
		if(violations !=null && violations.size()>0){
			throw new ValidationException("CodeNarc validation failure");
		}
		
	}
	
	/**
	 * Sets defaults values for baseDir,includes,ruleSetFiles and reports
	 * @param env
	 */
    protected void setDefaults(Environment env) {
        if (empty(baseDir)) {
            baseDir = env.basedir().getAbsolutePath();
        }
        if (empty(includes)) {
            includes = INCLUDES;
        }
        if (empty(ruleSetFiles)) {
            ruleSetFiles = RULESETS_BASIC_XML;
        }
        
        if(empty(title)){
        	title = CODE_NARC_REPORT;
        }
        
        if (reports.size() == 0) {
        	HtmlReportWriter htmlReportWriter = new HtmlReportWriter();
        	htmlReportWriter.setTitle(title);
        	
            reports.add(htmlReportWriter);
        }
    }

    protected boolean empty(String str){
    	return str==null || str.length()==0;
    }

    public String getRuleSetFiles() {
		return ruleSetFiles;
	}


	public void setRuleSetFiles(String ruleSetFiles) {
		this.ruleSetFiles = ruleSetFiles;
	}


	public String getBaseDir() {
		return baseDir;
	}


	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}


	public String getIncludes() {
		return includes;
	}


	public void setIncludes(String includes) {
		this.includes = includes;
	}


	public String getExcludes() {
		return excludes;
	}


	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public List getReports() {
		return reports;
	}


	public void setReports(List reports) {
		this.reports = reports;
	}

    
}
