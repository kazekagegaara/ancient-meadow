/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer;

import edu.asu.poly.se.staticanalyzer.results.Results;

public class Output {

	private static String source = null;
	private static String outputFormat = "text";
	private static String verbosityLevel = "medium";
	private static String recommendationSetting = "off";
	private static String rulesList = "all";

	public static void listResults(Results results) {
		if(!rulesList.equals("all")) {
			String rules[] = rulesList.split(",");
			System.out.println(String.format("%-40s%-90s%-90s%-5s%-5s","Type","Description","Source File","Location(row number)","Location(column number)"));
			boolean showWarningsCount = false;
			boolean showErrorsCount = false;
			int errorsCount = 0;
			int warningsCount = 0;
			for(int i=0;i<rules.length;i++) {
				if(rules[i].equals("ParseError")) {
					errorsCount += listParseErrors(results);
					showErrorsCount = true;
				} else if(rules[i].equals("ReferenceError")) {
					errorsCount += listReferenceErrors(results);
					showErrorsCount = true;
				} else if(rules[i].equals("FileNotFound")) {
					errorsCount += listFileNotFoundErrors(results);
					showErrorsCount = true;
				} else if(rules[i].equals("Warnings")) {
					warningsCount += listWarnings(results);
					showWarningsCount = true;
				}
			}
			if(showErrorsCount) {
				System.out.println("Total " + errorsCount + " errors!");
			}
			if(showWarningsCount) {
				System.out.println("Total " + warningsCount + " warnings!");
			}
		} else if(rulesList.equals("all")){
			if(!(verbosityLevel.equals("high") || verbosityLevel.equals("medium") || verbosityLevel.equals("low"))) {
				System.out.println("Unsupported value for verbosity, please use --help for supported values");
			} else {
				if(verbosityLevel.equals("low")) {
					System.out.println("Total " + results.getErrors().size() + " errors!");
					System.out.println("Total " + results.getWarnings().size() + " warnings!");
				} else {
					System.out.println(String.format("%-40s%-90s%-90s%-5s%-5s","Type","Description","Source File","Location(row number)","Location(column number)"));
					listFileNotFoundErrors(results);
					listParseErrors(results);
					listReferenceErrors(results);
					if(verbosityLevel.equals("high")) {
						listWarnings(results);
					}
					System.out.println("Total " + results.getErrors().size() + " errors!");
					System.out.println("Total " + results.getWarnings().size() + " warnings!");
				}
			}
		} else {
			System.out.println("Unsupported rule format. Please use --help to see supported values");
		}

	}

	private static int listParseErrors(Results results) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("ParseError")) {
				System.out.println(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				count++;
			}
		}
		return count;
	}

	private static int listReferenceErrors(Results results) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("ReferenceError")) {
				System.out.println(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				count++;
			}
		}
		return count;
	}

	private static int listFileNotFoundErrors(Results results) {
		int count = 0;
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("FileNotFound")) {
				System.out.println(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
				count++;
			}
		}
		return count;
	}

	private static int listWarnings(Results results) {
		int count = 0;
		for(int i=0;i<results.getWarnings().size();i++){
			String type = results.getWarnings().get(i).getWarningType();
			String desc = results.getWarnings().get(i).getDesc();
			String fileName = results.getWarnings().get(i).getFileName();
			int rowNumber = results.getWarnings().get(i).getRowNumber();
			int columnNumber = results.getWarnings().get(i).getColumnNumber();
			System.out.println(String.format("%-40s%-90s%-90s%-5d%-5d",type,desc,fileName,rowNumber,columnNumber));
			count++;
		}
		return count;
	}

	public static void listResultsAsJson(Results results) {
		System.out.println("JSON Result");
	}

	public static void listResultsAsXML(Results results) {
		System.out.println("XML Result");
	}

	public static String handleArgs(String args[]) {
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
				if(args[i].split("--")[1].equals("help")) {
					System.out.println("Help Menu");
					System.out.println(String.format("%-20s%-20s","--help","shows possible flags that can be used"));
					System.out.println(String.format("%-20s%-20s","--source","pass the path of the root directory of the code to be analyzed with this flag, e.g., --sourceFile=/User/home/testingdata/"));
					System.out.println(String.format("%-20s%-20s","--outputFormat","supported formats are json,xml or text, default value is text"));
					System.out.println(String.format("%-20s%-20s","--verbosity","low,medium or high, default value is medium"));
					System.out.println(String.format("%-20s%-20s","--recommendations","get suggestions to fix the defects, possible options are yes or no, default value is no"));
					System.out.println(String.format("%-20s%-20s","--rules","comma separated list of rules to check. Possible options are ParseError, ReferenceError, FileNotFound, Warnings or all. Default value is all."));
				} else {
					System.out.println("Unknown options, please use --help to get a list of possible options");
				}
			}
		}
		return null;
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
