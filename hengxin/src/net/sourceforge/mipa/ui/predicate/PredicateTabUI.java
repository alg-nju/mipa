package net.sourceforge.mipa.ui.predicate;

import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.ui.application.UI;
import net.sourceforge.mipa.ui.lattice.LatticeTabUI;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

/**
 * lattice tab part of ui 
 * 
 * Singleton design pattern
 * @author hengxin
 *
 */
public class PredicateTabUI implements UI
{
	private static Logger logger = Logger.getLogger(LatticeTabUI.class);
	
	private static PredicateTabUI instance = null;
	
	private StyledText styledTextPredicate = null;
	TreeViewer predicateTreeViewer = null;
	
	private PredicateTabUI(TabFolder tabFolderMIPA) 
	{
		TabItem tabItemPredicate = new TabItem(tabFolderMIPA, SWT.NONE);
		tabItemPredicate
				.setToolTipText("In this tab folder, the predicate is given in the form of xml file as well as the view of abstract syntax tree.");
		tabItemPredicate.setText("Predicate");

		SashForm sashPredicate = new SashForm(tabFolderMIPA, SWT.NONE);
		sashPredicate
				.setToolTipText("Drag to change the sizes of the two sides of the window.");
		tabItemPredicate.setControl(sashPredicate);
		
		styledTextPredicate = new StyledText(sashPredicate, SWT.BORDER);
		styledTextPredicate.setToolTipText("Predicate are shown here.");

		Group grpPredicateTree = new Group(sashPredicate, SWT.NONE);
		grpPredicateTree.setToolTipText("Tree view of predicate");
		grpPredicateTree.setText("Predicate Tree View");

		predicateTreeViewer = new TreeViewer(grpPredicateTree, SWT.BORDER);
		Tree treePredicate = predicateTreeViewer.getTree();
		treePredicate.setBounds(10, 28, 444, 527);
		sashPredicate.setWeights(new int[] { 1, 1 });
		predicateTreeViewer
				.setContentProvider(new PredicateTreeContentProvider());
		predicateTreeViewer.setLabelProvider(new PredicateTreeLabelProvider());
	}

	
	public static PredicateTabUI getInstance(TabFolder tabFolderMIPA)
	{
		// non UI
		if(tabFolderMIPA == null && instance == null)
			return null;
		
		if(instance == null)
			instance = new PredicateTabUI(tabFolderMIPA);
		
		return instance;
	}
	
	/**
	 * update treeviewer for predicate
	 */
	@Override
	public void update(final Object element)
	{
		logger.info("The lattice has been constructed.");

		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(3000);
					} catch (Exception te)
					{
						logger.error(te.getMessage());
					}
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							PredicateTabUI.this.predicateTreeViewer
									.setLabelProvider(new PredicateTreeLabelProvider());
							PredicateTabUI.this.predicateTreeViewer
									.setContentProvider(new PredicateTreeContentProvider());
							PredicateTabUI.this.predicateTreeViewer
									.setInput((Structure) element);
							PredicateTabUI.this.predicateTreeViewer.refresh();
							PredicateTabUI.this.predicateTreeViewer.expandAll();
						}
					});
				}
			}
		}).start();		
	}

	public void setText(String predicateTxt)
	{
		this.styledTextPredicate.setText(predicateTxt);
	}
}

