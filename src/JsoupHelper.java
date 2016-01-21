import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class JsoupHelper {

	private File file;

	public JsoupHelper(File file) {
		this.file = file;
	}

	public List<String> getAllElementIds() throws IOException {
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

	public List<String> getAllElementClasses() throws IOException {
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

	public List<String> getStyleSheetLinks() throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> styleSheetLinks = new ArrayList<String>();

        Elements imports = doc.select("link[href]");

        for (Element link : imports) {
            styleSheetLinks.add(link.attr("href"));
        }

        System.out.println(styleSheetLinks.toString());
        return styleSheetLinks;
	}

	public List<String> getMediaLinks() throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> mediaLinks = new ArrayList<String>();

        Elements media = doc.select("[src]");

        for (Element src : media) {
            if (src.tagName().equals("img")) {
				mediaLinks.add(src.attr("src"));
            }
        }

        System.out.println(mediaLinks.toString());
        return mediaLinks;
	}

	public List<String> getScriptLinks() throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> scriptLinks = new ArrayList<String>();

        Elements scripts = doc.select("script[src]");

        for (Element script : scripts) {
            scriptLinks.add(script.attr("src"));
        }

        System.out.println(scriptLinks.toString());
        return scriptLinks;
	}

	public List<String> getEventHandlers() throws IOException { // only handles onclick for now, can be extended
		Document doc = Jsoup.parse(file, "UTF-8", "");
		List<String> eventHandlers = new ArrayList<String>();

		Element body = doc.body();

		body.getAllElements().forEach(child -> {
			if(!child.attr("onclick").isEmpty()) {
				eventHandlers.add(child.attr("onclick"));
			}
		});

		System.out.println(eventHandlers.toString());
		return eventHandlers;
	}

}