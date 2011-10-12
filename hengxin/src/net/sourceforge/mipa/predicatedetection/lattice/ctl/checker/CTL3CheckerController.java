package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

import org.apache.log4j.Logger;

public class CTL3CheckerController extends CTLCheckerFramework
{
	Logger logger = Logger.getLogger(CTL3CheckerController.class);
	
	public CTL3CheckerController(Structure specification, AbstractLatticeNode startNode,
			AbstractLatticeNode endNode)
	{
		super(specification, startNode, endNode);
	}
	
	/*********************************************************************************
	 * ************************ related to  formula tree traversal *******************
	 * *******************************************************************************/
	@Override
	public boolean isLeaf(Composite node)
	{
		return node.getNodeType()  == NodeType.CGS;
	}
	
	/**
	 * @param leaf leaf node which is an atomic proposition in the term of ctl 
	 */
	@Override
	public void processLeaf(Composite leaf)
	{
	    new CTL3APChecker(specification, startNode, endNode).checkAP(leaf);
	}
	
	/**
	 * @param nonLeaf non-Leaf node which represents sub-formula which is not an atomic
	 *   proposition
	 */
	@Override
	public void processNonLeaf(Composite nonLeaf)
	{
		// get the concrete connector type(i.e., kind of ctl modalities)
		NodeType connector = ((Formula) nonLeaf).getConnetor().getOperator();
		
		CTL3ModalityCheckerFactory.buildModalityChecker(connector, nonLeaf, startNode, endNode).checkModality((Formula) nonLeaf);
	}
}
