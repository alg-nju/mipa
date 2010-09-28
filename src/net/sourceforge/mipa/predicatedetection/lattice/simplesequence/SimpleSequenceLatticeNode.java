package net.sourceforge.mipa.predicatedetection.lattice.simplesequence;

import net.sourceforge.mipa.predicatedetection.lattice.*;

public class SimpleSequenceLatticeNode extends AbstractLatticeNode {

	private int prefix;

	private boolean verified;

	public SimpleSequenceLatticeNode(LocalState[] gs, String[] s) {
		super(gs, s);
		prefix = -10;
		verified = false;
	}

	public int getLevel() {
		int level = Integer.valueOf(ID[0]).intValue();
		for (int i = 1; i < ID.length; i++) {
			int le = Integer.valueOf(ID[i]).intValue();
			if (level > le) {
				level = le;
			}
		}
		return level;
	}

	public void setprefix(int prefix) {
		this.prefix = prefix;
	}

	public int getprefix() {
		return prefix;
	}

	public void setverified(boolean verified) {
		this.verified = verified;
	}

	public boolean getverified() {
		return verified;
	}

}
