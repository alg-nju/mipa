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
 * The <code>ContextModeling</code> manages mapping between high context and
 * low context.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ContextModeling {
    /** mapping event name to entity id */
    private Map<String, String> mapContext;

    private Map<String, String> mapValueType;

    public ContextModeling() {
        mapContext = new HashMap<String, String>();
        mapValueType = new HashMap<String, String>();
    }

    /**
     * maps eventName to entityId.
     * 
     * @param highContext
     *            a high level context
     * @param lowContext
     *            a low level context
     * @param valueType
     *            value type of event name
     */
    public synchronized void map(String highContext, 
                                      String lowContext, 
                                      String valueType)
                                          throws EventNameBoundTwiceException {
        if (mapContext.containsKey(highContext) == true)
            throw new EventNameBoundTwiceException(highContext);

        mapContext.put(highContext, lowContext);
        mapValueType.put(lowContext, valueType);
    }

    /**
     * gets mapping low context.
     * 
     * @param highContext
     *            high context
     * @return mapping result: low context(a <code>String</code>)
     * @throws EventNameNotFoundException
     */
    public String getLowContext(String highContext)
                                     throws EventNameNotFoundException {
        if (mapContext.containsKey(highContext) == false)
            throw new EventNameNotFoundException(highContext);
        
        return mapContext.get(highContext);
    }
    
    /**
     * gets mapping value type.
     * 
     * @param lowContext low context
     * @return value type of low context.
     * @throws EventNameNotFoundException
     */
    public String getValueType(String lowContext) 
                                    throws EventNameNotFoundException {
        if(mapValueType.containsKey(lowContext) == false)
            throw new EventNameNotFoundException(lowContext);
        
        return mapValueType.get(lowContext);
    }
}