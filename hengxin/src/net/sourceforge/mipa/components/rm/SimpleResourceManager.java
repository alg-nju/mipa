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
import net.sourceforge.mipa.components.exception.EventNameNotFoundException;

/**
 *
 * @author Jianping Yu <jpyu.mail@gmail.com>
 */
public class SimpleResourceManager extends ResourceManager {

    /**
     * @param modeling
     * @param retrieving
     */
    public SimpleResourceManager(ContextModeling modeling,
                                 ContextRetrieving retrieving) {
        super(modeling, retrieving);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mipa.components.rm.ResourceManager#findResource(java.lang.String)
     */
    @Override
    public String[] findResource(String highContext) {
        String id = null;
        try {
            String lowContext = modeling.getLowContext(highContext);
            id = retrieving.getEntityId(lowContext);   
        } catch(EventNameNotFoundException e) {
            id = null;
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        String[] IDs = {id};
        if (id == null) IDs = null;
        return IDs;
    }
    
    public String[] getAtomicContextNames(String highContext) {
        String lowContext = null;
        try {
            lowContext = modeling.getLowContext(highContext);
        } catch(Exception e) {
            e.printStackTrace();
        }
        String[] names = {lowContext};
        if(lowContext == null) names = null;
        return names;
    }
    
    public String getTypeOfAtomicContext(String atomicContext) {
        String valueType = null;
        try {
            valueType = modeling.getValueType(atomicContext);
        } catch(EventNameNotFoundException e) {
            // do nothing
        } catch(Exception e) {
            e.printStackTrace();
        }
        return valueType;
    }
    
    public synchronized void registerResource(String resourceName, 
                                 String valueType,
                                 String entityId) {
        try {
            modeling.map(resourceName, resourceName, valueType);
            retrieving.setEntityID(resourceName, entityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
