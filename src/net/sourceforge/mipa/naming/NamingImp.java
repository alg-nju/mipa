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

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class provides system Naming interface for others modules in mipa
 * system.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class NamingImp implements Naming {

    /** rmi registry port */
    private int port = 1099;

    /** rmi registry address */
    private String registryAddress = "";

    /** rmi registry format address */
    private String formatAddress = null;

    public NamingImp() throws RemoteException {
        super();
    }

    /**
     * sets rmi registry port.
     * 
     * @param port
     *            an integer presents rmi registry port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * returns rmi registry port.
     * 
     * @return an integer presents rmi registry port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets rmi registry address.
     * 
     * @param address
     *            a URL.
     */
    public void setRegistryAddress(String address) {
        this.registryAddress = address;
    }

    /**
     * Returns rmi registry address which sets by
     * <code>setRegistryAddress</code>.
     * 
     * @return a URL of rmi registry address.
     */
    public String getRegistryAddress() {
        return registryAddress;
    }

    /**
     * Formats registry address.
     */
    public void formatAddress() {
        formatAddress = "rmi://" + registryAddress + ":" + port + "/";
        //System.out.println(formatAddress);
    }

    public void bind(String name, Remote obj) throws AccessException,
                                             RemoteException,
                                             AlreadyBoundException,
                                             MalformedURLException {

        java.rmi.Naming.bind(formatAddress + name, obj);

    }

    public Remote lookup(String name) throws AccessException, RemoteException,
                                     NotBoundException, MalformedURLException {
        return java.rmi.Naming.lookup(formatAddress + name);
    }

    public void rebind(String name, Remote obj) throws AccessException,
                                               RemoteException,
                                               MalformedURLException {
        java.rmi.Naming.rebind(formatAddress + name, obj);
    }

    public void unbind(String name) throws AccessException, RemoteException,
                                   NotBoundException, MalformedURLException {
        java.rmi.Naming.unbind(formatAddress + name);
    }
}
