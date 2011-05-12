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
import java.util.ArrayList;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Composite implements Structure,Serializable {

    private static final long serialVersionUID = -1344580735973602117L;

    private ArrayList<Structure> children;
    
    private Structure father;
    
    private NodeType type;

    private String nodeName;
    
    protected boolean nodeValue = false;
    
    protected boolean lastValue;

    public Composite(NodeType type, String nodeName) {
        children = new ArrayList<Structure>();
        this.type = type;
        this.nodeName = nodeName;
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
     * @param type
     *            the type to set
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
     * @param nodeName
     *            the name to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the name
     */
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public NodeType getNodeType() {
        // TODO Auto-generated method stub
        return type;
    }

    public void setFather(Structure father) {
        this.father = father;
    }

    public Structure getFather() {
        return father;
    }

    public void setNodeValue(boolean nodeValue) {
        this.nodeValue = nodeValue;
    }

    public boolean getNodeValue() {
        return nodeValue;
    }
    public boolean getLastValue() {
        return lastValue;
    }

    public void setLastValue(boolean lastValue) {
        this.lastValue = lastValue;
    }
}
