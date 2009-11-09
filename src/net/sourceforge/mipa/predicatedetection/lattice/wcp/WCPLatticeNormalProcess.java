package net.sourceforge.mipa.predicatedetection.lattice.wcp;

import static config.Debug.DEBUG;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeVectorClock;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeMessageContent;
import net.sourceforge.mipa.predicatedetection.scp.SCPMessageContent;
import net.sourceforge.mipa.predicatedetection.scp.SCPVectorClock;

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
		if(value==true){
			localPredicate=true;
			//update vector clock
			currentClock.increment(id);
			//localState changed, send localState to checker
			LatticeVectorClock clock=new LatticeVectorClock(currentClock);
			LatticeMessageContent content=new LatticeMessageContent(clock,value);
			for(int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, content);
            }
			//send message to other normal process
            broadcast(MessageType.Control, null);
		}else if(value==false){
			localPredicate=false;
			currentClock.increment(id);
			LatticeVectorClock clock=new LatticeVectorClock(currentClock);
			LatticeMessageContent content=new LatticeMessageContent(clock,value);
			for(int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, content);
            }
		}
			
	}
	
	private void send(MessageType type, String receiverName, LatticeMessageContent content) {
        Message m = new Message();
        m.setType(type);
        m.setSenderID(name);
        m.setReceiverID(receiverName);
        VectorClock current = new LatticeVectorClock(currentClock);
        m.setTimestamp(current);
        m.setLatticeMessageContent(content);
        
        if(currentMessageCount.containsKey(receiverName) == true) {
            long currentCount = currentMessageCount.get(receiverName);
            m.setMessageID(currentCount);
            currentMessageCount.put(receiverName, new Long(currentCount + 1));
        } else {
            assert(false);
        }
        
        try {
            messageDispatcher.send(m);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
    private void broadcast(MessageType type, LatticeMessageContent content) {
        for(int i = 0; i < normalProcesses.length; i++) {
            if(i != id) {
                send(type, normalProcesses[i], content);
            }
        }
    }

	@Override
	public void application() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMsg(Message message) {
		//update the vector clock
		currentClock.increment(id);
		VectorClock timestamp = message.getTimestamp();
        currentClock.update(timestamp);
        //localState changed, send localState to checker
        LatticeVectorClock clock=new LatticeVectorClock(currentClock);
		LatticeMessageContent content=new LatticeMessageContent(clock,localPredicate);
		for(int i = 0; i < checkers.length; i++) {
            String checker = checkers[i];
            send(MessageType.Detection, checker, content);
        }
	}

}
