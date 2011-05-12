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
package net.sourceforge.mipa.predicatedetection.normal.wcp;

import static config.Config.ENABLE_PHYSICAL_CLOCK;
import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;

/**
 * 
 * @author YiLing Yang <csylyang@gmail.com>
 *
 */
public class WCPNormalProcess extends AbstractNormalProcess {

    private static final long serialVersionUID = -4204158380607695058L;

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
                out = new PrintWriter(LOG_DIRECTORY + "/" + name + ".log");
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
            
            if(ENABLE_PHYSICAL_CLOCK) {
                IDManager idManager = MIPAResource.getIDManager();
                try {
                    String contentID = idManager.getID(Catalog.Numerical);
                    wcpMessageContent.setContentID(contentID);
                    out.println(contentID + ":[" +wcpVectorClock.toString() + "]");
                    out.flush();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            //??? WCP doesn't send any message to other normal processes.
            for(int i = 0; i < checkers.length; i++) {
                String checker = checkers[i];
                send(MessageType.Detection, checker, wcpMessageContent);
            }
            
            firstflag = false;
            broadcast(MessageType.Control, null);
            currentClock.increment(id);
            
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
        m.setMessageContent(content);
        
        if(currentMessageCount.containsKey(receiverName) == true) {
            long currentCount = currentMessageCount.get(receiverName);
            m.setMessageID(currentCount);
            currentMessageCount.put(receiverName, new Long(currentCount + 1));
        } else {
            assert(false);
        }

        sender.send(m);
    }
    
    private void broadcast(MessageType type, WCPMessageContent content) {
        for(int i = 0; i < normalProcesses.length; i++) {
            if(i != id) {
                send(type, normalProcesses[i], content);
            }
        }
        firstflag = true;
        
    }
}
