import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalyzer {

	public static void main(String args[]) {
		List<HTMLFile> htmlFiles = new ArrayList<HTMLFile>();
		FileHelper fileHelper = new FileHelper();
		JsoupHelper jsouphelper = new JsoupHelper();
		
		fileHelper.getHTMLFiles("D:\\ASU\\Thesis\\TestingData").forEach(file -> htmlFiles.add(new HTMLFile(file)));

		
		htmlFiles.forEach(file -> {
			try {
				jsouphelper.getAllElementIds(file.getFile()); // gets all IDs for a given HTML File, set it in HTMLFile class
				jsouphelper.getAllElementClasses(file.getFile()); // gets all Classes for a given HTML File, set it in HTMLFile class
			} catch(IOException e) {
				System.out.println("Unable to read file");
				e.printStackTrace(System.out);		
			}
		});
		
	}

}