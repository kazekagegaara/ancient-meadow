/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.plugin.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.asu.poly.se.staticanalyzer.StaticAnalyzer;
import edu.asu.poly.se.staticanalyzer.results.Results;
import edu.asu.poly.se.staticanalyzer.results.Error;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.*;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PluginView extends ViewPart {

	public static final String ID = "edu.asu.poly.se.staticanalyzer.plugin.views.PluginView";

	private TableViewer viewer;	
	private Action doubleClickAction;
	private Results results = new Results();
	private boolean useRecommendations = false;
	private boolean useDevMode = false;
	private boolean showWarning = false;
	private boolean workspaceListenerInit = false;
	private final static Logger LOGGER = Logger.getLogger(PluginView.class.getName());
	private static FileHandler fileTxt;
	private static SimpleFormatter formatterTxt;
	private static List<Integer> instantiatedEditors = new ArrayList<Integer>();


	public PluginView() {
		LOGGER.setLevel(Level.INFO);
		try {
			fileTxt = new FileHandler("Logging.txt");
			formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			LOGGER.addHandler(fileTxt);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public void createPartControl(Composite parent) {		
		GridLayout outerLayout = new GridLayout(1, true);
		parent.setLayout(outerLayout);

		Button runBtn = new Button(parent, SWT.PUSH);
		runBtn.setText("Run");
		runBtn.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IEditorPart  editorPart = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
				if(editorPart  != null)
				{
					IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
					IFile file = input.getFile();
					IProject activeProject = file.getProject();
					System.out.println(activeProject.getRawLocation().toOSString());
					String[] args = new String[2];
					args[0] = "--source="+activeProject.getRawLocation().toOSString();
					args[1] = "--recommendations=yes";
					results.getErrors().clear();
					results.getWarnings().clear();
					results = StaticAnalyzer.runStaticAnalyzer(args, true);
					updateViewer();
				}
				//				String path = getCurrentFilePath();				
				//				if(!path.equals("")){
				//					path = new File(path).getParent().toString();
				//					String[] args = new String[2];
				//					args[0] = "--source="+path;
				//					args[1] = "--recommendations=yes";
				//					results.getErrors().clear();
				//					results.getWarnings().clear();
				//					results = StaticAnalyzer.runStaticAnalyzer(args, true);
				//					updateViewer();
				//				}
			}
		});
		runBtn.setLayoutData(new GridData());

		Button reportBtn = new Button(parent, SWT.PUSH);
		reportBtn.setText("Report");
		reportBtn.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ReportDialog dialog = new ReportDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				dialog.create();
				if (dialog.open() == Window.OK) {
					LOGGER.info("------------------DEFECT REPORT------------------");
					LOGGER.info(dialog.getUid());
					LOGGER.info(dialog.getHowWasDefectFound());
					LOGGER.info(dialog.getHowToReproduceDefect());
					LOGGER.info(dialog.getLineNumber());
					LOGGER.info(dialog.getFileName());
					LOGGER.info(dialog.getDescription());
					LOGGER.info("------------------DEFECT REPORT------------------");
				} 

			}
		});
		reportBtn.setLayoutData(new GridData());

		Button recommendationCheck = new Button(parent, SWT.CHECK);
		recommendationCheck.setText("Use Recommendations");
		recommendationCheck.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				useRecommendations = recommendationCheck.getSelection();
				if(useRecommendations) {
					for(final TableColumn column : viewer.getTable().getColumns())
					{
						if(column.getText().equals("Fix Recommendation")) {
							if(column.getWidth() == 0) {
								column.setWidth(800);
							}
						}
					}
				} else {
					for(final TableColumn column : viewer.getTable().getColumns())
					{
						if(column.getText().equals("Fix Recommendation")) {
							if(column.getWidth() == 800) {
								column.setWidth(0);
							}
						}
					}
				}
			}
		});
		recommendationCheck.setLayoutData(new GridData());

		Button devMode = new Button(parent, SWT.CHECK);
		devMode.setText("Enable Dev Mode");
		devMode.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				useDevMode = devMode.getSelection();
			}
		});

		Button showWarnings = new Button(parent, SWT.CHECK);
		showWarnings.setText("Show Warnings");
		showWarnings.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				showWarning = showWarnings.getSelection();
			}
		});		
		
		if(!workspaceListenerInit) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
				@Override
				public void resourceChanged(IResourceChangeEvent event) {
					workspaceListenerInit = true;
					System.out.println(event.getSource());
					System.out.println(event.getDelta().getFlags());
					if(event.getType() == IResourceChangeEvent.POST_CHANGE && event.getDelta().getFlags() == IResourceDelta.LOCAL_CHANGED) {
						System.out.println("Something changed!");
						IEditorPart  editorPart = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
						if(editorPart  != null)
						{
							IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
							IFile file = input.getFile();
							IProject activeProject = file.getProject();
							System.out.println(activeProject.getRawLocation().toOSString());
							String[] args = new String[2];
							args[0] = "--source="+activeProject.getRawLocation().toOSString();
							args[1] = "--recommendations=yes";
							results.getErrors().clear();
							results.getWarnings().clear();
							results = StaticAnalyzer.runStaticAnalyzer(args, true);
							if(useDevMode) {
								updateViewer();
							}
							LOGGER.info("File saved");
							results.getErrors().forEach(error -> {
								LOGGER.info(error.getErrorType() + " " + error.getDesc() + " " + error.getFileName() + " " + error.getRowNumber() + " " + error.getColumnNumber());
							});
						}
					}
				}
			});
		}		

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage[] pages = window.getPages();
			for (int i = 0; i < pages.length; i++) {
				IWorkbenchPage p = pages[i];
				p.addPartListener(new IPartListener2() {

					@Override
					public void partDeactivated(IWorkbenchPartReference arg0) {
					}

					@Override
					public void partHidden(IWorkbenchPartReference arg0) {						
					}

					@Override
					public void partInputChanged(IWorkbenchPartReference arg0) {
					}

					@Override
					public void partOpened(IWorkbenchPartReference arg0) {
					}

					@Override
					public void partVisible(IWorkbenchPartReference arg0) {
						System.out.println("new window visible");
						System.out.println(arg0.getTitle());
						initHandler();
					}

					@Override
					public void partActivated(IWorkbenchPartReference arg0) {					
					}

					@Override
					public void partBroughtToTop(IWorkbenchPartReference arg0) {
					}

					@Override
					public void partClosed(IWorkbenchPartReference arg0) {
						System.out.println("window closed");
					}

				});
			}
		}

		createViewer(parent);
	}

	private void generateMarkers() {
		List<Error> errorsShown = results.getErrors();		
		IEditorReference[] refs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
//		removeExistingMarkers(refs);		
		for(int i=0; i<refs.length; i++) {
			try {
				IEditorInput input = refs[i].getEditorInput();
				IFile file = ((IFileEditorInput)input).getFile();
				String srcFile = file.getRawLocation().toOSString();
				for(int j=0;j<errorsShown.size();j++) {
					Error err = errorsShown.get(j);
					if(srcFile.equals(err.getFileName())) {									
						IMarker m;
						try {
							m = file.createMarker(IMarker.PROBLEM);
							m.setAttribute(IMarker.LINE_NUMBER, err.getRowNumber());
							m.setAttribute(IMarker.MESSAGE, err.getErrorType() + " " + err.getDesc());
							m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
							m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}					
	}

	private void removeExistingMarkers(IEditorReference[] refs) {
		String parentPath = new File(getCurrentFilePath()).getParent().toString();
		for(int i=0; i<refs.length; i++) { 			
			try {
				System.out.println(refs[i].getEditorInput().getName());
				String srcFile = parentPath + File.separator + refs[i].getEditorInput().getName();
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot workspaceRoot = workspace.getRoot();
				IPath path = new Path(srcFile);
				IFile file = workspaceRoot.getFileForLocation(path);
				IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IFile.DEPTH_INFINITE);
				for(int j=0; j<markers.length;j++) {
					markers[j].delete();
				}
			} catch (Exception e) {			
				e.printStackTrace();
			}			
		}
	}

	private void updateViewer() {		
		viewer.getTable().removeAll();
		if ((results != null) && (results.getErrors().size() <= 0)) {
			Error err = new Error("No error found","There was no error found","",0,0);
			err.setFixRecommendation("Nothing here");
			results.setError(err);
			viewer.setInput(results.getErrors());
		} else {
			if(showWarning) {				
				List<Error> totalResults = results.getErrors();
				results.getWarnings().forEach(warning -> {
					totalResults.add(new Error(warning.getWarningType(), warning.getDesc(), warning.getFileName(), warning.getRowNumber(), warning.getColumnNumber()));
				});				
				Collections.sort(totalResults, (r1,r2) -> r1.getRowNumber() - r2.getRowNumber());
				viewer.setInput(totalResults);
				generateMarkers();
			} else {
				Collections.sort(results.getErrors(), (r1,r2) -> r1.getRowNumber() - r2.getRowNumber());
				viewer.setInput(results.getErrors());
				generateMarkers();
			}
		}	
	}		

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		Error err = new Error("Not Initialized","Please open any file in a project and click on run","",0,0);
		err.setFixRecommendation("Please open any file in a project and click on run");
		results.setError(err);		
		viewer.setInput(results.getErrors());
		getSite().setSelectionProvider(viewer);

		makeActions();
		hookDoubleClickAction();

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		initHandler();
	}

	private void initHandler() {
		ITextEditor editor = (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editor != null) {			
			if(!instantiatedEditors.contains(editor.hashCode())) {
				instantiatedEditors.add(editor.hashCode());
				((StyledText)editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {				
						LOGGER.info("Keycode : " + e.keyCode + " Character : " + e.character + " StateMask : " + e.stateMask);				
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}
				});
			} 
		}		
	}

	private String getCurrentFilePath() {
		String path = "";
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IFile file = (IFile)workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		if(file != null) {
			path = file.getRawLocation().toOSString();
		}
		return path;
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {

		String[] titles = { "Error Type", "Description", "Source File Name", "Location(row number)", "Location(column number)", "Fix Recommendation" };

		int[] bounds = { 250, 800, 400, 200, 200};

		// first column
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {				
				return ((Error)element).getErrorType();
			}
		});

		// second column
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Error)element).getDesc();
			}
		});

		// Third column
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {				
				return ((Error)element).getFileName();
			}
		});

		// Fourth column
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {				
				return String.valueOf(((Error)element).getRowNumber());
			}
		});

		// Fifth column
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Error)element).getColumnNumber());
			}
		});

		// Sixth column
		col = createTableViewerColumn(titles[5], 0, 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Error)element).getFixRecommendation());
			}
		});		
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				goToLine(obj);
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void goToLine(Object obj) {
		Error err = (Error)obj;
		int lineNumber = err.getRowNumber();
		String fileName = err.getFileName();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IPath path = new Path(fileName);
		IFile file = workspaceRoot.getFileForLocation(path);

		try {
			ITextEditor textEditor = (ITextEditor)IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true );
			IDocumentProvider provider = textEditor.getDocumentProvider();
			IDocument document = provider.getDocument(textEditor.getEditorInput());
			IRegion loc = document.getLineInformation(lineNumber - 1);
			textEditor.selectAndReveal(loc.getOffset(), loc.getLength());
			generateMarkers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
