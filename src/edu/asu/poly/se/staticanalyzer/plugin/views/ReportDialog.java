package edu.asu.poly.se.staticanalyzer.plugin.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReportDialog extends TitleAreaDialog {

	private Text howWasDefectFound;
	private Text howToReproduceDefect;
	private Text lineNumber;
	private Text fileName;
	private Text description;	

	private String howWasDefectFoundString;
	private String howToReproduceDefectString;
	private String lineNumberString;
	private String fileNameString;
	private String descriptionString;

	public ReportDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Report Defect");
		setMessage("Please provide the following data to the best of your knowledge", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createHowDefectWasFound(container);
		createHowToReproduce(container);
		createLineNumber(container);
		createFileName(container);
		createDescription(container);		

		return area;
	}	

	private void createHowDefectWasFound(Composite container) {
		Label howDefectWasFoundLbl = new Label(container, SWT.NONE);
		howDefectWasFoundLbl.setText("How did you find the defect?");

		GridData dataHowDefectWasFound = new GridData();
		dataHowDefectWasFound.grabExcessHorizontalSpace = true;
		dataHowDefectWasFound.horizontalAlignment = GridData.FILL;

		howWasDefectFound = new Text(container, SWT.BORDER);
		howWasDefectFound.setLayoutData(dataHowDefectWasFound);
	}

	private void createHowToReproduce(Composite container) {
		Label howToReproduceLbl = new Label(container, SWT.NONE);
		howToReproduceLbl.setText("How can it be reproduced by another developer?");

		GridData dataHowToReproduce = new GridData();
		dataHowToReproduce.grabExcessHorizontalSpace = true;
		dataHowToReproduce.horizontalAlignment = GridData.FILL;
		howToReproduceDefect = new Text(container, SWT.BORDER);
		howToReproduceDefect.setLayoutData(dataHowToReproduce);
	}
	
	private void createLineNumber(Composite container) {		
		Label lineNumberLbl = new Label(container, SWT.NONE);
		lineNumberLbl.setText("What line number is the defect at?");

		GridData dataLineNumber = new GridData();
		dataLineNumber.grabExcessHorizontalSpace = true;
		dataLineNumber.horizontalAlignment = GridData.FILL;

		lineNumber = new Text(container, SWT.BORDER);
		lineNumber.setLayoutData(dataLineNumber);
	}

	private void createFileName(Composite container) {
		Label fileNameLbl = new Label(container, SWT.NONE);
		fileNameLbl.setText("What file is the defect in?");

		GridData dataFileName = new GridData();
		dataFileName.grabExcessHorizontalSpace = true;
		dataFileName.horizontalAlignment = GridData.FILL;

		fileName = new Text(container, SWT.BORDER);
		fileName.setLayoutData(dataFileName);
	}

	private void createDescription(Composite container) {
		Label descriptionLbl = new Label(container, SWT.NONE);
		descriptionLbl.setText("Describe the defect in 1-2 lines");

		GridData dataDescription = new GridData();
		dataDescription.grabExcessHorizontalSpace = true;
		dataDescription.horizontalAlignment = GridData.FILL;

		description = new Text(container, SWT.BORDER);
		description.setLayoutData(dataDescription);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		howWasDefectFoundString = howWasDefectFound.getText();
		howToReproduceDefectString = howToReproduceDefect.getText();
		lineNumberString = lineNumber.getText();
		fileNameString = fileName.getText();
		descriptionString = description.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getHowWasDefectFound() {
		return howWasDefectFoundString;
	}

	public String getHowToReproduceDefect() {
		return howToReproduceDefectString;
	}
	
	public String getLineNumber() {
		return lineNumberString;
	}
	
	public String getFileName() {
		return fileNameString;
	}
	
	public String getDescription() {
		return descriptionString;
	}
}