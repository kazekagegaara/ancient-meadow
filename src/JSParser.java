import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.util.Map;
import jdk.nashorn.api.scripting.*;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.options.Options;
import jdk.nashorn.internal.runtime.ErrorManager;

class JSParser {

	public void parseJS(File file) {
		String code = "function a(x,y) { \n"
			+ "var b = 5; \n"
			+ "document.getElementById('demo'); \n"
			+ "document.getElementByClass('stopButton');\n"
		+ "} \n"
		    + "function c() { \n"
		+ "}\n"
		+ "\n"
		+ "var t = 5;\n"
		+ "var m = document.getElementById('testtest'); \n"
		+ "document.getElementById('tdaaaa') \n"
		+ "document.getElementByClass('stopButton');";

		Options options = new Options("nashorn");
		options.set("anon.functions", true);
		options.set("parse.only", true);
		options.set("scripting", true);

		ErrorManager errors = new ErrorManager();
		Context contextm = new Context(options, errors, Thread.currentThread().getContextClassLoader());
		Context.setGlobal(contextm.createGlobal());
		String json = ScriptUtils.parse(code, "script", true);
		JsonObject o = new JsonParser().parse(json).getAsJsonObject();
		System.out.println(o.toString());
		for (Map.Entry<String,JsonElement> entry : o.entrySet()) {
			if(entry.getKey().toString().equals("body")) {
				JsonArray array = entry.getValue().getAsJsonArray();
				for (JsonElement types : array) {
					//System.out.println(types.toString());
					if(types.getAsJsonObject().get("type").getAsString().equals("FunctionDeclaration")) {
						System.out.println("Function " + types.getAsJsonObject().getAsJsonObject("id").get("name"));
						JsonArray params = types.getAsJsonObject().getAsJsonArray("params");
						for(JsonElement param : params) {
							System.out.println("Parameters " + param.getAsJsonObject().getAsJsonPrimitive("name").toString());
						}
						JsonArray functionBody = types.getAsJsonObject().getAsJsonObject("body").getAsJsonArray("body");
						for(JsonElement statement : functionBody) {
							//System.out.println(statement.getAsJsonObject().getAsJsonPrimitive("type").toString());
							JsonPrimitive statementType = statement.getAsJsonObject().getAsJsonPrimitive("type");
							if(statementType.getAsString().equals("ExpressionStatement")) {
								JsonObject statementExpression = statement.getAsJsonObject().getAsJsonObject("expression");
								if(statementExpression.getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
									statementExpression.getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
									String property = statementExpression.getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
									if(property.equals("getElementById")) {
										System.out.print("id - ");
										System.out.println(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
									} else if (property.equals("getElementByClass")) {
										System.out.print("class - ");
										System.out.println(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
									}
								}
							}
						}
					} else if(types.getAsJsonObject().get("type").getAsString().equals("VariableDeclaration")) {
						JsonObject declarationStatement = types.getAsJsonObject().getAsJsonArray("declarations").get(0).getAsJsonObject();
						if(declarationStatement.getAsJsonObject("init").getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
							declarationStatement.getAsJsonObject("init").getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
							String property = declarationStatement.getAsJsonObject("init").getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
							if(property.equals("getElementById")) {
								System.out.print("id - ");
								System.out.println(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
							} else if (property.equals("getElementByClass")) {
								System.out.print("class - ");
								System.out.println(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
							}
						}
					} else if(types.getAsJsonObject().get("type").getAsString().equals("ExpressionStatement")) {
						JsonObject statementExpression = types.getAsJsonObject().getAsJsonObject("expression");
						if(statementExpression.getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
							statementExpression.getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
							String property = statementExpression.getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
							if(property.equals("getElementById")) {
								System.out.print("id - ");
								System.out.println(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
							} else if (property.equals("getElementByClass")) {
								System.out.print("class - ");
								System.out.println(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").toString());
							}
						}
					}
				}
			}
		}
	}
}