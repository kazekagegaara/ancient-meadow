/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.parsers;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.asu.poly.se.staticanalyzer.results.Error;
import edu.asu.poly.se.staticanalyzer.results.Location;
import edu.asu.poly.se.staticanalyzer.results.Results;


public class FileHelper {

	public List<File> getHTMLFiles(String directoryName) {
		return getFiles(directoryName,"html");
	}

	public List<File> getCSSFiles(String directoryName) {
		return getFiles(directoryName,"css");
	}

	public List<File> getCSSFiles(List<File> files) {
		return getFiles(files,"css");
	}

	public List<File> getJSFiles(String directoryName) {
		return getFiles(directoryName,"js");
	}

	public List<File> getJSFiles(List<File> files) {
		return getFiles(files,"js");
	}

	public List<File> getFilesToProcess(String directoryName, String srcFile, List<String> filenames,Results results) {
		List<File> filesToProcess = new ArrayList<File>();
		filenames.forEach(name -> {
			if(!name.contains("http://") && !name.contains("https://")) {
				String filepath = directoryName+"/"+name;
				filepath = Paths.get(filepath).toString();
				File f = new File(filepath);
				if(f.exists() && !f.isDirectory()) {
					filesToProcess.add(f);
				} else {
					Location loc = getLocationInFile(name,srcFile);
					results.setError(new Error("FileNotFound",filepath,srcFile,loc.getRowNumber(),loc.getColumnNumber()));
				}
			}
		});

		return filesToProcess;
	}

	public Location getLocationInFile(String str,String loc) {
		int rowNumber = -1;
		int columnNumber = -1;
		try {
			FileReader fr = new FileReader(loc);
			LineNumberReader lnr = new LineNumberReader(fr);
			String line;
			int i;
			while((line=lnr.readLine()) != null) {
				i = lnr.getLineNumber();
				if(exactMatch(line,str)) {
					rowNumber = i;
					columnNumber = line.indexOf(str) + 1;
					break;
				}
			}
			fr.close();
			lnr.close();
		} catch(Exception e) {
			// this should never happen
		}
		return new Location(rowNumber,columnNumber);
	}

	private static boolean exactMatch(String source, String subItem) {
		String pattern = "\\b.*"+subItem+"\\b";
		Pattern p=Pattern.compile(pattern);
		Matcher m=p.matcher(source);
		return m.find();
	}

	private List<File> getFiles(String directoryName,String fileType) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<File>();

		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				String filename = file.getName();
				String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				if(extension.equals(fileType)) {
					resultList.add(file);
				}
			} else if (file.isDirectory()) {
				resultList.addAll(getFiles(file.getAbsolutePath(),fileType));
			}
		}
		return resultList;
	}

	private List<File> getFiles(List<File> files,String fileType) {
		List<File> resultList = new ArrayList<File>();

		for (File file : files) {
			if (file.isFile()) {
				String filename = file.getName();
				String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				if(extension.equals(fileType)) {
					resultList.add(file);
				}
			}
		}
		return resultList;
	}

}