/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the term of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.mipa.predicatedetection.lattice;

import java.util.ArrayList;

import net.sourceforge.mipa.predicatedetection.lattice.SCP.SCPLatticeNode;
//import net.sourceforge.mipa.predicatedetection.lattice.simplesequence.SSLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.wcp.WCPLatticeNode;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public abstract class AbstractLatticeNode {

	// label the node, not necessary.
	protected String[] ID;

	protected LocalState[] globalState;

	protected ArrayList<AbstractLatticeNode> next;

	protected ArrayList<AbstractLatticeNode> previous;

	// link to children type node
	protected WCPLatticeNode WCPNode = null;

	protected SCPLatticeNode SCPNode = null;

	//protected SSLatticeNode SSNode = null;

	public AbstractLatticeNode(LocalState[] gs, String[] s) {
		ID = s;
		globalState = new LocalState[gs.length];
		for (int i = 0; i < gs.length; i++) {
			globalState[i] = gs[i];
		}
		next = new ArrayList<AbstractLatticeNode>();
		previous = new ArrayList<AbstractLatticeNode>();
	}

	public String[] getID() {
		return ID;
	}

	public LocalState[] getglobalState() {
		return globalState;
	}

	public ArrayList<AbstractLatticeNode> getnext() {
		return next;
	}

	public ArrayList<AbstractLatticeNode> getprevious() {
		return previous;
	}

	public void setWCPNode(WCPLatticeNode WCPNode) {
		this.WCPNode = WCPNode;
	}

	public WCPLatticeNode getWCPNode() {
		return WCPNode;
	}

/*	public void setSSNode(SSLatticeNode SSNode) {
		this.SSNode = SSNode;
	}

	public SSLatticeNode getSSNode() {
		return SSNode;
	}
*/
	public void setSCPNode(SCPLatticeNode SCPNode) {
		this.SCPNode = SCPNode;
	}

	public SCPLatticeNode getSCPNode() {
		return SCPNode;
	}

}
