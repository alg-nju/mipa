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
package net.sourceforge.mipa.predicatedetection.normal.cada;

import net.sourceforge.mipa.components.MessageContent;
import net.sourceforge.mipa.predicatedetection.normal.cada.CADAVectorClock;
/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 */
public class CADAMessageContent extends MessageContent {
    
    private static final long serialVersionUID = -2634403899989234685L;
    
    private long pTimeLo;
    private long pTimeHi;
    
    private String intervalID;
    
    private CADAVectorClock lo;
    private CADAVectorClock hi;
    
    public CADAMessageContent() {
        
    }

    public CADAMessageContent(CADAVectorClock lo, CADAVectorClock hi) {
        this.lo = lo;
        this.hi = hi;
    }
    
    public CADAVectorClock getLo() {
        return lo;
    }
    
    public CADAVectorClock getHi() {
        return hi;
    }
    
    public void setLo(CADAVectorClock lo) {
        this.lo = lo;
    }

    public void setHi(CADAVectorClock hi) {
        this.hi = hi;
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
