import java.io.File;
import java.util.ArrayList;
import java.util.List;

class JSFile {

	private File file;
	private List<String> ids = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<String> getIds() {
		return this.ids;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public JSFile(File file){
		this.file = file;
	}
}