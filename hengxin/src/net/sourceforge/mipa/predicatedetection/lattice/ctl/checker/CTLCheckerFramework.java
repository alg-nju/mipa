package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.ctl.IFormulaTreeTraversal;
import net.sourceforge.mipa.predicatedetection.ctl.PostOrderTraversal;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

/**
 * default implementation for AbstractCTLChecker
 *   @see net.sourceforge.mipa.predicatedetection.lattice.ctl.checker.AbstractCTLChecker
 *  
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public abstract class CTLCheckerFramework extends AbstractCTLChecker implements IFormulaTreeTraversal
{

	public CTLCheckerFramework(Structure specification,
			AbstractLatticeNode startNode, AbstractLatticeNode endNode)
	{
		super(specification, startNode, endNode);
	}

	/**
	 * framework for ctl checking algorithm:
	 * travels and check the syntax tree of specification(ctl formula) 
	 * in post-order manner.
	 * 
	 * // FIXME: return value
	 * 
	 * @return true if the specification is satisfied; false otherwise.
	 */
	public boolean checkFramework()
	{
		PostOrderTraversal pot = new PostOrderTraversal();
		pot.postOrder(this.ctlFormula, this);
		
		return false;
	}
	
	@Override
	public boolean checkAP(Composite ap)
	{
		return false;
	}
	
	@Override
	public boolean checkModality(Formula f)
	{
		return false;
	}

	/************ IFormulaTreeTraversal ************/
	@Override
	public boolean isLeaf(Composite node)
	{
		return false;
	}
	
	@Override
	public void processLeaf(Composite node)
	{
	}
	
	@Override
	public void processNonLeaf(Composite node)
	{
	}
}
