/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009-2010 the original author or authors.
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
package net.sourceforge.mipa.components.rm;

import net.sourceforge.mipa.components.ContextModeling;
import net.sourceforge.mipa.components.ContextRetrieving;

/**
 *
 * @author Jianping Yu <jpyu.mail@gmail.com>
 */
public abstract class ResourceManager {
    /** context modeling */
    protected ContextModeling modeling;
    
    /** context retrieving */
    protected ContextRetrieving retrieving;
    
    public ResourceManager(ContextModeling modeling, ContextRetrieving retrieving) {
        this.modeling = modeling;
        this.retrieving = retrieving;
    }
    
    /**
     * find the locations of atomic contexts which are specified by <code>highContext</code>.
     * Usually, a high level context consists of several low level contexts(atomic context).
     * 
     * @param highContext context
     * @return
     *      null, if the resource doesn't exist.
     *      the ECA manager IDs, otherwise.
     */
    public abstract String[] findResource(String highContext);
    
    /**
     * find the names of atomic contexts which are specified by <code>highContext</code>.
     * Usually, a high level context consists of several low level contexts(atomic context).
     * 
     * @param highContext context
     * @return
     *      null, if the atomic context doesn't exist,
     *      A list of names of atomic contexts, otherwise.
     */
    public abstract String[] getAtomicContextNames(String highContext);
    
    /**
     * find the value type of <code>atomicContext</code>.
     * 
     * @param atomicContext atomic context
     * @return
     *          the value type.
     */
    public abstract String getTypeOfAtomicContext(String atomicContext);
    
    /**
     * resource register provides register to ECA Manager.
     * 
     * @param resourceName
     *            resource name
     * @param valueType
     *            value type of resource
     * @param entityId
     *            ECA manager ID(name)
     */
    public abstract void registerResource(String resourceName, 
                                              String valueType,
                                              String entityId);
}
