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

import java.util.ArrayList;

/**
 * @author jpyu
 *
 */
public class Composite implements Structure {

    private ArrayList<Structure> children;
    
    private NodeType type;
    
    private String name;
    
    public Composite(NodeType type, String name) {
	children = new ArrayList<Structure>();
	this.type = type;
	this.name = name;
    }
    
    @Override
    public void add(Structure child) {
	// TODO Auto-generated method stub
	children.add(child);
	
    }

    @Override
    public ArrayList<Structure> getChildren() {
	// TODO Auto-generated method stub
	return children;
    }

    /**
     * @param type the type to set
     */
    public void setType(NodeType type) {
	this.type = type;
    }

    /**
     * @return the type
     */
    public NodeType getType() {
	return type;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    @Override
    public NodeType getNodeType() {
	return type;
    }

    @Override
    public LocalPredicate getLocalPredicate() {
	
	return null;
    }

}
