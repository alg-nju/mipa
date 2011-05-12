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
import java.util.ArrayList;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.predicatedetection.AbstractFIFOChecker;
import net.sourceforge.mipa.predicatedetection.normal.cada.CADAMessageContent;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 */
public class CADAChecker extends AbstractFIFOChecker {

    private static final long serialVersionUID = -4024329536304185640L;

    private ArrayList<ArrayList<CADAMessageContent>> queues;
    
    private PrintWriter out = null;
    
    /**
     * @param application
     * @param checkerName
     * @param normalProcesses
     */
    public CADAChecker(ResultCallback application, String predicateID, String checkerName,
                      String[] normalProcesses) {
        super(application, predicateID, checkerName, normalProcesses);
      
        queues = new ArrayList<ArrayList<CADAMessageContent>>();
        for (int i = 0; i < normalProcesses.length; i++) {
            queues.add(new ArrayList<CADAMessageContent>());
        }
        
        if(ENABLE_PHYSICAL_CLOCK) {
            try {
                out = new PrintWriter(LOG_DIRECTORY + "/found_interval.log");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // FIX bug of issue 8 at http://mipa.googlecode.com
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
        
        ArrayList<CADAMessageContent> contents = new ArrayList<CADAMessageContent> ();
        for(int i = 0; i < messages.size(); i++)
            contents.add((CADAMessageContent) messages.get(i).getMessageContent());

        ArrayList<CADAMessageContent> queue = queues.get(id);
        for(int i = 0; i < contents.size(); i++) {
            //message is [null,hi] of which interval has been detected as early-detection
            if(queue.size() == 0
                    && contents.get(i).getLo() == null) {
                continue;
            }
            //queue is empty or (the head of queue is [lo,null] and message is [null,hi]) can trigger checking
            if(queue.size() == 0
                    ||(queue.get(0).getLo() != null
                            && queue.get(0).getHi() == null
                            && contents.get(i).getLo() == null
                            && contents.get(i).getHi() != null)) {
                String found = "";
                if(contents.get(i).getLo() != null
                        && contents.get(i).getHi() == null) {//message is [lo,null]
                    queue.add(contents.get(i));
                    for(int j = 0; j<children.length; j++) {
                        if(j == id)
                            continue;
                        CADAMessageContent qid = queue.get(0);
                        if(queues.get(j).size()!=0) {
                            CADAMessageContent qj = queues.get(j).get(0);
                            while(qj.getHi() != null
                                    && qid.getLo().notLessThan(qj.getHi())) {
                                queues.get(j).remove(0);
                                if(queues.get(j).size() == 0)
                                    break;
                                qj = queues.get(j).get(0);
                            }
                        }
                    }
                    int num= getHiNull();
                    if(hasEmptyQueue()||num >= 2) {
                        found = "";
                    }
                    else {//the number of queues of which the head is [lo,null] is 1
                        found = "early-detection";
                        for(int j=0;j<queues.size();j++) {
                            if(j == id)
                                continue;
                            if(!(queues.get(j).get(0)
                                    .getLo().lessOrEqual(
                                            queues.get(id).get(0).getLo())
                                            && queues.get(id).get(0)
                                            .getLo().lessOrEqual(
                                                    queues.get(j).get(0).getHi()))) {
                                found = "";
                                break;
                            }
                        }
                    }
                }
                else {
                    if(contents.get(i).getLo() == null
                        && contents.get(i).getHi() != null) {//message is [null,hi]
                        assert(queue.get(0).getLo() != null);
                        queue.get(0).setHi(contents.get(i).getHi());
                        queue.get(0).setpTimeHi(contents.get(i).getpTimeHi());
                    }
                    else {//message is [lo,hi]
                        queue.add(contents.get(i));
                    }
                    ArrayList<Integer> changed = new ArrayList<Integer>();
                    changed.add(new Integer(id));
                    while (changed.size() != 0) {
                        ArrayList<Integer> newchanged = new ArrayList<Integer>();
                        for (int m = 0; m < changed.size(); m++) {
                            int elem = changed.get(m).intValue();
                            for (int n = 0; n < children.length; n++) {
                                if (elem == n)
                                    continue;
                                if(queues.get(elem).size()!=0 && queues.get(n).size()!=0) {
                                    CADAMessageContent qm = queues.get(elem).get(0);
                                    CADAMessageContent qn = queues.get(n).get(0);
                                    if(qm.getHi() != null
                                            && (qn.getLo().notLessThan(qm.getHi()))) {
                                        addOnce(newchanged, new Integer(elem));
                                    }
                                    if(qn.getHi() != null
                                            && (qm.getLo().notLessThan(qn.getHi()))) {
                                        addOnce(newchanged, new Integer(n));
                                    }
                                }
                            }
                        }
                        changed = newchanged;
                        for (int j = 0; j < changed.size(); j++) {
                            int elem = changed.get(j).intValue();
                            queues.get(elem).remove(0);
                        }
                    }
                    int num= getHiNull();
                    if(hasEmptyQueue()||num >= 2) {
                        found = "";
                    }
                    else if(num == 1) {
                        int index=0;
                        for(int j=0;j<queues.size();j++) {
                            if(queues.get(j).get(0).getHi() == null) {
                                index = j;
                                break;
                            }
                        }
                        found = "early-detection";
                        for(int j=0;j<queues.size();j++) {
                            if(j == index)
                                continue;
                            if(!(queues.get(j).get(0)
                                    .getLo().lessOrEqual(
                                            queues.get(index).get(0).getLo())
                                            && queues.get(index).get(0)
                                            .getLo().lessOrEqual(
                                                    queues.get(j).get(0).getHi()))) {
                                found = "";
                                break;
                            }
                        }
                    }
                    else {
                        found = "concurrency-detection";
                    }
                }
                if (found.equals("early-detection")
                        ||found.equals("concurrency-detection")) {
                    try {
                        application.callback(predicateID, String.valueOf(true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(ENABLE_PHYSICAL_CLOCK) {
                        if(found.equals("early-detection")) {
                            out.print("early" +" ");
                            out.flush();
                        }
                        else {
                            out.print("concurrency" +" ");
                            out.flush();  
                        }
                    }
                    for (int j = 0; j < children.length; j++) {
                        CADAMessageContent foundContent = queues.get(j).remove(0);
                        if(ENABLE_PHYSICAL_CLOCK) {
                            String intervalID = foundContent.getIntervalID();
                            long lo = foundContent.getpTimeLo();
                            long hi = foundContent.getpTimeHi();
                            try {
                                String end = j + 1 != children.length ? " " : "\n";
                                if(hi != 0) {
                                    out.print(intervalID + " " + lo + " " + hi + end);
                                    out.flush();
                                }
                                else {
                                    out.print(intervalID + " " + lo + " " + "null" + end);
                                    out.flush();
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            else {
                if(contents.get(i).getLo() != null) {//[lo,null] or [lo,hi]
                    queue.add(contents.get(i));
                }
                else {//[null,hi]
                    queue.get(queue.size()-1).setHi(contents.get(i).getHi());
                    queue.get(queue.size()-1).setpTimeHi(contents.get(i).getpTimeHi());
                }
            }
        }
    }
    
    private int getHiNull() {
        int count = 0;
        for(int i=0;i<queues.size();i++) {
            if(queues.get(i).size()!=0 
                    &&  queues.get(i).get(0).getHi() == null) {
                count++;
            }
        }
        return count;
    }
    
    private boolean hasEmptyQueue() {
        for (int i = 0; i < children.length; i++) {
            if (queues.get(i).size() == 0) {
                return true;
            }
        }
        return false;
    }
}