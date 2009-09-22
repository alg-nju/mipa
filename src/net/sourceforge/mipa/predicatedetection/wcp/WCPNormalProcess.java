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
package net.sourceforge.mipa.predicatedetection.wcp;

import static config.Config.ENABLE_PHYSICAL_CLOCK;
import static config.Config.LOG_DIRECTORY;
import static config.Debug.DEBUG;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;
import net.sourceforge.mipa.predicatedetection.wcp.WCPVectorClock;

/**
 * 
 * @author sorrybone <sorrybone@gmail.com>
 *
 */
public class WCPNormalProcess extends AbstractNormalProcess {
    
    private static final long serialVersionUID = 5563952119457480166L;

    private boolean firstflag;
    
    private PrintWriter out;
    
    private Map<String, Long> currentMessageCount;
    
    public WCPNormalProcess(String name, String[] checkers, String[] normalProcesses) {
        super(name, checkers, normalProcesses);
        // TODO Auto-generated constructor stub
        currentClock = new WCPVectorClock(normalProcesses.length);
        currentClock.increment(id);
        
        // TODO Auto-generated constructor stub
        currentMessageCount = new HashMap<String, Long>();
        for(int i = 0; i < checkers.length; i++) {
            currentMessageCount.put(checkers[i], new Long(0));
        }
        
        firstflag = true;
        
        if(ENABLE_PHYSICAL_CLOCK) {
            try {
                out = new PrintWriter(LOG_DIRECTORY + "/" + name);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void action(boolean value) {
        // TODO Auto-generated method stub
        if(value == true && firstflag) {
            WCPVectorClock wcpVectorClock= new WCPVectorClock(currentClock);
            WCPMessageContent wcpMessageContent = new WCPMessageContent(wcpVectorClock);
            //??? WCP doesn't send any message to other normal processes.
            for(int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, wcpMessageContent);
            }
            firstflag = false;
            broadcast(MessageType.Control, null);

            if(DEBUG) {
                System.out.println(name + " firstflag: false");
            }
        }
    }

    @Override
    public void application() {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveMsg(Message message) {
        // TODO Auto-generated method stub
        VectorClock timestamp = message.getTimestamp();
        currentClock.update(timestamp);
    }

    private void send(MessageType type, String receiverName, WCPMessageContent content) {
        Message m = new Message();
        m.setType(type);
        m.setSenderID(name);
        m.setReceiverID(receiverName);
        VectorClock current = new WCPVectorClock(currentClock);
        m.setTimestamp(current);
        m.setWcpMessageContent(content);
        
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
        
        if(!receiverName.equals("checker")) {
            firstflag = true;//firstflag should be set as true when sending messages to NormalProcesses
            if(DEBUG) {
                System.out.println(name + " firstflag: true.");
            }
            currentClock.increment(id);
        }
    }
    
    private void broadcast(MessageType type, WCPMessageContent content) {
        for(int i = 0; i < normalProcesses.length; i++) {
            if(i != id) {
                send(type, normalProcesses[i], content);
            }
        }
    }
}
