package net.sourceforge.mipa.predicatedetection.lattice;

public class LocalState {

	protected int processID;

	protected LatticeVectorClock vc;

	protected boolean localPredicate;

	public LocalState(int pID, LatticeVectorClock lvc, boolean lp) {
		processID = pID;
		vc = new LatticeVectorClock(lvc);
		localPredicate = lp;
	}

	public void setVC(LatticeVectorClock lvc) {

		vc.setVectorClock(lvc.getVectorClock());

	}

}
