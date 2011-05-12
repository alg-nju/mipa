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

import static config.Config.LOG_DIRECTORY;
import static config.Debug.DEBUG;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.predicatedetection.Automaton;
import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.RegularExpression;
import net.sourceforge.mipa.predicatedetection.State;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 * 
 */
public class SequenceLatticeChecker extends LatticeChecker {
    private static final long serialVersionUID = -4389010288731310197L;

    private SequenceLatticeNode precedeNode = null;;
    private PrintWriter out = null;
    private PrintWriter debug = null;
    private int count;
    private HashMap<String, ArrayList<String>> CGSToNPs;
    private RegularExpression predicate;
    private Automaton automaton;
    private boolean flag = false;
    private PrintWriter outTime = null;
    
    public SequenceLatticeChecker(ResultCallback application, String predicateID, 
            String checkerName, String[] normalProcesses,
            Structure specification) {
        // TODO Auto-generated constructor stub
        super(application, predicateID, checkerName, normalProcesses);
        try {
            out = new PrintWriter(LOG_DIRECTORY + "/Sequence.log");
            debug = new PrintWriter(LOG_DIRECTORY + "/Sequence_Debug.log");
            outTime = new PrintWriter(LOG_DIRECTORY + "/Sequence_Time.log");
            out.println("Start...");
            debug.println("Start...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        count = 0;
        CGSToNPs = new HashMap<String, ArrayList<String>>();
        getCGSToNPs(specification);
        parsePredicateToAutomaton(specification);
    }

    @Override
    public void check(AbstractLatticeNode startNode,
            AbstractLatticeNode currentNode) {
        // TODO Auto-generated method stub
        if(flag == false) {
            Long start = (new Date()).getTime();
            ((SequenceLatticeNode) currentNode).setTailFlag(true);
            detectSequence((SequenceLatticeNode) currentNode);
            precedeNode = (SequenceLatticeNode) currentNode;
            ((SequenceLatticeNode) currentNode).setVisited(true);
            long end = (new Date()).getTime();
            outTime.println(end-start);
            outTime.flush();
        }
    }

    private void computeCGS(SequenceLatticeNode node) {
        Set<String> CGSs = CGSToNPs.keySet();
        Iterator<String> it = CGSs.iterator();
        while (it.hasNext()) {
            String CGS = it.next();
            boolean result = true;
            ArrayList<String> NPs = CGSToNPs.get(CGS);
            for (int i = 0; i < NPs.size(); i++) {
                String np = NPs.get(i);
                String[] c = np.split("ss");
                assert (c.length == 2);
                int npIndex = Integer.valueOf(c[1]);
                result = result
                        && (getGlobalState()[npIndex].getlocalPredicate());
            }
            if (result == true) {
                node.addSatisfiedPredicates(CGS.charAt(0));
            }
        }
        //add {}
        if(node.getSatisfiedPredicates().trim().equals("")) {
            node.addSatisfiedPredicates('z');
        }
    }

    private void detectSequence(SequenceLatticeNode node) {
        // TODO Auto-generated method stub
        computeCGS(node);
        ArrayList<SequenceLatticeNode> precede = new ArrayList<SequenceLatticeNode>();
        for (int i = 0; i < node.getprevious().size(); i++) {
            precede.add((SequenceLatticeNode) node.getprevious().get(i));
        }

        // if the node is the initial node, precede is null.
        if (precede.size() == 0) {
            precedeNode = node;
            node.addReachedStates(automaton.getInitialState());
        }
        else {
            Iterator<SequenceLatticeNode> it = precede.iterator();
            while (it.hasNext()) {
                SequenceLatticeNode predNode = it.next();
                if (predNode.getVisited() == false) {
                    detectSequence(predNode);
                    predNode.setVisited(true);
                }
            }
    
            it = precede.iterator();
            while (it.hasNext()) {
                SequenceLatticeNode preNode = it.next();
                HashSet<State> precedeState = preNode.getReachedStates();
                Iterator<State> iterator = precedeState.iterator();
                while (iterator.hasNext()) {
                    State state = iterator.next();
                    String[] satisfiedPredicate = node.getSatisfiedPredicates()
                            .split(" ");
                    for (int i = 0; i < satisfiedPredicate.length; i++) {
                        if(!satisfiedPredicate[i].equals("")) {
                            char c = satisfiedPredicate[i].charAt(0);
                            State step = state.step(c);
                            node.addReachedStates(step);
                        }
                    }
                }
            }
            
            if (node.getTailFlag() == true) {
                computePredicate(node);
                //pos: Intersection
                //def: Inclusion
                if (node.getFlagIntersection() == true) {
                    // detect Def(sequence),output the information
                    try {
                        application.callback(predicateID, String.valueOf(true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count++;
                    out.print(count + ": ");
                    String[] s = node.getID();
                    for (int i = 0; i < s.length; i++) {
                        out.print(s[i] + " ");
                    }
                    out.print(": reachedStates: ");
                    Iterator<State> iterator = node.getReachedStates().iterator();
                    while(iterator.hasNext()) {
                        out.print(iterator.next().getName()+" ");
                    }
                    out.print(": ");
                    out.println("tail: "+node.getTailFlag());
                    out.print("IntervalID: ");
                    for(int i=0;i<node.getglobalState().length;i++) {
                        out.print(node.getglobalState()[i].getintervalID() + " ");
                    }
                    out.println();
                    out.flush();
                    flag = true;
                }
            }
        }
        if(DEBUG) {
            String[] s = node.getID();
            for (int i = 0; i < s.length; i++) {
                debug.print(s[i] + " ");
                debug.flush();
            }
            debug.print(": ");
            LocalState[] gs = node.getglobalState();
            for (int i = 0; i < gs.length; i++) {
                // String end = i + 1 != children.length ? " " : "\r\n";
                debug.print(gs[i].getlocalPredicate()+ " ");
                debug.flush();
            }
            debug.print(": SatisfiedPredicates: ");
            debug.print(node.getSatisfiedPredicates());
            debug.print(": ");
            debug.print("reachedStates: ");
            debug.flush();
            Iterator<State> iterator = node.getReachedStates().iterator();
            while(iterator.hasNext()) {
                debug.print(iterator.next().getName()+" ");
                debug.flush();
            }
            debug.print(": ");
            debug.println("tail: "+node.getTailFlag());
            debug.flush();
        }
    }

    private void computePredicate(SequenceLatticeNode node) {
        boolean flagIntersection = false;
        boolean flagInclusion = true;
        HashSet<State> reachedStates = node.getReachedStates();
        HashSet<State> acceptStates = automaton.getAcceptStates();
        Iterator<State> it = reachedStates.iterator();
        while (it.hasNext()) {
            boolean flag = false;
            State state = it.next();
            Iterator<State> iterator = acceptStates.iterator();
            while (iterator.hasNext()) {
                State acceptState = iterator.next();
                if (state.getName().equals(acceptState.getName())) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                flagInclusion = false;
            } else {
                flagIntersection = true;
            }
            if (flagInclusion == false && flagIntersection == true) {
                break;
            }
        }
        if(reachedStates.isEmpty()) {
            node.setFlagInclusion(false);
            node.setFlagIntersection(false);
        }
        else {
            node.setFlagInclusion(flagInclusion);
            node.setFlagIntersection(flagIntersection);
        }
    }

    @Override
    public SequenceLatticeNode createNode(LocalState[] globalState, String[] s) {
        // TODO Auto-generated method stub
        SequenceLatticeNode node = new SequenceLatticeNode(globalState, s);
        return node;
    }

    private void getCGSToNPs(Structure specification) {
        Structure CGSs = specification.getChildren().get(0);
        for (int i = 0; i < CGSs.getChildren().size(); i++) {
            ArrayList<String> NPs = new ArrayList<String>();
            Structure CGS = CGSs.getChildren().get(i);
            String name = ((Composite) CGS).getNodeName();
            for (int j = 0; j < CGS.getChildren().size(); j++) {
                NPs.add(((LocalPredicate) CGS.getChildren().get(j))
                        .getNormalProcess());
            }
            CGSToNPs.put(name, NPs);
        }
        if (DEBUG) {
            printCGSToNPs();
        }
    }

    private void parsePredicateToAutomaton(Structure specification) {
        Structure GSE = specification.getChildren().get(1);
        predicate = parsePredicateToRegExp(GSE);
        predicate = modifyRegularExpression(predicate);
        automaton = parseRegExpToAutomaton(predicate);
    }

    private void printCGSToNPs() {
        System.out.println("========================================");
        System.out.println("Print CGS name to NPs:");
        Set<String> names = CGSToNPs.keySet();
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            String result = "";
            String name = it.next();
            result += name + ":  ";
            ArrayList<String> NPs = CGSToNPs.get(name);
            for (int i = 0; i < NPs.size(); i++) {
                result += NPs.get(i) + "  ";
            }
            System.out.println(result);
        }
        System.out.println("Print over");
        System.out.println("----------------------------------------");
        System.out.println();
    }

    private Automaton parseRegExpToAutomaton(RegularExpression regularExpression) {
        if (DEBUG) {
            System.out.println("========================================");
            System.out.println("Parse regular expression to automaton:");
        }
        Automaton a = new Automaton(regularExpression);
        if (DEBUG) {
            System.out.println(a.toString());
            System.out.println("Parse over");
            System.out.println("----------------------------------------");
            System.out.println();
        }
        if(DEBUG) {
            debug.print("initialState: ");
            debug.println(a.getInitialState().getName());
            debug.print("acceptStates: ");
            Iterator<State> it = a.getAcceptStates().iterator();
            while(it.hasNext()) {
                debug.print(it.next().getName()+" ");
            }
            debug.println();
            debug.flush();
        }
        return a;
    }

    private RegularExpression parsePredicateToRegExp(Structure GSE) {
        if (DEBUG) {
            System.out.println("========================================");
            System.out.println("Parse predicate to regular expression:");
        }
        String string = "";
        for (int i = 0; i < GSE.getChildren().size(); i++) {
            string += parseElement(GSE.getChildren().get(i));
        }
        RegularExpression predicate = new RegularExpression(string);
        if (DEBUG) {
            System.out.println(string);
            System.out.println("Parse over");
            System.out.println("----------------------------------------");
            System.out.println();
        }
        return predicate;
    }

    private RegularExpression modifyRegularExpression(
            RegularExpression regularExpression) {
        if (DEBUG) {
            System.out.println("========================================");
            System.out.println("Modify regular expression:");
        }
        HashSet<String> identifiers = regularExpression.getIdentifiers();
        String expression = regularExpression.getRegularExpression();
        String result = "";
        Iterator<String> it = identifiers.iterator();
        boolean firstPosition = true;
        while (it.hasNext()) {
            String identifier = it.next();
            if (firstPosition == true) {
                result += "(" + identifier + ")";
                firstPosition = false;
            } else {
                result += "|(" + identifier + ")";
            }
        }
        //add {}
        result += "|(" + "z" + ")";
        
        result = "((" + result + ")*)";
        result = result + expression + result;
        RegularExpression predicate = new RegularExpression(result);
        if (DEBUG) {
            System.out.println(result);
            System.out.println("Modify over");
            System.out.println("----------------------------------------");
            System.out.println();
        }
        return predicate;
    }

    private String parseElement(Structure structure) {
        // TODO Auto-generated method stub
        NodeType nodeType = structure.getNodeType();
        String result = "";
        switch (nodeType) {
        case ZEROORMORE: {
            String elements = "";
            for (int i = 0; i < structure.getChildren().size(); i++) {
                String element = parseElement(structure.getChildren().get(i));
                elements += element;
            }
            result = "((" + elements + ")*)";
            break;
        }
        case ONEORMORE: {
            String elements = "";
            for (int i = 0; i < structure.getChildren().size(); i++) {
                String element = parseElement(structure.getChildren().get(i));
                elements += element;
            }
            result = "((" + elements + ")+)";
            break;
        }
        case CHOICE: {
            String elements = "";
            for (int i = 0; i < structure.getChildren().size(); i++) {
                String element = parseElement(structure.getChildren().get(i));
                if (i == 0) {
                    elements += element;
                } else {
                    elements += "|" + element;
                }
            }
            result = "(" + elements + ")";
            break;
        }
        case OPTIONAL: {
            String elements = "";
            for (int i = 0; i < structure.getChildren().size(); i++) {
                String element = parseElement(structure.getChildren().get(i));
                elements += element;
            }
            result = "((" + elements + ")?)";
            break;
        }
        case CGS: {
            String name = ((Composite) structure).getNodeName();
            result = "(" + name + ")";
            break;
        }
        default: {
            System.out.println("Parse error: not defined connector: "
                    + nodeType.toString());
            break;
        }
        }
        return result;
    }
}
