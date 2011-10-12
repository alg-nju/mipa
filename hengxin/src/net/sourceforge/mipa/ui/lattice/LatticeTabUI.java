package net.sourceforge.mipa.ui.lattice;

import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;
import net.sourceforge.mipa.ui.application.UI;

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
public class LatticeTabUI implements UI
{
	private static Logger logger = Logger.getLogger(LatticeTabUI.class);
	
	private static LatticeTabUI instance = null;
	
	private TreeViewer latticeTreeViewer = null;
	
	private LatticeTabUI(TabFolder tabFolderMIPA) 
	{
		TabItem tabItemLattice = new TabItem(tabFolderMIPA, SWT.NONE);
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
		sashLattice.setWeights(new int[] { 1, 1 });
	}

	
	public static LatticeTabUI getInstance(TabFolder tabFolderMIPA)
	{
		if(instance == null)
			instance = new LatticeTabUI(tabFolderMIPA);
		
		return instance;
	}
	
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
							LatticeTabUI.this.latticeTreeViewer
									.setLabelProvider(new LatticeTreeLabelProvider());
							LatticeTabUI.this.latticeTreeViewer
									.setContentProvider(new LatticeTreeContentProvider());
							LatticeTabUI.this.latticeTreeViewer
									.setInput((CTLLatticeNode) element);
							LatticeTabUI.this.latticeTreeViewer.refresh();
							LatticeTabUI.this.latticeTreeViewer.expandAll();
						}
					});
				}
			}
		}).start();
	}

}
