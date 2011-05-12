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
package net.sourceforge.mipa.predicatedetection.normal.oga;

import static config.Config.ENABLE_PHYSICAL_CLOCK;
import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
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

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class OGANormalProcess extends AbstractNormalProcess {

    private static final long serialVersionUID = -663144941748622894L;

    private boolean prevState;

    private boolean flagMsgAct;

    private OGAVectorClock lo;

    private String[] groupNormalProcesses;

    private Map<String, Long> currentMessageCount;

    private PrintWriter out;

    /**
     * construction.
     * 
     * @param name
     * @param checkers
     * @param normalProcesses
     * @param subNormalProcesses
     *            Global activity group.
     */
    public OGANormalProcess(String name, String[] checkers,
                            String[] normalProcesses,
                            String[] subNormalProcesses) {
        super(name, checkers, normalProcesses);

        currentClock = new OGAVectorClock(normalProcesses.length);
        currentClock.increment(id);

        currentMessageCount = new HashMap<String, Long>();
        for (int i = 0; i < checkers.length; i++) {
            currentMessageCount.put(checkers[i], new Long(0));
        }

        prevState = false;
        flagMsgAct = true;

        groupNormalProcesses = subNormalProcesses;

        if (ENABLE_PHYSICAL_CLOCK) {
            try {
                out = new PrintWriter(LOG_DIRECTORY + "/" + name + ".log");
                for (int i = 0; i < checkers.length; i++) {
                    out.print(checkers[0]);
                    if (i != checkers.length - 1)
                        out.print(" ");
                }
                out.println();
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void action(boolean value) {
        boolean changed = false;
        if (prevState != value)
            changed = true;

        if (changed == true && prevState == false) {
            // interval begins. Sending control message to GA group.
            //////////////

            if (flagMsgAct) {
                lo = new OGAVectorClock(currentClock);
                
                if (ENABLE_PHYSICAL_CLOCK) {
                    lo.setPhysicalClock((new Date()).getTime());
                }
            }
            groupBroadcast(MessageType.Control, null);
            
        } else if (changed == true && prevState == true) {
            // interval ends. Sending control message to all processes.
            ////////////
            if (flagMsgAct) {
                
                OGAVectorClock hi = new OGAVectorClock(currentClock);
                
                OGAMessageContent content = new OGAMessageContent(lo, hi);

                if (ENABLE_PHYSICAL_CLOCK) {
                    IDManager idManager = MIPAResource.getIDManager();
                    try {
                        String intervalID = idManager.getID(Catalog.Numerical);
                        content.setIntervalID(intervalID);
                        hi.setPhysicalClock((new Date()).getTime());
                        out.println(intervalID + " " + lo.getPhysicalClock()
                                    + " " + hi.getPhysicalClock());
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < checkers.length; i++) {
                    String checker = checkers[i];
                    send(MessageType.Detection, checker, content);
                }
                
                flagMsgAct = false;
            }
            
            broadcast(MessageType.Control, null);
        }
        prevState = value;
    }

    @Override
    public void receiveMsg(Message message) {
        VectorClock timestamp = message.getTimestamp();
        currentClock.update(timestamp);
        flagMsgAct = true;
    }

    @Override
    public void application() {
        // TODO Auto-generated method stub

    }

    private void send(MessageType type, String receiverName,
                      OGAMessageContent content) {

        Message m = new Message();
        m.setType(type);
        m.setSenderID(name);
        m.setReceiverID(receiverName);
        VectorClock current = new OGAVectorClock(currentClock);
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
        
        currentClock.increment(id);
    }

    private void groupBroadcast(MessageType type, OGAMessageContent content) {
        for (int i = 0; i < groupNormalProcesses.length; i++) {
            if (!name.equals(groupNormalProcesses[i])) {
                send(type, groupNormalProcesses[i], content);
            }
        }
    }

    private void broadcast(MessageType type, OGAMessageContent content) {
        for (int i = 0; i < normalProcesses.length; i++) {
            if (!name.equals(normalProcesses[i])) {
                send(type, normalProcesses[i], content);
            }
        }
    }
}
