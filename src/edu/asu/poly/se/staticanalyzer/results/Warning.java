/**
 * @author Manit Singh Kalsi
 */

package edu.asu.poly.se.staticanalyzer.results;

public class Warning {

	private String warningType;
	private String desc;
	private String fileName;
	private int rowNumber;
	private int columnNumber;

	public Warning(String warningType, String desc, String fileName, int rowNumber, int columnNumber) {
		this.warningType = warningType;
		this.desc = desc;
		this.fileName = fileName;
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
	}

	public String getWarningType() {
		return this.warningType;
	}

	public String getDesc() {
		return this.desc;
	}

	public String getFileName() {
		return this.fileName;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public int getColumnNumber() {
		return this.columnNumber;
	}
}