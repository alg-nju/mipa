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

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.mipa.components.Group;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;

/**
 * ECA manager.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public interface ECAManager extends Remote {
    /**
     * register local predicate.
     * 
     * @param localPredicate
     *            local predicate
     * @param name
     * 		  normal process name
     * @param g
     *            group
     * @throws RemoteException
     */
    public void registerLocalPredicate(LocalPredicate localPredicate, 
                                       String name, 
                                       Group g) 
                                           throws RemoteException;
    
    /**
     * unregister normal process.
     * @param npID
     * 			normal process ID
     * @param g
     * 			group
     * @throws RemoteException
     */
    public void unregisterNormalProcess(String npID,
    									Group g) throws RemoteException;
}
