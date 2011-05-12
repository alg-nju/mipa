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
package net.sourceforge.mipa.eca.sensor;

/**
 * sensor in environment.
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public interface Sensor {
    /**
     * reads sensor data from environment.
     * 
     * @return sensor data
     */
    public String[] getData();
    
    /**
     * provide for push type sensor agent.
     * 
     * @return time gap in millisecond
     */
    public long getGap();
}
