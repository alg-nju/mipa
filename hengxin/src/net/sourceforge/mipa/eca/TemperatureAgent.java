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
import net.sourceforge.mipa.eca.sensor.Temperature;

/**
 * Temperature sensor agent.
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class TemperatureAgent extends SensorAgent {
    
    /** the sensor which sensor agent manages */
    private Sensor sensor;
    
    
    public TemperatureAgent(DataDisseminate disseminate, 
                               String name, 
                               String valueType) {
        super(disseminate, name, valueType);
        sensor = new Temperature();
    }
    
    @Override
    public void generateData() {
        
    }

    @Override
    public String getData() {
        
        return null;
    }

    @Override
    public void run() {
        //Thread.yield();
        try {
            while(true) {
                Thread.sleep(1000);
                String[] values = sensor.getData();
                if(DEBUG) {
                    if(values != null)
                        System.out.println("temperature sensor list:\n\t" + values[0]);
                    else
                        System.out.println("temperature sensor has no data now.");
                }
                
                if(values != null) dataDisseminate.update(this.name, values);
                
                
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
