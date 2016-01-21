import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalyzer {

	public static void main(String args[]) {
		List<HTMLFile> htmlFiles = new ArrayList<HTMLFile>();
		FileHelper fileHelper = new FileHelper();
		fileHelper.getHTMLFiles("D:\\ASU\\Thesis\\TestingData").forEach(file -> htmlFiles.add(new HTMLFile(file)));

		
		htmlFiles.forEach(file -> {
			try {
				JsoupHelper jsouphelper = new JsoupHelper(file.getFile());
				file.setIds(jsouphelper.getAllElementIds());
				file.setClasses(jsouphelper.getAllElementClasses());
				file.setStyleSheetLinks(jsouphelper.getStyleSheetLinks());
				file.setMediaLinks(jsouphelper.getMediaLinks());
				file.setScriptLinks(jsouphelper.getScriptLinks());
				file.setEventHandlers(jsouphelper.getEventHandlers());
			} catch(IOException e) {
				System.out.println("Unable to read file");
				e.printStackTrace(System.out);		
			}
		});
		
	}

}