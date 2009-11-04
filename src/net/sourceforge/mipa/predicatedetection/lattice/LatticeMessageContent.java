package net.sourceforge.mipa.predicatedetection.lattice;

import java.io.Serializable;

public class LatticeMessageContent implements Serializable {

	private static final long serialVersionUID = -2767579403415223094L;

	private LatticeVectorClock lvc;

	private boolean localPredicate;

	public LatticeMessageContent(LatticeVectorClock lvc, boolean localPredicate) {
		this.lvc = lvc;
		this.localPredicate = localPredicate;
	}

	public LatticeVectorClock getlvc() {
		return lvc;
	}

	public boolean getlocalPredicate() {
		return localPredicate;
	}

	public void setlvc(LatticeVectorClock lvc) {
		this.lvc = lvc;
	}

	public void setlocalPredicate(boolean localPredicate) {
		this.localPredicate = localPredicate;
	}

}
