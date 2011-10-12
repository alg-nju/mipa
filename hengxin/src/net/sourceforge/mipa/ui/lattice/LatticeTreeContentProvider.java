package net.sourceforge.mipa.ui.lattice;

import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * provide tree content for lattice tree view
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class LatticeTreeContentProvider implements ITreeContentProvider
{

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		return ((CTLLatticeNode) parentElement).getnext().toArray();
	}

	@Override
	public Object[] getElements(Object element)
	{
		return this.getChildren(element);
	}

	/*
	 * FIXME: how to represent non-tree structure in treeviewer ?
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element)
	{
		return ((CTLLatticeNode) element).getprevious().get(0);
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return this.getChildren(element).length != 0;
	}

}
