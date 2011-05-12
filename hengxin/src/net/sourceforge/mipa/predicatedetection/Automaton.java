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
import java.util.Set;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class Automaton implements Serializable {
    private static final long serialVersionUID = 8140784300941885225L;
    
    private State initialState;
    private HashSet<State> acceptStates;
    private HashSet<State> states;
    
    private dk.brics.automaton.Automaton automaton;
    
    public Automaton(RegularExpression regExp) {
        automaton = regExp.toAutomaton();
        initialState = new State(automaton.getInitialState());
        acceptStates = new HashSet<State>();
        states = new HashSet<State>();
        Iterator<dk.brics.automaton.State> it = automaton.getAcceptStates().iterator();
        while(it.hasNext()) {
            acceptStates.add(new State(it.next()));
        }
        it = automaton.getStates().iterator();
        while(it.hasNext()) {
            states.add(new State(it.next()));
        }
    }
    
    public State getInitialState() {
        return initialState;
    }

    public HashSet<State> getStates() {
        return states;
    }

    public HashSet<State> getAcceptStates() {
        return acceptStates;
    }
    
    public boolean check(String s) {
        return automaton.run(s);
    }
    
    public String toString() {
        String result = "all states:";
        Iterator<State> it = states.iterator();
        while(it.hasNext()) {
            result += "\n"+it.next().toString();
        }
        result += "\ninitial state:\n  "+initialState.getName();
        result += "\naccepting states:\n";
        it = acceptStates.iterator();
        while(it.hasNext()) {
            result += "  "+it.next().getName();
        }
        return result;
    }
}
