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
package net.sourceforge.mipa.predicatedetection.lattice.sequence;

import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeMessageContent;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeVectorClock;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class SequenceLatticeNormalProcess extends AbstractNormalProcess {

    private static final long serialVersionUID = -3908797397730424171L;
    
    private boolean localPredicate;

    private Map<String, Long> currentMessageCount;
    
    private PrintWriter out;
    private long pTimeLo;
    private int index;

    public SequenceLatticeNormalProcess(String name, String[] checkers,
            String[] normalProcesses) {
        super(name, checkers, normalProcesses);
        // TODO Auto-generated constructor stub
        currentClock = new LatticeVectorClock(normalProcesses.length);
        currentClock.increment(id);

        currentMessageCount = new HashMap<String, Long>();
        for (int i = 0; i < checkers.length; i++) {
            currentMessageCount.put(checkers[i], new Long(0));
        }
        
        localPredicate = false;
        
        try {
            out = new PrintWriter(LOG_DIRECTORY + "/" + name + ".log");
        } catch(Exception e) {
            e.printStackTrace();
        }
        index = 0;
    }

    @Override
    public void action(boolean value) {

        if ((value == true) && (localPredicate == false)) {
            localPredicate = true;

            index++;
            pTimeLo = (new Date()).getTime();
            
            // send message to other normal process
            broadcast(MessageType.Control, null);
            
            // update vector clock
            currentClock.increment(id);
            
            // localState changed, send localState to checker
            LatticeVectorClock clock = new LatticeVectorClock(currentClock);
            LatticeMessageContent content = new LatticeMessageContent(clock,
                    value);
            for (int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, content);
            }
            
        } else if ((value == false) && (localPredicate == true)) {
            localPredicate = false;
            
            //out put the physical time, to compare with the lattice result
            try {
                long pTimeHi = (new Date()).getTime();
                out.println(index + " " + pTimeLo + " " + pTimeHi);
                out.flush();
            } catch(Exception e) {
                e.printStackTrace();
            }
            
            // update vector clock
            currentClock.increment(id);

            // localState changed, send localState to checker
            LatticeVectorClock clock = new LatticeVectorClock(currentClock);
            LatticeMessageContent content = new LatticeMessageContent(clock,
                    value);
            for (int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, content);
            }
            
        }

    }

    private void send(MessageType type, String receiverName,
            LatticeMessageContent content) {
        Message m = new Message();
        m.setType(type);
        m.setSenderID(name);
        m.setReceiverID(receiverName);
        VectorClock current = new LatticeVectorClock(currentClock);
        m.setTimestamp(current);
        m.setMessageContent(content);

        if (currentMessageCount.containsKey(receiverName) == true) {
            long currentCount = currentMessageCount.get(receiverName);
            m.setMessageID(currentCount);
            currentMessageCount.put(receiverName, new Long(currentCount + 1));
        } else {
            assert (false);
        }

        sender.send(m);
    }

    private void broadcast(MessageType type, LatticeMessageContent content) {
        for (int i = 0; i < normalProcesses.length; i++) {
            if (i != id) {
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
        // update the vector clock
        VectorClock timestamp = message.getTimestamp();
        currentClock.update(timestamp);

        // fix Issue 10, add check message sending
        // localState changed, send localState to checker
        LatticeVectorClock clock = new LatticeVectorClock(currentClock);
        LatticeMessageContent content = new LatticeMessageContent(clock,
                localPredicate);
        for (int i = 0; i < checkers.length; i++) {
            String checker = checkers[i];
            send(MessageType.Detection, checker, content);
        }
    }


}
