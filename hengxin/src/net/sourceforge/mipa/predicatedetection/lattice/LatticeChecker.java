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

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.AbstractFIFOChecker;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.data.CheckerInfo;
import net.sourceforge.mipa.ui.lattice.LatticeDot;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public abstract class LatticeChecker extends AbstractFIFOChecker implements ILatticeConstructor {

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

	private int[] interNumArray;

	private boolean[] interNumFlag;

	/**
	 * added by hengxin(hengxin0912@gmail.com)
	 * 
	 * store the lattice nodes in Hashtable in addition to tree view 
	 */
//	protected Hashtable<String,AbstractLatticeNode> nodeTable = new Hashtable<String,AbstractLatticeNode>();
	
	/** output the lattice constructor procedure information */
	private PrintWriter out = null;
	
	public LatticeChecker(ResultCallback application, String predicateID, String checkerName,
			String[] normalProcesses) {

		super(application, predicateID, checkerName, normalProcesses);

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

		String[] s = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			stateSet.add(new ArrayList<LocalState>());
			stateSet.get(i).add(globalState[i]);
			buffer.add(new ArrayList<LocalState>());
			// buffer.get(i).add(globalState[i]);
			s[i] = "0";
		}

		startNode = createNode(globalState, s);
		
		// store the start node in the hash table
		CheckerInfo.getInstance().storeNode2Table(startNode);
		
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
			if (content.getlocalPredicate()) {
				if (interNumFlag[id]) {
					interNumArray[id]++;
					interNumFlag[id] = false;
				}
			} else {
				interNumFlag[id] = true;
			}

			LocalState localstate = new LocalState(id, interNumArray[id],
					content.getlvc(), content.getlocalPredicate());
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

	/**
	 * check whether the state is happen-before ordered.
	 * 
	 * fix Issue 15.
	 * 
	 * @param state
	 * @return
	 */
	private boolean buffer(LocalState state) {

		boolean result = false;
		int pID = state.processID;
		buffer.get(pID).add(state);

		int index = pID;
		while (index < dimension) {
			boolean flag = true;

			if (buffer.get(index).size() > 0) {
				for (int i = 0; i < dimension; i++) {
					if ((i != index)
							&& (globalState[i].vc.lessThan(buffer.get(index)
									.get(0).vc))) {
						flag = false;
					}
				}
			} else {
				flag = false;
			}

			if (flag) {
				// output the lattice constructor procedure information
				try {
					out
							.println("to call expandLattice(state), state's vector clcok is"
									+ buffer.get(index).get(0).vc.toString());
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}

				expandLattice(buffer.get(index).get(0));

				buffer.get(index).remove(0);
				result = true;
				index = 0;
			} else {
				index++;
			}
		}

		return result;
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
		else
		{
			/**
			 * debug by hengxin(hengxin0912@gmail.com)
			 */
			if(node1.getIDLiteral().equals(node2.getIDLiteral()))
				System.out.println("Compare Error");
		}
		
		if (firstneq && (position > -1)) {
			firstneq = false;
			for (int j = 0; j < stateSet.get(position).size() - 1; j++) {
				if ((node2.globalState[position] == stateSet.get(position).get(j))
						&& (node1.globalState[position] == stateSet.get(position).get(j + 1))) {
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
		
		// store new node into the hash table
		CheckerInfo.getInstance().storeNode2Table(node);
		
		if (CheckerInfo.DOT)
		{
			//output to .dot file
			LatticeDot.getInstance().write2LatticeDotLink(currentNode, node,
					true);
		}
		
		currentNode = node;
		LocalState[] cgs = new LocalState[globalState.length];
		for (int i = 0; i < globalState.length; i++) {
			cgs[i] = globalState[i];
		}

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

		// recursively construct the branch of the lattice
		dfs_construction(pID, currentNode, cgs);
	}

	/**
	 * deep-first recursively construct the branch of the lattice
	 * 
	 * @param
	 * @return
	 */
	private void dfs_construction(int pID, AbstractLatticeNode cur_node,
			LocalState[] cgs)
	{
		// compute the cur_node's max_list
		ArrayList<LocalState> max = new ArrayList<LocalState>();
		for (int k = 0; k < dimension; k++)
		{
			if (k != pID)
			{
				int index = stateSet.get(k).indexOf(cgs[k]);
				// if the process_k has state to roll back
				if (index + 1 < stateSet.get(k).size())
				{
					boolean flag = true;
					LocalState comps = stateSet.get(k).get(index + 1);
					for (int j = 0; j < dimension; j++)
					{
						if ((j != k) && (comps.vc.lessThan(cgs[j].vc)))
						{
							flag = false;
						}
					}
					// if the state could roll back
					if (flag)
					{
						max.add(comps);
					}
					/**
					 * added by hengxin(hengxin0912@gmail.com)
					 * 
					 * processing the non-lattice node explicitly if the state
					 * cannot roll back
					 */
					else
					{
						// the corresponding state cannot roll back
						int fail2RollPid = k;
						int fail2RollIndex = index + 1;

						this.handleNonNode(cur_node, fail2RollPid,
								fail2RollIndex);
					}
				}
			}
		}
		// for every state in max, create new node, recurse the procedure.
		if (max.size() < 1)
		{
			return;
		} else
		{
			Iterator<LocalState> iter = max.iterator();
			while (iter.hasNext())
			{
				LocalState s = iter.next();
				LocalState[] gs = new LocalState[cgs.length];
				for (int i = 0; i < cgs.length; i++)
				{
					gs[i] = cgs[i];
				}
				gs[s.processID] = s;
				String[] str = new String[dimension];
				for (int j = 0; j < dimension; j++)
				{
					int temp = stateSet.get(j).size()
							- stateSet.get(j).indexOf(gs[j]) - 1;
					str[j] = String.valueOf(temp);
				}
				AbstractLatticeNode newnode = createNode(gs, str);
				// check whether the node has been created.
				ArrayList<AbstractLatticeNode> nodelist = cur_node.previous;
				Iterator<AbstractLatticeNode> nodeiter = nodelist.iterator();
				boolean bool = false;
				while (nodeiter.hasNext())
				{
					AbstractLatticeNode lnode = nodeiter.next();
					if (compare(lnode, newnode) == 0)
					{
						bool = true;
					}
				}
				// if the node has been created
				if (bool)
				{
//					/**
//					 * modified by hengxin(hengxin0912@gmail.com)
//					 */
//					CheckerInfo.getInstance().removeNodeFromTable(newnode);
					
					continue;
				} else
				{
					// output the lattice constructor procedure information
					try
					{
						out.print("inside created new node: ");
						for (int m = 0; m < dimension; m++)
						{
							String end = m + 1 != dimension ? " " : "\r\n";
							out.print(newnode.getID()[m] + end);
						}
						out.flush();
					} catch (Exception e)
					{
						e.printStackTrace();
					}

					cur_node.previous.add(newnode);
					newnode.next.add(cur_node);

					// store the new node into the hash table
					CheckerInfo.getInstance().storeNode2Table(newnode);
					
					// output to .dot file
					
					if (CheckerInfo.DOT)
					{
						LatticeDot.getInstance().write2LatticeDotLink(newnode,
								cur_node, false);
					}
					
					for (int j = 0; j < cur_node.previous.size() - 1; j++)
					{
						AbstractLatticeNode pnode = cur_node.previous.get(j);
						for (int k = 0; k < pnode.previous.size(); k++)
						{
							AbstractLatticeNode ppnode = pnode.previous.get(k);
							if (compare(ppnode, newnode) == 1)
							{
								ppnode.next.add(newnode);
								newnode.previous.add(ppnode);

								if (CheckerInfo.DOT)
								{
									// output to .dot file
									LatticeDot.getInstance()
											.write2LatticeDotLink(ppnode,
													newnode, false);
								}
							}
						}
					}
					// recursively construct
					dfs_construction(pID, newnode, gs);
				}

			}
		}
	}

	/**
	 * processing non-lattice node explicitly
	 * 
	 * added by hengxin(hengxin0912@gmail.com)
	 * 
	 * @param fail2RollPid pid of process whose local state cannot be rolled back
	 * @param fail2RollIndex index of local state which cannot be rolled back 
	 */
	private void handleNonNode(AbstractLatticeNode aln,int fail2RollPid, int fail2RollIndex)
	{
		// id of non-lattice node
		String nonNodeIdLiteral = null;
		String nonNodeId[] = new String[this.dimension];
		String alnNodeId[] = aln.getID();
		for(int pid = 0; pid < this.dimension; pid++)
		{
			if(pid != fail2RollPid)
				nonNodeId[pid] = alnNodeId[pid];
		}

		int length = stateSet.get(fail2RollPid).size();
		for(int index = fail2RollIndex; index < length; index++)
		{
			// each state with such id is not a lattice node
			nonNodeId[fail2RollPid] = String.valueOf(length - 1 - index);
			
			StringBuilder sb = new StringBuilder();
			
			for(String localId : nonNodeId)
			{
				sb.append(localId).append('_');
			}
			nonNodeIdLiteral = sb.toString();
			
			if (CheckerInfo.DOT)
			{
				// for .dot
				if (!LatticeDot.getInstance().isDefinedVirtualNode(
						nonNodeIdLiteral))
				{
					LatticeDot.getInstance().write2LatticeDotVirtualNode(
							nonNodeIdLiteral);
					LatticeDot.getInstance().add2DefinedVirtualNode(
							nonNodeIdLiteral);
				}
			}
			
			if(index == fail2RollIndex)
			{
				if (CheckerInfo.DOT)
				{
					LatticeDot.getInstance().write2LatticeDotVirtualLink(
							nonNodeIdLiteral, aln);
				}
			}
			
			this.handleNonLatticeNode(nonNodeId);
		}
	}

	public abstract AbstractLatticeNode createNode(LocalState[] globalState,
			String[] s);

	public abstract void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode);

	public void handleLatticeNode(String[] id)
	{
		
	}

	public void handleNonLatticeNode(String[] id)
	{
		
	}
	
    public LocalState[] getGlobalState() {
        return globalState;
    }

}
