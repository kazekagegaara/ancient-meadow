
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

class JsoupHelper {

	public List<String> getAllElementIds(File file) throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> ids = new ArrayList<String>();

		Element body = doc.body();		

		body.getAllElements().forEach(child -> {
			if(!child.id().isEmpty()) {
    			ids.add(child.id());
    		}
		});

		System.out.println(ids.toString());
		return ids;
	}

	public List<String> getAllElementClasses(File file) throws IOException{
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> classes = new ArrayList<String>();

		Element body = doc.body();		

		body.getAllElements().forEach(child -> {
			if(!child.classNames().isEmpty()) {
    			classes.addAll(child.classNames());
    		}
		});

		System.out.println(classes.toString());
		return classes;		
	}
}