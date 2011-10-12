package net.sourceforge.mipa.ui.lattice;

import java.util.Arrays;

import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LatticeTreeLabelProvider extends LabelProvider
{
	public String getText(Object element)
	{
		return Arrays.toString(((CTLLatticeNode) element).getID());
	}
	
	public Image getImage(Object element)
	{
		//TODO: return real images 
		
		return null;
	}

}
