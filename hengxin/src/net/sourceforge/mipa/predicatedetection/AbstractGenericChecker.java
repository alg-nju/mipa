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
package net.sourceforge.mipa.predicatedetection;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.AbstractSender;
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.Mode;
import net.sourceforge.mipa.components.RealSender;
import net.sourceforge.mipa.components.SimulatedSender;

/**
 *
 * @author Yiling Yang
 */
public abstract class AbstractGenericChecker implements Serializable, Communication, Runnable{

    private static final long serialVersionUID = -5023931031473945453L;
    
    protected ResultCallback application;
    
    protected String name;
    
    protected String[] children;
    
    protected Map<String, Integer> nameToID;
    
    protected int index;

    /** index --> Message queue */
    protected Map<Integer, ArrayList<Message>> messageQueues;
    
    protected Map<String, Integer> indexMap;
    
    private boolean finished = true;
    
    protected AbstractSender sender;
    
    protected String predicateID;
    
    public AbstractGenericChecker(ResultCallback application, 
    						String predicateID,
                            String checkerName, 
                            String[] children) {
        this.application = application;
        this.name = checkerName;
        this.children = children;
        
        this.predicateID = predicateID;
        
        nameToID = new HashMap<String, Integer>();
        for(int i = 0; i < children.length; i++) {
            nameToID.put(children[i], new Integer(i));
        }
        
        index = 1;
        
        indexMap = new HashMap<String, Integer>();
        messageQueues = new HashMap<Integer, ArrayList<Message>>();
        for(int i = 0; i < children.length; i++) {
            indexMap.put(children[i], new Integer(i + 1));
            messageQueues.put(new Integer(i + 1), new ArrayList<Message>());
        }
        
        Mode mode = MIPAResource.getMode();
        switch(mode) {
            case SIMULATED:
                sender = new SimulatedSender();
                break;
            case REAL:
                sender = new RealSender();
                break;
            default:
                System.out.println("wrong mode!");
            break;
        }
        
        Thread thread = new Thread(this);
        thread.start();
    }
    
    public synchronized void receive(Message message)throws RemoteException
    {
        String senderName = message.getSenderID();
        Integer messageSenderIndex = indexMap.get(senderName);

        synchronized(messageQueues) {
            ArrayList<Message> queue = messageQueues.get(messageSenderIndex);
            queue.add(message);
        }
        
        if(finished == true) {//notify checker
            synchronized(this) {
                this.notify();
            }
        }
    }
    
    public void run() {
        while(true)
        {
            int size = messageQueues.get(new Integer(index)).size();
            if(size != 0)
            {
                finished = false;
                handle(messageQueues.get(new Integer(index)));
                finished = true;
                
            }
            else
            {
                try {
                    synchronized(this){
                       this.wait();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    protected abstract void handle(ArrayList<Message> message);
}
