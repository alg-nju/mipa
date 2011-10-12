package net.sourceforge.mipa.predicatedetection.lattice.ctl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

/**
 * represents lattice node for ctl
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class CTLLatticeNode extends AbstractLatticeNode
{
	private static final long serialVersionUID = 6817453887264877985L;

	private int dimension = 0;
	
	/**
	 * labels for this lattice node
	 * Labeling is a function "N(node) -> 2^(AP)"
	 */
	private Hashtable<String, Boolean> labels = new Hashtable<String, Boolean>();
	
	/*
	 *  is the node has been labeled 
	 *  (related to on-line checking algorithm)
	 */
	private boolean isLabeled = false;
	
	// sub-formulae have been checked already(i.e., have been evaluated to be true or false) 
	private List<Composite> checkedSubFormulae = new ArrayList<Composite>();
	
	// sub-formulae have been evaluated to be true
	private List<Composite> satSubFormulae = new ArrayList<Composite>();
	// sub-formulae have been evaluated to be false
	private List<Composite> falSubFormulae = new ArrayList<Composite>();
	
	// count of children( for checking EU(false update) and AU(true update))
	private Hashtable<Composite,Integer> postCount = new Hashtable<Composite,Integer>();

	
	public CTLLatticeNode(LocalState[] gs, String[] s)
	{
		super(gs, s);
		
	    this.dimension = gs.length;
	    
		this.initCountForEU();
		this.initCountForAU();
	}

	/**
	 * add new label to this node
	 */
	public void addLabel(String label, boolean sat)
	{
		this.labels.put(label, sat);
		
		if(! this.isLabeled)
			this.isLabeled = true;
	}
	
	public boolean isLabeled()
	{
		return this.isLabeled;
	}
	
	public Hashtable<String, Boolean> getLabels()
	{
		return this.labels;
	}
	
	/**
	 * for logger and debug
	 * @return the labels(i.e., APs) which is in the form of string attached to this node
	 */
	public String getLabelsLiteral()
	{
		return "The lattice node [ " + Arrays.toString(this.getID()) + " ] is labeled with "
		  + this.labels.toString();
	}
	
	/************* general information related to checking algorithm *******************/
	
	/**
	 * add this sub-formula which has been checked on this node into related data structure
	 * @param subFormula sub-formula be checked
	 */
	public void add2CheckedAlready(Composite subFormula)
	{
		this.checkedSubFormulae.add(subFormula);
	}
	
	/**
	 * is this subformula has been checked on this node
	 * @param subFormula sub-formula
	 * @return true if this node has been checked with respect to(wrt) the subformula
	 */
	public boolean isCheckedWRT(Composite subFormula)
	{
		return this.checkedSubFormulae.contains(subFormula);
	}
	
	/**
	 * add this sub-formula which has been checked and evaluated to be true into related data structure 
	 * @param subFormula sub-formula evaluated to be true 
	 */
	public void addSatSubFormula(Composite subFormula)
	{
		this.satSubFormulae.add(subFormula);
	}
	
	/**
	 * add this sub-formula which has been checked and evaluated to be false into related data structure 
	 * @param subFormula sub-formula evaluated to be false
	 */
	public void addFalSubFormula(Composite formula)
	{
		this.falSubFormulae.add(formula);
	}
	
	/**
	 * get list of sub-formulae which have been checked and evaluated to be true
	 * @return list of sub-formulae which have been checked and evaluated to be true
	 */
	public List<Composite> getSatSubFormulae()
	{
		return this.satSubFormulae;
	}
	
	/**
	 * get list of sub-formulae which have been checked and evaluated to be false
	 * @return list of sub-formulae which have been checked and evaluated to be false
	 */
	public List<Composite> getFalSubFormulae()
	{
		return this.falSubFormulae;
	}
	
	

	/**************************************************************************
	 * related to checking algorithm specific to modalities EU and AU
	 * FIXME: to be extracted from this class
	 *************************************************************************/
	// initialize "postCount" data structure for update of EU(false)
	private void initCountForEU()
	{
		List<Composite> subFormulae = CTLParser.getInstance().getSubFormulae();
		Iterator<Composite> iter = subFormulae.iterator();
		Composite subFormula = null;
		while(iter.hasNext())
		{
			subFormula = iter.next();

			if(CTLParser.getInstance().isEUSubFormula(subFormula))
				this.resetPostCount(subFormula);
		}
	}
	
	/**
	 * initialize "postCount" data structure for update of AU(true)
	 */
	private void initCountForAU()
	{
		List<Composite> subFormulae = CTLParser.getInstance().getSubFormulae();
		Iterator<Composite> iter = subFormulae.iterator();
		Composite subFormula = null;
		while(iter.hasNext())
		{
			subFormula = iter.next();

			if(CTLParser.getInstance().isAUSubFormula(subFormula))
				this.resetPostCount(subFormula);
		}
	}
	
	public void resetPostCount(Composite formula)
	{
		this.postCount.put(formula, this.dimension);
	}
	
	public void decPostCount()
	{
		Iterator<Composite> iter = this.postCount.keySet().iterator();
		while(iter.hasNext())
		{
			this.decPostCount(iter.next());
		}
	}
	
	public void decPostCount(Composite formula)
	{
		int count = this.postCount.get(formula);
		count = count - 1;
		this.postCount.put(formula, count);
	}
	
	public boolean isZeroPostCount(Composite formula)
	{
		return this.postCount.get(formula) == 0;
	}
	
}
