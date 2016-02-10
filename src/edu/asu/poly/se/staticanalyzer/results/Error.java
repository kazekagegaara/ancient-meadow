/**
 * @author Manit Singh Kalsi
 */

package edu.asu.poly.se.staticanalyzer.results;

public class Error {

	private String errorType;
	private String desc;
	private String fileName;
	private int rowNumber;
	private int columnNumber;

	public Error(String errorType, String desc, String fileName, int rowNumber, int columnNumber) {
		this.errorType = errorType;
		this.desc = desc;
		this.fileName = fileName;
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
	}

	public String getErrorType() {
		return this.errorType;
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