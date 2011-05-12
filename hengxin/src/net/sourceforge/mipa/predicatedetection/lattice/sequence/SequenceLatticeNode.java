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
package net.sourceforge.mipa.predicatedetection.lattice.sequence;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.sourceforge.mipa.predicatedetection.State;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class SequenceLatticeNode extends AbstractLatticeNode {

    /**record whether the node has been checked**/
    private boolean visited;
    
    /**record whether the node is the tail of the lattice**/
    private boolean tailFlag;
    
    private String satisfiedPredicates;
    
    private HashSet<State> reachedStates;
    
    private boolean flagIntersection = false;
    
    private boolean flagInclusion = true;

    public SequenceLatticeNode(LocalState[] gs, String[] s) {
        super(gs, s);
        // TODO Auto-generated constructor stub
        visited = false;
        tailFlag = false;
        satisfiedPredicates = "";
        reachedStates = new HashSet<State>();
    }

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public boolean getTailFlag() {
        return tailFlag;
    }

    public void setTailFlag(boolean tailFlag) {
        this.tailFlag = tailFlag;
    }

    public String getSatisfiedPredicates() {
        return satisfiedPredicates;
    }

    public void addSatisfiedPredicates(char satisfiedPredicates) {
        if(!this.satisfiedPredicates.contains(String.valueOf(satisfiedPredicates)))
            this.satisfiedPredicates += " "+satisfiedPredicates;
    }

    public HashSet<State> getReachedStates() {
        return reachedStates;
    }

    public void addReachedStates(State reachedStates) {
        Iterator<State> it = this.reachedStates.iterator();
        boolean flag = true;
        while(it.hasNext()) {
            if(it.next().getName().equals(reachedStates.getName())) {
                flag = false;
                break;
            }
        }
        if(flag == true) {
            this.reachedStates.add(reachedStates);
        }
    }

    public void setSatisfiedPredicates(String satisfiedPredicates) {
        this.satisfiedPredicates = satisfiedPredicates;
    }

    public void setReachedStates(HashSet<State> reachedStates) {
        this.reachedStates = reachedStates;
    }

    public boolean getFlagIntersection() {
        return flagIntersection;
    }

    public void setFlagIntersection(boolean flagIntersection) {
        this.flagIntersection = flagIntersection;
    }

    public boolean getFlagInclusion() {
        return flagInclusion;
    }

    public void setFlagInclusion(boolean flagInclusion) {
        this.flagInclusion = flagInclusion;
    }
}
