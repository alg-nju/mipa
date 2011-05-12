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

import java.util.ArrayList;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class PriorityQueue {

    ArrayList<Message> list;
    Object sync;
    
    public PriorityQueue() {
        list = new ArrayList<Message>();
        sync = new Object();
    }
    
    public Message peek() {
        synchronized(sync) {
        if(list.size() == 0) return null;
        return list.get(0);
        }
    }
    
    public Message poll() {
        synchronized(sync) {
        if(list.size() == 0) return null;
        return list.remove(0);
        }
    }
    
    public void offer(Message e) {
        synchronized(sync) {
        boolean inserted = false;
        for(int i = 0; i < list.size(); i++) {
            Message x = list.get(i);
            if(e.getDispatchTime() < x.getDispatchTime()) {
                list.add(i, e);
                inserted = true;
                break;
            }
        }
        if(inserted == false) list.add(e);
        }
    }
}
