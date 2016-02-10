/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.asu.poly.se.staticanalyzer.results.Location;

public class JSFile {
	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<Location> idLocation = new ArrayList<Location>();
	private List<String> classes = new ArrayList<String>();
	private List<Location> classesLocation = new ArrayList<Location>();
	private List<String> functions = new ArrayList<String>();
	private List<Integer> functionParamCount = new ArrayList<Integer>();
	private List<Location> functionLocation = new ArrayList<Location>();


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

	public void setIdLocation(Location location) {
		idLocation.add(location);
	}

	public List<Location> getIdLocation() {
		return this.idLocation;
	}

	public void setClasses(String classname) {
		classes.add(classname);
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public void setClassesLocation(Location location) {
		classesLocation.add(location);
	}

	public List<Location> getClassesLocation() {
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

	public void setFunctionLocation(Location location) {
		functionLocation.add(location);
	}

	public List<Location> getFunctionLocation() {
		return this.functionLocation;
	}

	public JSFile(File file){
		this.file = file;
	}
}