/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.parsers;

import edu.asu.poly.se.staticanalyzer.beans.CSSFile;
import edu.asu.poly.se.staticanalyzer.results.Error;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.CSSException;

public class CSSParseErrorHandler implements ErrorHandler {

	private CSSFile file;

	public CSSParseErrorHandler(CSSFile file) {
		this.file = file;
	}

	@Override
	public void error(CSSParseException exception) throws CSSException {
		String type = "CSSParseError";
		String desc = exception.getMessage();
		String fileName = file.getFile().toString();
		int rowNumber = exception.getLineNumber();
		int columnNumber = exception.getColumnNumber();
		file.setSyntaxErrors(new Error(type,desc,fileName,rowNumber,columnNumber));
	}

	@Override
	public void fatalError(CSSParseException exception) throws CSSException {
		String type = "CSSParseError";
		String desc = exception.getMessage();
		String fileName = file.getFile().toString();
		int rowNumber = exception.getLineNumber();
		int columnNumber = exception.getColumnNumber();
		file.setSyntaxErrors(new Error(type,desc,fileName,rowNumber,columnNumber));
	}

	@Override
	public void warning(CSSParseException exception) throws CSSException {
		String type = "CSSParseError";
		String desc = exception.getMessage();
		String fileName = file.getFile().toString();
		int rowNumber = exception.getLineNumber();
		int columnNumber = exception.getColumnNumber();
		file.setSyntaxErrors(new Error(type,desc,fileName,rowNumber,columnNumber));
	}

}
