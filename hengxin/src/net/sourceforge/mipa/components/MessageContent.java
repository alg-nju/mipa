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
package net.sourceforge.mipa.components;

import java.io.Serializable;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class MessageContent implements Serializable {

    private static final long serialVersionUID = 3007128772811018095L;
    
   //protected SCPMessageContent scp;
    
    //public abstract VectorClock getLo();
    
    //public abstract VectorClock getHi();
    
    /*
    public SCPMessageContent getSCPRelatedContent() {
        return scp;
    }
    
    public void setSCPRelatedConttent(SCPMessageContent content) {
        scp = content;
    }
    */
    
}
