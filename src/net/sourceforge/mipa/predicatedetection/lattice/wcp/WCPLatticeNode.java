package net.sourceforge.mipa.predicatedetection.lattice.wcp;

import net.sourceforge.mipa.predicatedetection.lattice.*;

public class WCPLatticeNode extends AbstractLatticeNode {

	private boolean visited;

	public WCPLatticeNode(LocalState[] gs, String[] s) {
		super(gs, s);
		// TODO Auto-generated constructor stub
		visited = false;
	}

	public boolean cgs() {
		boolean result = true;
		for (int i = 0; i < globalState.length; i++) {
			if (globalState[i].getlocalPredicate() == false) {
				result = false;
			}
		}
		return result;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean getVisited() {
		return visited;
	}

}
