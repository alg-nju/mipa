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

import net.sourceforge.mipa.components.MessageContent;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class OGAMessageContent extends MessageContent {

    private static final long serialVersionUID = -2412107813802433533L;

    private OGAVectorClock lo;
    
    private OGAVectorClock hi;
    
    private String intervalID;
    
    // information used between top checker and sub checker.
    private ArrayList<OGAVectorClock> SetLo;
    private ArrayList<OGAVectorClock> SetHi;
    
    public OGAMessageContent() {
        this.lo = null;
        this.hi = null;
        this.setSetLo(null);
        this.setSetHi(null);
    }
    
    public OGAMessageContent(OGAVectorClock lo, OGAVectorClock hi) {
        this.lo = lo;
        this.hi = hi;
    }
    
    public OGAVectorClock getLo() {
        return lo;
    }
    
    public OGAVectorClock getHi() {
        return hi;
    }

    /**
     * @param setLo the setLo to set
     */
    public void setSetLo(ArrayList<OGAVectorClock> setLo) {
        SetLo = setLo;
    }

    /**
     * @return the setLo
     */
    public ArrayList<OGAVectorClock> getSetLo() {
        return SetLo;
    }

    /**
     * @param setHi the setHi to set
     */
    public void setSetHi(ArrayList<OGAVectorClock> setHi) {
        SetHi = setHi;
    }

    /**
     * @return the setHi
     */
    public ArrayList<OGAVectorClock> getSetHi() {
        return SetHi;
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
