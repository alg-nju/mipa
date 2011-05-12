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
package net.sourceforge.mipa.predicatedetection.normal.scp;

import net.sourceforge.mipa.components.MessageContent;


/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class SCPMessageContent extends MessageContent {

    private static final long serialVersionUID = -2494955328740349416L;

    private long pTimeLo;
    
    private long pTimeHi;
    
    private String intervalID;
    
    private SCPVectorClock lo;
    
    private SCPVectorClock hi;
    
    public SCPMessageContent(SCPVectorClock lo, SCPVectorClock hi) {
        this.lo = lo;
        this.hi = hi;
    }
    
    public SCPVectorClock getLo() {
        return lo;
    }
    
    public SCPVectorClock getHi() {
        return hi;
    }

    /**
     * @param pTimeLo the pTimeLo to set
     */
    public void setpTimeLo(long pTimeLo) {
        this.pTimeLo = pTimeLo;
    }

    /**
     * @return the pTimeLo
     */
    public long getpTimeLo() {
        return pTimeLo;
    }

    /**
     * @param pTimeHi the pTimeHi to set
     */
    public void setpTimeHi(long pTimeHi) {
        this.pTimeHi = pTimeHi;
    }

    /**
     * @return the pTimeHi
     */
    public long getpTimeHi() {
        return pTimeHi;
    }

    /**
     * @param intervalID the intervalID to set
     */
    public void setIntervalID(String intervalID) {
        this.intervalID = intervalID;
    }

    /**
     * @return the intervalID
     */
    public String getIntervalID() {
        return intervalID;
    }
}
