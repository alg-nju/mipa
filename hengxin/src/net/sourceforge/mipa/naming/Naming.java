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
 * Naming server interface.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public interface Naming extends Remote {
    /**
     * Returns a reference for the remote object associated with the specified
     * <code>name</code>.
     * 
     * @param name
     *            a name in Naming Server
     * @return a reference for a remote object if the specified
     *         <code>name</code> object exists, <code>null</code> otherwise
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public Remote lookup(String name) throws AccessException, 
                                                  RemoteException,
                                                  NotBoundException, 
                                                  MalformedURLException;

    /**
     * Binds the specified <code>name</code> to a remote object.
     * 
     * @param name
     *            a name in Naming Server
     * @param obj
     *            a reference for remote object
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws MalformedURLException
     */
    public void bind(String name, Remote obj) throws AccessException,
                                                          RemoteException,
                                                          AlreadyBoundException,
                                                          MalformedURLException;

    /**
     * Rebinds the specified <code>name</code> to a new object.
     * 
     * @param name
     *            a name in Naming Server
     * @param obj
     *            new object to associate with the <code>name</code>
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws MalformedURLException
     */
    public void rebind(String name, Remote obj) throws AccessException,
                                                            RemoteException,
                                                            MalformedURLException;

    /**
     * Destroys the binding for the specified <code>name</code> that is
     * associated with an object.
     * 
     * @param name
     *            a name in Naming Server
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public void unbind(String name) throws AccessException, 
                                               RemoteException,
                                               NotBoundException, 
                                               MalformedURLException;
}
