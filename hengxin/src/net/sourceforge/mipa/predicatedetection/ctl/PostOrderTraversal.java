package net.sourceforge.mipa.predicatedetection.ctl;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.NodeType;

/**
 * traverse the abstract syntax tree of ctl formula
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class PostOrderTraversal
{
	/**
	 * 
	 * @param node ctl formula to check
	 * @param who
	 */
	public void postOrder(Composite node,IFormulaTreeTraversal who)
	{
		// leaf node
		if(who.isLeaf(node))
			who.processLeaf(node);
		else  // non-leaf node
		{
			if(((Formula)node).getConnetor().getNodeType()  == NodeType.BINARY)
			{
				Composite leftFormula = (Composite) node.getChildren().get(0);
				Composite rightFormula = (Composite) node.getChildren().get(1);
				
				this.postOrder(leftFormula, who);
				this.postOrder(rightFormula, who);
				who.processNonLeaf(node);
			}
			else  // (node.getConnetor().getNodeType()  == NodeType.UNARY)
			{
				Composite subFormula = (Composite) node.getChildren().get(0);
				this.postOrder(subFormula, who);
				who.processNonLeaf(node);
			}
		}
	}
}
