package net.sourceforge.mipa.util.algorithm.bfs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

/**
 * algorithmic framework for breadth-first search based on lattice structure
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class BFSFramework
{
//	private static Logger logger = Logger.getLogger(BFSFramework.class);
	
	private IBFS bfsCaller = null;
	
	public BFSFramework(IBFS caller)
	{
		this.bfsCaller = caller;
	}
	
	/**
	 * do breadth first search based on lattice structure from root
	 * 
	 * @param root node from which the search is conducted.
	 */
	public void breadthFirstSearchRev(AbstractLatticeNode root)
	{
		Queue<AbstractLatticeNode> pending = new LinkedList<AbstractLatticeNode>();
		pending.offer(root);
		
		AbstractLatticeNode node = null;
		ArrayList<AbstractLatticeNode> preNodes = null;
		AbstractLatticeNode preNode = null;
		Iterator<AbstractLatticeNode> iter = null;
		
		while(! pending.isEmpty())
		{
			node = pending.poll();
			preNodes = node.getprevious();
			iter = preNodes.iterator();
			while(iter.hasNext())
			{
				preNode = iter.next();
				
				this.bfsCaller.processLink(preNode,node);
				
				if(! pending.contains(preNode) && this.bfsCaller.toVisit(preNode))
					pending.offer(preNode);
			}
			
			// process ''node''
			this.bfsCaller.processNode(node);
		}
	}
}
