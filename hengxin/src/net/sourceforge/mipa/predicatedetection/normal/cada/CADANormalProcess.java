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
package net.sourceforge.mipa.predicatedetection.normal.cada;

import static config.Config.ENABLE_PHYSICAL_CLOCK;
import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;
import net.sourceforge.mipa.predicatedetection.normal.cada.CADAMessageContent;
import net.sourceforge.mipa.predicatedetection.normal.cada.CADAVectorClock;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 */
public class CADANormalProcess extends AbstractNormalProcess {

    private static final long serialVersionUID = -3335612839908866249L;

    private boolean prevState;
    
    private boolean firstflag;
    
    private CADAVectorClock lo;
    
    private long pTimeLo;
    
    private PrintWriter out;
    
    private Map<String, Long> currentMessageCount;
    
    private  String intervalID = "0";
    /**
     * @param name
     */
    public CADANormalProcess(String name, String[] checkers, String[] normalProcesses) {
        super(name, checkers, normalProcesses);
        
        currentClock = new CADAVectorClock(normalProcesses.length);
        currentClock.increment(id);
        
        // TODO Auto-generated constructor stub
        currentMessageCount = new HashMap<String, Long>();
        for(int i = 0; i < checkers.length; i++) {
            currentMessageCount.put(checkers[i], new Long(0));
        }
        
        prevState = false;
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
    public void receiveMsg(Message message) {
        VectorClock timestamp = message.getTimestamp();
        currentClock.update(timestamp);
        firstflag = true;
    }

    @Override
    public void action(boolean value) {
        // TODO Auto-generated method stub
        if(prevState != value) {
            if(prevState == false) {
                broadcast(MessageType.Control, null);
                lo = new CADAVectorClock(currentClock);
                if(ENABLE_PHYSICAL_CLOCK) {
                    pTimeLo = (new Date()).getTime();
                }
                IDManager idManager = MIPAResource.getIDManager();
                try {
                    intervalID = idManager.getID(Catalog.Numerical);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(firstflag == true) {    
                    CADAMessageContent content = new CADAMessageContent(); 
                    content.setLo(lo);
                    content.setIntervalID(intervalID);
                    content.setpTimeLo(pTimeLo);
                    for(int i = 0; i < checkers.length; i++) {
                        String checker = checkers[i];
                        send(MessageType.Detection, checker, content);
                    }
                    if(ENABLE_PHYSICAL_CLOCK) {
                        out.println(intervalID + " " + pTimeLo + " " + "null");
                        out.flush();
                    }
                    lo = null;
                }
                else {
                    //lo has already been stored.
                }
                currentClock.increment(id);
            }
            else if(prevState == true) {
                CADAVectorClock hi = new CADAVectorClock(currentClock);
                long pTimeHi = 0;
                if(ENABLE_PHYSICAL_CLOCK) {
                    pTimeHi = (new Date()).getTime();
                }
                if(firstflag == true) {
                    if(lo != null) {
                        CADAMessageContent content = new CADAMessageContent(lo, hi);
                        content.setIntervalID(intervalID);
                        content.setpTimeLo(pTimeLo);
                        content.setpTimeHi(pTimeHi);
                        for(int i = 0; i < checkers.length; i++) {
                            String checker = checkers[i];
                            send(MessageType.Detection, checker, content);
                        }
                        if(ENABLE_PHYSICAL_CLOCK) {
                            out.println(intervalID + " " + pTimeLo + " " + pTimeHi);
                            out.flush();
                        }
                        lo = null;
                        firstflag = false;
                    }
                    else {
                        CADAMessageContent content = new CADAMessageContent(); 
                        content.setHi(hi);
                        content.setIntervalID(intervalID);
                        content.setpTimeLo(pTimeLo);
                        content.setpTimeHi(pTimeHi);
                        for(int i = 0; i < checkers.length; i++) {
                            String checker = checkers[i];
                            send(MessageType.Detection, checker, content);
                        }
                        if(ENABLE_PHYSICAL_CLOCK) {
                            out.println(intervalID + " " + "null" + " " + pTimeHi);
                            out.flush();
                        }
                        firstflag = false;
                    }
                }
                else {
                    lo = null;
                }
            }
        }
        prevState = value;
    }
    
    private void send(MessageType type, String receiverName, CADAMessageContent content) {
        Message m = new Message();
        m.setType(type);
        m.setSenderID(name);
        m.setReceiverID(receiverName);
        VectorClock current = new CADAVectorClock(currentClock);
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
    
    private void broadcast(MessageType type, CADAMessageContent content) {
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

}