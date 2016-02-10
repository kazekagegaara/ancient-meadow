/**
 * @author Manit Singh Kalsi
 */

package edu.asu.poly.se.staticanalyzer.results;

import java.util.ArrayList;
import java.util.List;

public class Results {

	private List<Error> errors;
	private List<Warning> warnings;

	public Results() {
		errors = new ArrayList<Error>();
		warnings = new ArrayList<Warning>();
	}

	public void setError(Error error) {
		errors.add(error);
	}

	public void setWarning(Warning warning) {
		warnings.add(warning);
	}

	public List<Error> getErrors() {
		return this.errors;
	}

	public List<Warning> getWarnings() {
		return this.warnings;
	}
}