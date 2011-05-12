package net.sourceforge.mipa.components;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class SimulatedSender extends AbstractSender implements Serializable {

    private static final long serialVersionUID = -925287751705805498L;
    
    private MessageDispatcher messageDispatcher;
    
    public SimulatedSender() {
        messageDispatcher = MIPAResource.getMessageDispatcher();
    }
    
    @Override
    public void send(Message m) {
        // TODO Auto-generated method stub
        try {
            messageDispatcher.send(m);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
