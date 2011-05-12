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

import net.sourceforge.mipa.eca.preprocessing.DataDisseminate;


/**
 * Sensor Agent.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class SensorAgent implements Runnable {
    
    /** data source of ECA mechanism. */
    protected DataDisseminate dataDisseminate;
    
    /** sensor agent's name */
    protected String name;
    
    /** sensor agent's value type */
    protected String valueType;
    
    public SensorAgent(DataDisseminate dataDisseminate, 
                         String name, 
                         String valueType) {
        this.dataDisseminate = dataDisseminate;
        this.name = name;
        this.valueType = valueType;
    }
    /**
     * sensor agent's name.
     * 
     * @return a <code>String</code> represents name
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * sensor agent's value type.
     * 
     * @return value type
     */
    public String getValueType() {
        return valueType;
    }
    
    /**
     * sensor agent works in pull mode.
     * 
     * @return sensor data
     */
    public abstract String getData();
    
    /**
     * sensor agent works in push mode.
     */
    public abstract void generateData();
}
