package net.sourceforge.mipa.predicatedetection.ctl;

import net.sourceforge.mipa.predicatedetection.Composite;

/**
 * interface for tree traversal in the manner of pre-order,
 * in-order, and post-order.
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 * FIXME: refactor it and make it more general
 */
public interface IFormulaTreeTraversal
{
	// FIXME: provides Node interface
	/**
	 * @param node formula node which is a leaf or non-leaf node
	 * @return true if this formula node is a leaf node;false otherwise.
	 */
	public boolean isLeaf(Composite node);
	
	/**
	 * process leaf(node)
	 * 
	 * @param node leaf node
	 */
	public void processLeaf(Composite node);
	
	
	/**
	 * process non-leaf(node) [post-order trav0ersal]
	 * 
	 * @param node non-leaf node
	 * // @param leftValue value returned by left sub-formula
	 * // @param rightValue value returned by right sub-formula
	 */
	public void processNonLeaf(Composite node);

}
