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

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class SimulationRFID implements Sensor {

    /** time gap between continuous data generations. */
    private long gap;

    /** tags that RFID reader manages. */
    private ArrayList<String> tags;

    /** data source per tag. */
    private ArrayList<String> sources;

    /** data source indicator per tag. */
    private int[] indicators;

    public SimulationRFID(Object args) {

        try {
            BufferedReader source = new BufferedReader(
                                               new FileReader((String) Array.get(args, 0)));
            
            gap = Long.parseLong(source.readLine());
            int num = Integer.parseInt(source.readLine());
            tags = new ArrayList<String>();
            sources = new ArrayList<String>();
            indicators = new int[num];
            
            for (int i = 0; i < num; i++) {
                String tag = source.readLine();
                tags.add(tag);
            }
            for (int i = 0; i < num; i++) {
                String data = source.readLine();
                sources.add(data);
            }

            for (int i = 0; i < num; i++) {
                indicators[i] = 0;
            }
            /*
            if(DEBUG) {
                for(int i = 0; i < sources.size(); i++) {
                    System.out.println(sources.get(i));
                }
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.mipa.eca.sensor.Sensor#getData()
     */
    @Override
    public String[] getData() {
        ArrayList<String> detectedTags = new ArrayList<String>();

        for (int i = 0; i < indicators.length; i++) {
            String source = sources.get(i);
            if (source.length() <= indicators[i])
                continue;
            
            if (source.charAt(indicators[i]) != '0')
                detectedTags.add(tags.get(i));
            
            
            indicators[i]++;
        }
        
        if (detectedTags.size() != 0) {
            String[] detectedTagsArray = new String[detectedTags.size()];
            detectedTags.toArray(detectedTagsArray);
            return detectedTagsArray;
        } else {
            boolean end = true;
            for(int i = 0; i < indicators.length; i++) {
                if(sources.get(i).length() > indicators[i]) {
                    end = false;
                    break;
                }
            }
            String[] results = {""};
            if (end == true) results = null;
            return results;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.mipa.eca.sensor.Sensor#getGap()
     */
    @Override
    public long getGap() {
        return gap;
    }
}
