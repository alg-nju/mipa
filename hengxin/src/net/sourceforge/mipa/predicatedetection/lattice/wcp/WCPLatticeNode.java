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
package net.sourceforge.mipa.predicatedetection.lattice.wcp;

import net.sourceforge.mipa.predicatedetection.lattice.*;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class WCPLatticeNode extends AbstractLatticeNode {

	/** label the node whether been checked */
	private boolean visited;

	public WCPLatticeNode(LocalState[] gs, String[] s) {
		super(gs, s);
		// TODO Auto-generated constructor stub
		visited = false;
	}

	/**
	 * compute the global predicate value on lattice node.
	 * 
	 * @return the result
	 */
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
