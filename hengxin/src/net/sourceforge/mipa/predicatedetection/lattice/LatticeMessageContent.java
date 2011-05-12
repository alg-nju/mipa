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

import net.sourceforge.mipa.components.MessageContent;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class LatticeMessageContent extends MessageContent {

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
