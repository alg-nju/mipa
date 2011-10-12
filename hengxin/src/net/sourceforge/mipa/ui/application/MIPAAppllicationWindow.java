package net.sourceforge.mipa.ui.application;

/**
 * @author hengxin(hengxin0912@gmail.com)
 * 
 * ui for mipa application
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.mipa.ui.lattice.LatticeTabUI;
import net.sourceforge.mipa.ui.predicate.PredicateTabUI;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import util.MIPAAllInOne;

/**
 * mipa application window
 * 
 * Singleton design pattern
 * @author hengxin
 *
 */
//TODO: separate this huge window into small classes which implement UI.
public class MIPAAppllicationWindow extends ApplicationWindow
{
	private static Logger logger = Logger.getLogger(MIPAAppllicationWindow.class);
	
//	private StyledText styledTextPredicate = null;
	private String predicateFileName = null;
	
	// open the predicate xml file
	private Action actionOpen;
	// run the mipa all in one
	private Action actionRun;

	TreeViewer latticeTreeViewer = null;
	/**
	 * Create the application window.
	 */
	public MIPAAppllicationWindow()
	{
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		/********************************************************************************
		 * Menu Bar
		 ********************************************************************************/
		// Menu menuBar = new Menu(parent.getShell(),SWT.BAR);
		// MenuItem menuItemRun = new MenuItem(menuBar,SWT.CASCADE);
		// menuItemRun.setText("&Run");
		// Menu menuRun = new Menu(menuItemRun);
		// menuItemRun.setMenu(menuRun);
		// parent.getShell().setMenuBar(menuBar);

		Composite containerMIPA = new Composite(parent, SWT.NONE);

		TabFolder tabFolderMIPA = new TabFolder(containerMIPA, SWT.BORDER);
		tabFolderMIPA.setBounds(0, 0, 943, 599);

//		TabItem tabItemPredicate = new TabItem(tabFolderMIPA, SWT.NONE);
//		tabItemPredicate
//				.setToolTipText("In this tab folder, the predicate is given in the form of xml file as well as the view of abstract syntax tree.");
//		tabItemPredicate.setText("Predicate");
//
//		SashForm sashPredicate = new SashForm(tabFolderMIPA, SWT.NONE);
//		sashPredicate
//				.setToolTipText("Drag to change the sizes of the two sides of the window.");
//		tabItemPredicate.setControl(sashPredicate);
//		
//		styledTextPredicate = new StyledText(sashPredicate, SWT.BORDER);
//		styledTextPredicate.setToolTipText("Predicate are shown here.");
//
//		Group grpPredicateTree = new Group(sashPredicate, SWT.NONE);
//		grpPredicateTree.setToolTipText("Tree view of predicate");
//		grpPredicateTree.setText("Predicate Tree View");
//
//		TreeViewer predicateTreeViewer = new TreeViewer(grpPredicateTree,
//				SWT.BORDER);
//		Tree treePredicate = predicateTreeViewer.getTree();
//		treePredicate.setBounds(10, 28, 444, 527);
//		sashPredicate.setWeights(new int[] { 1, 1 });
//		predicateTreeViewer
//				.setContentProvider(new PredicateTreeContentProvider());
//		predicateTreeViewer.setLabelProvider(new PredicateTreeLabelProvider());

		PredicateTabUI.getInstance(tabFolderMIPA);
		
/*		TabItem tabItemLattice = new TabItem(tabFolderMIPA, SWT.NONE);
		tabItemLattice.setToolTipText("representation of lattice");
		tabItemLattice.setText("Lattice");

		SashForm sashLattice = new SashForm(tabFolderMIPA, SWT.NONE);
		tabItemLattice.setControl(sashLattice);

		StyledText styledTextLattice = new StyledText(sashLattice, SWT.BORDER
				| SWT.READ_ONLY);
		styledTextLattice.setText("Information for lattice are given here \n.");
		styledTextLattice.setToolTipText("Information for lattice.");

		Group grpLatticeTree = new Group(sashLattice, SWT.NONE);
		grpLatticeTree.setToolTipText("tree view of lattice");
		grpLatticeTree.setText("Lattice Tree View");
		
		latticeTreeViewer = new TreeViewer(grpLatticeTree, SWT.BORDER);
		Tree latticeTree = latticeTreeViewer.getTree();
		latticeTree.setBounds(10, 29, 444, 526);
		latticeTreeViewer.setLabelProvider(new LatticeTreeLabelProvider());
		latticeTreeViewer.setContentProvider(new LatticeTreeContentProvider());
		sashLattice.setWeights(new int[] { 1, 1 });*/
		
		LatticeTabUI.getInstance(tabFolderMIPA);

		return containerMIPA;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() 
	{
		// Create the actions
		
		// Open the predicate xml file and show it in text area <code>styledTextPredicate</code>
		{
			actionOpen = new Action("Open")
			{
				public void run()
				{
					// choose the predicate xml file
					FileDialog openDlg = new FileDialog(MIPAAppllicationWindow.this.getShell(),SWT.SINGLE);
					openDlg.setFilterExtensions(new String[] {"*.xml"});
					MIPAAppllicationWindow.this.predicateFileName = openDlg.open();
					
					// show the predicate in text are <code>styledTextPredicate</code>
					if(MIPAAppllicationWindow.this.predicateFileName != null)
					{
						logger.info("The predicate xml file is: " + MIPAAppllicationWindow.this.predicateFileName);
//						MIPAAppllicationWindow.this.styledTextPredicate.setText(MIPAAppllicationWindow.this.retrieveContent(predicateFileName));
						PredicateTabUI.getInstance(null).setText(MIPAAppllicationWindow.this.retrieveContent(predicateFileName));
					}
				}			};
			actionOpen.setToolTipText("Choose the predicate xml file and open it.");
			actionOpen.setAccelerator(SWT.CTRL | 'O');
		}
		// Run mipa application
		{
			actionRun = new Action("Run")
			{
				public void run()
				{
					if(MIPAAppllicationWindow.this.predicateFileName == null)
					{
						logger.info("Please choose the predicate xml file first!");
					}
					else
					{
						logger.info("Run MIPA application all in one.");
						MIPAAllInOne.getInstance(MIPAAppllicationWindow.this.predicateFileName).runMIPAAllInOne();
					}
				}
			};
			actionRun.setToolTipText("Run MIPA all in one.");
			actionRun.setAccelerator(SWT.CTRL | 'R');
		}
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager()
	{
		MenuManager menuManager = new MenuManager("menu");

		// FIXME: to create menu
		// Shell shell = this.getShell();
		// Menu menuBar = menuManager.createMenuBar(shell);
		// MenuItem menuItemRun = new MenuItem(menuBar, SWT.NONE);
		// menuItemRun.setText("Run");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style)
	{
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(actionOpen);
		toolBarManager.add(actionRun);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager()
	{
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			MIPAAppllicationWindow window = new MIPAAppllicationWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("MIPA Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(969, 723);
	}
	
	// Retrieve file content
	private String retrieveContent(String fileName)
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			FileReader fr = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(fr);
			
			String line = null;
			while((line = bf.readLine()) != null)
			{
				sb.append(line).append('\n');
			}
		} catch (FileNotFoundException fnfe)
		{
			logger.error("Cannot find this file: " + fileName);
			logger.error(fnfe.getMessage());
			return null;
		} catch (IOException ioe)
		{
			logger.error("Fail to read this file: " + fileName);
			logger.error(ioe.getMessage());
			return null;
		} 

		return sb.toString();
	}
}
