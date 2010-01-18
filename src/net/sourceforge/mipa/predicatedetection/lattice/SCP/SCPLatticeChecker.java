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

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * SCP check based on lattice
 * 
 * @author Tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class SCPLatticeChecker extends LatticeChecker {

	private static final long serialVersionUID = -1172728258002666284L;

	private PrintWriter out = null;

	private int count;

	public SCPLatticeChecker(ResultCallback application, String checkerName,
			String[] normalProcesses) {
		super(application, checkerName, normalProcesses);
		// TODO Auto-generated constructor stub
		try {
			out = new PrintWriter(LOG_DIRECTORY + "/SCP.log");
		} catch (Exception e) {
			e.printStackTrace();
		}

		count = 0;
	}

	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode) {
		// TODO Auto-generated method stub
		detectSCP(currentNode);
	}

	public void detectSCP(AbstractLatticeNode node) {

		ArrayList<AbstractLatticeNode> pred = node.getprevious();

		Iterator<AbstractLatticeNode> itor = pred.iterator();
		while (itor.hasNext()) {
			AbstractLatticeNode prednode = itor.next();
			if (prednode.getSCPNode().getvisited() == false) {
				detectSCP(prednode);
				prednode.getSCPNode().setvisited(true);
			}
		}

		boolean flag = true;
		Iterator<AbstractLatticeNode> iterator = pred.iterator();
		while (iterator.hasNext()) {
			AbstractLatticeNode prenode = iterator.next();
			if (prenode.getSCPNode().getpathflag() == false) {
				flag = false;
				break;
			}
		}

		if (flag) {
			// detect SCP
			try {
				application.callback(String.valueOf(true));
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
			out.print(count + " ");
			String[] s = node.getID();
			out.print(s + " ");
			LocalState[] gs = node.getglobalState();
			for (int i = 0; i < gs.length; i++) {
				String end = i + 1 != children.length ? " " : "\r\n";
				out.print("[" + gs[i].getvc().toString() + "]" + end);
				out.flush();
			}
			flag = false;
		}

		node.getSCPNode().setpathflag(flag || node.getSCPNode().cgs());

	}

	@Override
	public AbstractLatticeNode createNode(LocalState[] globalState, String[] s) {
		// TODO Auto-generated method stub
		SCPLatticeNode node = new SCPLatticeNode(globalState, s);
		node.setSCPNode(node);
		return node;
	}

}
