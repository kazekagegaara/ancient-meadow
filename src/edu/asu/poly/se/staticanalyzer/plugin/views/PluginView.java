/**
 * @author Manit Singh Kalsi
 */
package edu.asu.poly.se.staticanalyzer.plugin.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
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
//	private Action action1;
//	private Action action2;
	private Action doubleClickAction;
	private Results results = new Results();
	private boolean useRecommendations = false;
	private boolean useDevMode = false;
	private boolean showWarning = false;
	private final static Logger LOGGER = Logger.getLogger(PluginView.class.getName());
	static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;


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
				String path = getCurrentFilePath();				
				if(!path.equals("")){
					path = new File(path).getParent().toString();
					String[] args = new String[2];
					args[0] = "--source="+path;
					args[1] = "--recommendations=yes";
					results.getErrors().clear();
					results.getWarnings().clear();
					results = StaticAnalyzer.runStaticAnalyzer(args, true);
					updateViewer();
				}
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
				  System.out.println(dialog.getHowWasDefectFound());
				  System.out.println(dialog.getHowToReproduceDefect());
				  System.out.println(dialog.getLineNumber());
				  System.out.println(dialog.getFileName());
				  System.out.println(dialog.getDescription());
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
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				if(event.getType() == 1) {
					System.out.println("Something changed!");
					String path = getCurrentFilePath();				
					if(!path.equals("")){
						path = new File(path).getParent().toString();
						String[] args = new String[2];
						args[0] = "--source="+path;
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
//						generateMarkers();
					}
				}
	        }
	    });
		
		createViewer(parent);
	}

	private void generateMarkers() {
		List<Error> errorsShown = results.getErrors();		
		IEditorReference[] refs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		String parentPath = new File(getCurrentFilePath()).getParent().toString();
		removeExistingMarkers(refs);
		errorsShown.forEach(error -> {
			for(int i=0; i<refs.length; i++) {
				try {				
					String srcFile = parentPath + File.separator + refs[i].getEditorInput().getName();
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot workspaceRoot = workspace.getRoot();
					IPath path = new Path(srcFile);
					IFile file = workspaceRoot.getFileForLocation(path);					
					if(srcFile.equals(error.getFileName())) {									
						IMarker m = file.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.LINE_NUMBER, error.getRowNumber());
						m.setAttribute(IMarker.MESSAGE, error.getErrorType() + " " + error.getDesc());
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		});
	}
	
	private void removeExistingMarkers(IEditorReference[] refs) {
		String parentPath = new File(getCurrentFilePath()).getParent().toString();
		for(int i=0; i<refs.length; i++) { 			
			try {
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
		Error err = new Error("Not Initialized","Please open a HTML file in a project and click on run","",0,0);
		err.setFixRecommendation("Please open a HTML file in a project and click on run");
		results.setError(err);		
		viewer.setInput(results.getErrors());
		getSite().setSelectionProvider(viewer);

		makeActions();
//		hookContextMenu();
		hookDoubleClickAction();
//		contributeToActionBars();

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
		((StyledText)editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(new KeyListener() {
						
			@Override
			public void keyPressed(KeyEvent e) {				
				LOGGER.info("Keycode : " + e.keyCode + " Character : " + e.character + " StateMask : " + e.stateMask);				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// Do nothing
			}
		});
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

//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				PluginView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
//	}
//
//	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
//	}

//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
//
//	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//	}

	private void makeActions() {
//		action1 = new Action() {
//			public void run() {
//				showMessage("Action 1 executed");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//
//		action2 = new Action() {
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setText("Action 2");
//		action2.setToolTipText("Action 2 tooltip");
//		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
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
//	private void showMessage(String message) {
//		MessageDialog.openInformation(
//				viewer.getControl().getShell(),
//				"Static Analyzer",
//				message);
//	}

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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
