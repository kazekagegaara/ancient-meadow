import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalyzer {

	public static void main(String args[]) throws Exception {
		List<HTMLFile> htmlFiles = new ArrayList<HTMLFile>();
		List<CSSFile> cssFiles = new ArrayList<CSSFile>();
		FileHelper fileHelper = new FileHelper();

		fileHelper.getHTMLFiles("D:\\ASU\\Thesis\\TestingData").forEach(file -> {
			htmlFiles.add(new HTMLFile(file));
		});

		fileHelper.getCSSFiles("D:\\ASU\\Thesis\\TestingData").forEach(file -> {
			cssFiles.add(new CSSFile(file));
		});
		
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

		cssFiles.forEach(file -> {
			try {
				CSSParser cssParser = new CSSParser();
				file.setIds(cssParser.getIds(file.getFile()));
				file.setClasses(cssParser.getClasses(file.getFile()));
			} catch(Exception e) {
				System.out.println("Unrecoverable error parsing CSS file");
				e.printStackTrace(System.out);
			}
		});
	}

}