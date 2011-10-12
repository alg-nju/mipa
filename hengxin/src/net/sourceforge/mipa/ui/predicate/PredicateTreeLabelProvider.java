package net.sourceforge.mipa.ui.predicate;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author hengxin
 *
 * provides label for <code>TreeLabelProvider</code> in order to represent predicate in tree view.
 * 
 * @see <code>Formula</code>
 */
public class PredicateTreeLabelProvider extends LabelProvider
{
	public String getText(Object element)
	{
		if(element instanceof Composite)
		{
			if(element instanceof Formula)
				return ((Composite) element).getNodeName() + "[ " + ((Formula) element).getConnetor().getNodeValue() + " ]";
			return ((Composite) element).getNodeName();
		}
		else 
			return null;
	}
	
	public Image getImage(Object element)
	{
		//TODO: return real images 
		
		return null;
	}
}
