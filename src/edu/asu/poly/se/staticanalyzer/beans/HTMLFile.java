/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HTMLFile {

	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();
	private List<String> styleSheetLinks = new ArrayList<String>();
	private List<String> mediaLinks = new ArrayList<String>();
	private List<String> scriptLinks = new ArrayList<String>();
	private List<String> eventHandlers = new ArrayList<String>();

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

	public void setStyleSheetLinks(List<String> styleSheetLinks) {
		this.styleSheetLinks = styleSheetLinks;
	}

	public List<String> getStyleSheetLinks() {
		return this.styleSheetLinks;
	}

	public void setMediaLinks(List<String> mediaLinks) {
		this.mediaLinks = mediaLinks;
	}

	public List<String> getMediaLinks() {
		return this.mediaLinks;
	}

	public void setScriptLinks(List<String> scriptLinks) {
		this.scriptLinks = scriptLinks;
	}

	public List<String> getScriptLinks() {
		return this.scriptLinks;
	}

	public void setEventHandlers(List<String> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}

	public List<String> getEventHandlers() {
		return this.eventHandlers;
	}


	public HTMLFile(File file){
		this.file = file;
	}

}