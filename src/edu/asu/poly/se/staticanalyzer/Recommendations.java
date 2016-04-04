/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import edu.asu.poly.se.staticanalyzer.results.Results;

public class Recommendations {

	public static String getNearestMatchingString(String str, List<String> list) {
		String nearestMatch = "";
		int highestFuzzyDistance = -1;

		for(int i=0;i<list.size();i++) {
			int newFuzzyDistance = StringUtils.getFuzzyDistance(str,list.get(i),Locale.ENGLISH);
			if(newFuzzyDistance > 0 && newFuzzyDistance > highestFuzzyDistance) {
				highestFuzzyDistance = newFuzzyDistance;
				nearestMatch = list.get(i);
			}
		}
		
		return nearestMatch;
	}
	
	public static void generateRecommendations(Results results, List<String> completeCSSClassList, List<String> completeIDList) {
		for(int i=0;i<results.getErrors().size();i++) {
			String type = results.getErrors().get(i).getErrorType();
			String desc = results.getErrors().get(i).getDesc();
			if(type.contains("ReferenceError")) {
				if(desc.contains("CSS")) {
					String fix = getNearestMatchingString(desc.split("-")[1].substring(1),completeCSSClassList);
					results.getErrors().get(i).setFixRecommendation("Did you mean " + fix + " ?");
				} else if(desc.contains("ID")) {
					String fix = getNearestMatchingString(desc.split("-")[1].substring(1),completeIDList);
					results.getErrors().get(i).setFixRecommendation("Did you mean " + fix + " ?");
				} else {
					results.getErrors().get(i).setFixRecommendation("Check method name and number of parameters");
				}
			} else if(type.contains("FileNotFound")) {				
				results.getErrors().get(i).setFixRecommendation("Check if the file exists and if the path is correct");
			} else {
				results.getErrors().get(i).setFixRecommendation("Check for syntax errors!");
			}
		}
		for(int i=0;i<results.getWarnings().size();i++) {
			results.getWarnings().get(i).setFixRecommendation("Remove the unused attribute.");
		}
	}
}