package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import java.util.Hashtable;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;
import net.sourceforge.mipa.util.algorithm.bfs.BFSFramework;
import net.sourceforge.mipa.util.algorithm.bfs.IBFS;

import org.apache.log4j.Logger;

/**
 * ctl3 checker which extends the abstract ctl checker framework
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class CTL3APChecker extends CTLCheckerFramework implements IBFS
{
	
	private static Logger logger = Logger.getLogger(CTL3APChecker.class);
	
	// atomic proposition "ap" to be checked
	private Composite ap = null;
	
	public CTL3APChecker(Structure specification,AbstractLatticeNode startNode,AbstractLatticeNode endNode)
	{
		super(specification,startNode,endNode);
	}

	/****************************************************************************
	 * 					related to ctl checking algorithm
	 * **************************************************************************/
	
	/**
	 * ctl3 checking algorithm for Atomic Proposition.
	 */
	@Override
	public boolean checkAP(Composite ap)
	{
		this.ap = ap;

		CheckerInfo.getInstance().resetCheckerInfo(this.ap, CheckerInfo.satNew4SubFormula);
		CheckerInfo.getInstance().resetCheckerInfo(this.ap, CheckerInfo.falNew4SubFormula);
		
		BFSFramework bfs = new BFSFramework(this);
		bfs.breadthFirstSearchRev(super.endNode);
		
		return true;
	}

	/*********************************************************************
	 * **************** related to bfs traversal framework ***************
	 * *******************************************************************/
	/**
	 * is the node should be visited(i.e., checked)
	 * @param aln lattice node
	 * @return true if this node should be visited(i.e., checked)
	 */
	@Override
	public boolean toVisit(AbstractLatticeNode aln)
	{
		return ! ((CTLLatticeNode) aln).isCheckedWRT(ap);
	}

	/**
	 * checking @param aln with respect to atomic proposition "ap"
	 * 
	 * @param aln lattice node
	 */
	@Override
	public void processNode(AbstractLatticeNode aln)
	{
		logger.info("Checking atomic proposition [ " + ap + " ] for " + 
				"lattice node [ " + aln + " ].");
		
		Hashtable<String, Boolean> lables = ((CTLLatticeNode) aln).getLabels();
		
		// FIXME: optimization: one for many ???
		if(lables.containsKey(ap.getNodeName()))
		{
			if(lables.get(ap.getNodeName()))
			{
				logger.info(ap + " is satisfied by " + aln);
				
				CheckerInfo.getInstance().add2CheckerInfo(this.ap, aln, CheckerInfo.sat4SubFormula, true);
				CheckerInfo.getInstance().add2CheckerInfo(this.ap, aln, CheckerInfo.satNew4SubFormula, false);
			}
			else
			{
				logger.info(ap + " is not satisfied by " + aln);
				
				CheckerInfo.getInstance().add2CheckerInfo(this.ap, aln, CheckerInfo.fal4SubFormula, true);
				CheckerInfo.getInstance().add2CheckerInfo(this.ap, aln, CheckerInfo.falNew4SubFormula, false);
			}
		}

		logger.info("Satisfaction set for lattice nodes is as follows: \n" + CheckerInfo.sat4SubFormula);
		logger.info("Falsifation set for lattice nodes is as follows: \n" + CheckerInfo.fal4SubFormula);
		logger.info("New satisfaction set for lattice nodes is as follows: \n" + CheckerInfo.satNew4SubFormula);
		logger.info("New falsifation set for lattice nodes is as follows: \n" + CheckerInfo.falNew4SubFormula);
	}

	@Override
	public void processLink(AbstractLatticeNode preNode,
			AbstractLatticeNode node)
	{
		
	}

}