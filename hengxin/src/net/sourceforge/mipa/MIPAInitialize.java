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
package net.sourceforge.mipa;

import static config.Config.EXPERIMENT;
import static config.Debug.DEBUG;

import java.rmi.server.UnicastRemoteObject;

import net.sourceforge.mipa.components.Broker;
import net.sourceforge.mipa.components.BrokerInterface;
import net.sourceforge.mipa.components.ContextModeling;
import net.sourceforge.mipa.components.ContextRetrieving;
import net.sourceforge.mipa.components.Coordinator;
import net.sourceforge.mipa.components.CoordinatorImp;
import net.sourceforge.mipa.components.ExponentDelayMessageDispatcher;
import net.sourceforge.mipa.components.GroupManager;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.MessageDispatcher;
import net.sourceforge.mipa.components.rm.ResourceManager;
import net.sourceforge.mipa.components.rm.SimpleResourceManager;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.IDManagerImp;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.naming.NamingService;
import net.sourceforge.mipa.predicatedetection.PredicateParser;
import net.sourceforge.mipa.predicatedetection.PredicateParserMethod;
import net.sourceforge.mipa.tools.GCRunner;

/**
 * MIPA platform initialization.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class MIPAInitialize {

    /**
     * initialize MIPA Infrastructure.
     * 
     * @return true if successful, false otherwise
     */
    public boolean initialize() {

        if(EXPERIMENT) {
            GCRunner r = new GCRunner();
            Thread t = new Thread(r);
            t.start();
        }

        try {
            Naming server = MIPAResource.getNamingServer();

            if (DEBUG) {
                System.out.println("Creating IDManager...");
            }

            IDManagerImp idManager = new IDManagerImp();
            IDManager managerStub = (IDManager) UnicastRemoteObject
                                                         .exportObject(idManager,
                                                                       0);
            server.bind("IDManager", managerStub);

            if (DEBUG) {
                System.out.println("Creating ContextRegister...");
            }

            ContextModeling contextModeling = new ContextModeling();
            
            ContextRetrieving contextRetrieving = new ContextRetrieving();

            ResourceManager resourceManager =
                                    new SimpleResourceManager(contextModeling,
                                                                  contextRetrieving);
            
            
            /*
            ContextRegisterImp contextRegister 
                                    = new ContextRegisterImp(contextModeling, 
                                                               contextRetrieving);
            
            ContextRegister contextRegisterStub 
                                    = (ContextRegister) UnicastRemoteObject
                                                             .exportObject(contextRegister,
                                                                            0);
            server.bind("ContextRegister", contextRegisterStub);
            */
            
            //RandomDelayMessageDispatcher messageDispatcher = new RandomDelayMessageDispatcher();
//            NoDelayMessageDispatcher messageDispatcher = new NoDelayMessageDispatcher();
            ExponentDelayMessageDispatcher messageDispatcher = new ExponentDelayMessageDispatcher();
            
            MessageDispatcher messageDispatcherStub 
                                    = (MessageDispatcher) UnicastRemoteObject
                                                                .exportObject(messageDispatcher,
                                                                              0);
            
            server.bind("MessageDispatcher", messageDispatcherStub);
            
            Thread t = new Thread(messageDispatcher);
            t.start();

            if (DEBUG) {
                System.out.println("Creating PredicateParser...");
            }
            
            GroupManager groupManager 
                                = new GroupManager(resourceManager);
            
            Broker broker = new Broker(resourceManager, groupManager);
            
            BrokerInterface brokerStub = 
                            (BrokerInterface) UnicastRemoteObject
                                                    .exportObject(broker, 0);
            
            server.bind("Broker", brokerStub);
            
            groupManager.setBroker(broker);
            
            PredicateParser predicateParser = new PredicateParser(groupManager);
            
            PredicateParserMethod predicateParserStub 
                                        = (PredicateParserMethod) UnicastRemoteObject
                                                                        .exportObject(predicateParser,
                                                                                      0);

            server.bind("PredicateParser", predicateParserStub);
            
            CoordinatorImp coordinator = new CoordinatorImp();
            Coordinator coordinatorStub = (Coordinator) UnicastRemoteObject.exportObject(coordinator, 0);
            server.bind("Coordinator", coordinatorStub);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        // start naming service
        NamingService service = new NamingService();
        System.out.println("MIPA system Naming Service starts sucessfully.");
        service.startService();
        
        boolean result = new MIPAInitialize().initialize();
        if (result == true) {
            System.out.println("Initialization finished.");
        } else {
            System.out.println("Error occurs when initializing");
        }

        if (DEBUG) {
            System.out.println(MIPAResource.getNamingAddress());
        }
    }
}
