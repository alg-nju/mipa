package net.sourceforge.mipa.predicatedetection.lattice;

/**
 * interface for lattice construction
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public interface ILatticeConstructor
{
	public void handleLatticeNode(String[] id);
	public void handleNonLatticeNode(String[] id);
}
