package net.sourceforge.mipa.predicatedetection.lattice;

import java.util.ArrayList;
import net.sourceforge.mipa.predicatedetection.lattice.wcp.WCPLatticeNode;

public abstract class AbstractLatticeNode {

	protected String[] ID;

	protected LocalState[] globalState;

	protected ArrayList<AbstractLatticeNode> next;

	protected ArrayList<AbstractLatticeNode> previous;
	
	protected WCPLatticeNode WCPNode=null;

	public AbstractLatticeNode(LocalState[] gs, String[] s) {
		ID = s;
		globalState = new LocalState[gs.length];
		for (int i = 0; i < gs.length; i++) {
			globalState[i] = gs[i];
		}
		next = new ArrayList<AbstractLatticeNode>();
		previous = new ArrayList<AbstractLatticeNode>();
	}
	
	public String[] getID(){
		return ID;
	}
	
	public LocalState[] getglobalState(){
		return globalState;
	}
	
	public ArrayList<AbstractLatticeNode> getnext(){
		return next;
	}
	
	public ArrayList<AbstractLatticeNode> getprevious(){
		return previous;
	}
	
	public void setWCPNode(WCPLatticeNode WCPNode){
		this.WCPNode=WCPNode;
	}
	
	public WCPLatticeNode getWCPNode(){
		return WCPNode;
	}

}
