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

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class RegularExpression extends dk.brics.automaton.RegExp implements Serializable{
    private static final long serialVersionUID = 9089972318285509259L;
    
    private String regularExpression;
    private HashSet<String> identifiers;
    
    public RegularExpression(String regularExpression) {
        super(regularExpression);
        this.regularExpression = regularExpression;
        //this.identifiers = (HashSet<String>) super.getIdentifiers();
        identifiers = new HashSet<String>();
        getIdentifier();
    }
    
    private void getIdentifier() {
        for(int i=0;i<regularExpression.length();i++) {
            char c = regularExpression.charAt(i);
            if(isIdentifier(c)&&!isInIdentifiers(c)) {
                identifiers.add(String.valueOf(c));
            }
        }
    }
    
    private boolean isIdentifier(char c) {
        // TODO Auto-generated method stub
        String s = "()|*+?";
        if(!s.contains(String.valueOf(c))) {
            return true;
        }
        return false;
    }

    private boolean isInIdentifiers(char c) {
        String s = String.valueOf(c);
        if(identifiers.contains(s)) {
            return true;
        }
        return false;
    }
    
    /*
    public Automaton toAutomaton() {
        return super.toAutomaton();
    }
    */

    public String getRegularExpression() {
        return regularExpression;
    }

    public HashSet<String> getIdentifiers() {
        return identifiers;
    }
}
