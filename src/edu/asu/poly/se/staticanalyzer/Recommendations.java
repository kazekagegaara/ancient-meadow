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
			String fileName = results.getErrors().get(i).getFileName();
			int rowNumber = results.getErrors().get(i).getRowNumber();
			int columnNumber = results.getErrors().get(i).getColumnNumber();
			if(type.contains("ReferenceError")) {
				System.out.println("Error is " + type + " " + desc + " " + fileName + " " + rowNumber + " " + columnNumber);
				if(desc.contains("CSS")) {
					String fix = getNearestMatchingString(desc.split("-")[1].substring(1),completeCSSClassList);
					System.out.println("Did you mean " + fix + " ?");
				} else if(desc.contains("ID")) {
					String fix = getNearestMatchingString(desc.split("-")[1].substring(1),completeIDList);
					System.out.println("Did you mean " + fix + " ?");
				}
			}
		}
	}
}