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

import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

/**
 * WCP check based on lattice
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class WCPLatticeChecker extends LatticeChecker {

	private static final long serialVersionUID = 4805292792830427418L;

	private PrintWriter out = null;

	public WCPLatticeChecker(ResultCallback application, String predicateID, String checkerName,
			String[] normalProcesses) {
		super(application, predicateID, checkerName, normalProcesses);
		// TODO Auto-generated constructor stub

		try {
			out = new PrintWriter(LOG_DIRECTORY + "/found_WCP.log");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public WCPLatticeNode createNode(LocalState[] globalState, String[] s) {
		// TODO Auto-generated method stub
		WCPLatticeNode node = new WCPLatticeNode(globalState, s);
		return node;
	}

	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode) {

		detect((WCPLatticeNode) currentNode);
	}

	public void detect(WCPLatticeNode node) {
		ArrayList<WCPLatticeNode> list = new ArrayList<WCPLatticeNode>();
		for (int i = 0; i < node.getprevious().size(); i++) {
            list.add((WCPLatticeNode) node.getprevious().get(i));
        }
		Iterator<WCPLatticeNode> iter = list.iterator();
		while (iter.hasNext()) {
			WCPLatticeNode child = iter.next();
			// if the node has not been visited
			if (child.getVisited() == false) {
				// if the global predicate is true, then detected
				child.setVisited(true);
				if (child.cgs()) {
					LocalState[] gs = child.getglobalState();
					for (int i = 0; i < gs.length; i++) {
						try {
							application.callback(predicateID, String.valueOf(true));
							String end = i + 1 != children.length ? " "
									: "\r\n";
							out.print("[" + gs[i].getvc().toString() + "]"
									+ end);
							out.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				detect(child);
			}
		}
	}
}
