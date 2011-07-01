package net.sourceforge.mipa.predicatedetection.lattice.simplesequence;

import static config.Config.LOG_DIRECTORY;
import static config.Debug.DEBUG;

import java.io.PrintWriter;
import java.util.*;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

public class SimpleSequenceLatticeChecker extends LatticeChecker {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3337602040263799879L;

	private int dimension;

	/* store the current check level number */
	private int level;

	/* store the node of the last computed level in the lattice */
	private ArrayList<SimpleSequenceLatticeNode> previous;

	/* store the full level number of each dimension */
	private int[] fullLevel;

	/* store the corresponding NPs of each CGSpredicate in SS */
	private HashMap<String, ArrayList<String>> CGSToNPs;

	/* store the simple sequence predicate */
	private ArrayList<String> ssPredicate;

	private int count;

	//private PrintWriter out = null;
	
	private PrintWriter result = null;

	public SimpleSequenceLatticeChecker(ResultCallback application,
			String checkerName, String[] normalProcesses,
			Structure specification) {
		super(application, checkerName, normalProcesses);

		dimension = normalProcesses.length;
		level = 0;
		previous = new ArrayList<SimpleSequenceLatticeNode>();
		fullLevel = new int[normalProcesses.length];
		for (int i = 0; i < normalProcesses.length; i++) {
			fullLevel[i] = 0;
		}
		CGSToNPs = new HashMap<String, ArrayList<String>>();
		ssPredicate = new ArrayList<String>();
		getCGSToNPs(specification);
		getssPredicate(specification);
		count = 0;
		try {
			//out = new PrintWriter(LOG_DIRECTORY + "/simpleSequence.log");
			result = new PrintWriter(LOG_DIRECTORY + "/SSResult.log");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * compute attribute CGSToNPs
	 * 
	 * @param specification
	 */
	private void getCGSToNPs(Structure specification) {
		Structure CGSs = specification.getChildren().get(0);
		for (int i = 0; i < CGSs.getChildren().size(); i++) {
			ArrayList<String> NPs = new ArrayList<String>();
			Structure CGS = CGSs.getChildren().get(i);
			String name = ((Composite) CGS).getNodeName();
			for (int j = 0; j < CGS.getChildren().size(); j++) {
				NPs.add(((LocalPredicate) CGS.getChildren().get(j))
						.getNormalProcess());
			}
			CGSToNPs.put(name, NPs);
		}
		if (DEBUG) {
			printCGSToNPs();
		}
	}

	private void printCGSToNPs() {
		System.out.println("========================================");
		System.out.println("Print CGS name to NPs:");
		Set<String> names = CGSToNPs.keySet();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String result = "";
			String name = it.next();
			result += name + ":  ";
			ArrayList<String> NPs = CGSToNPs.get(name);
			for (int i = 0; i < NPs.size(); i++) {
				result += NPs.get(i) + "  ";
			}
			System.out.println(result);
		}
		System.out.println("Print over");
		System.out.println("----------------------------------------");
		System.out.println();
	}

	/**
	 * compute attribute ssPredicate
	 * 
	 * @param specification
	 */
	private void getssPredicate(Structure specification) {
		Structure GSE = specification.getChildren().get(1);
		for (int i = 0; i < GSE.getChildren().size(); i++) {
			Structure CGS = GSE.getChildren().get(i);
			String name = ((Composite) CGS).getNodeName();
			ssPredicate.add(name);
		}
	}

	/**
	 * compute whether the current level is full
	 * 
	 * @param currentlevel
	 * @return
	 */
	private boolean isfull(ArrayList<SimpleSequenceLatticeNode> currentlevel,
			SimpleSequenceLatticeNode currentnode) {
		boolean flag = true;
		ArrayList<ArrayList<SimpleSequenceLatticeNode>> edge = new ArrayList<ArrayList<SimpleSequenceLatticeNode>>();
		for (int i = 0; i < dimension; i++) {
			edge.add(new ArrayList<SimpleSequenceLatticeNode>());
		}
		Iterator<SimpleSequenceLatticeNode> iter = currentlevel.iterator();
		while (iter.hasNext()) {
			SimpleSequenceLatticeNode node = iter.next();
			for (int i = 0; i < dimension; i++) {
				if (edge.get(i).size() == 0) {
					edge.get(i).add(node);
				} else {
					SimpleSequenceLatticeNode inside = edge.get(i).get(0);
					if (Integer.valueOf(node.getID()[i]).intValue() == Integer
							.valueOf(inside.getID()[i]).intValue()) {
						edge.get(i).add(node);
					} else if (Integer.valueOf(node.getID()[i]).intValue() > Integer
							.valueOf(inside.getID()[i]).intValue()) {
						edge.get(i).clear();
						edge.get(i).add(node);
					}
				}
			}
		}
		String[] ID = currentnode.getID();
		for (int i = 0; i < dimension; i++) {
			// out.print("edge "+i+" : ");
			Iterator<SimpleSequenceLatticeNode> it = edge.get(i).iterator();
			while (it.hasNext()) {
				SimpleSequenceLatticeNode node = it.next();

				// for (int j = 0; j < node.getID().length; j++) {
				// out.print(node.getID()[j] + " ");
				// }
				// out.print("|| ");

				ArrayList<SimpleSequenceLatticeNode> pred = new ArrayList<SimpleSequenceLatticeNode>();
				for (int j = 0; j < node.getprevious().size(); j++) {
					pred.add((SimpleSequenceLatticeNode) node.getprevious()
							.get(j));
				}
				Iterator<SimpleSequenceLatticeNode> prediter = pred.iterator();
				while (prediter.hasNext()) {
					SimpleSequenceLatticeNode pnode = prediter.next();
					// for (int j = 0; j < pnode.getID().length; j++) {
					// out.print(pnode.getID()[j] + " ");
					// }
					// out.print("## ");
					// out.print(Integer.valueOf(ID[i])+"vs"+Integer.valueOf(pnode.getID()[i]));
					if (Integer.valueOf(ID[i]).intValue() == Integer.valueOf(
							pnode.getID()[i]).intValue()) {
						flag = false;
					}
					// out.println(flag);
				}
			}
		}
		//out.println("level " + level + "+1 is " + flag);
		return flag;
	}

	/**
	 * compute the succeed set of the current previous set
	 * 
	 * @return
	 */
	private ArrayList<SimpleSequenceLatticeNode> getsucceed() {
		ArrayList<SimpleSequenceLatticeNode> succeed = new ArrayList<SimpleSequenceLatticeNode>();
		Iterator<SimpleSequenceLatticeNode> it = previous.iterator();
		while (it.hasNext()) {
			SimpleSequenceLatticeNode node = it.next();
			ArrayList<SimpleSequenceLatticeNode> succ = new ArrayList<SimpleSequenceLatticeNode>();
			for (int i = 0; i < node.getnext().size(); i++) {
				succ.add((SimpleSequenceLatticeNode) node.getnext().get(i));
			}
			Iterator<SimpleSequenceLatticeNode> itor = succ.iterator();
			while (itor.hasNext()) {
				SimpleSequenceLatticeNode snode = itor.next();
				if (succeed.contains(snode) == false) {
					succeed.add(snode);
				}
			}
		}
		return succeed;
	}

	/**
	 * compute the minimum prefix value
	 * 
	 * @param nodelist
	 * @return
	 */
	private int getMinPrefix(ArrayList<SimpleSequenceLatticeNode> nodelist) {
		Iterator<SimpleSequenceLatticeNode> it = nodelist.iterator();
		int prefix = -100;
		while (it.hasNext()) {
			int temp = it.next().getprefix();
			if (prefix == -100) {
				prefix = temp;
			}
			if (prefix > temp) {
				prefix = temp;
			}
		}
		return prefix;
	}

	/**
	 * compute whether the node satisfy the NO.number predicate of simple
	 * sequence
	 * 
	 * @param node
	 * @param number
	 * @return
	 */
	private boolean getCGS(SimpleSequenceLatticeNode node, int number) {
		boolean bool = true;
		ArrayList<String> NPs = new ArrayList<String>();

		//System.out.print(ssPredicate.get(number)+" ");
		NPs = CGSToNPs.get(ssPredicate.get(number));
		for (int i = 0; i < NPs.size(); i++) {
			String np = NPs.get(i);
			String[] c = np.split("ss");
			assert (c.length == 2);
			int npIndex = Integer.valueOf(c[1]);
			bool = bool && (node.getglobalState()[npIndex].getlocalPredicate());
			//System.out.print(node.getglobalState()[npIndex].getintervalID()+" "+node.getglobalState()[npIndex].getlocalPredicate()+" ");
		}
		return bool;
	}

	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode) {

		/*.print("currentNode: ");
		for (int j = 0; j < currentNode.getID().length; j++) {
			out.print(currentNode.getID()[j] + " ");
		}
		out.println();*/

		// previous 初始化
		if ((level == 0) && (previous.size() == 0)) {
			previous.add((SimpleSequenceLatticeNode) startNode);
			((SimpleSequenceLatticeNode) startNode).setprefix(-1);
			//out.println("level 0 initialized.");
		}

		// check each full level
		ArrayList<SimpleSequenceLatticeNode> current = getsucceed();
		while (isfull(current, (SimpleSequenceLatticeNode) currentNode)) { // 若下一层长满，则进行SS检测
			level++;

			//输出current集合，即当前层的节点
			/*out.println("------------level " + level + "-----------");
			for (int i = 0; i < current.size(); i++) {
				for (int j = 0; j < current.get(i).getID().length; j++) {
					out.print(current.get(i).getID()[j] + " ");
				}
				out.print("||");
			}
			out.println();*/

			// 对current集合中的每一个node计算prefix值，并判断是否满足SS
			Iterator<SimpleSequenceLatticeNode> iter = current.iterator();
			while (iter.hasNext()) {
				SimpleSequenceLatticeNode node = iter.next();
				
				//for (int j = 0; j < node.getID().length; j++) {
				//	out.print(node.getID()[j] + " ");
				//}
				//out.print("||");
				
				ArrayList<SimpleSequenceLatticeNode> pred = new ArrayList<SimpleSequenceLatticeNode>();
				for (int i = 0; i < node.getprevious().size(); i++) {
					pred.add((SimpleSequenceLatticeNode) node.getprevious()
							.get(i));

					//for (int j = 0; j < node.getprevious().get(i).getID().length; j++) {
					//	out.print(node.getprevious().get(i).getID()[j] + " ");
					//}
					//out.print("$$");

				}
				int u = getMinPrefix(pred);
				//out.print("minprefix " + u);

				if (u < ssPredicate.size() - 1) {
					if (getCGS(node, u + 1)) {
						node.setprefix(u + 1);
						//out.print(" grow a step of predicate " + u + " ");
					} else {
						node.setprefix(u);
						//out.print(" unchanged!" + u + " ");
					}
				} else {
					node.setprefix(u);
					//out.print(" unchanged!!" + u + " ");
				}

				if (node.getprefix() == ssPredicate.size() - 1) {
					node.setverified(true);
				}

				//out.println(" node's prefix" + node.getprefix() + " ");
			}

			Iterator<SimpleSequenceLatticeNode> it_current = current.iterator();
			boolean bool = true;
			for (int i = 0; i < current.size(); i++) {
				if (it_current.next().getverified() == false) {
					bool = false;
					break;
				}
			}
			// 检测到Simple Sequence
			if (bool) {
				// 将当前level的node的prefix恢复初始值
				Iterator<SimpleSequenceLatticeNode> itor_current = current
						.iterator();
				for (int i = 0; i < current.size(); i++) {
					itor_current.next().setprefix(-1);
				}

				count++;
				System.out.println(count + "  SimpleSequence");
				//out.println("<<<<<<<<<<<<<<<<<  " + count + " simple sequence  >>>>>>>>>>>>>>>>>>>>");
				
				result.print("No. "+  count +",");
				Iterator<SimpleSequenceLatticeNode> iter_current = current.iterator();
				for (int i = 0; i < current.size(); i++) {
					SimpleSequenceLatticeNode ssnode = iter_current.next();
					LocalState[] gs = ssnode.getglobalState();
					result.print("( ");
					for (int j = 0; j < gs.length; j++) {
						
						String end = j + 1 != children.length ? " " : " ),";
						result.print(gs[j].getintervalID() + end);
						result.flush();
					}
				}
				result.println(" level: " + level);
				
			}

			previous = current;
			current = getsucceed();
			//out.flush();
			result.flush();
		}

	}

	@Override
	public SimpleSequenceLatticeNode createNode(LocalState[] globalState,
			String[] s) {
		// TODO Auto-generated method stub
		SimpleSequenceLatticeNode node = new SimpleSequenceLatticeNode(
				globalState, s);
		return node;
	}

}
