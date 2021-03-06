/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer;

import edu.asu.poly.se.staticanalyzer.beans.CSSFile;
import edu.asu.poly.se.staticanalyzer.beans.HTMLFile;
import edu.asu.poly.se.staticanalyzer.beans.JSFile;
import edu.asu.poly.se.staticanalyzer.parsers.CSSParser;
import edu.asu.poly.se.staticanalyzer.parsers.FileHelper;
import edu.asu.poly.se.staticanalyzer.parsers.JSParser;
import edu.asu.poly.se.staticanalyzer.parsers.JsoupHelper;
import edu.asu.poly.se.staticanalyzer.results.Error;
import edu.asu.poly.se.staticanalyzer.results.Location;
import edu.asu.poly.se.staticanalyzer.results.Results;
import edu.asu.poly.se.staticanalyzer.results.Warning;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticAnalyzer {

	private static Results results = new Results();
	private static List<String> completeCSSClassList;
	private static List<String> completeIDList;
	private static List<CSSFile> allCSSFiles = new ArrayList<CSSFile>();

	public static void main(String args[]) throws Exception {
		runStaticAnalyzer(args,false);
	}

	public static Results runStaticAnalyzer(String args[], boolean pluginMode) {
		Output.handleArgs(args);

		List<HTMLFile> htmlFiles = new ArrayList<HTMLFile>();
		FileHelper fileHelper = new FileHelper();

		if(Output.getSource() != null) {
			// get all html files in the provided parent directory
			fileHelper.getHTMLFiles(Output.getSource()).forEach(file -> {
				htmlFiles.add(new HTMLFile(file));
			});

			processHTMLFiles(htmlFiles, Output.getSource());

			// // get all css files in the provided parent directory
			// fileHelper.getCSSFiles(args[0]).forEach(file -> {
			// 	cssFiles.add(new CSSFile(file));
			// });

			// // get all js files in the provided parent directory
			// fileHelper.getJSFiles(args[0]).forEach(file -> {
			// 	jsFiles.add(new JSFile(file));
			// });
		} else {
			System.out.println("Please specify source. Use --help for more options.");
		}
		if(pluginMode) {			
			if(Output.getRecommendationSetting().equals("yes")) {
				Recommendations.generateRecommendations(results,completeCSSClassList,completeIDList);
			}
			return results;
		} else {
			if(Output.getOutputFormat().equals("text")) {
				Output.listResults(results);
			} else if(Output.getOutputFormat().equals("json")) {
				Output.listResultsAsJson(results);
			}
			if(Output.getRecommendationSetting().equals("yes")) {
				Recommendations.generateRecommendations(results,completeCSSClassList,completeIDList);
			}
			return null;
		}
	}

	private static void processHTMLFiles(List<HTMLFile> htmlFiles, String directoryName) {

		completeCSSClassList = new ArrayList<String>(); // all classes in CSS
		List<String> completeReferencedCSSClassList = new ArrayList<String>(); // all classes referenced from either HTML or JS

		completeIDList = new ArrayList<String>(); // all ids defined in HTML
		List<String> completeReferencedIDList = new ArrayList<String>(); // all ids referenced in JS or CSS


		htmlFiles.forEach(file -> {
			List<File> filesToProcess = new ArrayList<File>();
			List<CSSFile> cssFiles = new ArrayList<CSSFile>();
			List<JSFile> jsFiles = new ArrayList<JSFile>();
			FileHelper fileHelper = new FileHelper();
			try {
				JsoupHelper jsouphelper = new JsoupHelper(file);
				jsouphelper.getAllElementIds();
				jsouphelper.getAllElementClasses();
				jsouphelper.getStyleSheetLinks();
				jsouphelper.getMediaLinks();
				jsouphelper.getScriptLinks();
				jsouphelper.getEventHandlers();
			} catch(IOException e) {
				results.setError(new Error("HTMLParseError",file.getFile().toString(),file.getFile().toString(),-1,-1));
			}

			// System.out.println(file.getIds());
			// System.out.println(file.getClasses());
			// System.out.println(file.getEventHandlers());

			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getFile().toString(),file.getStyleSheetLinks(),results));
			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getFile().toString(),file.getMediaLinks(),results));
			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getFile().toString(),file.getScriptLinks(),results));

			fileHelper.getCSSFiles(filesToProcess).forEach(dependentFile -> {
				CSSFile cssFile = new CSSFile(dependentFile);
				cssFiles.add(cssFile);
				allCSSFiles.add(cssFile);
			});

			fileHelper.getJSFiles(filesToProcess).forEach(dependentFile -> {
				jsFiles.add(new JSFile(dependentFile));
			});

			processCSSFiles(file.getFile().toString(),cssFiles,file.getIds(),file.getClasses(),null);
			processJSFiles(file.getFile().toString(),jsFiles,file.getIds(),null,file.getEventHandlers());

			List<String> cumulativeCSSClassList = new ArrayList<String>();
			List<String> cumulativeJSClassList = new ArrayList<String>();
			List<String> cumulativeCSSIDList = new ArrayList<String>();
			List<String> cumulativeJSIDList = new ArrayList<String>();

			cssFiles.forEach(cssFile -> {
				cumulativeCSSClassList.addAll(cssFile.getClasses());
				cumulativeCSSIDList.addAll(cssFile.getIds());
			});

			jsFiles.forEach(jsFile -> {
				cumulativeJSClassList.addAll(jsFile.getClasses());
				cumulativeJSIDList.addAll(jsFile.getIds());
			});

			completeReferencedCSSClassList.addAll(file.getClasses());
			completeReferencedCSSClassList.addAll(cumulativeJSClassList);
			completeCSSClassList.addAll(cumulativeCSSClassList);
			processJSFiles(file.getFile().toString(),jsFiles,null,cumulativeCSSClassList,null);

			completeIDList.addAll(file.getIds());
			completeReferencedIDList.addAll(cumulativeCSSIDList);
			completeReferencedIDList.addAll(cumulativeJSIDList);

			// fileHelper.getJSFiles(filesToProcess).forEach(dependentFile -> {
			// 	jsFiles.add(new JSFile(dependentFile));
			// });

		});

		List<String> unusedCSS = new ArrayList<String>();
		unusedCSS.addAll(completeCSSClassList);
		unusedCSS.removeAll(completeReferencedCSSClassList);

		List<String> unusedID = new ArrayList<String>();
		unusedID.addAll(completeIDList);
		unusedID.removeAll(completeReferencedIDList);

		if(unusedCSS.size() > 0) {
			allCSSFiles.forEach(cssfile -> {
				FileHelper fileHelper = new FileHelper();
				String currentSrc = cssfile.getFile().getPath().toString();
				try {
					List<String> fileClasses = cssfile.getClasses();
					for(int i=0;i<unusedCSS.size();i++) {
						if(fileClasses.contains(unusedCSS.get(i))) {
							List<Location> locs = fileHelper.getLocationInFile(unusedCSS.get(i), currentSrc);
							for(int j=0; j<locs.size(); j++) {
								Location loc = locs.get(j);
								results.setWarning(new Warning("UnusedCSSWarning",unusedCSS.get(i),currentSrc,loc.getRowNumber(),loc.getColumnNumber()));
							}															
						}
					}
				} catch (Exception e) {					
				}					
			});
		}

		if(unusedID.size() > 0) {
			htmlFiles.forEach(file -> {
				FileHelper fileHelper = new FileHelper();				
				try {
					JsoupHelper jsouphelper = new JsoupHelper(file);
					jsouphelper.getAllElementIds();					
				} catch(IOException e) {		
				}
				List<String> fileIds = file.getIds();
				String currentSrc = file.getFile().getPath().toString();
				for(int i=0;i<unusedID.size();i++) {
					if(fileIds.contains(unusedID.get(i))) {
						List<Location> locs = fileHelper.getLocationInFile(unusedID.get(i), currentSrc);
						for(int j=0; j<locs.size(); j++) {
							Location loc = locs.get(j);
							results.setWarning(new Warning("UnusedIDWarning",unusedID.get(i),currentSrc,loc.getRowNumber(),loc.getColumnNumber()));
						}
					}					
				}
			});			
		}

	}

	private static void processCSSFiles(String src,List<CSSFile> cssFiles,List<String> idList,List<String> classList,List<String> functonList) {
		List<String> originalClassList = classList;
		List<String> originalIdList = idList;
		List<String> nonExistentIdRef = new ArrayList<String>();
		List<String> nonExistentIdRefSrc = new ArrayList<String>();
		FileHelper fileHelper = new FileHelper();

		cssFiles.forEach(file -> {
			try {
				CSSParser cssParser = new CSSParser(file);
				cssParser.getIds();
				cssParser.getClasses();
			} catch(Exception e) {
				results.setError(new Error("CSSParseError",file.getFile().toString(),file.getFile().toString(),-1,-1));
			}
			if(!file.getSyntaxErrors().isEmpty()) {
				file.getSyntaxErrors().forEach(error -> {
					results.setError(error);
				});
			}

			List<String> retrievedClassList = file.getClasses();			
			if(retrievedClassList.size() > 0) {
				for(int i=0;i<retrievedClassList.size();i++) {					
					if(originalClassList.contains(retrievedClassList.get(i))) {
						originalClassList.removeAll(Collections.singleton(retrievedClassList.get(i)));
					}
				}
			}			

			List<String> retrievedIdList = file.getIds();
			if(retrievedIdList.size() > 0) {
				for(int i=0;i<retrievedIdList.size();i++) {
					if(!originalIdList.contains(retrievedIdList.get(i))) {
						nonExistentIdRef.add(retrievedIdList.get(i));
						nonExistentIdRefSrc.add(file.getFile().toString());
					} else {
						if(nonExistentIdRef.contains(retrievedIdList.get(i))) {
							int indexToRemove = nonExistentIdRef.indexOf(retrievedIdList.get(i));
							nonExistentIdRef.remove(indexToRemove);
							nonExistentIdRefSrc.remove(indexToRemove);
						}
					}
				}
			}
		});

//		cssFiles.forEach(file -> {
//			//			if(originalClassList.size() > 0) {
//			//				originalClassList.removeIf(className -> fileHelper.getLocationInFile(className,file.getFile().toString()).getRowNumber() != -1);
//			//			}
//			for (Iterator<String> iterator = originalClassList.iterator(); iterator.hasNext();) {
//			    String className = iterator.next();
//			    Location loc = fileHelper.getLocationInFile(className,file.getFile().toString());
//			    if (loc.getRowNumber() != -1) {
//			        iterator.remove();
//			    }
//			}
//		});

		if(originalClassList.size() > 0) {
			for(int i=0;i<originalClassList.size();i++) {
				List<Location> locs = fileHelper.getLocationInFile(originalClassList.get(i),src);
				for(int j=0; j<locs.size(); j++) {
					Location loc = locs.get(j);
					results.setError(new Error("ReferenceError(HTMLtoCSS : NonExistentClass)","CSS class not found - " + originalClassList.get(i),src,loc.getRowNumber(),loc.getColumnNumber()));
				}
			}
		}

		if(nonExistentIdRef.size() > 0) {
			for(int i=0;i<nonExistentIdRef.size();i++) {
				List<Location> locs = fileHelper.getLocationInFile(nonExistentIdRef.get(i),nonExistentIdRefSrc.get(i));
				for(int j=0; j<locs.size(); j++) {
					Location loc = locs.get(j);
					results.setError(new Error("ReferenceError(CSStoHTML : NonExistentID)","ID not found - " + nonExistentIdRef.get(i),nonExistentIdRefSrc.get(i),loc.getRowNumber(),loc.getColumnNumber()));
				}
			}
		}
	}

	private static void processJSFiles(String src,List<JSFile> jsFiles,List<String> idList,List<String> classList,List<String> functonList) {
		FileHelper fileHelper = new FileHelper();

		List<String> unusedIdentifier = new ArrayList<String>();
		List<Location> unusedIdentifierLocations = new ArrayList<Location>();
		List<String> unusedIdentifierSrcFile = new ArrayList<String>();

		List<String> functionNotFoundList = new ArrayList<String>();
		List<Location> functionNotFoundLocationList = new ArrayList<Location>();
		List<String> functionNotFoundSrcFile = new ArrayList<String>();

		List<String> originalFuncList = new ArrayList<String>();
		List<Integer> originalFuncParamList = new ArrayList<Integer>();

		if(functonList != null) {
			functonList.forEach(functionSignature -> {
				originalFuncList.add(functionSignature.split("\\(")[0]);
				Pattern p = Pattern.compile(",");
				Matcher m = p.matcher(functionSignature);
				int paramcount = 0;
				while (m.find()) {
					paramcount +=1;
				}
				if(paramcount != 0) {
					paramcount++;
				} else {
					if(!(functionSignature.indexOf("()") > -1)) {
						paramcount++;
					}
				}
				originalFuncParamList.add(paramcount);
			});
		}

		jsFiles.forEach(file -> {

			if(classList == null) {
				try {
					JSParser jsParser = new JSParser(file);
					jsParser.parseJS();
				} catch(Exception e) {
					results.setError(new Error("JSParseError",file.getFile().toString(),file.getFile().toString(),-1,-1));
				}
			}

			if(idList != null) {
				List<String> retrievedIds = file.getIds();
				List<Location> retrievedIdLocations = file.getIdLocation();
				for(int i=0;i<retrievedIds.size();i++) {
					if(!idList.contains(retrievedIds.get(i))) {
						unusedIdentifier.add(retrievedIds.get(i));
						unusedIdentifierLocations.add(retrievedIdLocations.get(i));
						unusedIdentifierSrcFile.add(file.getFile().toString());
					}
				}
			}

			if(classList != null) {
				List<String> retrievedClasses = file.getClasses();
				List<Location> retrievedClassLocations = file.getClassesLocation();
				for(int i=0;i<retrievedClasses.size();i++) {
					if(!classList.contains(retrievedClasses.get(i))) {
						unusedIdentifier.add(retrievedClasses.get(i));
						unusedIdentifierLocations.add(retrievedClassLocations.get(i));
						unusedIdentifierSrcFile.add(file.getFile().toString());
					}
				}
			}

			if(functonList != null) {
				List<String> retrievedFunctions = file.getFunctions();
				List<Integer> retrievedFunctionsParams = file.getFunctionParams();
				
				for(int i=0;i<retrievedFunctions.size();i++) {
					while(originalFuncList.contains(retrievedFunctions.get(i))) {						
						int index = originalFuncList.indexOf(retrievedFunctions.get(i));
						if(originalFuncParamList.get(index) == retrievedFunctionsParams.get(i)) {							
							originalFuncList.remove(index);
							originalFuncParamList.remove(index);
						}
					}
				}
			}
		});

		Set<String> depdupeFuncList = new LinkedHashSet<>(originalFuncList);
		originalFuncList.clear();
		originalFuncList.addAll(depdupeFuncList);
		
		if(originalFuncList.size() > 0) {			
			for(int i=0;i<originalFuncList.size();i++) {
				List<Location> locs = fileHelper.getLocationInFile(originalFuncList.get(i),src);
				for(int j=0; j<locs.size(); j++) {					
					functionNotFoundList.add(originalFuncList.get(i));
					functionNotFoundSrcFile.add(src);
					functionNotFoundLocationList.add(locs.get(j));
				}			
			}
		}

		if(unusedIdentifier.size() > 0) {
			if(idList != null) {
				for(int i=0;i<unusedIdentifier.size();i++) {
					Location loc = unusedIdentifierLocations.get(i);
					results.setError(new Error("ReferenceError(JStoHTML : NonExistentID)","ID not found - " + unusedIdentifier.get(i),unusedIdentifierSrcFile.get(i),loc.getRowNumber(),loc.getColumnNumber()));
				}
			} else if(classList != null) {
				for(int i=0;i<unusedIdentifier.size();i++) {
					Location loc = unusedIdentifierLocations.get(i);
					results.setError(new Error("ReferenceError(JStoCSS : NonExistentClass)","CSS class not found - " + unusedIdentifier.get(i),unusedIdentifierSrcFile.get(i),loc.getRowNumber(),loc.getColumnNumber()));
				}
			}
		}

		if(functionNotFoundList.size() > 0) {
			for(int i=0;i<functionNotFoundList.size();i++) {
				Location loc = functionNotFoundLocationList.get(i);
				results.setError(new Error("ReferenceError(HTMLtoJS : NonExistentFunction)","Function not found - " + functionNotFoundList.get(i),functionNotFoundSrcFile.get(i),loc.getRowNumber(),loc.getColumnNumber()));
			}
		}

	}

}