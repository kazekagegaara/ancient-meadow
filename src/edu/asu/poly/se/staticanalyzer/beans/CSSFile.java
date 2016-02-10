/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import edu.asu.poly.se.staticanalyzer.results.Error;

public class CSSFile {

	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();
	private List<Error> syntaxErrors = new ArrayList<Error>();

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<String> getIds() {
		return this.ids;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public void setSyntaxErrors(Error error) {
		syntaxErrors.add(error);
	}

	public List<Error> getSyntaxErrors() {
		return this.syntaxErrors;
	}

	public CSSFile(File file){
		this.file = file;
	}
}