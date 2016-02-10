/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.results;

public class Location {

	private int rowNumber;
	private int columnNumber;

	public Location(int rowNumber,int columnNumber) {
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public int getColumnNumber() {
		return this.columnNumber;
	}
}
