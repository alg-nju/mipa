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
import net.sourceforge.mipa.components.Coordinator;
import net.sourceforge.mipa.components.Group;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.eca.ECAManager;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.Naming;

import org.w3c.dom.Document;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class PredicateParser implements PredicateParserMethod {

    /** structure of predicate parser reference */
    private StructureParser structureParser;

    /** checker logic parser reference */
    // private CheckerParser checkerParser;

    private ContextMapping contextMapping;

    private String callback;

    /**
     * default construction.
     */
    public PredicateParser(ContextMapping contextMapping) {
        structureParser = new StructureParser();
        this.contextMapping = contextMapping;
        // checkerParser = new CheckerParser();
    }

    public synchronized void parsePredicate(String applicationName,
                                            Document predicate)
                                                               throws RemoteException {

        if (DEBUG) {
            System.out.println("parsing predicate...");
        }
        this.callback = applicationName;

        PredicateType type = PredicateIdentify.predicateIdentify(predicate);

        Structure predicateStructure = structureParser
                                                      .parseStructure(predicate);
        if (type == PredicateType.OGA)
            parseOGAStructure(predicateStructure);
        else if (type == PredicateType.SCP)
            parseSCPStructure(predicateStructure);
        else if (type == PredicateType.WCP)
            parseWCPStructure(predicateStructure);
        else {
            System.out
                      .println("This predicate type have not been implemented yet.");
        }

        // checkerParser.parseChecker(predicate, applicationName, type);
    }

    private void parseOGAStructure(Structure s) {
        ArrayList<Structure> children = s.getChildren();
        
        assert(children != null);
        
        IDManager idManager = MIPAResource.getIDManager();
        Coordinator coordinator = MIPAResource.getCoordinator();
        
        ArrayList<String> topCheckers = new ArrayList<String>();
        ArrayList<String> subCheckers = new ArrayList<String>();
        ArrayList<String> normalProcesses = new ArrayList<String>();
        Map<String, ArrayList<String>> subGroups = new HashMap<String, ArrayList<String>>();
        Map<String, LocalPredicate> normalProcessToLocalPredicate = new HashMap<String, LocalPredicate>();
        
        String topChecker = null;
        try {
            topChecker = idManager.getID(Catalog.Checker);
        } catch(Exception e) {
            e.printStackTrace();
        }
        topCheckers.add(topChecker);
        
        assert(s.getNodeType() == NodeType.GSE);
        for(int i = 0; i < children.size(); i++) {
            // parse CGS
            String subChecker = null;
            try {
                subChecker = idManager.getID(Catalog.Checker);
            } catch (Exception e) {
                e.printStackTrace();
            }
            subCheckers.add(subChecker);
            ArrayList<String> members = new ArrayList<String>();
            subGroups.put(subChecker, members);
            
            ArrayList<Structure> LPs = children.get(i).getChildren();
            
            for(int j = 0; j < LPs.size(); j++) {
                Structure unit = LPs.get(j);
                assert (unit.getNodeType() == NodeType.LP);
                String memberID = null;
                try {
                    memberID = idManager.getID(Catalog.NormalProcess);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                members.add(memberID);
                normalProcesses.add(memberID);
                normalProcessToLocalPredicate.put(memberID, (LocalPredicate) unit);
            }
        }
        String groupID = null;
        try {
            groupID = idManager.getID(Catalog.Group);
        } catch(Exception e) {
            e.printStackTrace();
        }
        // Coordinator does not care about owners in group. We set it to null now.
        Group coordinatorGroup = new Group(groupID, null, normalProcesses, PredicateType.OGA);
        coordinatorGroup.setCoordinatorID(groupID);
        try{
            coordinator.newCoordinator(coordinatorGroup);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        if(DEBUG) {
            System.out.println("Top Checker:");
            for(int i = 0; i < topCheckers.size(); i++) {
                System.out.println("\t" + topCheckers.get(i));
            }
            
            System.out.println("Sub Checkers:");
            for(int i = 0; i < subCheckers.size(); i++) {
                System.out.println("\t" + subCheckers.get(i));
            }
            
            System.out.println("Normal Processes:");
            for(int i = 0; i < normalProcesses.size(); i++) {
                System.out.println("\t" + normalProcesses.get(i));
            }
            
            System.out.println("Mapping:");
            for(int i = 0; i < subCheckers.size(); i++) {
                System.out.println("\t" + subCheckers.get(i));
                ArrayList<String> m = subGroups.get(subCheckers.get(i));
                for(int j = 0; j < m.size(); j++) {
                    System.out.println("\t\t" + m.get(j));
                }
            }
        }
        
        
        // create top checker in OGA.
        String[] topCheckersArray = new String[topCheckers.size()];
        topCheckers.toArray(topCheckersArray);
        String[] subCheckersArray = new String[subCheckers.size()];
        subCheckers.toArray(subCheckersArray);
        CheckerFactory.ogaChecker(callback, topCheckers.get(0), null, subCheckersArray, 0);
        
        // create sub checkers in OGA.
        for(int i = 0; i < subCheckers.size(); i++) {
            String subCheckerName = subCheckers.get(i);
            ArrayList<String> subMembers =  subGroups.get(subCheckerName);
            String[] subMembersArray = new String[subMembers.size()];
            subMembers.toArray(subMembersArray);
            CheckerFactory.ogaChecker(null, subCheckerName, topCheckersArray, subMembersArray, 1);
        }
        
        // create Normal Processes in OGA.
        for(int i = 0; i < subCheckers.size(); i++) {
            String subGroupID = null;
            try {
                subGroupID = idManager.getID(Catalog.Group);
            } catch(Exception e) {
                e.printStackTrace();
            }
            ArrayList<String> fathers = new ArrayList<String>();
            fathers.add(subCheckers.get(i));
            
            Group subGroup = new Group(subGroupID, fathers, normalProcesses, PredicateType.OGA);
            subGroup.setCoordinatorID(groupID);
            ArrayList<String> subMembers = subGroups.get(subCheckers.get(i));
            subGroup.setSubMembers(subMembers);
            
            for(int j = 0; j < subMembers.size(); j++) {
                LocalPredicate lp = normalProcessToLocalPredicate.get(subMembers.get(j));
                registerLocalPredicate(lp, subMembers.get(j), subGroup);
            }   
        }
    }
    /**
     * 
     * @param s
     */
    private void parseSCPStructure(Structure s) {
        ArrayList<Structure> children = s.getChildren();

        if (s.getNodeType() == NodeType.GSE) {
            for (int i = 0; i < children.size(); i++) {
                parseSCPStructure(children.get(i));
            }
        } else if (s.getNodeType() == NodeType.CGS) {
            IDManager idManager = MIPAResource.getIDManager();
            String owner = null;
            try {
                owner = idManager.getID(Catalog.Checker);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<String> owners = new ArrayList<String>();
            ArrayList<String> members = new ArrayList<String>();
            Map<String, LocalPredicate> mapping = new HashMap<String, LocalPredicate>();

            owners.add(owner);
            for (int i = 0; i < children.size(); i++) {
                Structure unit = children.get(i);

                assert (unit.getNodeType() == NodeType.LP);
                String memberID = null;
                try {
                    memberID = idManager.getID(Catalog.NormalProcess);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                members.add(memberID);
                mapping.put(memberID, (LocalPredicate) unit);
            }
            String groupID = null;
            try {
                groupID = idManager.getID(Catalog.Group);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // create group for CGS
            Group g = new Group(groupID, owners, members, PredicateType.SCP);
            g.setCoordinatorID(groupID);

            Coordinator coordinator = MIPAResource.getCoordinator();
            try {
                coordinator.newCoordinator(g);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // create CGS checker.
            String checkerName = g.getOwners().get(0);
            String[] normalProcesses = new String[g.getMembers().size()];
            g.getMembers().toArray(normalProcesses);
            CheckerFactory.newChecker(callback, checkerName, normalProcesses,
                                      g.getType());

            // create Normal Processes.
            for (int i = 0; i < members.size(); i++) {
                LocalPredicate lp = mapping.get(members.get(i));
                registerLocalPredicate(lp, members.get(i), g);
            }
        }
    }

    /**
     * 
     * @param s
     */
    private void parseWCPStructure(Structure s) {
        ArrayList<Structure> children = s.getChildren();

        if (s.getNodeType() == NodeType.GSE) {
            for (int i = 0; i < children.size(); i++) {
                parseWCPStructure(children.get(i));
            }
        } else if (s.getNodeType() == NodeType.CGS) {
            IDManager idManager = MIPAResource.getIDManager();
            String owner = null;
            try {
                owner = idManager.getID(Catalog.Checker);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<String> owners = new ArrayList<String>();
            ArrayList<String> members = new ArrayList<String>();
            Map<String, LocalPredicate> mapping = new HashMap<String, LocalPredicate>();

            owners.add(owner);
            for (int i = 0; i < children.size(); i++) {
                Structure unit = children.get(i);

                assert (unit.getNodeType() == NodeType.LP);
                String memberID = null;
                try {
                    memberID = idManager.getID(Catalog.NormalProcess);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                members.add(memberID);
                mapping.put(memberID, (LocalPredicate) unit);
            }
            String groupID = null;
            try {
                groupID = idManager.getID(Catalog.Group);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // create group for CGS
            Group g = new Group(groupID, owners, members, PredicateType.WCP);
            g.setCoordinatorID(groupID);

            Coordinator coordinator = MIPAResource.getCoordinator();
            try {
                coordinator.newCoordinator(g);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // create CGS checker.
            String checkerName = g.getOwners().get(0);
            String[] normalProcesses = new String[g.getMembers().size()];
            g.getMembers().toArray(normalProcesses);
            CheckerFactory.newChecker(callback, checkerName, normalProcesses,
                                      g.getType());

            // create Normal Processes.
            for (int i = 0; i < members.size(); i++) {
                LocalPredicate lp = mapping.get(members.get(i));
                registerLocalPredicate(lp, members.get(i), g);
            }
        }
    }
    
    /**
     * 
     * @param fatherID
     * @param s
     */

    @SuppressWarnings("unused")
    private void parseOGAPStructure(String fatherID, Structure s) {

        ArrayList<Structure> children = s.getChildren();

        assert (children != null);

        IDManager idManager = MIPAResource.getIDManager();
        // get id
        String id = null;
        try {
            id = idManager.getID(Catalog.Checker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (s.getNodeType() == NodeType.GSE) {
            assert (fatherID == null);

            // create GSE checker, and bind to rmi registry

            // parser the others
            for (int i = 0; i < children.size(); i++) {
                Structure unit = children.get(i);
                parseOGAPStructure(id, unit);
            }
        } else if (s.getNodeType() == NodeType.CGS) {
            assert (fatherID != null);

            // create CGS checker, pass father ID to it

            // create group
            ArrayList<String> members = new ArrayList<String>();
            ArrayList<String> owners = new ArrayList<String>();
            Map<String, LocalPredicate> mapping = new HashMap<String, LocalPredicate>();

            owners.add(id);
            for (int i = 0; i < children.size(); i++) {
                Structure unit = children.get(i);

                assert (unit.getNodeType() == NodeType.LP);
                String memberID = null;
                try {
                    memberID = idManager.getID(Catalog.NormalProcess);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                members.add(memberID);
                mapping.put(memberID, (LocalPredicate) unit);

            }
            String groupID = null;
            try {
                groupID = idManager.getID(Catalog.Group);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Group g = new Group(groupID, owners, members, PredicateType.OGA);
            // register group to coordinator.

            // register each local predicate.

            for (int i = 0; i < members.size(); i++) {
                LocalPredicate LP = mapping.get(members.get(i));
                // FIND THE ECA MANAGER ID. 鐩存帴浼犻�group

            }

        } else {
            assert (false);
        }
    }

    private void registerLocalPredicate(LocalPredicate lp, String name, Group g) {
        try {
            String ecaManagerID = contextMapping.getEntityId(lp.getName());
            lp.setValueType(contextMapping.getValueType(lp.getName()));

            Naming server = MIPAResource.getNamingServer();
            ECAManager ecaManager = (ECAManager) server.lookup(ecaManagerID);

            System.out.println("find eca manager successfully.");
            System.out.println(ecaManagerID);
            ecaManager.registerLocalPredicate(lp, name, g);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
