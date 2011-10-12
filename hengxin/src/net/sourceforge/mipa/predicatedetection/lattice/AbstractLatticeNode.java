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
import java.util.Arrays;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 *
 * extends SerialCloneable
 * modified by hengxin(hengxin0912@gmail.com)
 */
public abstract class AbstractLatticeNode /* extends SerialCloneable */{

	private static final long serialVersionUID = 3898874491584424406L;

	// label the node, not necessary.
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

	public String[] getID() {
		return ID;
	}

	public String getIDLiteral()
	{
		StringBuilder sb = new StringBuilder();
		
		for(String localId : ID)
		{
			sb.append(localId).append('_');
		}
		
		return sb.toString();
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

	/**
	 * added by hengxin(hengxin0912@gmail.com)
	 */
	@Override
	public String toString()
	{
		return Arrays.toString(this.ID);
	}
}
