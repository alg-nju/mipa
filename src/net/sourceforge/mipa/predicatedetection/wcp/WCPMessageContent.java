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
package net.sourceforge.mipa.predicatedetection.wcp;

import java.io.Serializable;

/**
 * 
 * @author sorrybone <sorrybone@gmail.com>
 *
 */
public class WCPMessageContent implements Serializable {

    private static final long serialVersionUID = 8645743008656858830L;
    
    private WCPVectorClock wcpVectorClock;
    
    private String contentID;

    public WCPMessageContent(WCPVectorClock wcpVectorClock) {
        this.wcpVectorClock = wcpVectorClock;
    }
    
    public void setWcpVectorClock(WCPVectorClock wcpVectorClock) {
        this.wcpVectorClock = wcpVectorClock;
    }

    public WCPVectorClock getWcpVectorClock() {
        return wcpVectorClock;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getContentID() {
        return contentID;
    }
}
