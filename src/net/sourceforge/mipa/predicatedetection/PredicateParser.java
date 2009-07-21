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

import static config.Debug.DEBUG;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.components.ContextMapping;
import net.sourceforge.mipa.components.Group;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;

import org.w3c.dom.Document;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class PredicateParser implements PredicateParserMethod {

    /** structure of predicate parser reference */
    private StructureParser structureParser;

    /** checker logic parser reference */
    //private CheckerParser checkerParser;

    /**
     * default construction.
     */
    public PredicateParser(ContextMapping contextMapping) {
        structureParser = new StructureParser(contextMapping);
        //checkerParser = new CheckerParser();
    }

    public void parsePredicate(String applicationName, Document predicate)
                                                                          throws RemoteException {

        if (DEBUG) {
            System.out.println("parsing predicate...");
        }

        PredicateType type = PredicateIdentify.predicateIdentify(predicate);
        

        Structure predicateStructure = structureParser.parseStructure(predicate);
        if(type == PredicateType.OGAP)
            parseOGAPStructure(null, predicateStructure);
        else if(type == PredicateType.SCP)
            parseSCPStructure(predicateStructure);
        else {
            System.out.println("This predicate type have not been implemented yet.");
        }

        //checkerParser.parseChecker(predicate, applicationName, type);
    }
    
    
    /**
     * 
     * @param s
     */
    private void parseSCPStructure(Structure s) {
	ArrayList<Structure> children = s.getChildren();
	
	if(s.getNodeType() == NodeType.GSE) {
	    for(int i = 0; i < children.size(); i++) {
		parseSCPStructure(children.get(i));
	    }
	} else if(s.getNodeType() == NodeType.CGS) {
	    IDManager idManager = MIPAResource.getIDManager();
	    String owner = null;
	    try {
		owner = idManager.getID(Catalog.Checker);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    
	    ArrayList<String> owners = new ArrayList<String>();
	    ArrayList<String> members = new ArrayList<String>();
	    Map<String, LocalPredicate> mapping = new HashMap<String, LocalPredicate>();
	    
	    owners.add(owner);
	    for(int i = 0; i < children.size(); i++) {
		Structure unit = children.get(i);
		
		assert(unit.getNodeType() == NodeType.LP);
		String memberID = null;
		try {
		    memberID = idManager.getID(Catalog.NormalProcess);
		} catch(Exception e) {
		    e.printStackTrace();
		}
		members.add(memberID);
		mapping.put(memberID, (LocalPredicate) s);
	    }
	    String groupID = null;
	    try {
		groupID = idManager.getID(Catalog.Group);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    Group g = new Group(groupID, owners, members, PredicateType.SCP);
	    
	    
	}
    }
    
    
    /**
     * 
     * @param fatherID
     * @param s
     */
    
    private void parseOGAPStructure(String fatherID, Structure s) {
	
	ArrayList<Structure> children = s.getChildren();
	
	assert(children != null);
	
	IDManager idManager = MIPAResource.getIDManager();
	// get id
	String id = null;
	try {
	    id = idManager.getID(Catalog.Checker);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
	
	if(s.getNodeType() == NodeType.GSE) {
	    assert(fatherID == null);

	    // create GSE checker, and bind to rmi registry
	
	    
	    // parser the others
	    for(int i = 0; i < children.size(); i++) {
		Structure unit = children.get(i);
		parseOGAPStructure(id, unit);
	    }
	} else if(s.getNodeType() == NodeType.CGS) {
	    assert(fatherID != null);
	    
	    //create CGS checker, pass father ID to it
	    
	    
	    // create group
	    ArrayList<String> members = new ArrayList<String>();
	    ArrayList<String> owners = new ArrayList<String>();
	    Map<String, LocalPredicate> mapping = new HashMap<String, LocalPredicate>();
	    
	    owners.add(id);
	    for(int i = 0; i < children.size(); i++) {
		Structure unit = children.get(i);
		
		assert(unit.getNodeType() == NodeType.LP);
		String memberID = null;
		try {
		    memberID = idManager.getID(Catalog.NormalProcess);
		} catch(Exception e) {
		    e.printStackTrace();
		}
		members.add(memberID);
		mapping.put(memberID, (LocalPredicate) s);
	   
	    }
	    String groupID = null;
	    try{
		groupID = idManager.getID(Catalog.Group);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    Group g = new Group(groupID, owners, members, PredicateType.OGAP);
	    // register group to coordinator.
	    
	    
	    // register each local predicate.
	    
	    for(int i = 0; i < members.size(); i++) {
		LocalPredicate LP = mapping.get(members.get(i));
		//FIND THE ECA MANAGER ID. 直接传递group
		
	    }
	    
	} else {
	    assert(false);
	}
    }
}
