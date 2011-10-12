package net.sourceforge.mipa.experiment;

import org.apache.log4j.Logger;

import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;

public class Experiment
{
	private static Logger logger = Logger.getLogger(Experiment.class);
	
	public Experiment()
	{
		
	}
	
	/**
	 * keep accounts for experiment 1 : Occurrences of the third value "inconclusive"
	 * 
	 * format: N_{inc} \t N_{Lattice} \n where N_{inc} denotes the number of lattice nodes evaluated unknown
	 * and N_{Lattice} denotes the number of lattice nodes checked in all
	 *
	 */
	public void keepAccount4Experiment1(AbstractLatticeNode aln)
	{
		Formula formula = CTLParser.getInstance().getCtlFormula();
		
////		if(unknown4SubFormula.get(formula).contains(aln.getIDLiteral()))
//			logger.fatal(unknown4SubFormula.get(formula).size() + "\t" + nodeTable.size());
		
		if(CheckerInfo.unknown4SubFormula.get(formula).size() != 0)
			logger.fatal('1');
		else
			logger.fatal('0');
	}
	
	/**
	 * keep accounts for experiment 2 : Response time and space cost
	 * @param time response time
	 */
	public void keepAccount4Experiment2(long time)
	{
		System.out.println(time);
		System.out.println(CheckerInfo.nodeTable.size());
		
		logger.fatal(time + "\t" + CheckerInfo.nodeTable.size());
	}
}
