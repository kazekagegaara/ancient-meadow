/**
 * @author Manit Singh Kalsi
 */
package edu.asu.staticanalyzer.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JSFile {
	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<Integer> idLocation = new ArrayList<Integer>();
	private List<String> classes = new ArrayList<String>();
	private List<Integer> classesLocation = new ArrayList<Integer>();
	private List<String> functions = new ArrayList<String>();
	private List<Integer> functionParamCount = new ArrayList<Integer>();
	private List<Integer> functionLocation = new ArrayList<Integer>();


	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void setIds(String id) {
		ids.add(id);
	}

	public List<String> getIds() {
		return this.ids;
	}

	public void setIdLocation(int location) {
		idLocation.add(location);
	}

	public List<Integer> getIdLocation() {
		return this.idLocation;
	}

	public void setClasses(String classname) {
		classes.add(classname);
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public void setClassesLocation(int location) {
		classesLocation.add(location);
	}

	public List<Integer> getClassesLocation() {
		return this.classesLocation;
	}

	public void setFunctions(String name) {
		functions.add(name);
	}

	public List<String> getFunctions() {
		return this.functions;
	}

	public void setFunctionParams(int count) {
		functionParamCount.add(count);
	}

	public List<Integer> getFunctionParams() {
		return this.functionParamCount;
	}

	public void setFunctionLocation(int location) {
		functionLocation.add(location);
	}

	public List<Integer> getFunctionLocation() {
		return this.functionLocation;
	}

	public JSFile(File file){
		this.file = file;
	}
}