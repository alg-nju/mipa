package net.sourceforge.mipa.predicatedetection.lattice.wcp;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeVectorClock;

public class WCPLatticeNormalProcess extends AbstractNormalProcess{

	private static final long serialVersionUID = 970638743111272003L;
	
	private boolean localPredicate;
	
	private Map<String, Long> currentMessageCount;

	public WCPLatticeNormalProcess(String name, String[] checkers,
			String[] normalProcesses) {
		super(name, checkers, normalProcesses);
		
		currentClock=new LatticeVectorClock(normalProcesses.length);
		currentClock.increment(id);
		currentMessageCount = new HashMap<String, Long>();
        for(int i = 0; i < checkers.length; i++) {
            currentMessageCount.put(checkers[i], new Long(0));
        }
        localPredicate=false;
	}

	@Override
	public void action(boolean value) {
		
			
	}

	@Override
	public void application() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMsg(Message message) {
		// TODO Auto-generated method stub
		
	}

}
