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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.NormalProcess;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class CoordinatorImp implements Coordinator {

    private Map<String, Group> groupMap;

    private Naming server;

    public CoordinatorImp() {
        groupMap = new HashMap<String, Group>();

        try {
            server = MIPAResource.getNamingServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void memberFinished(String groupID, String memberID)
                                                    throws RemoteException {
        // TODO Auto-generated method stub
        assert (groupMap.containsKey(groupID));

        Group g = groupMap.get(groupID);
        if(DEBUG) {
            System.out.println("group ID is " + groupID);
        }
        int finishedNum = g.getNumberOfFinishedMembers();
        int total = g.getMembers().size();

        assert (finishedNum < total);

        finishedNum++;
        g.setNumberOfFinishedMembers(finishedNum);

        if (DEBUG) {
            System.out.println("Coordinator receives normal process name: "
                               + memberID);
        }

        if (finishedNum == total) {
            ArrayList<String> members = g.getMembers();
            for (int i = 0; i < members.size(); i++) {
                try {
                    NormalProcess np = (NormalProcess) server
                                                             .lookup(members
                                                                            .get(i));
                    np.finished();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized void memberStopReady(String groupID, String memberID)
    											throws RemoteException {
    	assert (groupMap.containsKey(groupID));
    	
    	Group g = groupMap.get(groupID);
    	
    	int finishedNum = g.getNumberOfFinishedMembers();
    	int total = g.getMembers().size();
    	
    	assert (finishedNum < total);
    	
    	finishedNum++;
    	g.setNumberOfFinishedMembers(finishedNum);
    	
    	if(finishedNum == total) {
    		ArrayList<String> members = g.getMembers();
    		for(int i = 0; i < members.size(); i++) {
    			try {
    				NormalProcess np = (NormalProcess) server.lookup(members.get(i));
    				np.stopReady();
    			} catch(Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    @Override
    public synchronized void newCoordinator(Group g) throws RemoteException {

        assert (groupMap.containsKey(g.getCoordinatorID()) == false);
        groupMap.put(g.getGroupID(), g);
    }
}
