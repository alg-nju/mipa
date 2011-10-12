package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

/**
 * abstract framework for ctl checking algorithm
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public abstract class AbstractCTLChecker
{
	
	protected Structure specification = null;
	protected AbstractLatticeNode startNode = null;
	protected AbstractLatticeNode endNode = null;
	protected Formula ctlFormula = null;
	
	/**
	 * constructor
	 * @param specification  ctl formula to be checked
	 * @param startNode start node to be checked
	 * @param endNode end node to be checked
	 */
	public AbstractCTLChecker(Structure specification,AbstractLatticeNode startNode,AbstractLatticeNode endNode)
	{
		this.specification = specification;
		this.startNode = startNode;
		this.endNode = endNode;
		
		this.ctlFormula = CTLParser.getInstance().getCtlFormula();
	}
	
	/**
	 * ctl checking algorithm for Atomic Proposition
	 * and ctl modalities.
	 */
	public abstract boolean checkModality(Formula f);
	public abstract boolean checkAP(Composite leaf);
	public abstract boolean checkFramework();
	
}
