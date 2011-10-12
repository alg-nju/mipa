package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import java.util.List;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;

/**
 * checker for "NOT" connector
 * @author hengxin
 *
 */
public class CTL3NotChecker extends CTLCheckerFramework
{

	public CTL3NotChecker(Structure specification,
			AbstractLatticeNode startNode, AbstractLatticeNode endNode)
	{
		super(specification, startNode, endNode);
	}
	
	private Composite not = null;
	private Composite arg = null;
	
	/****************************************************************************
	 * 					related to ctl checking algorithm
	 * **************************************************************************/
	/**
	 * ctl3 checking algorithm for EU modality.
	 */
	@Override
	public boolean checkModality(Formula not)
	{
		this.not = not;
		this.arg = (Composite) not.getChildren().get(0);
		
		CheckerInfo.getInstance().resetCheckerInfo(not, CheckerInfo.satNew4SubFormula);
		this.updateSat();
		
		CheckerInfo.getInstance().resetCheckerInfo(not, CheckerInfo.falNew4SubFormula);
		this.updateFal();
		
		return true;
	}

	private void updateFal()
	{
		List<String> updateFalAlns = CheckerInfo.satNew4SubFormula.get(arg);
		if(updateFalAlns.size() == 0)
			return;
		
		CheckerInfo.getInstance().add2CheckerInfo(not, updateFalAlns, CheckerInfo.fal4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(not, updateFalAlns, CheckerInfo.falNew4SubFormula, false);
	}

	private void updateSat()
	{
		List<String> updateSatAlns = CheckerInfo.falNew4SubFormula.get(arg);
		if(updateSatAlns.size() == 0)
			return;
		
		CheckerInfo.getInstance().add2CheckerInfo(not, updateSatAlns, CheckerInfo.sat4SubFormula, true);
		CheckerInfo.getInstance().add2CheckerInfo(not, updateSatAlns, CheckerInfo.satNew4SubFormula, false);
	}

}
