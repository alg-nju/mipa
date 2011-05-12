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

import static config.Debug.DEBUG;
import net.sourceforge.mipa.eca.preprocessing.DataDisseminate;
import net.sourceforge.mipa.eca.sensor.Sensor;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class PushSensorAgent extends SensorAgent {
    
    /** sensor class */
    private Sensor sensor;
    
    /** time gap between two data generations. */
    private long gap;

    public PushSensorAgent(DataDisseminate dataDisseminate, 
                             String name,
                             String valueType, 
                             Sensor sensor) {
        
        super(dataDisseminate, name, valueType);
        this.sensor = sensor;
        gap = sensor.getGap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.mipa.eca.SensorAgent#generateData()
     */
    @Override
    public void generateData() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.mipa.eca.SensorAgent#getData()
     */
    @Override
    public String getData() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            while(true) {
                Thread.sleep(gap);
                
                String[] values = sensor.getData();
                
                if(values != null) {
                    dataDisseminate.update(this.name, values);
                } else {
                    if(DEBUG) {
                        System.out.println("Sensor " + name + " Stopped!");
                    }
                    return;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
