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
package net.sourceforge.mipa.predicatedetection.lattice.SCP;

import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

/**
 * 
 * @author Tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class SCPLatticeNode extends AbstractLatticeNode {

	/**record whether all the path to the current node satisfy the predicate**/
	private boolean pathflag;

	/**record whether the node has been checked**/
	private boolean visited;
	
	/**record whether the node is the tail of the lattice**/
	private boolean tailflag;
	
	/**record whether the node is inside the continuous true-value nodes region**/
	private boolean insideflag;

	public SCPLatticeNode(LocalState[] gs, String[] s) {
		super(gs, s);
		// TODO Auto-generated constructor stub
		pathflag = false;
		visited = false;
		tailflag = false;
		insideflag = false;
	}

	public boolean getvisited() {
		return visited;
	}

	public void setvisited(boolean visited) {
		this.visited = visited;
	}

	public boolean getpathflag() {
		return pathflag;
	}

	public void setpathflag(boolean pathflag) {
		this.pathflag = pathflag;
	}
	
	public boolean gettailflag() {
		return tailflag;
	}

	public void settailflag(boolean tailflag) {
		this.tailflag = tailflag;
	}
	
	public boolean getinsideflag() {
		return insideflag;
	}

	public void setinsideflag(boolean insideflag) {
		this.insideflag = insideflag;
	}

	/**
	 * compute the global predicate value on lattice node.
	 * 
	 * @return
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

}
