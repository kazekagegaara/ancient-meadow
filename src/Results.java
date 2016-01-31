import java.util.ArrayList;
import java.util.List;

class Results {

	private List<String> warnings = new ArrayList<String>();
	private List<String> errors = new ArrayList<String>();

	public void setWarnings(String warning) {
		warnings.add(warning);
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setErrors(String error) {
		errors.add(error);
	}

	public List<String> getErrors() {
		return errors;
	}
}