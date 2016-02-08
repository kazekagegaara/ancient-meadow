/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CSSFile {

	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();
	private List<String> syntaxWarnings = new ArrayList<String>();
	private List<String> syntaxErrors = new ArrayList<String>();

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

	public void setSyntaxWarnings(String errorMessage) {
		syntaxWarnings.add(errorMessage);
	}

	public List<String> getSyntaxWarnings() {
		return this.syntaxWarnings;
	}

	public void setSyntaxErrors(String errorMessage) {
		syntaxErrors.add(errorMessage);
	}

	public List<String> getSyntaxErrors() {
		return this.syntaxErrors;
	}

	public CSSFile(File file){
		this.file = file;
	}
}