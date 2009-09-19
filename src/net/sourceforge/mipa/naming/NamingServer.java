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

package net.sourceforge.mipa.naming;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * This class <code>NamingServer</code> provides Naming resolving.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class NamingServer {

    /** registry port */
    private int port;

    /** registry address */
    private String address;

    /**
     * <code>NamingServer</code> construction.
     * 
     * @param configFile config file.
     */
    public NamingServer(String configFile) {
        parseConfig(configFile);
    }

    /**
     * starts naming server.
     */
    public void startServer() {
        try {
            // TODO add security policy

            LocateRegistry.createRegistry(port);

            NamingImp server = new NamingImp();

            server.setRegistryAddress(address);
            server.setPort(port);
            server.formatAddress();

            Naming stub = (Naming) UnicastRemoteObject.exportObject(server, 0);

            java.rmi.Naming.rebind("rmi://" + address + ":" + port + "/Naming", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void parseConfig(String fileName) {
        try {
            File f = new File(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory
                                                                   .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);

            address = doc.getElementsByTagName("address").item(0)
                                .getFirstChild().getNodeValue();

            port = Integer.parseInt(doc.getElementsByTagName("port").item(0)
                             .getFirstChild().getNodeValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        //TODO parse config will move into MIPAResource.
        NamingServer server = new NamingServer("config/config.xml");
        //System.out.println("Naming server is running...");
        System.out.println("========== MIPA system Naming Server ==========");
        System.out.println("Naming server information:");
        System.out.println("*\tIP address: " + server.address);
        System.out.println("*\tport: " + server.port);
        server.startServer();
    }
}
