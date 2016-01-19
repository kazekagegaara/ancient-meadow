import java.io.File;

class HTMLFile {

	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public HTMLFile(File file){
		this.file = file;
	}
}