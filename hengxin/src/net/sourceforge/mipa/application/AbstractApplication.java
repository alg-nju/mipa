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
package net.sourceforge.mipa.application;

import static config.Debug.DEBUG;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.components.BrokerInterface;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.Naming;

import org.w3c.dom.Document;

/**
 * provides application abstract method.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class AbstractApplication implements ResultCallback {

    /** application name binds in Naming server */
    private String applicationName;

    /** predicate represented by Document. */
    //private Document predicate;

    public AbstractApplication() {
    	try {
    		String namingAddress = MIPAResource.getNamingAddress();

            if (DEBUG) {
                System.out.println("naming address is " + namingAddress);
            }
    		Naming server = MIPAResource.getNamingServer();
            if (DEBUG) {
                System.out.println("application lookup Naming successfully.");
            }

    		IDManager idManager = MIPAResource.getIDManager();
    		applicationName = idManager.getID(Catalog.Application);

    		ResultCallback stub = (ResultCallback) UnicastRemoteObject
                                                   .exportObject(this,
                                                                  0);
    		server.bind(applicationName, stub);

            if (DEBUG) {
                System.out.println("application binds successfully.");
            }
            
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public AbstractApplication(String configFilename) {
        this();
    	//this.predicate = parseXml(xmlName);
    }

    public AbstractApplication(Document configFile) {
    	this();
        //this.predicate = xml;
    }

    public abstract void callback(String predicateID, String value) throws RemoteException;

    /**
     * register predicate
     * 
     * @param predicateFilename
     *            predicate file name
     * @return predicate ID
     */
    public String register(String predicateFilename) {

    	Document predicate = parseXml(predicateFilename);
    	String predicateID = null;
        try {

            // get predicate parser and transfer xml document to it.

            //PredicateParserMethod parser = MIPAResource.getPredicateParser();
            BrokerInterface broker = MIPAResource.getBroker();
            if (DEBUG) {
                System.out
                          .println("application lookups predicate parser successfully.");
            }

            //parser.parsePredicate(applicationName, predicate);
            predicateID = broker.registerPredicate(applicationName, predicate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return predicateID;
    }
    
    /**
     * unregister the predicate.
     */
    public void unregister(String predicateID) {
    	BrokerInterface broker = MIPAResource.getBroker();
    	try {
    		broker.unregisterPredicate(predicateID);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }


    /**
     * parse predicate to document.
     * 
     * @param fileName
     *            a string
     * @return predicate document
     */
    private Document parseXml(String fileName) {
        Document doc = null;
        try {
            File f = new File(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory
                                                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(f);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
}
