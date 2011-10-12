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

import java.io.Serializable;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 * implements Serializable
 * modified by hengxin(hengxin0912@gmail.com)
 * 
 */
public class LocalState implements Serializable{

	private static final long serialVersionUID = -16077873833967489L;

	protected int processID;
	
	protected int intervalID;

	protected LatticeVectorClock vc;

	protected boolean localPredicate;

	public LocalState(int pID, int iID, LatticeVectorClock lvc, boolean lp) {
		processID = pID;
		intervalID = iID;
		vc = new LatticeVectorClock(lvc);
		localPredicate = lp;
	}

	public void setVC(LatticeVectorClock lvc) {

		vc.setVectorClock(lvc.getVectorClock());

	}
	
	public int getintervalID(){
		return intervalID;
	}

	public boolean getlocalPredicate() {
		return localPredicate;
	}

	public LatticeVectorClock getvc() {
		return vc;
	}

}
