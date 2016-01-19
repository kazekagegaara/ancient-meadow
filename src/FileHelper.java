import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FileHelper {

	public List<File> getHTMLFiles(String directoryName) {
		File directory = new File(directoryName);
        List<File> resultList = new ArrayList<File>();

        File[] fList = directory.listFiles();   
        for (File file : fList) {
            if (file.isFile()) {                
                String filename = file.getName();
                String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
                if(extension.equals("html")) {                
                	resultList.add(file);
                }
            } else if (file.isDirectory()) {
                resultList.addAll(getHTMLFiles(file.getAbsolutePath()));
            }
        }        
        return resultList;
	}

}