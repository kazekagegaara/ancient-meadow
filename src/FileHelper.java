import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileHelper {

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

    public List<File> getFilesToProcess(String directoryName, List<String> filenames,Results results) {
        List<File> filesToProcess = new ArrayList<File>();
        filenames.forEach(name -> {
            if(!name.contains("http://") && !name.contains("https://")) {
                String filepath = directoryName+"/"+name;
                filepath = Paths.get(filepath).toString();
                File f = new File(filepath);
                if(f.exists() && !f.isDirectory()) {
                    filesToProcess.add(f);
                } else {
                    results.setErrors("FileNotFound ::: " + f.toString() + " file doesn't exist!");
                }
            }            
        });        
        
        return filesToProcess;
    }

    public int getLocationInFile(String str,String loc) {
        int lineNumber = -1;
        try {
            FileReader fr = new FileReader(loc);
            LineNumberReader lnr = new LineNumberReader(fr); 
            String line;
            int i;           
            while((line=lnr.readLine()) != null) {
                i = lnr.getLineNumber();
                if(exactMatch(line,str)) {
                    lineNumber = i;
                    break;
                }
            }
            fr.close();
            lnr.close();
        } catch(Exception e) {            
            // this should never happen
        }
        return lineNumber;
    }

    private static boolean exactMatch(String source, String subItem){
         String pattern = "\\b"+subItem+"\\b";
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