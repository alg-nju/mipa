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
    //private ContextMapping contextMapping;
    
    public StructureParser() {
	System.out.println("Testing constructor of STRUCTUREPARSER.");
    }

    /*
    public StructureParser(ContextMapping contextMapping) {
	this.contextMapping = contextMapping;
    }
    */

    /**
     * parse structure of predicate from <code>Document</code>.
     * 
     * @param predicate
     *            a document
     * @param type
     *            predicate type
     */
    public Structure parseStructure(Document predicate) {
	Structure result = null;

	NodeList elements = predicate.getElementsByTagName("GSE");

	if (elements != null) {
	    result = new Composite(NodeType.GSE, "GSE");

	    for (int i = 0; i < elements.getLength(); i++) {
		Node GSE = elements.item(i);

		for (Node node = GSE.getFirstChild(); node != null; node = node
			.getNextSibling()) {

		    if (node.getNodeType() == Node.ELEMENT_NODE) {

			if (node.getNodeName().equals("CGS")) {
			    Structure CGSNode = new Composite(NodeType.CGS,
				    "CGS");
			    result.add(CGSNode);

			    for (Node LP = node.getFirstChild(); LP != null; LP = LP
				    .getNextSibling()) {
				if (LP.getNodeType() == Node.ELEMENT_NODE) {

				    if (LP.getNodeName().equals("LP")) CGSNode.add(parseLocalPredicate(LP));
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
	
	for (Node node = localPredicate.getFirstChild(); node != null; node = node.getNextSibling()) {

	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		if (node.getNodeName().equals("formula")) {
		    for (Node atom = node.getFirstChild(); atom != null; atom = atom.getNextSibling()) {
			if (atom.getNodeType() == Node.ELEMENT_NODE) {
			    if (atom.getNodeName().equals("atom")) {
				String operator = atom.getAttributes().getNamedItem("operator").getNodeValue();
				String name = atom.getAttributes().getNamedItem("name").getNodeValue();
				String value = atom.getAttributes().getNamedItem("value").getNodeValue();
				
				LP.setOperator(operator);
				LP.setName(name);
				LP.setValue(value);
				/*
				try {
				    LP.setValueType(contextMapping.getValueType(LP.getName()));
				} catch(Exception e) {
				    e.printStackTrace();
				}
				*/
			    } // :end if
			} // :end if
		    } // :end for
		}// :end if
	    }// :end if
	}// :end for
	
	return LP;
    }
}