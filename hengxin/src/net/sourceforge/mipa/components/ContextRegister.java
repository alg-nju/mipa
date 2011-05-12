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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * context register.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public interface ContextRegister extends Remote {

    /**
     * resource register provides register to ECA Manager.
     * 
     * @param resourceName
     *            resource name
     * @param valueType
     *            value type of resource
     * @param entityId
     *            ECA manager ID(name)
     * @throws RemoteException
     */
    public void registerResource(String resourceName, 
                                  String valueType,
                                  String entityId)
                                      throws RemoteException;
}
