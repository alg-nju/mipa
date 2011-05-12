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
public class AbstractGroup {

    private String groupId;
    
    private int level;
    
    private String father;
    
    private ArrayList<Object> children;

    public AbstractGroup() {
        groupId = null;
        level = -1;
        father = null;
        children = null;
    }
    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param father the father to set
     */
    public void setFather(String father) {
        this.father = father;
    }

    /**
     * @return the father
     */
    public String getFather() {
        return father;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<Object> children) {
        this.children = children;
    }

    /**
     * @return the children
     */
    public ArrayList<Object> getChildren() {
        return children;
    }
}
