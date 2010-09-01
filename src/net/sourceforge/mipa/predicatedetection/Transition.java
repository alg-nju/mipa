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

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class Transition implements Serializable{
    private static final long serialVersionUID = 553630444432361467L;
    
    private char labelMin;
    private char labelMax;
    private State state;
    private dk.brics.automaton.Transition transition;
    
    public Transition(dk.brics.automaton.Transition transition) {
        this.transition = transition;
        this.labelMin = transition.getMin();
        this.labelMax = transition.getMax();
        this.state = new State(transition.getDest());
    }
    
    public String getLabels() {
        String label = ""+labelMin;
        for(int i=1;i<=(labelMax-labelMin);i++) {
            label += ","+((char)(labelMin+i));
        }
        return label;
    }
    
    public State getDestination() {
        return state;
    }
    
    public String toString() {
        String result = getLabels()+" -> "+state.getName();
        return result;
    }
}
