/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.parsers;

import org.w3c.css.sac.ErrorHandler;

import edu.asu.poly.se.staticanalyzer.beans.CSSFile;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.CSSException;

public class CSSParseErrorHandler implements ErrorHandler {

	private CSSFile file;

	public CSSParseErrorHandler(CSSFile file) {
		this.file = file;
	}

	@Override
	public void error(CSSParseException exception) throws CSSException {		
		file.setSyntaxWarnings("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());
	}

	@Override
	public void fatalError(CSSParseException exception) throws CSSException {
		file.setSyntaxErrors("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());
	}

	@Override
	public void warning(CSSParseException exception) throws CSSException {
		file.setSyntaxErrors("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());

	}

}
