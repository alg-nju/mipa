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
package net.sourceforge.mipa.predicatedetection;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * generic vector clock class.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class VectorClock implements Serializable {

    private static final long serialVersionUID = -3242932698549998388L;

    /** vector clock */
    protected ArrayList<Long> vectorClock;

    public VectorClock(int number) {
        this.vectorClock = new ArrayList<Long>();
        
        for (int i = 0; i < number; i++) {
            getVectorClock().add(new Long(0));
        }
    }
    
    public VectorClock(VectorClock clock) {
        this.vectorClock = new ArrayList<Long>();
        ArrayList<Long> vectorList = clock.getVectorClock();
        
        for(int i = 0; i < vectorList.size(); i++)
            vectorClock.add(new Long(vectorList.get(i)));
    }

    /**
     * @param vectorClock the vectorClock to set
     */
    public void setVectorClock(ArrayList<Long> vectorClock) {
        this.vectorClock = vectorClock;
    }

    /**
     * @return the vectorClock
     */
    public ArrayList<Long> getVectorClock() {
        return vectorClock;
    }

    /**
     * update policy of Vector Clock.
     * 
     * @param timestamp
     *            external vector clock
     */
    public abstract void update(VectorClock timestamp);
    
    public abstract void increment(int id);
    
    public abstract boolean notLessThan(VectorClock timestamp);

}
