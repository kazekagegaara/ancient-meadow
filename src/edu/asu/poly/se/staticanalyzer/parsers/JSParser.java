/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import edu.asu.poly.se.staticanalyzer.beans.JSFile;
import edu.asu.poly.se.staticanalyzer.results.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import jdk.nashorn.api.scripting.*;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.options.Options;
import jdk.nashorn.internal.runtime.ErrorManager;

public class JSParser {
	private String code;
	private JSFile file;

	public JSParser(JSFile file) throws Exception {
		this.file = file;
		BufferedReader reader = new BufferedReader(new FileReader(file.getFile()));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		try {
			while((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			code = stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	public void parseJS() {
		Options options = new Options("nashorn");
		options.set("anon.functions", true);
		options.set("parse.only", true);
		options.set("scripting", true);

		ErrorManager errors = new ErrorManager();
		Context contextm = new Context(options, errors, Thread.currentThread().getContextClassLoader());
		Context.setGlobal(contextm.createGlobal());
		String json = ScriptUtils.parse(code, file.getFile().toString(), true);
		JsonObject o = new JsonParser().parse(json).getAsJsonObject();
		for (Map.Entry<String,JsonElement> entry : o.entrySet()) {
			if(entry.getKey().toString().equals("body")) {
				JsonArray array = entry.getValue().getAsJsonArray();
				for (JsonElement types : array) {					
					if(types.getAsJsonObject().get("type").getAsString().equals("FunctionDeclaration")) {
						file.setFunctions(types.getAsJsonObject().getAsJsonObject("id").get("name").getAsString());
						int rowNumber = types.getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
						int columnNumber = types.getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
						Location loc = new Location(rowNumber,columnNumber);
						file.setFunctionLocation(loc);
						JsonArray params = types.getAsJsonObject().getAsJsonArray("params");
						file.setFunctionParams(params.size());
						JsonArray functionBody = types.getAsJsonObject().getAsJsonObject("body").getAsJsonArray("body");
						for(JsonElement statement : functionBody) {
							JsonPrimitive statementType = statement.getAsJsonObject().getAsJsonPrimitive("type");
							if(statementType.getAsString().equals("ExpressionStatement")) {
								JsonObject statementExpression = statement.getAsJsonObject().getAsJsonObject("expression");
								if(statementExpression.getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
										statementExpression.getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
									String property = statementExpression.getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
									if(property.equals("getElementById")) {										
										file.setIds(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
										int idRowNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
										int idColumnNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
										Location idLoc = new Location(idRowNumber,idColumnNumber);
										file.setIdLocation(idLoc);
									} else if (property.equals("getElementByClass")) {
										file.setClasses(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
										int classRowNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
										int classColumnNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
										Location classLoc = new Location(classRowNumber,classColumnNumber);
										file.setClassesLocation(classLoc);
									}
								}
							} else if(statementType.getAsString().equals("VariableDeclaration")) {
								JsonObject declarationStatement = statement.getAsJsonObject().getAsJsonArray("declarations").get(0).getAsJsonObject();
								if(declarationStatement.getAsJsonObject("init").getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
										declarationStatement.getAsJsonObject("init").getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
									String property = declarationStatement.getAsJsonObject("init").getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
									if(property.equals("getElementById")) {
										file.setIds(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
										int idRowNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
										int idColumnNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
										Location idLoc = new Location(idRowNumber,idColumnNumber);
										file.setIdLocation(idLoc);
									} else if (property.equals("getElementByClass")) {
										file.setClasses(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
										int classRowNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
										int classColumnNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
										Location classLoc = new Location(classRowNumber,classColumnNumber);
										file.setClassesLocation(classLoc);
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
								file.setIds(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
								int idRowNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
								int idColumnNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
								Location idLoc = new Location(idRowNumber,idColumnNumber);
								file.setIdLocation(idLoc);
							} else if (property.equals("getElementByClass")) {								
								file.setClasses(declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
								int classRowNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
								int classColumnNumber = declarationStatement.getAsJsonObject("init").getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
								Location classLoc = new Location(classRowNumber,classColumnNumber);
								file.setClassesLocation(classLoc);
							}
						}
					} else if(types.getAsJsonObject().get("type").getAsString().equals("ExpressionStatement")) {
						JsonObject statementExpression = types.getAsJsonObject().getAsJsonObject("expression");
						if(statementExpression.getAsJsonPrimitive("type").getAsString().equals("CallExpression") &&
								statementExpression.getAsJsonObject("callee").getAsJsonObject("object").getAsJsonPrimitive("name").getAsString().equals("document")) {
							String property = statementExpression.getAsJsonObject("callee").getAsJsonPrimitive("property").getAsString();
							if(property.equals("getElementById")) {
								file.setIds(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
								int idRowNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
								int idColumnNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
								Location idLoc = new Location(idRowNumber,idColumnNumber);
								file.setIdLocation(idLoc);
							} else if (property.equals("getElementByClass")) {
								file.setClasses(statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString().substring(1));
								int classRowNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("line").getAsInt();
								int classColumnNumber = statementExpression.getAsJsonArray("arguments").get(0).getAsJsonObject().getAsJsonObject("loc").getAsJsonObject("start").getAsJsonPrimitive("column").getAsInt();
								Location classLoc = new Location(classRowNumber,classColumnNumber);
								file.setClassesLocation(classLoc);
							}
						}
					}
				}
			}
		}
	}
}