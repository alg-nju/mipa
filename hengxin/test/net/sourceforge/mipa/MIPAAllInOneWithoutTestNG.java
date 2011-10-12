package net.sourceforge.mipa;

import util.MIPAAllInOne;

/**
 * run mipa system all in one
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class MIPAAllInOneWithoutTestNG
{
	
	public static void main(String[] args) 
	{
		// ctl test
		MIPAAllInOne.getInstance("ctl/predicate_ctl_au_experiment0.xml").runMIPAAllInOne();
		
		// sequence test
		// MIPAAllInOne.getInstance("predicate_sequence.xml").runMIPAAllInOne();
	}

}
