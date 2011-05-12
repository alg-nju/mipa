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
package net.sourceforge.mipa.test;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.CheckerFactory;
import net.sourceforge.mipa.predicatedetection.normal.scp.SCPMessageContent;
import net.sourceforge.mipa.predicatedetection.normal.scp.SCPVectorClock;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class CheckerTestCase implements ResultCallback {
    public static void main(String[] args) {
        try {
            MIPAResource.setNamingAddress("rmi://127.0.0.1:1099/");
            Naming server = (Naming) java.rmi.Naming.lookup("rmi://127.0.0.1/" + "Naming");
            
            IDManager idManager = (IDManager) server.lookup("IDManager");
            
            String id = idManager.getID(Catalog.Application);
            String predicateID = idManager.getID(Catalog.Predicate);
            String checker = idManager.getID(Catalog.Checker);
            
            CheckerTestCase tc = new CheckerTestCase();
            
            ResultCallback stub = (ResultCallback) UnicastRemoteObject.exportObject(tc, 0);
            
            server.bind(id, stub);
            
            String[] normalProcesses = new String[2];
            normalProcesses[0] = idManager.getID(Catalog.NormalProcess);
            normalProcesses[1] = idManager.getID(Catalog.NormalProcess);
            
            CheckerFactory.createSCPChecker(id, predicateID, checker, normalProcesses);
            
            Communication checkerProcess = (Communication) server.lookup(checker);
            
            Message m = new Message();
            m.setSenderID(normalProcesses[0]);
            m.setReceiverID(checker);
            m.setType(MessageType.Detection);
            SCPVectorClock lo = new SCPVectorClock(2);
            ArrayList<Long> clock = new ArrayList<Long>();
            clock.add(new Long(1));
            clock.add(new Long(0));
            lo.setVectorClock(clock);
            
            SCPVectorClock hi = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(2));
            clock.add(new Long(1));
            hi.setVectorClock(clock);
            SCPMessageContent content = new SCPMessageContent(lo, hi);
            m.setMessageContent(content);
            
            m.getMessageContent();
            checkerProcess.receive(m);
            
            m = new Message();
            m.setSenderID(normalProcesses[0]);
            m.setReceiverID(checker);
            m.setType(MessageType.Detection);
            lo = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(3));
            clock.add(new Long(3));
            lo.setVectorClock(clock);
            
            hi = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(4));
            clock.add(new Long(4));
            hi.setVectorClock(clock);
            content = new SCPMessageContent(lo, hi);
            m.setMessageContent(content);
            
            checkerProcess.receive(m);
            
            m = new Message();
            m.setSenderID(normalProcesses[1]);
            m.setReceiverID(checker);
            m.setType(MessageType.Detection);
            lo = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(1));
            clock.add(new Long(1));
            lo.setVectorClock(clock);
            
            hi = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(1));
            clock.add(new Long(2));
            hi.setVectorClock(clock);
            content = new SCPMessageContent(lo, hi);
            m.setMessageContent(content);
            
            checkerProcess.receive(m);
            
            m = new Message();
            m.setSenderID(normalProcesses[1]);
            m.setReceiverID(checker);
            m.setType(MessageType.Detection);
            lo = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(2));
            clock.add(new Long(2));
            lo.setVectorClock(clock);
            
            hi = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(2));
            clock.add(new Long(3));
            hi.setVectorClock(clock);
            content = new SCPMessageContent(lo, hi);
            m.setMessageContent(content);
            
            checkerProcess.receive(m);
            
            m = new Message();
            m.setSenderID(normalProcesses[1]);
            m.setReceiverID(checker);
            m.setType(MessageType.Detection);
            lo = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(2));
            clock.add(new Long(3));
            lo.setVectorClock(clock);
            
            hi = new SCPVectorClock(2);
            clock = new ArrayList<Long>();
            clock.add(new Long(3));
            clock.add(new Long(4));
            hi.setVectorClock(clock);
            content = new SCPMessageContent(lo, hi);
            m.setMessageContent(content);
            
            checkerProcess.receive(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void callback(String predicateID, String value) {
        System.out.println(value);
    }
}
