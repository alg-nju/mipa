package net.sourceforge.mipa.predicatedetection.lattice.wcp;

import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.mipa.ResultCallback;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeMessageContent;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

public class WCPLatticeChecker extends LatticeChecker {

	private static final long serialVersionUID = 4805292792830427418L;
	
	private PrintWriter out = null;

	public WCPLatticeChecker(ResultCallback application, String checkerName,
			String[] normalProcesses) {
		super(application, checkerName, normalProcesses);
		// TODO Auto-generated constructor stub
		
		try {
            out = new PrintWriter(LOG_DIRECTORY + "/found_WCP.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public AbstractLatticeNode createNode(LocalState[] globalState, String[] s) {
		// TODO Auto-generated method stub
		WCPLatticeNode node = new WCPLatticeNode(globalState, s);
		node.setWCPNode(node);
		return node;
	}

	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode) {
		
		detect(currentNode);
	}
	
	public void detect(AbstractLatticeNode node){
		ArrayList<AbstractLatticeNode> list=node.getprevious();
		Iterator<AbstractLatticeNode> iter=list.iterator();
		while(iter.hasNext()){
			AbstractLatticeNode child=iter.next();
			//if the node has not been visited
			if(child.getWCPNode().getVisited()==false){
				//if the global predicate is true, then detected
				child.getWCPNode().setVisited(true);
				if(child.getWCPNode().cgs()){
					LocalState[] gs=child.getglobalState();
					for(int i=0;i<gs.length;i++){
						try {
							application.callback(String.valueOf(true));
	                        String end = i + 1 != children.length ? " " : "\r\n";
	                        out.print("[" +gs[i].getvc().toString() +"]"+ end);
	                        out.flush();
	                    } catch(Exception e) {
	                        e.printStackTrace();
	                    }
					}
				}
				detect(child);
			}
		}
	}
}
