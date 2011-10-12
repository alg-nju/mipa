package net.sourceforge.mipa.predicatedetection.lattice.ctl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.experiment.Experiment;
import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.checker.AbstractCTLChecker;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.checker.CTL3CheckerController;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;
import net.sourceforge.mipa.ui.lattice.LatticeDot;
import net.sourceforge.mipa.util.algorithm.bfs.BFSFramework;
import net.sourceforge.mipa.util.algorithm.bfs.IBFS;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

/**
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 * ctl check algorithm based on lattice
 */
public class CTLLatticeChecker extends LatticeChecker implements IBFS
{

	private static final long serialVersionUID = -8870142186085258590L;
	private Experiment experiment = new Experiment();
	private StopWatch watcher = new StopWatch();
	
	private static Logger logger = Logger.getLogger(CTLLatticeChecker.class);
	
	// just to control the termination of checking algorithm for test
	private static int checkNum = 0;
	
	// related to cgs(es) which will be used as atomic propositions in ctl formulae 
	private HashMap<String, ArrayList<String>> cgs2nps = new HashMap<String, ArrayList<String>>();
	
	// the specification (in the form of ctl formula) to be checked
	private Structure specification = null;
	
	public CTLLatticeChecker(ResultCallback application, String predicateID,
			String checkerName, String[] normalProcesses,
			Structure specification)
	{
		super(application, predicateID, checkerName, normalProcesses);
		
		this.specification = specification;
		
		// initialize the cgs(es) related data structure and prepare for the checking algorithm
		this.cgs2nps = CTLParser.getInstance().getCgs2NPs();
	}

	/**
	 * create lattice node for ctl 
	 * 
	 * @param globalState : global state for lattice node
	 * @param s : label for lattice node
	 * 
	 * @return lattice node for ctl
	 */
	@Override
	public AbstractLatticeNode createNode(LocalState[] globalState, String[] s)
	{
		AbstractLatticeNode ctlNode = new CTLLatticeNode(globalState, s);
		
		/**
		 * commented by hengxin(hengxin0912@gmail.com)
		 * 
		 * it is NOT the time that the node being linked in the tree view
		 */
//		CheckerInfo.getInstance().storeNode2Table(ctlNode);
		
		return ctlNode;
	}

	/**
	  *  This is the main procedure of checking algorithm for ctl formula.
	  *  The ctl(ctl3) model checking can be performed by a recursive procedure that calculates
	  *  the satisfaction set for all sub-formulae of specification. 
	  */
	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode)
	{
		if(this.specification != null)
		{
			checkNum++;
		
			// (1) to label these new lattice nodes first
			logger.info("(1) To label these new lattice nodes first.");
			
			this.watcher.start();
			
			if(CheckerInfo.CHECK)
			{
				this.computeCGSForAll(currentNode);
				
				// (2) to check ctl specification according to the abstract syntax tree structure of it
				logger.info("(2) to check ctl specification according to the abstract syntax tree structure of it.");
				
				AbstractCTLChecker ctl3Checker = new CTL3CheckerController(this.specification,startNode,currentNode);
				ctl3Checker.checkFramework();
			}
			
			long time = this.watcher.getTime();
			this.watcher.reset();
			
			/*
			 *  experiment 1 : Occurrences of third value "inconclusive"
			 *  keep accounts for experiment
			 */
//			experiment.keepAccount4Experiment1(startNode);
			
			/*
			 *  experiment 2 : Response time vs. space cost
			 */
//			if(checkNum % 100 == 0)
//			{
				System.out.println("Checker number is : " + checkNum);
				experiment.keepAccount4Experiment2(time);
//			}
			
			// just for debug
			if(checkNum == 4)
			{
				if (CheckerInfo.DOT)
				{
					LatticeDot.getInstance().write2CheckDot(currentNode);
				}
			}
		}
	}

	/**
	 * determine which cgs(es) are satisfied by each node.
	 * Due to the on-line character of this checking algorithm, the underlying breadth-first
	 * search should be controlled to just examine the new nodes which have been created since
	 * the last checking algorithm.
	 *   
	 * @param currentNode calculate backward recursively from this node.
	 */
	private void computeCGSForAll(AbstractLatticeNode currentNode)
	{
		BFSFramework bfs = new BFSFramework(this);
		bfs.breadthFirstSearchRev(currentNode);
	}
	
	/****************************************************************************
	 *                   related to bfs algorithm.
	 *    @see package net.sourceforge.mipa.util.algorithm.bfs
	 *****************************************************************************/
	@Override
	public boolean toVisit(AbstractLatticeNode node)
	{
		return ! ((CTLLatticeNode) node).isLabeled();
	}

	/**
	 * calculate the cgs for this lattice node
	 * (actually, it is to label this node
	 * with atomic propositions in the term of ctl.)
	 */
	@Override
	public void processNode(AbstractLatticeNode node)
	{
		Set<String> CGSs = this.cgs2nps.keySet();
		Iterator<String> iter = CGSs.iterator();
		String cgs = null;
		while (iter.hasNext())
		{
			cgs = iter.next();
			boolean result = true;
			ArrayList<String> NPs = this.cgs2nps.get(cgs);
			int length = NPs.size();
			for (int i = 0; i < length; i++)
			{
				// this statement extracts npIndex from string : "NormalProcess + npIndex"
				int npIndex = Integer.parseInt(NPs.get(i).substring(13));
				
				result = result
						&& (node.getglobalState()[npIndex].getlocalPredicate());
			}
			((CTLLatticeNode) node).addLabel(cgs, result);
		}
		
		// for logger
		logger.info(((CTLLatticeNode) node).getLabelsLiteral());
	}


	@Override
	public void processLink(AbstractLatticeNode preNode,
			AbstractLatticeNode node)
	{
		
	}
	
	
	/***********************************************************
	 * ILatticeConstructor interface
	 * ********************************************************/
	
	@Override
	public void handleLatticeNode(String[] nodeId)
	{
		
	}

	/**
	 * cooperate with 
	 * {@link net.sourceforge.mipa.predicatedetection.lattice.ctl.checker.CTL3EUChecker#checkModality(net.sourceforge.mipa.predicatedetection.Formula)}
	 * 
	 * More specifically, for the update of false part of EU
	 *  and the update of true part of AU.
	 * 
	 */
	@Override
	public void handleNonLatticeNode(String[] nodeId)
	{
		// update the possible "postCount"
		String nodeIdLiteral = null;
		StringBuilder sb = new StringBuilder();
		
		for(String localId : nodeId)
		{
			sb.append(localId).append('_');
		}
		nodeIdLiteral = sb.toString();
		
		List<String> preNodes = CheckerInfo.getInstance().getAllPossiblePreviousByIds(nodeId);
		Iterator<String> iter = preNodes.iterator();
		
		String preNodeId = null;
		AbstractLatticeNode aln = null;
		while(iter.hasNext())
		{
			preNodeId = iter.next();
			if(! CheckerInfo.getInstance().containsNodeById(preNodeId))
			{
				if (CheckerInfo.DOT)
				{
					// for .dot
					if (!LatticeDot.getInstance().isDefinedVirtualNode(
							preNodeId))
					{
						LatticeDot.getInstance().write2LatticeDotVirtualNode(
								preNodeId);
						LatticeDot.getInstance().add2DefinedVirtualNode(
								preNodeId);
					}
					LatticeDot.getInstance().write2LatticeDotVirtualLink(
							preNodeId, nodeIdLiteral);
				}
			}
			else
			{
				aln = CheckerInfo.getInstance().getAlnById(preNodeId);
				
				if (CheckerInfo.DOT)
				{
					// for .dot
					LatticeDot.getInstance().write2LatticeDotVirtualLink(aln,
							nodeIdLiteral);
				}
				if(aln instanceof CTLLatticeNode)
				{
					((CTLLatticeNode) aln).decPostCount();
					
					List<Composite> subFormulae = CTLParser.getInstance().getSubFormulae();
					Iterator<Composite> formulaIter = subFormulae.iterator();
					Composite subFormula = null;
					while(formulaIter.hasNext())
					{
						subFormula = formulaIter.next();
						
						if(CTLParser.getInstance().isEUSubFormula(subFormula) && ((CTLLatticeNode) aln).isZeroPostCount(subFormula))
							CheckerInfo.getInstance().add2CheckerInfo(subFormula, aln, CheckerInfo.falNew4CountSubFormula, false);
						else if(CTLParser.getInstance().isAUSubFormula(subFormula) && ((CTLLatticeNode) aln).isZeroPostCount(subFormula))
							CheckerInfo.getInstance().add2CheckerInfo(subFormula, aln, CheckerInfo.satNew4CountSubFormula, false);
					}
				}
			}
		}
	}

}
