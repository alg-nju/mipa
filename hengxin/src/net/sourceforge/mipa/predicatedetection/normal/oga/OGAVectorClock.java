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
package net.sourceforge.mipa.predicatedetection.normal.oga;

import java.util.ArrayList;

import net.sourceforge.mipa.predicatedetection.VectorClock;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class OGAVectorClock extends VectorClock {

    private static final long serialVersionUID = 8524644883721946851L;
    
    /** For DEBUG */
    private long physicalClock = 0;
    
    public OGAVectorClock(int number) {
        super(number);
    }
    
    public OGAVectorClock(VectorClock clock) {
        super(clock);
    }

    /**
     * @param physicalClock the physicalClock to set
     */
    public void setPhysicalClock(long physicalClock) {
        this.physicalClock = physicalClock;
    }

    /**
     * @return the physicalClock
     */
    public long getPhysicalClock() {
        return physicalClock;
    }

    @Override
    public void increment(int id) {
        assert (id < getVectorClock().size());

        Long clock = getVectorClock().get(id);
        getVectorClock().set(id, new Long(clock.longValue() + 1));
        
        /*if(DEBUG) {
            System.out.print("Normal Process " + id + ":\n\t");
            ArrayList<Long> list = getVectorClock();
            for(int i = 0; i < list.size(); i++) {
                System.out.print(list.get(i) + ", ");
            }
            System.out.println();
        }*/
    }

    @Override
    public boolean notLessThan(VectorClock timestamp) {
        ArrayList<Long> right = timestamp.getVectorClock();
        ArrayList<Long> left = vectorClock;
        
        assert(right.size() == left.size());
        boolean result = true, first = false;
        for(int i = 0; i < right.size(); i++) {
            long rightValue = right.get(i).longValue();
            long leftValue = left.get(i).longValue();
            if(leftValue > rightValue) result = false;
            else if(leftValue < rightValue) first = true;
        }
        return !(result && first);
    }

    @Override
    public void update(VectorClock timestamp) {
        ArrayList<Long> clock = timestamp.getVectorClock();

        assert (vectorClock.size() == clock.size());

        for (int i = 0; i < vectorClock.size(); i++) {
            long clockValue = vectorClock.get(i).longValue();
            long msgClockValue = clock.get(i).longValue();

            Long newValue = new Long(clockValue > msgClockValue ? clockValue
                                                               : msgClockValue);
            vectorClock.set(i, newValue);
        }
    }
    
    /**
     * 
     * @param v1
     * @param v2
     * @return 0, if v1 == v2
     *           1, if v1 > v2
     *           -1, if v1 < v2
     *           -2, if concurrent
     */
    public static int compare(VectorClock v1, VectorClock v2) {
        ArrayList<Long> clock1 = v1.getVectorClock();
        ArrayList<Long> clock2 = v2.getVectorClock();
        
        assert(clock1.size() == clock2.size());
        
        int flag = 0;
        for(int i = 0; i < clock1.size(); i++) {
            long value1 = clock1.get(i).longValue();
            long value2 = clock2.get(i).longValue();
            
            if(value1 > value2) {
                if(flag >= 0) flag++;
                else return -2;
            } else if(value1 < value2) {
                if(flag <= 0) flag--;
                else return -2;
            } else {    // value1 == value2
                // do nothing
            }
        }
        if(flag == 0) return 0;
        else if(flag > 0) return 1;
        else return -1;
    }
}
