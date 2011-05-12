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
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.AbstractSender;
import net.sourceforge.mipa.components.Mode;
import net.sourceforge.mipa.components.RealSender;
import net.sourceforge.mipa.components.SimulatedSender;

/**
 *
 * @author Yiling Yang
 */
public abstract class AbstractFIFOChecker implements Serializable, Communication, Runnable{

    private static final long serialVersionUID = -5023931031473945453L;
    
    protected ResultCallback application;
    
    protected String name;
    
    protected String[] children;
    
    protected Map<String, Integer> nameToID;
    
    private long[] currentMessageCount;

    private ArrayList<ArrayList<Message>> msgBuffer;
    
    private ArrayList<ArrayList<Message>> continousMessageBuffer;
    
    protected AbstractSender sender;
    
    protected String predicateID;
    
    public AbstractFIFOChecker(ResultCallback application, 
    						String predicateID,
                            String checkerName, 
                            String[] children) {
        this.application = application;
        this.predicateID = predicateID;
        this.name = checkerName;
        this.children = children;
        
        nameToID = new HashMap<String, Integer>();
        for(int i = 0; i < children.length; i++) {
            nameToID.put(children[i], new Integer(i));
        }
        
        msgBuffer = new ArrayList<ArrayList<Message>>();
        continousMessageBuffer = new ArrayList<ArrayList<Message>>();
        currentMessageCount = new long[children.length];
        for (int i = 0; i < children.length; i++) {
            msgBuffer.add(new ArrayList<Message>());
            currentMessageCount[i] = 0;
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
    
    public void receive(Message message)throws RemoteException
    {
        String normalProcess = message.getSenderID();
        int id = nameToID.get(normalProcess).intValue();

        synchronized(msgBuffer) {
            add(msgBuffer.get(id), message);
            updateCurrentMessageCount(id);
        }
        ArrayList<Message> messages = getContinousMessages(msgBuffer.get(id),currentMessageCount[id]);
        if(messages.size() != 0) {
            continousMessageBuffer.add(messages);
        }
        if(continousMessageBuffer.size() != 0) {//notify checker
            synchronized(this) {
                this.notify();
            }
        }
    }
    
    private void add(ArrayList<Message> messages, Message msg) {
        long msgID = msg.getMessageID();

        for (int i = 0; i < messages.size(); i++) {
            long tempID = messages.get(i).getMessageID();

            if (msgID < tempID) {
                messages.add(i, msg);
                return;
            }
        }
        messages.add(msg);
    }   
    
    private void updateCurrentMessageCount(int id) {
        // TODO Auto-generated method stub
        long messageID = -1;
        int size = msgBuffer.get(id).size();
        ArrayList<Message> arrayList = msgBuffer.get(id);
        for(int i = 0; i<size; i++) {
            if(arrayList.get(i).getMessageID() == currentMessageCount[id])
            {
                for(int j = i+1; j<size; j++) {
                    if((arrayList.get(j-1).getMessageID()+1) != arrayList.get(j).getMessageID()) {
                        messageID = arrayList.get(j-1).getMessageID()+1;
                        break;
                    }
                }
                if(messageID == -1) {
                    messageID = arrayList.get(size-1).getMessageID()+1;
                }
                break;
            }
        }
        if(messageID != -1) {
            currentMessageCount[id] = messageID;
        }
    }

    public void run() {
        while(true)
        {
            if(continousMessageBuffer.size() != 0)
            {
                handleBuffer();
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
    
    private void handleBuffer() {
        ArrayList<Message> messages = continousMessageBuffer.remove(0);
        handle(messages);
    }
    
    private ArrayList<Message> getContinousMessages(ArrayList<Message> bufferedMessages,
                                                    long currentMessageCount) {
        ArrayList<Message> messages = new ArrayList<Message>();
        if(bufferedMessages.size() > 0) {
            int index = 0;
            while(index < bufferedMessages.size()
                  &&bufferedMessages.get(index).getMessageID() < currentMessageCount) {
                messages.add(bufferedMessages.get(index));
                index++;
            }
            for(int i = 0; i<index; i++) {
                bufferedMessages.remove(0);
            }
        }
        return messages;
    }
    protected abstract void handle(ArrayList<Message> messages);
}
