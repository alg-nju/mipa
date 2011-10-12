package net.sourceforge.mipa.util.algorithm.bfs;

import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

/**
 * interface for algorithmic framework of breadth-first search
 * {@see net.sourceforge.mipa.util.algorithm.bfs.AbstractBFSFramework}
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public interface IBFS
{
	/**
	 * check if the node should be visited.
	 * 
	 * @return true if the node should be visited; false, otherwise.
	 */
	public boolean toVisit(AbstractLatticeNode perNode);
	
	/**
	 * The method of processing this node can be put here.
	 * 
	 * @param node node can be and should be processed now
	 */
	public void processNode(AbstractLatticeNode node);

	public void processLink(AbstractLatticeNode preNode,
			AbstractLatticeNode node);
}
