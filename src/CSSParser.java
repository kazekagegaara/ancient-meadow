import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleDeclaration;

class CSSParser {

  private CSSFile file;
  private CSSRuleList ruleList;

  public CSSParser(CSSFile file) throws Exception {
    this.file = file;
    InputStream stream = new FileInputStream(file.getFile());
    InputSource source = new InputSource(new InputStreamReader(stream));
    CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
    ErrorHandler errorHandler = new CSSParseErrorHandler(file);
    parser.setErrorHandler(errorHandler);
    CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);
    ruleList = stylesheet.getCssRules();
  }

  public void getClasses() {
    file.setClasses(new ArrayList<String>(getClassesOrIds("classes")));
  }

  public void getIds() {
    file.setIds(new ArrayList<String>(getClassesOrIds("ids")));
  }

  private Set<String> getClassesOrIds(String type) {
    Set<String> identifiers = new HashSet<String>();
    for (int i = 0; i < ruleList.getLength(); i++)
    {
      CSSRule rule = ruleList.item(i);
      if (rule instanceof CSSStyleRule) {
        CSSStyleRule styleRule=(CSSStyleRule)rule;
        String pattern = "";
        if(type.equals("classes")) {
          pattern = "(\\.)-?[_a-zA-Z]+[_a-zA-Z0-9-]*";
        } else if(type.equals("ids")) {
          pattern = "(#)-?[_a-zA-Z]+[_a-zA-Z0-9-]*";
        }
        Matcher m = Pattern.compile(pattern).matcher(styleRule.getSelectorText().toString());
        while (m.find()) {
          identifiers.add(m.group().substring(1));
        }
      }
    }
    return identifiers;
  }
}