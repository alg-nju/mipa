/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009-2010 the original author or authors.
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
package net.sourceforge.mipa.eca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Connector;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NodeType;

/**
 * Condition of ECA mechanism.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public abstract class Condition implements Serializable {

    private static final long serialVersionUID = -5349061659963216502L;

    /** local predicate which <code>Condition</code> should concern */
    //protected LocalPredicate localPredicate;
    
    protected Composite localPredicate;
    protected HashMap<String, ArrayList<Atom>> map;
    /**
     * called by DataSource for notifying event change.
     * 
     * @param eventName
     *            event name
     * @param values
     *            event values
     * @see DataSource
     */
    public abstract void update(String eventName, String[] values);

    /**
     * notify the action of ECA mechanism.
     * 
     * @param eventName
     *            event name
     * @param value
     *            event value
     */
    public abstract void notifyListener(String eventName, String value);
    
    public Composite parseLocalPredicate(LocalPredicate localPredicate)
    {
        Formula formulaNode = (Formula)localPredicate.getChildren().get(0);
        System.out.println("parseLocalPredicate:");
        return parseFormula(formulaNode);
    }
    
    public Composite parseFormula(Formula formula)
    {
        if(formula.getConnetor() != null)
        {
            if(formula.getConnetor().getNodeType() == NodeType.QUANTIFIER)
            {
                Connector quantifier = formula.getConnetor();
                Composite composite = parseFormula((Formula)formula.getChildren().get(0));
                quantifier.add(composite);
                composite.setFather(quantifier);
                return quantifier;
            }
            else if(formula.getConnetor().getNodeType() == NodeType.BINARY)
            {
                Connector binary = formula.getConnetor();
                Composite compositeLeft = parseFormula((Formula)formula.getChildren().get(0));
                Composite compositeRight = parseFormula((Formula)formula.getChildren().get(1));
                binary.add(compositeLeft);
                binary.add(compositeRight);
                compositeLeft.setFather(binary);
                compositeRight.setFather(binary);
                return binary;
            }
            else if(formula.getConnetor().getNodeType() == NodeType.UNARY)
            {
                Connector unary = formula.getConnetor();
                Composite composite = parseFormula((Formula)formula.getChildren().get(0));
                unary.add(composite);
                composite.setFather(unary);
                return unary;
            }
            else
            {
                System.out.println("Non-defined node type: "+formula.getConnetor().getNodeType());
            }
        }
        else //Atom
        {
            Atom atom = (Atom)formula.getChildren().get(0);
            String eventName = atom.getName();
            if(map.get(eventName) == null)
            {
                ArrayList<Atom> arrayList = new ArrayList<Atom>();
                arrayList.add(atom);
                map.put(eventName, arrayList);
            }
            else
            {
                ArrayList<Atom> arrayList = map.get(eventName);
                arrayList.add(atom);
            }
            return atom;
        }
        return null;
    }
    /**
     * calculate the local predicate value.
     * 
     * @param eventName
     *            event name
     * @param values
     *            event values
     */
    public void assign(String eventName, String[] values) {
        ArrayList<Atom> arrayList = map.get(eventName);
        for(int i = 0; i < arrayList.size(); i++)
        {
            Atom atom = arrayList.get(i);
            atom.update(values);
            if(atom.getLastValue() != atom.getNodeValue())//value changed
            {
                if(atom.getFather()!= null)
                {
                    Connector connector = (Connector)atom.getFather();
                    while(connector != null)
                    {
                        connector.update();
                        if(connector.getLastValue() == connector.getNodeValue())
                        {
                            break;
                        }
                        connector = (Connector)connector.getFather();
                    }
                }
            }
            else
            {
                //do nothing
            }
        }
    }
    
    public HashMap<String, ArrayList<Atom>> getMap()
    {
        return map;
    }
    public Composite getLocalPredicate()
    {
        return localPredicate;
    }
    public void setLocalPredicate(Composite localPredicate)
    {
        this.localPredicate = localPredicate;
    }
}
