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
package config;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Config {
    /** output physical clock for debugging */
    public final static boolean ENABLE_PHYSICAL_CLOCK = false;
    
    /** experiment mode: output memory consumption, latency, etc. */
    public final static boolean EXPERIMENT = false;
    
    
    /** log directory for MIPA component output */
    public final static String LOG_DIRECTORY = "log";
    
    /** sensor configuration directory for MIPA */
    public final static String SENSORS_CONFIG_DIRECTORY = "config/sensors/";
    
    /** configuration file for MIPA */
    public final static String CONFIG_FILE = "config/config.xml";
}
