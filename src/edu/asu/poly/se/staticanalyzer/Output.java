/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.asu.poly.se.staticanalyzer.results.Results;

public class Output {

	private static String source = null;
	private static String outputFormat = "text";
	private static String verbosityLevel = "medium";
	private static String recommendationSetting = "no";
	private static String rulesList = "all";
	private static String outputFile = "output";

	public static void listResults(Results results) {
		StringBuilder resultString = new StringBuilder();
		if(!rulesList.equals("all")) {
			String rules[] = rulesList.split(",");
			resultString.append(String.format("%-40s%-90s%-90s%-5s%-5s","Type","Description","Source File","Location(row number)","Location(column number)"));
			resultString.append(System.getProperty("line.separator"));
			boolean showWarningsCount = false;
			boolean showErrorsCount = false;
			int errorsCount = 0;
			int warningsCount = 0;
			for(int i=0;i<rules.length;i++) {
				if(rules[i].equals("ParseError")) {
					errorsCount += listParseErrors(results,resultString);
					showErrorsCount = true;
				} else if(rules[i].equals("ReferenceError")) {
					errorsCount += listReferenceErrors(results,resultString);
					showErrorsCount = true;
				} else if(rules[i].equals("FileNotFound")) {
					errorsCount += listFileNotFoundErrors(results,resultString);
					showErrorsCount = true;
				} else if(rules[i].equals("Warnings")) {
					warningsCount += listWarnings(results,resultString);
					showWarningsCount = true;
				}
			}
			if(showErrorsCount) {
				resultString.append("Total " + errorsCount + " errors!");
				resultString.append(System.getProperty("line.separator"));
			}
			if(showWarningsCount) {
				resultString.append("Total " + warningsCount + " warnings!");
				resultString.append(System.getProperty("line.separator"));
			}
			System.out.println(resultString);
			try(PrintWriter out = new PrintWriter(outputFile+".txt")) {
			    out.println(resultString);
			} catch (FileNotFoundException e) {
				System.out.println("Unable to write output to file!");
			}
		} else if(rulesList.equals("all")){
			if(!(verbosityLevel.equals("high") || verbosityLevel.equals("medium") || verbosityLevel.equals("low"))) {
				System.out.println("Unsupported value for verbosity, please use --help for supported values");
			} else {
				if(verbosityLevel.equals("low")) {
					resultString.append("Total " + results.getErrors().size() + " errors!");
					resultString.append(System.getProperty("line.separator"));
					resultString.append("Total " + results.getWarnings().size() + " warnings!");
					resultString.append(System.getProperty("line.separator"));
					System.out.println(resultString);
					try(PrintWriter out = new PrintWriter(outputFile+".txt")) {
					    out.println(resultString);
					} catch (FileNotFoundException e) {
						System.out.println("Unable to write output to file!");
					}
				} else {
					resultString.append(String.format("%-40s%-90s%-90s%-5s%-5s","Type","Description","Source File","Location(row number)","Location(column number)"));
					resultString.append(System.getProperty("line.separator"));
					listFileNotFoundErrors(results,resultString);
					listParseErrors(results,resultString);
					listReferenceErrors(results,resultString);
					if(verbosityLevel.equals("high")) {
						listWarnings(results,resultString);
					}
					resultString.append("Total " + results.getErrors().size() + " errors!");
					resultString.append(System.getProperty("line.separator"));
					resultString.append("Total " + results.getWarnings().size() + " warnings!");
					resultString.append(System.getProperty("line.separator"));
					System.out.println(resultString);
					try(PrintWriter out = new PrintWriter(outputFile+".txt")) {
					    out.println(resultString);
					} catch (FileNotFoundException e) {
						System.out.println("Unable to write output to file!");
					}
				}
			}
		} else {
			System.out.println("Unsupported rule format. Please use --help to see supported values");
		}

	}

	private static int listParseErrors(Results results, StringBuilder resultString) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("ParseError")) {
				resultString.append(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				resultString.append(System.getProperty("line.separator"));
				count++;
			}
		}
		return count;
	}

	private static int listReferenceErrors(Results results, StringBuilder resultString) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("ReferenceError")) {
				resultString.append(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				resultString.append(System.getProperty("line.separator"));
				count++;
			}
		}
		return count;
	}

	private static int listFileNotFoundErrors(Results results, StringBuilder resultString) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("FileNotFound")) {
				resultString.append(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				resultString.append(System.getProperty("line.separator"));
				count++;
			}
		}
		return count;
	}

	private static int listWarnings(Results results, StringBuilder resultString) {
		int count = 0;
		for(int i=0;i<results.getWarnings().size();i++){
			String type = results.getWarnings().get(i).getWarningType();
			String desc = results.getWarnings().get(i).getDesc();
			String fileName = results.getWarnings().get(i).getFileName();
			int rowNumber = results.getWarnings().get(i).getRowNumber();
			int columnNumber = results.getWarnings().get(i).getColumnNumber();
			resultString.append(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
			resultString.append(System.getProperty("line.separator"));
			count++;
		}
		return count;
	}

	public static void listResultsAsJson(Results results) {
		JsonObject resultsJson = new JsonObject();
		if(!rulesList.equals("all")) {
			String rules[] = rulesList.split(",");
			boolean showWarningsCount = false;
			boolean showErrorsCount = false;
			int errorsCount = 0;
			int warningsCount = 0;
			for(int i=0;i<rules.length;i++) {
				if(rules[i].equals("ParseError")) {
					JsonArray parseErrors = getParseErrorsAsJson(results);
					resultsJson.add("ParseErrors", parseErrors);
					errorsCount += parseErrors.size();
					showErrorsCount = true;
				} else if(rules[i].equals("ReferenceError")) {
					JsonArray referenceErrors = getReferenceErrorsAsJson(results);
					resultsJson.add("ReferenceErrors", referenceErrors);
					errorsCount += referenceErrors.size();
					showErrorsCount = true;
				} else if(rules[i].equals("FileNotFound")) {
					JsonArray fileNotFoundErrors = getFileNotFoundErrorsAsJson(results);
					resultsJson.add("FileNotFoundErrors", fileNotFoundErrors);
					errorsCount += fileNotFoundErrors.size();
					showErrorsCount = true;
				} else if(rules[i].equals("Warnings")) {
					JsonArray warnings = getWarningsAsJson(results);
					resultsJson.add("Warnings", warnings);
					warningsCount += warnings.size();
					showWarningsCount = true;
				}
			}
			if(showErrorsCount) {
				resultsJson.add("ErrorCount", new JsonPrimitive(errorsCount));
			}
			if(showWarningsCount) {
				resultsJson.add("WarningsCount", new JsonPrimitive(warningsCount));
			}
			System.out.println(resultsJson.toString());
			try(PrintWriter out = new PrintWriter(outputFile+".json")) {
			    out.println(resultsJson);
			} catch (FileNotFoundException e) {
				System.out.println("Unable to write output to file!");
			}
		} else if(rulesList.equals("all")){
			if(!(verbosityLevel.equals("high") || verbosityLevel.equals("medium") || verbosityLevel.equals("low"))) {
				System.out.println("Unsupported value for verbosity, please use --help for supported values");
			} else {
				if(verbosityLevel.equals("low")) {
					resultsJson.add("ErrorCount", new JsonPrimitive(results.getErrors().size()));
					resultsJson.add("WarningsCount", new JsonPrimitive(results.getWarnings().size()));
					System.out.println(resultsJson.toString());
					try(PrintWriter out = new PrintWriter(outputFile+".json")) {
					    out.println(resultsJson);
					} catch (FileNotFoundException e) {
						System.out.println("Unable to write output to file!");
					}
				} else {
					resultsJson.add("ParseErrors", getParseErrorsAsJson(results));
					resultsJson.add("ReferenceErrors", getReferenceErrorsAsJson(results));
					resultsJson.add("FileNotFoundErrors", getFileNotFoundErrorsAsJson(results));
					if(verbosityLevel.equals("high")) {
						resultsJson.add("Warnings", getWarningsAsJson(results));
					}
					resultsJson.add("ErrorCount", new JsonPrimitive(results.getErrors().size()));
					resultsJson.add("WarningsCount", new JsonPrimitive(results.getWarnings().size()));
					System.out.println(resultsJson.toString());
					try(PrintWriter out = new PrintWriter(outputFile+".json")) {
					    out.println(resultsJson);
					} catch (FileNotFoundException e) {
						System.out.println("Unable to write output to file!");
					}
				}
			}
		} else {
			System.out.println("Unsupported rule format. Please use --help to see supported values");
		}
	}

	private static JsonArray getParseErrorsAsJson(Results results) {
		JsonArray parseErrors = new JsonArray();
		Gson gson =  new Gson();
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			if(type.contains("ParseError")) {
				parseErrors.add(gson.toJsonTree(results.getErrors().get(i)));
			}
		}
		return parseErrors;
	}

	private static JsonArray getReferenceErrorsAsJson(Results results) {
		JsonArray referenceErrors = new JsonArray();
		Gson gson =  new Gson();
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			if(type.contains("ReferenceError")) {
				referenceErrors.add(gson.toJsonTree(results.getErrors().get(i)));
			}
		}
		return referenceErrors;
	}

	private static JsonArray getFileNotFoundErrorsAsJson(Results results) {
		JsonArray fileNotFoundErrors = new JsonArray();
		Gson gson =  new Gson();
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			if(type.contains("FileNotFound")) {
				fileNotFoundErrors.add(gson.toJsonTree(results.getErrors().get(i)));
			}
		}
		return fileNotFoundErrors;
	}

	private static JsonArray getWarningsAsJson(Results results) {
		JsonArray warnings = new JsonArray();
		Gson gson =  new Gson();
		for(int i=0;i<results.getWarnings().size();i++) {
			warnings.add(gson.toJsonTree(results.getWarnings().get(i)));
		}
		return warnings;
	}

	public static void handleArgs(String args[]) {
		for(int i=0;i<args.length;i++) {
			if(args[i].contains("=")) {
				String flag = args[i].split("=")[0].split("--")[1];
				String flagValue = args[i].split("=")[1];
				if(flag.equals("source")) {
					source = flagValue;
				} else if(flag.equals("outputFormat")) {
					outputFormat = flagValue;
				} else if(flag.equals("verbosity")) {
					verbosityLevel = flagValue;
				} else if(flag.equals("recommendations")) {
					recommendationSetting = flagValue;
				} else if(flag.equals("rules")) {
					rulesList = flagValue;
				} else {
					System.out.println("Unknown options, please use --help to get a list of possible options");
				}
			} else {
				if(args[i].contains("--") && args[i].split("--")[1].equals("help")) {
					System.out.println("Help Menu");
					System.out.println(String.format("%-20s%-20s","--help","shows possible flags that can be used"));
					System.out.println(String.format("%-20s%-20s","--source","pass the path of the root directory of the code to be analyzed with this flag, e.g., --sourceFile=/User/home/testingdata/"));
					System.out.println(String.format("%-20s%-20s","--outputFormat","supported formats are json or text, default value is text"));
					System.out.println(String.format("%-20s%-20s","--verbosity","low,medium or high, default value is medium"));
					System.out.println(String.format("%-20s%-20s","--recommendations","get suggestions to fix the defects, possible options are yes or no, default value is no"));
					System.out.println(String.format("%-20s%-20s","--rules","comma separated list of rules to check. Possible options are ParseError, ReferenceError, FileNotFound, Warnings or all. Default value is all."));
					System.out.println(String.format("%-20s%-20s","--outputFileName","File name to save the output to. Default value is output."));
				} else {
					System.out.println("Unknown options, please use --help to get a list of possible options");
				}
			}
		}
	}

	public static String getSource() {
		return source;
	}

	public static String getOutputFormat() {
		return outputFormat;
	}

	public static String getVerbosityLevel() {
		return verbosityLevel;
	}

	public static String getRecommendationSetting() {
		return recommendationSetting;
	}
}