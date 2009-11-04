package net.sourceforge.mipa.predicatedetection.lattice;

import java.util.ArrayList;

public abstract class AbstractLatticeNode {

	protected String[] ID;

	protected LocalState[] globalState;

	protected ArrayList<AbstractLatticeNode> next;

	protected ArrayList<AbstractLatticeNode> previous;

	public AbstractLatticeNode(LocalState[] gs, String[] s) {
		ID = s;
		globalState = new LocalState[gs.length];
		for (int i = 0; i < gs.length; i++) {
			globalState[i] = gs[i];
		}
		next = new ArrayList<AbstractLatticeNode>();
		previous = new ArrayList<AbstractLatticeNode>();
	}

}
