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
package net.sourceforge.mipa.test;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.StructureParser;

import org.w3c.dom.Document;

/**
 * @author jpyu
 *
 */
public class StructureParserTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub
	
	Document doc = null;
        try {
            File f = new File("predicate.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory
                                                                   .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(f);

        } catch (Exception e) {
            e.printStackTrace();
        }
        StructureParser p = new StructureParser();
        Structure s = p.parseStructure(doc);
        //System.out.println(s.getNodeType());
        printStructure(s);
    }
    
    public static void printStructure(Structure s) {
	System.out.println(s.getNodeType());
	
	if(s.getNodeType() == NodeType.LP) {
	    LocalPredicate lp = (LocalPredicate) s;
	    System.out.println(lp.getName());
	    System.out.println(lp.getValue());
	    return;
	}
	
	ArrayList<Structure> list = s.getChildren();
	for(int i = 0; i < list.size(); i++) {
	    printStructure(list.get(i));
	}
    }
}
