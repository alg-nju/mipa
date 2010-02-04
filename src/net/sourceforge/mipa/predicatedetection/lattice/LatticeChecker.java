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

import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.AbstractFIFOChecker;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public abstract class LatticeChecker extends AbstractFIFOChecker {

	private static final long serialVersionUID = 1230192910873066775L;

	protected int dimension;

	/** store the current globalState */
	private LocalState[] globalState;

	/** store all the received state */
	private ArrayList<ArrayList<LocalState>> stateSet;

	private ArrayList<ArrayList<LocalState>> buffer;

	/** lattice start node */
	private AbstractLatticeNode startNode;

	private AbstractLatticeNode currentNode;

	private ArrayList<LocalState> max;
	
	private int[] interNumArray;
	
	private boolean[] interNumFlag;

	/** output the lattice constructor procedure information */
	private PrintWriter out = null;

	public LatticeChecker(ResultCallback application, String checkerName,
			String[] normalProcesses) {

		super(application, checkerName, normalProcesses);

		dimension = normalProcesses.length;
		globalState = new LocalState[dimension];
		interNumArray = new int[dimension];
		interNumFlag = new boolean[dimension];
		for (int i = 0; i < dimension; i++) {
			LatticeVectorClock vc = new LatticeVectorClock(dimension);
			vc.increment(i);
			interNumArray[i] = 0;
			interNumFlag[i] = true;
			globalState[i] = new LocalState(i, interNumArray[i], vc, false);
		}
		stateSet = new ArrayList<ArrayList<LocalState>>();
		buffer = new ArrayList<ArrayList<LocalState>>();
		max = new ArrayList<LocalState>();

		String[] s = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			stateSet.add(new ArrayList<LocalState>());
			stateSet.get(i).add(globalState[i]);
			buffer.add(new ArrayList<LocalState>());
			// buffer.get(i).add(globalState[i]);
			max.add(globalState[i]);
			s[i] = "0";
		}

		startNode = createNode(globalState, s);
		currentNode = startNode;

		try {
			out = new PrintWriter(LOG_DIRECTORY + "/checker.log");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void handle(ArrayList<Message> messages) {

		String normalProcess = messages.get(0).getSenderID();
		int id = nameToID.get(normalProcess).intValue();

		for (int i = 0; i < messages.size(); i++) {
			Message mess = messages.get(i);
			LatticeMessageContent content = (LatticeMessageContent) mess
					.getMessageContent();
			
			// compute interval number of current process
			if(content.getlocalPredicate() == true){
				if(interNumFlag[id] == true){
					interNumArray[id]++;
					interNumFlag[id] = false;
				}
			}else{
				interNumFlag[id] = true;
			}
				
			LocalState localstate = new LocalState(id, interNumArray[id], content.getlvc(),
					content.getlocalPredicate());
			// output the lattice constructor procedure information
			try {
				out.println(mess.getMessageID() + ", " + normalProcess + ", "
						+ content.getlocalPredicate() + ", "
						+ content.getlvc().toString());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}

			boolean b = buffer(localstate);

			// output the lattice constructor procedure information
			try {
				out.println("whether generate lattice : " + b);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (b) {
				check(startNode, currentNode);
			}
		}
	}

	private void bufferrecursion(int pID) {
		boolean b = false;
		for (int i = 0; i < dimension; i++) {
			if ((i != pID)) {
				while ((buffer.get(i).size() > 0)) {
					boolean bo = true;
					for (int j = 0; j < dimension; j++) {
						if ((j != i)
								&& (globalState[j].vc.lessThan(buffer.get(i)
										.get(0).vc))) {
							bo = false;
						}
					}
					if (bo) {
						// output the lattice constructor procedure information
						try {
							out
									.println("to call expandLattice(state), state's vector clcok is"
											+ buffer.get(i).get(0).vc
													.toString());
							out.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
						expandLattice(buffer.get(i).get(0));
						buffer.get(i).remove(0);
						b = true;
					} else {
						break;
					}
				}
				if (b) {
					bufferrecursion(i);
					break;
				}
			}
		}
	}

	/**
	 * check whether the state is happen-before ordered.
	 * 
	 * @param state
	 * @return
	 */
	private boolean buffer(LocalState state) {
		boolean result = false;
		int pID = state.processID;
		if (buffer.get(pID).size() == 0) {
			boolean b = true;
			for (int i = 0; i < dimension; i++) {
				if ((i != pID) && (globalState[i].vc.lessThan(state.vc))) {
					b = false;
				}
			}
			if (b) {
				// buffer.get(pID).remove(0);
				// buffer.get(pID).add(state);

				// output the lattice constructor procedure information
				try {
					out
							.println("to call expandLattice(state), state's vector clcok is"
									+ state.vc.toString());
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}

				expandLattice(state);
				result = true;

				// recursive check the other processes whether have new state to
				// extend
				bufferrecursion(pID);

			} else {
				buffer.get(pID).add(state);

				// output the lattice constructor procedure information
				try {
					out.println("store in buffer, state's vector clcok is"
							+ state.vc.toString());
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {

			// output the lattice constructor procedure information
			try {
				out.println("store in buffer, state's vector clcok is"
						+ state.vc.toString());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			buffer.get(pID).add(state);
		}
		return result;
	}

	/**
	 * compute the set of state, which is pre-concurrent with state s and is
	 * maximum of each process
	 * 
	 * @param s
	 */
	private void updateMax(LocalState s) {

		int pID = s.processID;
		ArrayList<LocalState> pred = new ArrayList<LocalState>();
		for (int i = 0; i < dimension; i++) {
			if (i != pID) {
				if (stateSet.get(i).size() < 2) {
					continue;
				}
				LocalState state = stateSet.get(i).get(1);
				if (state.vc.lessThan(s.vc)) {
					pred.add(stateSet.get(i).get(0));
				}
			} else {
				pred.add(stateSet.get(i).get(1));
			}
		}
		Iterator<LocalState> iter = pred.iterator();
		while (iter.hasNext()) {
			max.remove(iter.next());
		}
		// max.add(0, s);
	}

	/**
	 * compare two nodes, if equal, return 0; if node1 linked before node2,
	 * return 1; others, return -1.
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	private int compare(AbstractLatticeNode node1, AbstractLatticeNode node2) {
		int position = -1;
		boolean eq = true, firstneq = true;
		for (int i = 0; i < dimension; i++) {
			if (node1.globalState[i] != node2.globalState[i]) {
				eq = false;
				if (position == -1) {
					position = i;
				} else {
					firstneq = false;
					break;
				}
			}
		}
		if (eq) {
			return 0; // if node1=node2, return 0;
		}
		if (firstneq && (position > -1)) {
			firstneq = false;
			for (int j = 0; j < stateSet.get(position).size() - 1; j++) {
				if ((node2.globalState[position] == stateSet.get(position).get(
						j))
						&& (node1.globalState[position] == stateSet.get(
								position).get(j + 1))) {
					firstneq = true;
				}
			}
			if (firstneq) {
				return 1; // if node1->node2, return 1;
			}
		}
		return -1;
	}

	/**
	 * generate the lattice by adding the new state.
	 * 
	 * @param state
	 */
	private void expandLattice(LocalState state) {
		int pID = state.processID;
		stateSet.get(pID).add(0, state);
		globalState[pID] = state;
		// add new node into Lattice with state as the edge
		String[] str = new String[dimension];
		for (int j = 0; j < dimension; j++) {
			int temp = stateSet.get(j).size()
					- stateSet.get(j).indexOf(globalState[j]) - 1;
			str[j] = String.valueOf(temp);
		}
		AbstractLatticeNode node = createNode(globalState, str);
		currentNode.next.add(node);
		node.previous.add(currentNode);
		currentNode = node;

		// output the lattice constructor procedure information
		try {
			out.print("created new node: ");
			for (int m = 0; m < dimension; m++) {
				String end = m + 1 != dimension ? " " : "\r\n";
				out.print(node.getID()[m] + end);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateMax(state);
		if (max.size() > 0) { // generate Lattice
			ArrayList<LocalState> sList = new ArrayList<LocalState>();
			Iterator<LocalState> iter = max.iterator();
			while (iter.hasNext()) {
				sList.add(iter.next());
			}
			Stack<LocalState> stateStack = new Stack<LocalState>();
			Stack<AbstractLatticeNode> nodeStack = new Stack<AbstractLatticeNode>();
			Stack<ArrayList<LocalState>> listStack = new Stack<ArrayList<LocalState>>();
			listStack.push(sList);
			nodeStack.push(currentNode);
			stateStack.push(sList.get(0));
			while (!listStack.empty()) {
				ArrayList<LocalState> list = listStack.pop();
				if (list.size() > 0) {
					LocalState st = list.get(0);
					list.remove(0);
					listStack.push(list);
					stateStack.push(st);
					nodeStack.push(currentNode);

					ArrayList<LocalState> stateList = stateSet
							.get(st.processID);
					Iterator<LocalState> it = stateList.iterator();
					while (it.hasNext()) {
						LocalState s = it.next();
						if (s == st) {
							if (it.hasNext()) {
								// create new LatticeNode by rollbacking state
								// st,
								// and build the link relation ship with existed
								// node
								LocalState news = it.next();
								globalState[st.processID] = news;
								for (int j = 0; j < dimension; j++) {
									int temp = stateSet.get(j).size()
											- stateSet.get(j).indexOf(
													globalState[j]) - 1;
									str[j] = String.valueOf(temp);
								}
								AbstractLatticeNode newnode = createNode(
										globalState, str);
								// check whether the node has been created.
								ArrayList<AbstractLatticeNode> nodelist = currentNode.previous;
								Iterator<AbstractLatticeNode> nodeiter = nodelist
										.iterator();
								boolean bool = false;
								while (nodeiter.hasNext()) {
									AbstractLatticeNode lnode = nodeiter.next();
									if (compare(lnode, newnode) == 0) {
										bool = true;
									}
								}
								if (bool) {
									listStack.push(new ArrayList<LocalState>());
									break;
								}

								// output the lattice constructor procedure
								// information
								try {
									out.print("inside created new node: ");
									for (int m = 0; m < dimension; m++) {
										String end = m + 1 != dimension ? " "
												: "\r\n";
										out.print(newnode.getID()[m] + end);
									}
									out.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}

								// if not created, add into the lattice.
								currentNode.previous.add(newnode);
								newnode.next.add(currentNode);
								for (int j = 0; j < currentNode.previous.size() - 1; j++) {
									AbstractLatticeNode pnode = currentNode.previous
											.get(j);
									for (int k = 0; k < pnode.previous.size(); k++) {
										AbstractLatticeNode ppnode = pnode.previous
												.get(k);
										if (compare(ppnode, newnode) == 1) {
											ppnode.next.add(newnode);
											newnode.previous.add(ppnode);
										}
									}
								}
								currentNode = newnode;
								// compute the new LatticeNode's list
								ArrayList<LocalState> new_s = new ArrayList<LocalState>();
								for (int k = 0; k < dimension; k++) {
									if ((k != pID)) {
										int index = stateSet.get(k).indexOf(
												globalState[k]);
										boolean flag = true;
										if (index + 1 < stateSet.get(k).size()) {
											LocalState comps = stateSet.get(k)
													.get(index + 1);
											for (int j = 0; j < dimension; j++) {
												if ((j != k)
														&& (comps.vc
																.lessThan(globalState[j].vc))) {
													flag = false;
												}
											}
										} else {
											flag = false;
										}
										if (flag) {
											new_s.add(globalState[k]);
										}
									}
								}
								listStack.push(new_s);
							} else {
								listStack.push(new ArrayList<LocalState>());
							}
							break;
						}
					}
				} else {
					LocalState s = stateStack.pop();
					globalState[s.processID] = s;
					currentNode = nodeStack.pop();
				}
			}
		}
		max.add(state);

	}

	public abstract AbstractLatticeNode createNode(LocalState[] globalState,
			String[] s);

	public abstract void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode);

}
