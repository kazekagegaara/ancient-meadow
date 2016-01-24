import java.io.File;
import jdk.nashorn.api.scripting.*;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.options.Options;
import jdk.nashorn.internal.runtime.ErrorManager;

class JSParser {

	public void parseJS(File file) {
		String code = "function a(x,y) { var b = 5; document.getElementById('demo'); document.getElementByClass('stopButton');} function c() { }";

		Options options = new Options("nashorn");
		options.set("anon.functions", true);
		options.set("parse.only", true);
		options.set("scripting", true);

		ErrorManager errors = new ErrorManager();
		Context contextm = new Context(options, errors, Thread.currentThread().getContextClassLoader());
		Context.setGlobal(contextm.createGlobal());
		String json = ScriptUtils.parse(code, "<unknown>", false);
		System.out.println(json);
	}
}