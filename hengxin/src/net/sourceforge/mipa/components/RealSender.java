package net.sourceforge.mipa.components;

import static config.Debug.DEBUG;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.naming.Naming;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class RealSender extends AbstractSender implements Serializable {

    private static final long serialVersionUID = -773577183450412779L;
    private Naming server;
    private Map<String, Communication> comTable;
    
    public RealSender() {
        server = MIPAResource.getNamingServer();
        comTable = new HashMap<String, Communication>();
    }
    
    @Override
    public void send(Message message) {
        // TODO Auto-generated method stub
        if(DEBUG) {
            System.out.println("Real-Mode Send:");
            System.out.println("\t" + message.getSenderID() + " -> " + message.getReceiverID());
        }
        String receiveName = message.getReceiverID();
        try {
            if (comTable.containsKey(receiveName)) {
                comTable.get(receiveName).receive(message);
            }
            else {
                Communication stub = (Communication) server.lookup(receiveName);
                comTable.put(receiveName, stub);
                stub.receive(message);
            }
        }
        catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }     
}