package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;

import org.apache.log4j.Logger;

/**
 * AU checker
 * 
 * implemented on 07/17/2011
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class CTL3AUChecker extends CTLCheckerFramework
{

	private static Logger logger = Logger.getLogger(CTL3EUChecker.class);
	
	/**
	 * au : AU subformula
	 * arg1 : the first argument of EU subformula
	 * arg2 : the second argument of EU subformula
	 */
	private Composite au = null;
	private Composite arg1 = null;
	private Composite arg2 = null;
	
	public CTL3AUChecker(Structure specification,
			AbstractLatticeNode startNode, AbstractLatticeNode endNode)
	{
		super(specification, startNode, endNode);
	}


	/****************************************************************************
	 * 					related to ctl checking algorithm
	 * **************************************************************************/
	/**
	 * ctl3 checking algorithm for AU modality.
	 */
	@Override
	public boolean checkModality(Formula au)
	{
		this.au = au;
		this.arg1 = (Composite) au.getChildren().get(0);
		this.arg2 = (Composite) au.getChildren().get(1);
		
		logger.info("Checking subformula [ " + this.au + " ] with [ arg1: " + 
				this.arg1 + " ] and [ arg2: " + this.arg2 + " ].");

//		super.resetSatNew(this.au);
//		this.updateAUSat();
		CheckerInfo.getInstance().resetCheckerInfo(this.au, CheckerInfo.satNew4SubFormula);	
		this.updateAUSatHash();
		logger.info("Satisfaction set for formula [ " + this.au + " ] is " + CheckerInfo.sat4SubFormula.get(this.au));
		
//		super.resetFalNew(this.au);
//		this.updateAUFal();
		CheckerInfo.getInstance().resetCheckerInfo(this.au, CheckerInfo.falNew4SubFormula);
		this.updateAUFalHash();
		logger.info("Falsifaction set for formula [ " + this.au + " ] is " + CheckerInfo.fal4SubFormula.get(this.au));

		return true;
	}
	
	private void updateAUSatHash()
	{
		// satUpdateAlns administers the states s with s satisfying AU
		List<String> satUpdateAlns = CheckerInfo.getInstance().getAUSatUpdateAlnsID(this.au, arg1, arg2);
		if(satUpdateAlns.size() == 0)
			return;
	
		// state s in satUpdateAlns satisfies AU trivially.
		CheckerInfo.getInstance().add2CheckerInfo(this.au, satUpdateAlns, CheckerInfo.sat4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(this.au, satUpdateAlns, CheckerInfo.satNew4SubFormula, false);
		
		// reCheckedAlns contains any state s which maybe satisfies AU and needs to be checked further
		List<String> reCheckedAlns = CheckerInfo.getInstance().getReChecked4EUorAU(arg1, arg2);
		
		/**
		 * The following is the process of update
		 */
		int index = 0;
		int length = satUpdateAlns.size();
		AbstractLatticeNode aln = null;
		while(index != length)
		{
			aln = CheckerInfo.getInstance().getAlnById(satUpdateAlns.get(index++));
			
			List<AbstractLatticeNode> preAlns = aln.getprevious();
			AbstractLatticeNode preAln = null;
			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
			while (iter.hasNext())
			{
				preAln = iter.next();
				
				if (CheckerInfo.getInstance().isUnknown(preAln, this.au))
					if (reCheckedAlns.contains(preAln.getIDLiteral()))
					{
						((CTLLatticeNode) preAln).decPostCount(this.au);

						if (((CTLLatticeNode) preAln).isZeroPostCount(this.au))
						{
							reCheckedAlns.remove(preAln.getIDLiteral());
							satUpdateAlns.add(preAln.getIDLiteral());
							length++;
							
							CheckerInfo.getInstance().add2CheckerInfo(this.au, preAln, CheckerInfo.sat4SubFormula, true);
							CheckerInfo.getInstance().add2CheckerInfo(this.au, preAln, CheckerInfo.satNew4SubFormula, false);
						}
					}
			}	/* inner while */
		}	/* outer while */
	}
	
	private void updateAUFalHash()
	{
		// get set: Sat(\top \circ)(\neg \Varphi \land \neg \Psi)
		List<String> falUpdateAlns = CheckerInfo.getInstance().getAUFalUpdateAlnsID(au, arg1, arg2);
		if(falUpdateAlns.size() == 0)
			return;
		
		// state in falUpdateAlns falsify AU trivially.
		CheckerInfo.getInstance().add2CheckerInfo(this.au, falUpdateAlns, CheckerInfo.fal4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(this.au, falUpdateAlns, CheckerInfo.falNew4SubFormula, false);
		
		AbstractLatticeNode aln = null;
		ArrayList<AbstractLatticeNode> preAlns = null;
		AbstractLatticeNode preAln = null;
		
		int length = falUpdateAlns.size();
		int index = 0;
		while(index != length)
		{
			aln = CheckerInfo.getInstance().getAlnById(falUpdateAlns.get(index++));
			
			preAlns = aln.getprevious();
			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
			while(iter.hasNext())
			{
				preAln = iter.next();
				// update for AU(?) to AU(true)
				if(CheckerInfo.getInstance().isUnknown(preAln, this.au))
				{
					if(CheckerInfo.getInstance().shouldUpdated4EUorAU(preAln, arg1, arg2))
					{
						falUpdateAlns.add(preAln.getIDLiteral());
						length++;

						CheckerInfo.getInstance().add2CheckerInfo(this.au, preAln, CheckerInfo.fal4SubFormula, true);
						CheckerInfo.getInstance().add2CheckerInfo(this.au, preAln, CheckerInfo.falNew4SubFormula, false);
					}
				}	/* outer if */
			}	/* inner while */
		}	/* outer while */
	}
//	
//	/**
//	 * update for AU(false)
//	 */
//	private void updateAUFal()
//	{
//		// get set: Sat(\top \circ)(\neg \Varphi \land \neg \Psi)
//		List<AbstractLatticeNode> falUpdateAlns = this.getFalUpdateAlns();
//		if(falUpdateAlns.size() == 0)
//			return;
//		
//		// state in falUpdateAlns falsify AU trivially.
//		super.add2FalNew(this.au, falUpdateAlns);
//		logger.info("New satisfaction set for formula [ " + this.au + " ] is " + AbstractCTLChecker.satNew4SubFormula.get(this.au));
//		super.add2Fal(this.au, falUpdateAlns);
//		logger.info("Satisfaction set for formula [ " + this.au + " ] is " + AbstractCTLChecker.sat4SubFormula.get(this.au));
//		
//		AbstractLatticeNode aln = null;
//		ArrayList<AbstractLatticeNode> preAlns = null;
//		AbstractLatticeNode preAln = null;
//		
//		int length = falUpdateAlns.size();
//		int index = 0;
//		while(index != length)
//		{
//			aln = falUpdateAlns.get(index++);
//			
//			preAlns = aln.getprevious();
//			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
//			while(iter.hasNext())
//			{
//				preAln = iter.next();
//				// update for AU(?) to AU(true)
//				if(super.isUnknown(preAln, this.au))
//				{
//					if(this.shouldUpdated(preAln))
//					{
//						falUpdateAlns.add(preAln);
//						length++;
//						
//						super.add2Fal(this.au, preAln);
//						super.add2FalNew(this.au, preAln);
//						
//						// for node
////						((CTLLatticeNode) preAln).addFalSubFormula(this.au);
//					}
//				}	/* outer if */
//			}	/* inner while */
//		}	/* outer while */
//	}
//	
//	/**
//	 * backward update for A(\top)(\Varphi U \Psi)
//	 */
//	private void updateAUSat()
//	{
//		// satUpdateAlns administers the states s with s satisfying AU
//		List<AbstractLatticeNode> satUpdateAlns = AbstractCTLChecker.satNew4SubFormula.get(arg2);
//		if(satUpdateAlns == null)
//			return;
//		
//		/**
//		 * modified by hengxin(hengxin0912@gmail.com)
//		 * 
//		 * 07/112011
//		 */
//		if(AbstractCTLChecker.satNew4CountSubFormula.get(this.au) != null)
//		{
//			satUpdateAlns = this.add2SatUpdateAlns(satUpdateAlns);
//		}
//		
//		// state s in satUpdateAlns satisfies AU trivially.
//		super.add2SatNew(this.au, satUpdateAlns);
//		logger.info("New satisfaction set for formula [ " + this.au + " ] is " + AbstractCTLChecker.satNew4SubFormula.get(this.au));
//		super.add2Sat(this.au, satUpdateAlns);
//		logger.info("Satisfaction set for formula [ " + this.au + " ] is " + AbstractCTLChecker.sat4SubFormula.get(this.au));
//	
//		// reCheckedAlns contains any state s which maybe satisfies AU and needs to be checked further
//		List<AbstractLatticeNode> reCheckedAlns = this.getReCheckedAlnsInFal();
//		
//		// compute count(@see net.sourceforge.mipa.predicatedetection.lattice.ctl)
//		
//		/**
//		 * The following is the process of update
//		 */
//		int index = 0;
//		int length = satUpdateAlns.size();
//		AbstractLatticeNode aln = null;
//		while(index != length)
//		{
//			aln = satUpdateAlns.get(index++);
//			
//			List<AbstractLatticeNode> preAlns = aln.getprevious();
//			AbstractLatticeNode preAln = null;
//			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
//			while (iter.hasNext())
//			{
//				preAln = iter.next();
//				
//				if (super.isUnknown(preAln, this.au))
//					if (reCheckedAlns.contains(preAln))
//					{
//						((CTLLatticeNode) preAln).decPostCount(this.au);
//
//						if (((CTLLatticeNode) preAln).isZeroPostCount(this.au))
//						{
//							reCheckedAlns.remove(preAln);
//							satUpdateAlns.add(preAln);
//							length++;
//
//							super.add2Sat(this.au, preAln);
//							super.add2SatNew(this.au, preAln);
//							
//							// for node
////							((CTLLatticeNode) preAln).addSatSubFormula(this.au);
//						}
//					}
//			}	/* inner while */
//		}	/* outer while */
//	}
//
//	/**
//	 *   
//	 * should the lattice node be updated?
//	 * 
//	 * @param aln lattice node
//	 * @return true if the lattice node satisfies arg1 and falsifies arg2.
//	 */
//	private boolean shouldUpdated(AbstractLatticeNode aln)
//	{
//		return (AbstractCTLChecker.sat4SubFormula.containsKey(arg1) 
//				&& AbstractCTLChecker.sat4SubFormula.get(arg1).contains(aln))
//		  && (AbstractCTLChecker.fal4SubFormula.containsKey(arg2)
//				  && AbstractCTLChecker.fal4SubFormula.get(arg2).contains(aln));
//	}
//	
//	/**
//	 * get list of lattice nodes used to falsify update
//	 *   
//	 * @return list of lattice nodes used to falsify update
//	 */
//	private List<AbstractLatticeNode> getFalUpdateAlns()
//	{
//		List<AbstractLatticeNode> falNewArg1Alns = AbstractCTLChecker.falNew4SubFormula.get(this.arg1);
//		List<AbstractLatticeNode> falNewArg2Alns = AbstractCTLChecker.falNew4SubFormula.get(this.arg2);
//		
//		List<AbstractLatticeNode> falNewAlns = new ArrayList<AbstractLatticeNode>();
//		
//		if(falNewArg1Alns == null)
//		{
//			if(falNewArg2Alns != null)
//				return falNewArg2Alns;
//			else
//				return falNewAlns;
//		}
//		else
//		{
//			if(falNewArg2Alns == null)
//				return falNewArg1Alns;
//			else
//				return ListUtils.intersection(falNewArg1Alns, falNewArg2Alns);
//		}
//	}
//	
//	/**
//	 * considering counting(???)
//	 * @return
//	 */
//	private List<AbstractLatticeNode> add2SatUpdateAlns(List<AbstractLatticeNode> falUpdateAlns)
//	{
//		List <AbstractLatticeNode> falUpdateAlnsAgain = ListUtils.union(falUpdateAlns, AbstractCTLChecker.satNew4CountSubFormula.get(this.au));
//		AbstractCTLChecker.resetFalNewCount(this.au);
//		
//		return falUpdateAlnsAgain;
//	}
//	
//	/**
//	 * get list of lattice nodes which need to be checked further during the process of updateFal()
//	 * 
//	 * @return list of lattice nodes which need to be checked further during the process of updateFal()
//	 */
//	private List<AbstractLatticeNode> getReCheckedAlnsInFal()
//	{
//		List<AbstractLatticeNode> satArg1Alns = AbstractCTLChecker.sat4SubFormula.get(this.arg1);
//		List<AbstractLatticeNode> falArg2Alns = AbstractCTLChecker.fal4SubFormula.get(this.arg2);
//		List<AbstractLatticeNode> reCheckedAlns = new ArrayList<AbstractLatticeNode>();
//		
//		if(satArg1Alns == null)
//		{
//			if(falArg2Alns == null)
//				return reCheckedAlns;
//			else
//				return falArg2Alns;
//		}
//		else
//		{
//			if(falArg2Alns == null)
//				return satArg1Alns;
//			else
//				return ListUtils.intersection(satArg1Alns, falArg2Alns);
//		}
//	}

}
