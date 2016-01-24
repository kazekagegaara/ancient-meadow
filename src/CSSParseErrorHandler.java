import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.CSSException;

class CSSParseErrorHandler implements ErrorHandler {

    public void warning(CSSParseException exception) throws CSSException {
        System.out.println("CSS Parse Warning: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
    }

    public void error(CSSParseException exception) throws CSSException {
        System.out.println("CSS Parse Error: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
    }

    public void fatalError(CSSParseException exception) throws CSSException {
        System.out.println("CSS Parse Fatal Error: " + exception.getMessage() + ", line number : " + exception.getLineNumber() + ", column number : " + exception.getColumnNumber());
    }
}