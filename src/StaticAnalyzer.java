import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticAnalyzer {

	private static Results results = new Results();

	public static void main(String args[]) throws Exception {
		List<HTMLFile> htmlFiles = new ArrayList<HTMLFile>();		
		
		FileHelper fileHelper = new FileHelper();

		// get all html files in the provided parent directory
		fileHelper.getHTMLFiles(args[0]).forEach(file -> {
			htmlFiles.add(new HTMLFile(file));
		});

		processHTMLFiles(htmlFiles, args[0]);

		// // get all css files in the provided parent directory
		// fileHelper.getCSSFiles(args[0]).forEach(file -> {
		// 	cssFiles.add(new CSSFile(file));
		// });

		// // get all js files in the provided parent directory
		// fileHelper.getJSFiles(args[0]).forEach(file -> {
		// 	jsFiles.add(new JSFile(file));
		// });
		listResults();

	}

	private static void processHTMLFiles(List<HTMLFile> htmlFiles, String directoryName) {
		List<File> filesToProcess = new ArrayList<File>();
		List<CSSFile> cssFiles = new ArrayList<CSSFile>();
		List<JSFile> jsFiles = new ArrayList<JSFile>();
		FileHelper fileHelper = new FileHelper();

		htmlFiles.forEach(file -> {
			try {
				JsoupHelper jsouphelper = new JsoupHelper(file);
				jsouphelper.getAllElementIds();
				jsouphelper.getAllElementClasses();
				jsouphelper.getStyleSheetLinks();
				jsouphelper.getMediaLinks();
				jsouphelper.getScriptLinks();
				jsouphelper.getEventHandlers();
			} catch(IOException e) {
				results.setErrors("HTMLParseError ::: " + e.getMessage().toString());
			}
			
			System.out.println(file.getIds());
			System.out.println(file.getClasses());			
			System.out.println(file.getEventHandlers());
			
			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getStyleSheetLinks(),results));
			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getMediaLinks(),results));
			filesToProcess.addAll(fileHelper.getFilesToProcess(directoryName, file.getScriptLinks(),results));

			fileHelper.getCSSFiles(filesToProcess).forEach(dependentFile -> {
				cssFiles.add(new CSSFile(dependentFile));
			});

			fileHelper.getJSFiles(filesToProcess).forEach(dependentFile -> {
				jsFiles.add(new JSFile(dependentFile));
			});

			processCSSFiles(file.getFile().toString(),cssFiles,file.getIds(),file.getClasses(),null);
			processJSFiles(file.getFile().toString(),jsFiles,file.getIds(),null,file.getEventHandlers());
		});				

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
				results.setErrors("CSSParseError ::: " + e.getMessage().toString());
			}
			if(!file.getSyntaxWarnings().isEmpty()) {
				file.getSyntaxWarnings().forEach(warning -> {
					results.setWarnings("CSSParseWarning ::: " + warning);
				});
			}
			if(!file.getSyntaxErrors().isEmpty()) {
				file.getSyntaxErrors().forEach(error -> {
					results.setErrors("CSSParseError ::: " + error);
				});
			}

			List<String> retrievedClassList = file.getClasses();
			if(retrievedClassList.size() > 0) {
				for(int i=0;i<retrievedClassList.size();i++) {
					if(originalClassList.contains(retrievedClassList.get(i))) {
						originalClassList.remove(retrievedClassList.get(i));
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

		if(originalClassList.size() > 0) {
			System.out.println(originalClassList.toString());
			for(int i=0;i<originalClassList.size();i++) {
				results.setErrors("ReferenceError ::: " + "CSS class not found - " + originalClassList.get(i) + " at line number " + fileHelper.getLocationInFile(originalClassList.get(i),src) + " in file " + src);
			}
		}

		if(nonExistentIdRef.size() > 0) {
			for(int i=0;i<nonExistentIdRef.size();i++) {
				results.setErrors("ReferenceError ::: " + "ID not found - " + nonExistentIdRef.get(i) + " at line number " + fileHelper.getLocationInFile(originalClassList.get(i),src) + " in file " + nonExistentIdRefSrc.get(i));
			}	
		}
	}

	private static void processJSFiles(String src,List<JSFile> jsFiles,List<String> idList,List<String> classList,List<String> functonList) {
		FileHelper fileHelper = new FileHelper();

		List<String> unusedIdentifier = new ArrayList<String>();
		List<Integer> unusedIdentifierLocations = new ArrayList<Integer>();
		List<String> unusedIdentifierSrcFile = new ArrayList<String>();

		List<String> functionNotFoundList = new ArrayList<String>();
		List<Integer> functionNotFoundLocationList = new ArrayList<Integer>();
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
			    }				    
			    originalFuncParamList.add(paramcount);
			});
		}


		jsFiles.forEach(file -> {
			try {
				JSParser jsParser = new JSParser(file);
				jsParser.parseJS();	
			} catch(Exception e) {				
				results.setErrors("JSParseError ::: " + e.getMessage().toString());
			}

			if(idList != null) {
				List<String> retrievedIds = file.getIds();
				List<Integer> retrievedIdLocations = file.getIdLocation();
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
				List<Integer> retrievedClassLocations = file.getClassesLocation();
				for(int i=0;i<retrievedClasses.size();i++) {
					if(!idList.contains(retrievedClasses.get(i))) {
						unusedIdentifier.add(retrievedClasses.get(i));
						unusedIdentifierLocations.add(retrievedClassLocations.get(i));
						unusedIdentifierSrcFile.add(file.getFile().toString());
					}
				}
			}
			
			if(functonList != null) {
				List<String> retrievedFunctions = file.getFunctions();
				List<Integer> retrievedFunctionsLocations = file.getFunctionLocation();
				List<Integer> retrievedFunctionsParams = file.getFunctionParams();

				for(int i=0;i<retrievedFunctions.size();i++) {
					if(originalFuncList.contains(retrievedFunctions.get(i))) {						
						int index = originalFuncList.indexOf(retrievedFunctions.get(i));
						if(originalFuncParamList.get(index) == retrievedFunctionsParams.get(i)) {
							originalFuncList.remove(index);
							originalFuncParamList.remove(index);
						}
					}
				}
			}
		});
		
		if(originalFuncList.size() > 0) {
			for(int i=0;i<originalFuncList.size();i++) {
				functionNotFoundList.add(originalFuncList.get(i));
				functionNotFoundSrcFile.add(src);
				functionNotFoundLocationList.add(fileHelper.getLocationInFile(originalFuncList.get(i),src));
			}
		}

		if(unusedIdentifier.size() > 0) {
			for(int i=0;i<unusedIdentifier.size();i++) {
				results.setErrors("ReferenceError ::: " + "ID not found - " + unusedIdentifier.get(i) + " at line number " + unusedIdentifierLocations.get(i) + " in file " + unusedIdentifierSrcFile.get(i));
			}	
		}
		
		if(functionNotFoundList.size() > 0) {
			for(int i=0;i<functionNotFoundList.size();i++) {
				results.setErrors("ReferenceError ::: " + "Function not defined - " + functionNotFoundList.get(i) + " at line number " + functionNotFoundLocationList.get(i) + " in file " + functionNotFoundSrcFile.get(i));
			}	
		}
		
	}

	private static void listResults() {
		results.getErrors().forEach(result -> {
			String resultType = result.split(":::")[0];
			String resultContent = result.split(":::")[1];
			System.out.println(String.format("%-20s ::: %s",resultType,resultContent));
		});
		results.getWarnings().forEach(result -> {
			String resultType = result.split(":::")[0];
			String resultContent = result.split(":::")[1];
			System.out.println(String.format("%-20s ::: %s",resultType,resultContent));
		});
		System.out.println("Total " + results.getErrors().size() + " errors!");
		System.out.println("Total " + results.getWarnings().size() + " warnings!");
	}
}