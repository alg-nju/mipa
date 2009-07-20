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

import net.sourceforge.mipa.predicatedetection.PredicateType;

/**
 * a <code>group</code> contains checker and normal processes.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Group {
    private String groupID;

    private int numberOfFinishedMembers;

    private PredicateType type;
    
    private ArrayList<String> owners;
    
    private ArrayList<String> members;

    public Group(String groupID, ArrayList<String> owners, ArrayList<String> members, PredicateType type) {
	this.groupID = groupID;
	this.owners = owners;
	this.setMembers(members);
	this.type = type;
    }

    /**
     * @param groupID
     *            the groupID to set
     */
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    /**
     * @return the groupID
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * @param owners
     *            the checkerName to set
     */
    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }

    /**
     * @return the owners
     */
    public ArrayList<String> getOwners() {
        return owners;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(ArrayList<String> members) {
	this.members = members;
    }

    /**
     * @return the members
     */
    public ArrayList<String> getMembers() {
	return members;
    }
    
    /**
     * @param numberOfFinishedNormalProcesses
     *            the numberOfFinishedNormalProcesses to set
     */
    public void setNumberOfFinishedMembers(int numberOfFinishedMembers) {
        this.numberOfFinishedMembers = numberOfFinishedMembers;
    }

    /**
     * @return the numberOfFinishedMembers
     */
    public int getNumberOfFinishedMembers() {
        return numberOfFinishedMembers;
    }

    /**
     * @param predicateType
     *            the predicateType to set
     */
    public void setType(PredicateType predicateType) {
        this.type = predicateType;
    }

    /**
     * @return the predicateType
     */
    public PredicateType getType() {
        return type;
    }
    
    /*
    public String[] getNormalProcesses() {
        String[] list = new String[members.size()];
        members.toArray(list);
        return list;
    }
    */
}
