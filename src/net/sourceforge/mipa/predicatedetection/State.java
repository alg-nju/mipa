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
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class State implements Serializable{
    private static final long serialVersionUID = 1052067975695129955L;
    
    private String name;
    private HashSet<Transition> transitions;
    private dk.brics.automaton.State state;

    public State(dk.brics.automaton.State state) {
        this.state = state;
        name = state.toString().split(" ")[1];
        transitions = new HashSet<Transition>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public State step(char c) {
        return new State(state.step(c));
    }
    
    public HashSet<Transition> getTransitions() {
        if(transitions.isEmpty()) {
            Iterator<dk.brics.automaton.Transition> it = state.getTransitions().iterator();
            while(it.hasNext()) {
                transitions.add(new Transition(it.next()));
            }
        }
        return transitions;
    }
    
    public String toString() {
        String result = "  state "+name+":";
        Iterator<Transition> it = getTransitions().iterator();
        while(it.hasNext()) {
            result += "\n  " + it.next().toString();
        }
        return result;
    }
    
    public boolean isAccept() {
        return state.isAccept();
    }
}
