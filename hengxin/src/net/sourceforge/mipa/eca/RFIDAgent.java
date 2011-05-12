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
import net.sourceforge.mipa.eca.sensor.RFID;
import net.sourceforge.mipa.eca.sensor.Sensor;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class RFIDAgent extends SensorAgent {

    
    /** the sensor which sensor agent manages */
    protected Sensor sensor;
    
    public RFIDAgent(DataDisseminate dataDisseminate, 
                       String name, 
                       String valueType) {
        super(dataDisseminate, name, valueType);
        sensor = new RFID();
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.mipa.eca.SensorAgent#generateData()
     */
    @Override
    public void generateData() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.sourceforge.mipa.eca.SensorAgent#getData()
     */
    @Override
    public String getData() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
        try {
            while(true) {
                Thread.sleep(1000);
                String[] values = sensor.getData();
                
                if(DEBUG) {
                    if(values != null) {
                        System.out.println("RFID sensor list:");
                        for(int i = 0; i < values.length; i++) {
                            System.out.println("\t" + values[i]);
                        }
                    } else 
                        System.out.println("RFID sensor has no data now.");
                }

                if(values != null) dataDisseminate.update(this.name, values);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
