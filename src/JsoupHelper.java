import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class JsoupHelper {

	private HTMLFile src;
	private Document doc;

	public JsoupHelper(HTMLFile file) throws IOException {
		this.src = file;
		doc = Jsoup.parse(file.getFile(), "UTF-8", "");
	}

	public void getAllElementIds() {
		List<String> ids = new ArrayList<String>();

		Element body = doc.body();		

		body.getAllElements().forEach(child -> {
			if(!child.id().isEmpty()) {
    			ids.add(child.id());
    		}
		});

		//System.out.println(ids.toString());
		src.setIds(ids);
	}

	public void getAllElementClasses() {
		List<String> classes = new ArrayList<String>();

		Element body = doc.body();		

		body.getAllElements().forEach(child -> {
			if(!child.classNames().isEmpty()) {
    			classes.addAll(child.classNames());
    		}
		});

		//System.out.println(classes.toString());
		src.setClasses(classes);
	}

	public void getStyleSheetLinks() {
		List<String> styleSheetLinks = new ArrayList<String>();

        Elements imports = doc.select("link[href]");

        for (Element link : imports) {
            styleSheetLinks.add(link.attr("href"));
        }

        //System.out.println(styleSheetLinks.toString());
        src.setStyleSheetLinks(styleSheetLinks);
	}

	public void getMediaLinks() {
		List<String> mediaLinks = new ArrayList<String>();

        Elements media = doc.select("[src]");

        for (Element src : media) {
            if (src.tagName().equals("img")) {
				mediaLinks.add(src.attr("src"));
            }
        }

        //System.out.println(mediaLinks.toString());
        src.setMediaLinks(mediaLinks);
	}

	public void getScriptLinks() {
		List<String> scriptLinks = new ArrayList<String>();

        Elements scripts = doc.select("script[src]");

        for (Element script : scripts) {
            scriptLinks.add(script.attr("src"));
        }

        //System.out.println(scriptLinks.toString());
        src.setScriptLinks(scriptLinks);
	}

	public void getEventHandlers() { // only handles onclick for now, can be extended
		List<String> eventHandlers = new ArrayList<String>();

		Element body = doc.body();

		body.getAllElements().forEach(child -> {
			if(!child.attr("onclick").isEmpty()) {
				eventHandlers.add(child.attr("onclick"));
			}
		});

		//System.out.println(eventHandlers.toString());
		src.setEventHandlers(eventHandlers);
	}

}