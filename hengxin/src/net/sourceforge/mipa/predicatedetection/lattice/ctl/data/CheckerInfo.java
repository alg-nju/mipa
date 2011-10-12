package net.sourceforge.mipa.predicatedetection.lattice.ctl.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;
import net.sourceforge.mipa.util.language.ListUtil;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

/**
 * data structure for checking algorithm
 * @author hengxin
 *
 */
public class CheckerInfo
{
	private static Logger logger = Logger.getLogger(CheckerInfo.class);

	// log of Dot ? if so, turn DOT on.
	public static boolean DOT = false;
	
	// to check ? if so, turn CHECK on.
	public static boolean CHECK = true;
	
	private static CheckerInfo instance = null;

	private CheckerInfo()
	{
		List<Composite> subFormulae = CTLParser.getInstance().getSubFormulae();
		Iterator<Composite> iter = subFormulae.iterator();
		Composite subFormula = null;
		while(iter.hasNext())
		{
			subFormula = iter.next();
			
			sat4SubFormula.put(subFormula, new ArrayList<String>());
			fal4SubFormula.put(subFormula, new ArrayList<String>());
			unknown4SubFormula.put(subFormula, new ArrayList<String>());
			satNew4SubFormula.put(subFormula, new ArrayList<String>());
			falNew4SubFormula.put(subFormula, new ArrayList<String>());
			falNew4CountSubFormula.put(subFormula, new ArrayList<String>());
			satNew4CountSubFormula.put(subFormula, new ArrayList<String>());
		}
	}
	public static CheckerInfo getInstance()
	{
		if(instance == null)
			instance = new CheckerInfo();
		
		return instance;
	}
	
	/***************************************************************************
	 * ***************** data structure for checking algorithm *****************
	 * *************************************************************************/
	/*
	 * sat4SubFormula : set of lattice nodes which satisfy the sub-formula
	 * fal4SubFormula : set of lattice nodes which falsify the sub-formula
	 * satNew4SubFormula : temporary set of new nodes which satisfy the sub-formula during the current iteration
	 * falNew4SubFormula : temporary set of new nodes which falsify the sub-formula during the current iteration
	 */
	public static Hashtable<Composite,List<String>> sat4SubFormula 
	  = new Hashtable<Composite,List<String>>();
	
	public static Hashtable<Composite, List<String>> fal4SubFormula
	  = new Hashtable<Composite, List<String>>();
	
	public static Hashtable<Composite, List<String>> unknown4SubFormula
	  = new Hashtable<Composite, List<String>>();
	
	public static Hashtable<Composite,List<String>> satNew4SubFormula 
	  = new Hashtable<Composite,List<String>>();
	
	public static Hashtable<Composite, List<String>> falNew4SubFormula
	  = new Hashtable<Composite, List<String>>();
	
	public static Hashtable<Composite, List<String>> falNew4CountSubFormula
	  = new Hashtable<Composite, List<String>>();
	
	public static Hashtable<Composite, List<String>> satNew4CountSubFormula
	  = new Hashtable<Composite, List<String>>();
	
	// store the lattice nodes in the form of hashtable
	public static Hashtable<String,AbstractLatticeNode> nodeTable = new Hashtable<String,AbstractLatticeNode>();
//	public static int numOfNodes = 0;
	
	/***************************************************************************
	 * *** related to manipulation of data structure for checking algorithm ****
	 * *************************************************************************/

	/**
	 * add checking information into data structures
	 * 
	 * @param subFormula subFormula to be checked with
	 * @param latticeNode lattice node to be checked on
	 * @param dataStructure data structure related
	 * @param fresh is it the first time to add this checking information  
	 */
	public void add2CheckerInfo(Composite subFormula, AbstractLatticeNode latticeNode, Hashtable<Composite, List<String>> dataStructure, boolean fresh)
	{
		// add to sat or fal
		List<String> nodeList = dataStructure.get(subFormula);
		nodeList.add(latticeNode.getIDLiteral());
		dataStructure.put(subFormula, nodeList);
		
		if (fresh)
		{
			// remove from unknown
			List<String> toRemoveList = unknown4SubFormula.get(subFormula);
			toRemoveList.remove(latticeNode.getIDLiteral());
			unknown4SubFormula.put(subFormula, toRemoveList);
			
			// @param subFormula has been checked(i.e., evaluated to be true or false)
			// with respect to @param latticeNode
			if (latticeNode instanceof CTLLatticeNode)
			{
				((CTLLatticeNode) latticeNode).add2CheckedAlready(subFormula);
				
				if(dataStructure == sat4SubFormula)
					((CTLLatticeNode) latticeNode).addSatSubFormula(subFormula);
				else
					((CTLLatticeNode) latticeNode).addFalSubFormula(subFormula);
			}
		}
	}
	
	/**
	 * add checking information into data structures
	 * 
	 * @param subFormula subFormula to be checked with
	 * @param alns lattice nodes to be checked on
	 * @param dataStructure data structure related
	 * @param fresh is it the first time to add this checking information  
	 */
	public void add2CheckerInfo(Composite subFormula, List<String> alns, Hashtable<Composite, List<String>> dataStructure, boolean fresh)
	{
		if(alns.size() == 0)
			return;
		
		// add to sat or fal
		List<String> nodeList = dataStructure.get(subFormula);
		nodeList.addAll(alns);
		dataStructure.put(subFormula, nodeList);
		
		if (fresh)
		{
			// remove from unknown
			List<String> toRemoveList = unknown4SubFormula.get(subFormula);
			toRemoveList.removeAll(alns);
			unknown4SubFormula.put(subFormula, toRemoveList);
			
			// @param subFormula has been checked(i.e., evaluated to be true or false)
			// with respect to @param latticeNode
			Iterator<String> iter = alns.iterator();
			AbstractLatticeNode aln = null;
			while (iter.hasNext())
			{
				aln = nodeTable.get(iter.next());
				if(aln instanceof CTLLatticeNode)
				{
					((CTLLatticeNode) aln).add2CheckedAlready(subFormula);

					if (dataStructure == sat4SubFormula)
						((CTLLatticeNode) aln).addSatSubFormula(subFormula);
					else
						((CTLLatticeNode) aln).addFalSubFormula(subFormula);
				}
			}
		}
	}
	
	/**
	 * reset checking information in data structure
	 * 
	 * @param subFormula sub-formula related
	 * @param dataStructure data structure to be reseted
	 */
	public void resetCheckerInfo(Composite subFormula, Hashtable<Composite, List<String>> dataStructure)
	{
		dataStructure.put(subFormula, new ArrayList<String>());
	}
	
	/**
	 * get lattice node in the hashtable by id
	 * @param id id of lattice node 
	 * @return lattice node
	 */
	public AbstractLatticeNode getAlnById(String id)
	{
		return nodeTable.get(id);
	}
	
	/**
	 * store lattice node into hash table 
	 * and initialize some checking information and related data structure
	 *   
	 * @param aln lattice node to be stored
	 */
	public void storeNode2Table(AbstractLatticeNode aln)
	{
//		numOfNodes++;
		
		nodeTable.put(aln.getIDLiteral(), aln);
		
		// initialize
		List<Composite> subFormulae = CTLParser.getInstance().getSubFormulae();
		Iterator<Composite> iter = subFormulae.iterator();
		while(iter.hasNext())
		{
			this.add2CheckerInfo(iter.next(), aln, unknown4SubFormula, false);
		}
	}
	
	/**
	 * remove lattice node from hash table
	 * 
	 * {@link LatticeChecker} 
	 * if the lattice has been added into table without creation in reality
	 * 
	 * @param aln lattice node in hash table but not in the tree
	 */
	public void removeNodeFromTable(AbstractLatticeNode aln)
	{
		nodeTable.remove(aln.getIDLiteral());
	}
	/**
	 * does hash table contains the lattice node with id
	 * @param id id of lattice node
	 * @return true if the lattice node is in hash table
	 */
    public boolean containsNodeById(String id)
    {
    	return nodeTable.containsKey(id);
    }
    
	/**
	 * return all the possible(maybe not in reality) previous lattice nodes 
	 * 
	 * added by hengxin(hengxin0912@gmail.com)
	 * 
	 * @param nodeId id of lattice node
	 * @return list of all the possible previous lattice nodes in form of id
	 */
	public List<String> getAllPossiblePreviousByIds(String[] nodeId)
	{
		int length = nodeId.length;
		List<String> preNodeIds = new ArrayList<String>();
		
		for(int index = 0; index < length; index++)
		{
			String[] preId = new String[nodeId.length];
			for(int i=0;i<length;i++)
			{
				preId[i] = nodeId[i];
			}
			
			int id = Integer.parseInt(nodeId[index]);
			if(id > 0)
			{
				id--;
				preId[index] = String.valueOf(id);
				
				StringBuilder sb = new StringBuilder();
				
				for(String localId : preId)
				{
					sb.append(localId).append('_');
				}
				
				preNodeIds.add(sb.toString());
			}
		}
		
		return preNodeIds;
	}

	/**
	 * @param aln lattice node
	 * @param subFormula  subformula
	 * @return true if the node neither satisfies nor falsifies the subformula 
	 * 
	 */
	public boolean isUnknown(AbstractLatticeNode aln, Composite subFormula)
	{
		return ! ((CTLLatticeNode) aln).isCheckedWRT(subFormula);
		
		// or like this
//		return unknown4SubFormula.get(subFormula).contains(aln.getIDLiteral());
	}
	
	/**************************** Checking Information Specific to Modalities ******************/
	
	/**
	 * get node evaluated to true with respect to formula (\neg arg1 \land \neg arg2) freshly
	 * 
	 * @param eu EU sub-formula in the form of E(arg1 U arg2)
	 * @param arg1 the first argument of eu sub-formula
	 * @param arg2 the second argument of eu sub-formula
	 * 
	 * @return list of ids of nodes
	 */
	public List<String> getEUFalUpdateAlnsID(Composite eu, Composite arg1, Composite arg2)
	{
		
		List<String> falNewCountAlns = falNew4CountSubFormula.get(eu);
		this.resetCheckerInfo(eu, CheckerInfo.falNew4CountSubFormula);
		
		return ListUtil.union(this.getUpdatedAlnsID4AUorEU(arg1, arg2) ,falNewCountAlns);
	}
	
	/**
	 * get nodes evaluated to be true with respect to formula (arg2) freshly
	 * 
	 * @param au AU sub-formula in the form of A(arg1 U arg2)
	 * @param arg1 the first argument of au sub-formula
	 * @param arg2 the second argument of au sub-formula
	 * @return list ids of nodes
	 */
	public List<String> getAUSatUpdateAlnsID(Composite au, Composite arg1, Composite arg2)
	{
		List<String> satNewCountAlns = satNew4CountSubFormula.get(au);
		this.resetCheckerInfo(au, CheckerInfo.satNew4CountSubFormula);
		
		return ListUtil.union(satNew4SubFormula.get(arg2),satNewCountAlns);
	}
	
	public List<String> getAUFalUpdateAlnsID(Composite au, Composite arg1, Composite arg2)
	{
//		return ListUtils.intersection(CheckerInfo.falNew4SubFormula.get(arg1), CheckerInfo.falNew4SubFormula.get(arg2));
		
		return this.getUpdatedAlnsID4AUorEU(arg1, arg2);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getReChecked4EUorAU(Composite arg1, Composite arg2)
	{
		return ListUtils.intersection(CheckerInfo.sat4SubFormula.get(arg1), CheckerInfo.fal4SubFormula.get(arg2));
	}
	
	public boolean shouldUpdated4EUorAU(AbstractLatticeNode aln, Composite arg1, Composite arg2)
	{
		return CheckerInfo.sat4SubFormula.get(arg1).contains(aln.getIDLiteral()) &&
		CheckerInfo.fal4SubFormula.get(arg2).contains(aln.getIDLiteral());
	}
	
	/**
	 * get node evaluated to true with respect to formula (\neg arg1 \land \neg arg2) freshly
	 * 
	 * @param eu EU sub-formula in the form of E(arg1 U arg2)
	 * @param arg1 the first argument of eu sub-formula
	 * @param arg2 the second argument of eu sub-formula
	 * 
	 * @return list of ids of nodes
	 */
	@SuppressWarnings("unchecked")
	private List<String> getUpdatedAlnsID4AUorEU(Composite arg1, Composite arg2)
	{
		List<String> falArg1Alns = fal4SubFormula.get(arg1);
		List<String> falNewArg1Alns = falNew4SubFormula.get(arg1);
		List<String> falArg2Alns = fal4SubFormula.get(arg2);
		List<String> falNewArg2Alns = falNew4SubFormula.get(arg2);
		
		List<String> arg1_newArg2 = ListUtils.intersection(falArg1Alns, falNewArg2Alns);
		List<String> arg2_newArg1 = ListUtils.intersection(falNewArg1Alns, falArg2Alns);
		List<String> newArg1_newArg2 = ListUtils.intersection(falNewArg1Alns, falNewArg2Alns);
		
		List<String> arg1_arg2 = ListUtil.union(newArg1_newArg2,ListUtil.union(arg1_newArg2, arg2_newArg1));

		return arg1_arg2;
	}

}
