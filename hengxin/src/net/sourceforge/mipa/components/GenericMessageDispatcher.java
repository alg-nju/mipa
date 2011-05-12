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
package net.sourceforge.mipa.components;

import static config.Debug.DEBUG;
import java.rmi.RemoteException;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
//import java.util.PriorityQueue;

import net.sourceforge.mipa.naming.Naming;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class GenericMessageDispatcher 
            implements Runnable, MessageDispatcher {

    /** heart beat time (ms) */
    protected final static int heartBeat = 1;

    /** current time of message dispatcher */
    protected long currentTime;

    /** dispatch queue */
    //private PriorityQueue<Message> dispatchQueue;
    private PriorityQueue dispatchQueue;

    /** mapping table */
    private Map<String, Communication> comTable;

    private Naming server;

    public GenericMessageDispatcher() {
        currentTime = 0;
        // FIXME find why PriorityQueue must have initial capacity.
        /*
        dispatchQueue = new PriorityQueue<Message>(100,
                                                   new Comparator<Message>() {
                                                   public int compare(
                                                                      Message i,
                                                                      Message j) {
                                                       // sun jdk has a bug 6495524. <PriorityQueue>.
                                                       // sometimes j will be null.
                                                       //if(j == null) return 1;
                                                   return (int) (i
                                                                  .getDispatchTime()
                                                   - j.getDispatchTime());
                                                   }
                                                           });
        */
        //dispatchQueue = new MyPriorityQueue(10000);
        dispatchQueue = new PriorityQueue();
        comTable = new HashMap<String, Communication>();
        this.server = MIPAResource.getNamingServer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(heartBeat);
                currentTime += heartBeat;
                if(DEBUG){
                    //System.out.println("current time is " + currentTime);
                }
                while (true) {
                        Message m = dispatchQueue.peek();
                        //Message m = (Message) dispatchQueue.top();
                        if (m == null || m.getDispatchTime() > currentTime)
                            break;
                        m = dispatchQueue.poll();
                        //m = (Message) dispatchQueue.pop();
                    
                        dispatch(m);
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * dispatch message to destination.
     * 
     * @param message
     *            message to delivery.
     */
    private void dispatch(Message message) {
        if(DEBUG) {
            System.out.println("Message dispatch:");
            System.out.println("\t" + message.getSenderID() + " -> " + message.getReceiverID());
        }
        
        try {
            if (comTable.containsKey(message.getReceiverID())) {
                comTable.get(message.getReceiverID()).receive(message);
            } else {
                Communication receiver = (Communication) server
                                                               .lookup(message
                                                                              .getReceiverID());
                comTable.put(message.getReceiverID(), receiver);
                receiver.receive(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.mipa.components.MessageDispatcher#send(java.lang.String,
     * net.sourceforge.mipa.components.Message)
     */
    @Override
    public synchronized void send(Message message) throws RemoteException {
        assert(message != null);
        
        message.setReachTime(currentTime);

        addDispatchTime(message);
        
        dispatchQueue.offer(message);
        //dispatchQueue.put(message);
    }

    public abstract void addDispatchTime(Message message);
}
