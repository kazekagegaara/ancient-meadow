import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.CSSException;

class CSSParseErrorHandler implements ErrorHandler {

	private CSSFile file;

	public CSSParseErrorHandler(CSSFile file) {
		this.file = file;
	}

    public void warning(CSSParseException exception) throws CSSException {
        //System.out.println("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
        file.setSyntaxWarnings("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());
    }

    public void error(CSSParseException exception) throws CSSException {
        //System.out.println("CSS Parse Error: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
        file.setSyntaxErrors("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());
    }

    public void fatalError(CSSParseException exception) throws CSSException {
        //System.out.println("CSS Parse Fatal Error: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
        file.setSyntaxErrors("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber() + ", file name : " + file.getFile().toString());
    }
}