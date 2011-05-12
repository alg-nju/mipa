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
import java.util.ArrayList;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.AbstractFIFOChecker;

/**
 * 
 * @author sorrybone <sorrybone@gmail.com>
 *
 */
public class WCPChecker extends AbstractFIFOChecker {

    private static final long serialVersionUID = 419844422773355168L;

    private ArrayList<ArrayList<WCPMessageContent>> queues;
    
    private PrintWriter out = null;
    
    public WCPChecker(ResultCallback application, String predicateID, String checkerName,
            String[] normalProcesses) {
        super(application, predicateID, checkerName, normalProcesses);

        queues = new ArrayList<ArrayList<WCPMessageContent>>();
        for (int i = 0; i < normalProcesses.length; i++) {
            queues.add(new ArrayList<WCPMessageContent>());
        }
        
        if(ENABLE_PHYSICAL_CLOCK) {
            try {
                out = new PrintWriter(LOG_DIRECTORY + "/found_interval.log");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void addOnce(ArrayList<Integer> list, Integer num) {
        boolean already = false;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).intValue() == num.intValue()) {
                already = true;
                break;
            }
        }
        if(already == false) list.add(num);
    }
    
    protected void handle(ArrayList<Message> messages) {
        check(messages);
    }
    
    private void check(ArrayList<Message> messages) {
        String normalProcess = messages.get(0).getSenderID();
        int id = nameToID.get(normalProcess).intValue();
        
        ArrayList<WCPMessageContent> contents = new ArrayList<WCPMessageContent> ();
        for(int i = 0; i < messages.size(); i++)
            contents.add((WCPMessageContent) messages.get(i).getMessageContent());

        ArrayList<WCPMessageContent> queue = queues.get(id);
        
        if (queue.size() == 0) {
            for(int i = 0; i < contents.size(); i++) queue.add(contents.get(i));
            
            ArrayList<Integer> changed = new ArrayList<Integer>();
            changed.add(new Integer(id));
            while (true) {
                while (changed.size() != 0) {
                    ArrayList<Integer> newchanged = new ArrayList<Integer>();
                    for (int i = 0; i < changed.size(); i++) {
                        int elem = changed.get(i).intValue();
                        for (int j = 0; j < children.length; j++) {
                            if (elem == j)
                                continue;
                            ArrayList<WCPMessageContent> qi = queues.get(elem);
                            ArrayList<WCPMessageContent> qj = queues.get(j);
                            if (qi.size() != 0 && qj.size() != 0) {
                                WCPMessageContent qiHead = qi.get(0);
                                WCPMessageContent qjHead = qj.get(0);
                                if (qiHead
                                          .getWcpVectorClock()
                                          .lessOrEqual(
                                                       qjHead
                                                       .getWcpVectorClock())) {
                                    addOnce(newchanged, new Integer(elem));
                                }
                                if (qjHead
                                          .getWcpVectorClock()
                                          .lessOrEqual(
                                                       qiHead
                                                       .getWcpVectorClock())) {
                                    addOnce(newchanged, new Integer(j));
                                }
                            }
                        } // end for j
                    }// end for i
                    changed = newchanged;
                    for (int i = 0; i < changed.size(); i++) {
                        int elem = changed.get(i).intValue();
                        queues.get(elem).remove(0);
                    }
                }// end while
                boolean found = true;
                for (int i = 0; i < children.length; i++) {
                    if (queues.get(i).size() == 0) {
                        found = false;
                        break;
                    }
                }
                if (found == true) {
                    try {
                        application.callback(predicateID, String.valueOf(true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    break;
                // detection found
                for (int i = 0; i < children.length; i++) {
                    WCPMessageContent foundContent = queues.get(i).remove(0);
                    if(ENABLE_PHYSICAL_CLOCK) {
                        String contentID = foundContent.getContentID();
                        WCPVectorClock wcpVectorClock = foundContent.getWcpVectorClock();
                        try {
                            String end = i + 1 != children.length ? " " : "\n";
                            out.print(contentID + ":[" +wcpVectorClock.toString() +"]"+ end);
                            out.flush();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    changed.add(new Integer(i));
                }
            }
        } // :end if(queue.size() == 0)
        else {
            for(int i = 0; i < contents.size(); i++) queue.add(contents.get(i));
        }
    }
}
