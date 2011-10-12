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
 * checker for modality "EU" 
 * @author hengxin
 *
 */
public class CTL3EUChecker extends CTLCheckerFramework /* implements IBFS */
{

	private static Logger logger = Logger.getLogger(CTL3EUChecker.class);
	
	/**
	 * eu : EU subformula
	 * arg1 : the first argument of EU subformula
	 * arg2 : the second argument of EU subformula
	 */
	private Composite eu = null;
	private Composite arg1 = null;
	private Composite arg2 = null;
	
	public CTL3EUChecker(Structure specification,
			AbstractLatticeNode startNode, AbstractLatticeNode endNode)
	{
		super(specification, startNode, endNode);
	}


	/****************************************************************************
	 * 					related to ctl checking algorithm
	 * **************************************************************************/
	/**
	 * ctl3 checking algorithm for EU modality.
	 */
	@Override
	public boolean checkModality(Formula eu)
	{
		this.eu = eu;
		this.arg1 = (Composite) eu.getChildren().get(0);
		this.arg2 = (Composite) eu.getChildren().get(1);
		
		logger.info("Checking subformula [ " + this.eu + " ] with [ arg1: " + 
				this.arg1 + " ] and [ arg2: " + this.arg2 + " ].");

		CheckerInfo.getInstance().resetCheckerInfo(this.eu, CheckerInfo.satNew4SubFormula);
		this.updateSatHash();
		logger.info("Satisfaction set for formula [ " + this.eu + " ] is " + CheckerInfo.sat4SubFormula.get(this.eu));
		
		CheckerInfo.getInstance().resetCheckerInfo(this.eu, CheckerInfo.falNew4SubFormula);
		this.updateFalHash();
		logger.info("Falsifaction set for formula [ " + this.eu + " ] is " + CheckerInfo.fal4SubFormula.get(this.eu));

		return true;
	}
	
	private void updateSatHash()
	{
		// satUpdateAlns administers the states s with s satisfying EU
		List<String> satUpdateAlns = CheckerInfo.satNew4SubFormula.get(arg2);
		if(satUpdateAlns.size() == 0)
			return;
		
		// s in satUpdateAlns satisfies EU trivially.
		CheckerInfo.getInstance().add2CheckerInfo(this.eu, satUpdateAlns, CheckerInfo.sat4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(this.eu, satUpdateAlns, CheckerInfo.satNew4SubFormula, false);

		AbstractLatticeNode aln = null;
		ArrayList<AbstractLatticeNode> preAlns = null;
		AbstractLatticeNode preAln = null;
		
		int length = satUpdateAlns.size();
		int index = 0;
		while(index != length)
		{
			aln = CheckerInfo.getInstance().getAlnById(satUpdateAlns.get(index++));
			
			preAlns = aln.getprevious();
			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
			while(iter.hasNext())
			{
				preAln = iter.next();
				
				// update for EU(?) to EU(true)
				if(CheckerInfo.getInstance().isUnknown(preAln, this.eu))
				{
					if(CheckerInfo.getInstance().shouldUpdated4EUorAU(preAln, arg1, arg2))
					{
						satUpdateAlns.add(preAln.getIDLiteral());
						length++;
						
						CheckerInfo.getInstance().add2CheckerInfo(this.eu, preAln, CheckerInfo.sat4SubFormula, true);
						CheckerInfo.getInstance().add2CheckerInfo(this.eu, preAln, CheckerInfo.satNew4SubFormula, false);
					}
				}	/* outer if */
			}	/* inner while */
		}	/* outer while */
	}
	
	private void updateFalHash()
	{
		// get set: Sat(\top \circ)(\neg \Varphi \land \neg \Psi)
		List<String> falUpdateAlns = CheckerInfo.getInstance().getEUFalUpdateAlnsID(this.eu, this.arg1, this.arg2);
		
		if(falUpdateAlns.size() == 0)
			return;
		
		// s in falUpdateAlns falsifies EU trivially
		CheckerInfo.getInstance().add2CheckerInfo(this.eu, falUpdateAlns, CheckerInfo.fal4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(this.eu, falUpdateAlns, CheckerInfo.falNew4SubFormula, false);

		// reCheckedAlns contains any state s which maybe falsify EU and needs to be checked further
		List<String> reCheckedAlns = CheckerInfo.getInstance().getReChecked4EUorAU(arg1, arg2);
		
		/**
		 * added by hengxin(hengxin0912@gmail.com)
		 */
//		if(reCheckedAlns.size() == 0)
//			return;
		
		/**
		 * The following is the process of update
		 */
		int index = 0;
		int length = falUpdateAlns.size();
		AbstractLatticeNode aln = null;
		while(index != length)
		{
			aln = CheckerInfo.getInstance().getAlnById(falUpdateAlns.get(index++));
			
			List<AbstractLatticeNode> preAlns = aln.getprevious();
			AbstractLatticeNode preAln = null;
			Iterator<AbstractLatticeNode> iter = preAlns.iterator();
			while(iter.hasNext())
			{
				preAln = iter.next();
				if(CheckerInfo.getInstance().isUnknown(preAln, this.eu))
				{
					if(reCheckedAlns.contains(preAln.getIDLiteral()))
					{
						((CTLLatticeNode) preAln).decPostCount(this.eu);
						
						if(((CTLLatticeNode) preAln).isZeroPostCount(this.eu))
						{
							reCheckedAlns.remove(preAln.getIDLiteral());
							falUpdateAlns.add(preAln.getIDLiteral());
							length++;

							CheckerInfo.getInstance().add2CheckerInfo(this.eu, preAln, CheckerInfo.fal4SubFormula, true);
							CheckerInfo.getInstance().add2CheckerInfo(this.eu, preAln, CheckerInfo.falNew4SubFormula, false);
						}
					}
				}	/* outer if */
			}	/* inner while */
		}	/* outer while */
	}

}
