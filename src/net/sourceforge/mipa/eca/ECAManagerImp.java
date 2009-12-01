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
package net.sourceforge.mipa.eca;

import static config.Config.EXPERIMENT;
import static config.Debug.DEBUG;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import net.sourceforge.mipa.components.ContextRegister;
import net.sourceforge.mipa.components.Coordinator;
import net.sourceforge.mipa.components.Group;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NormalProcess;
import net.sourceforge.mipa.predicatedetection.PredicateType;
import net.sourceforge.mipa.predicatedetection.oga.OGANormalProcess;
import net.sourceforge.mipa.predicatedetection.scp.SCPNormalProcess;
import net.sourceforge.mipa.test.TimeInfo;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ECAManagerImp implements ECAManager {

    /** name of ECAManager */
    private String ecaManagerName;

    private ContextRegister contextRegister;

    private DataSource dataSource;

    public ECAManagerImp(ContextRegister contextRegister,
                         DataSource dataSource, String ecaManagerName) {
        this.setContextRegister(contextRegister);
        this.ecaManagerName = ecaManagerName;
        this.dataSource = dataSource;
    }

    /**
     * @param ecaManagerName
     *            the ecaManagerName to set
     */
    public void setECAManagerName(String ecaManagerName) {
        this.ecaManagerName = ecaManagerName;
    }

    /**
     * @return the ecaManagerName
     */
    public String getECAManagerName() {
        return this.ecaManagerName;
    }

    /**
     * @param contextRegister
     *            the contextRegister to set
     */
    public void setContextRegister(ContextRegister contextRegister) {
        this.contextRegister = contextRegister;
    }

    /**
     * @return the contextRegister
     */
    public ContextRegister getContextRegister() {
        return contextRegister;
    }

    @Override
    public void registerLocalPredicate(LocalPredicate localPredicate,
                                       String name, 
                                       Group g)
                                           throws RemoteException {
        // EXPERIMENT
        TimeInfo timeInfo = new TimeInfo();
        if (EXPERIMENT) {
            timeInfo.item_1_begin = System.nanoTime();
        }
        
        try {
            Naming server = MIPAResource.getNamingServer();

            Coordinator coordinator = MIPAResource.getCoordinator();

            if (DEBUG) {
                System.out.println("Get normal process: " + name);
            }

            String[] checkers = new String[g.getOwners().size()];
            String[] normalProcesses = new String[g.getMembers().size()];
            g.getOwners().toArray(checkers);
            g.getMembers().toArray(normalProcesses);

            Listener action = null;
            NormalProcess npStub = null;
            if (g.getType() == PredicateType.SCP) {
                SCPNormalProcess np = new SCPNormalProcess(name, checkers,
                                                           normalProcesses);
                npStub = (NormalProcess) UnicastRemoteObject
                                                            .exportObject(np, 0);
                action = np;
            } else if (g.getType() == PredicateType.OGA) {
                String[] subMembers = new String[g.getSubMembers().size()];
                g.getSubMembers().toArray(subMembers);
                OGANormalProcess np = new OGANormalProcess(name, checkers,
                                                           normalProcesses,
                                                           subMembers);
                npStub = (NormalProcess) UnicastRemoteObject
                                                            .exportObject(np, 0);
                action = np;
            } else if (g.getType() == PredicateType.WCP) {

            } else if (g.getType() == PredicateType.LP) {

            } else {

            }

            server.bind(name, npStub);
            coordinator.memberFinished(g.getCoordinatorID(), name);

            if (DEBUG) {
                System.out.println("before binding condition...");
            }

            Condition everything = new EmptyCondition(action, localPredicate);

            if (DEBUG) {
                System.out.println("after binding condition...");
            }
            // attaching condition to data source.
            
            if (DEBUG) {
                System.out.println("local predicate includes:");
            }
            ArrayList<Atom> arrayList = localPredicate.getAtoms();
            for(int i=0; i<arrayList.size(); i++)
            {
                Atom atom = arrayList.get(i);
                if (DEBUG) {
                    System.out.print(" " + atom.getName());
                }
                dataSource.attach(everything, atom.getName());
            }
            if (DEBUG) {
                System.out.println(".");
                System.out.println("binding condition to data source successful.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(EXPERIMENT) {
            timeInfo.item_1_end = System.nanoTime();
        }
        
        if(EXPERIMENT) {
            try {
                PrintWriter out = new PrintWriter(new FileWriter("log/eca_time_cost", true), true);
                out.println((timeInfo.item_1_end - timeInfo.item_1_begin));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * registers local resource to resource manager.
     * 
     * @param resources
     *            local resources
     */
    public void registerResources(ArrayList<SensorAgent> resources) {
        // TODO auto scan local resource

        try {
            for (int i = 0; i < resources.size(); i++) {
                SensorAgent resource = resources.get(i);
                contextRegister.registerResource(resource.getName(),
                                                 resource.getValueType(),
                                                 ecaManagerName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
