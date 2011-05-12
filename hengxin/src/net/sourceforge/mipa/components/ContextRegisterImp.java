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

import java.rmi.RemoteException;

/**
 * <code>ContextRegister</code> implementation.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ContextRegisterImp implements ContextRegister {

    /** context modeling */
    private ContextModeling mapping;
    
    /** context retrieving */
    private ContextRetrieving retrieving;

    public ContextRegisterImp(ContextModeling mapping, ContextRetrieving retrieving) {
        this.mapping = mapping;
        this.retrieving = retrieving;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.mipa.components.ContextRegister#registerResource(java
     * .lang.String, java.lang.String)
     */
    @Override
    public synchronized void registerResource(String resourceName,
                                                  String valueType, 
                                                  String entityId)
                                                      throws RemoteException {
        try {
            mapping.map(resourceName, resourceName, valueType);
            retrieving.setEntityID(resourceName, entityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
