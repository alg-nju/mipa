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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.exception.EventNameBoundTwiceException;
import net.sourceforge.mipa.components.exception.EventNameNotFoundException;

/**
 * context retrieving module.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ContextRetrieving {
    /** mapping a low level context to an ECA infrastructure position */
    private Map<String, String> mapEntityId;
    
    public ContextRetrieving() {
        mapEntityId = new HashMap<String, String>();
    }
    
    /**
     * mapping a low level context to an ECA infrastructure id.
     * 
     * @param context a low level context
     * @param entityId ECA infrastructure id
     * @throws EventNameBoundTwiceException
     */
    
    public synchronized void setEntityID(String context, String entityId) 
                                                throws EventNameBoundTwiceException {
        if(mapEntityId.containsKey(context)) {
            throw new EventNameBoundTwiceException(context);
        }
        mapEntityId.put(context, entityId);
    }
    
    /**
     * get the ECA infrastructure id which contains <code>context</code>.
     * @param context a low level context
     * @return An ECA infrastructure id
     * @throws EventNameNotFoundException
     */
    public String getEntityId(String context) throws EventNameNotFoundException {
        if(mapEntityId.containsKey(context) == false) {
            throw new EventNameNotFoundException(context);
        }
        return mapEntityId.get(context);
    }
}
