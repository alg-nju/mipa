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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * local predicate parser module.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class StructureParser {

    /** context mapping */
    // private ContextModeling contextMapping;

    public StructureParser() {
        System.out.println("Testing constructor of STRUCTUREPARSER.");
    }

    /*
     * public StructureParser(ContextModeling contextMapping) {
     * this.contextMapping = contextMapping; }
     */

    //FIXME CGS should split into each GSE, but this code merges all CGS into one GSE.
    
    /**
     * parse structure of predicate from <code>Document</code>.
     * 
     * @param predicate
     *            a document
     */
    public Structure parseStructure(Document predicate) {
        Structure result = null;

        NodeList elements = predicate.getElementsByTagName("GSE");

        if (elements != null) {
            result = new Composite(NodeType.GSE, "GSE");
            System.out.println("GSE");
            for (int i = 0; i < elements.getLength(); i++) {
                Node GSE = elements.item(i);

                for (Node node = GSE.getFirstChild(); 
                      node != null; 
                      node = node.getNextSibling()) {

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        if (node.getNodeName().equals("CGS")) {
                            Structure CGSNode = new Composite(NodeType.CGS,
                                                              "CGS");
                            System.out.println("CGS");
                            result.add(CGSNode);

                            for (Node LP = node.getFirstChild(); 
                                  LP != null; 
                                  LP = LP.getNextSibling()) {
                                
                                if (LP.getNodeType() == Node.ELEMENT_NODE) {

                                    if (LP.getNodeName().equals("LP"))
                                        CGSNode.add(parseLocalPredicate(LP));
                                } // :end if
                            } // :end for
                        } // :end if
                    } // :end if
                } // :end for
            } // :end for
        } // :end if
        return result;
    }
    
    private LocalPredicate parseLocalPredicate(Node localPredicate) {
        LocalPredicate LP = new LocalPredicate();
        System.out.println("LP");
        
        for (Node node = localPredicate.getFirstChild(); 
              node != null; 
              node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("formula")) {
                    LP.add(parseFormula(LP,node));
                }// :end if
            }// :end if
        }// :end for
        return LP;
    }
    
    private Formula parseFormula(LocalPredicate localPredicate, Node formula)
    {
        Formula formulaNode = new Formula(NodeType.FORMULA,"formula");
        System.out.println("formula");
        for (Node node = formula.getFirstChild(); 
            node != null; 
            node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if(node.getNodeName().equals("quantifier"))
                {
                    Connector quantifierNode = new Connector(NodeType.QUANTIFIER,"quantifier");
                    String operator = node.getAttributes().getNamedItem("value").getNodeValue();
                    quantifierNode.setOperator(operator);
                    System.out.println("quantifier "+operator);
                    node = node.getNextSibling();
                    node = node.getNextSibling();
                   // if (node.getNodeType() == Node.ELEMENT_NODE) {
                   //     if(node.getNodeName().equals("quantifier"))
                    //    {
                            Formula subFormulaNode = parseFormula(localPredicate,node);
                            formulaNode.setConnetor(quantifierNode);
                            formulaNode.add(subFormulaNode);
                   //     }
                   // }
                }
                else if(node.getNodeName().equals("unary"))
                {
                    Connector unaryNode = new Connector(NodeType.UNARY,"unary");
                    String operator = node.getAttributes().getNamedItem("value").getNodeValue();
                    unaryNode.setOperator(operator);
                    System.out.println("unary "+operator);
                    node = node.getNextSibling();
                    node = node.getNextSibling();
                    Formula subFormulaNode = parseFormula(localPredicate,node);
                    formulaNode.setConnetor(unaryNode);
                    formulaNode.add(subFormulaNode);
                }
                else if(node.getNodeName().equals("atom"))
                {
                    Structure atom = parseAtom(node);
                    formulaNode.add(atom);
                    //将所有的Atom保存到LocalPredicate的ArrayList<Atom>中
                    localPredicate.addAtom((Atom)atom);
                    
                }
                else if(node.getNodeName().equals("formula"))
                {
                    Formula subFormulaNode_1 = parseFormula(localPredicate,node);
                    node = node.getNextSibling();
                    node = node.getNextSibling();
                    Connector binaryNode = new Connector(NodeType.BINARY,"binary");
                    String operator = node.getAttributes().getNamedItem("value").getNodeValue();
                    binaryNode.setOperator(operator);
                    System.out.println("binary "+operator);
                    node = node.getNextSibling();
                    node = node.getNextSibling();
                    Formula subFormulaNode_2 = parseFormula(localPredicate,node);
                    formulaNode.setConnetor(binaryNode);
                    formulaNode.add(subFormulaNode_1);
                    formulaNode.add(subFormulaNode_2);
                }
                else
                {
                    System.out.println("Node "+node.getNodeName()+" existing here is illegal!");
                }
            }
        }
        return formulaNode;
    }
    private Structure parseAtom(Node atom)
    {
        Atom atomNode = new Atom(NodeType.ATOM,"atom");
        System.out.println("atom");
        String operator = atom.getAttributes().getNamedItem("operator").getNodeValue();
        String name = atom.getAttributes().getNamedItem("name").getNodeValue();
        String value = atom.getAttributes().getNamedItem("value").getNodeValue();
        System.out.println(name);
        System.out.println(operator);
        System.out.println(value);
        atomNode.setOperator(operator);
        atomNode.setName(name);
        atomNode.setValue(value);
        return (Structure)atomNode;
    }
}