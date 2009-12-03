package net.sourceforge.mipa.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.eca.EmptyCondition;
import net.sourceforge.mipa.eca.Listener;
import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Connector;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.StructureParser;
import net.sourceforge.mipa.predicatedetection.scp.SCPNormalProcess;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ConditionTest {
    private static EmptyCondition emptyCondition;
    private static Document doc;
    private static StructureParser structureParser;
    private static Listener action;
    private static LocalPredicate localPredicate;
    private static Composite formula;
    private HashMap<String, ArrayList<Atom>> map;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        structureParser = new StructureParser();
        File f = new File("config/predicate/predicate_wcp.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(f);
        NodeList nodeList = doc.getElementsByTagName("LP");
        localPredicate = structureParser.parseLocalPredicate(nodeList.item(0));
        String[] checkers = new String[10];
        String[] normalProcesses = new String[10];
        for(int i=0;i<10;i++)
        {
            checkers[i]="checker"+i;
            normalProcesses[i]="normalProcess"+i;
        }
        action = new SCPNormalProcess("scpNormalProcess",checkers,normalProcesses);
        emptyCondition = new EmptyCondition(action,localPredicate);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseLocalPredicate() {
        Composite composite = emptyCondition.parseLocalPredicate(localPredicate);
        if(composite != null)
        {
            //do nothing
        }
        else
        {
            fail("Cannot be here!");
        }
    }

    @Test
    public void testParseFormula() {
        try
        {
            Formula formulaNode = (Formula)localPredicate.getChildren().get(0);
            Composite composite = emptyCondition.parseFormula(formulaNode);
            if(formulaNode.getConnetor()!=null)
            {
                assertEquals(formulaNode.getConnetor().getOperator()
                        ,((Connector)composite).getOperator());      
            }
            else
            {
                try
                {
                    Atom atom = (Atom)formulaNode.getChildren().get(0);
                    assertEquals(atom,composite);
                    map = emptyCondition.getMap();
                    assertTrue(map.containsKey(atom.getName()));
                    ArrayList<Atom> arrayList = map.get(atom.getName());
                    assertTrue(arrayList.contains(atom));
                }
                catch(ClassCastException e)
                {
                    fail("Cannot be here!");
                }
            }
        }
        catch(ClassCastException e)
        {
            fail("Cannot be here!");
        }
    }

    @Test
    public void testAssign() {
        formula = emptyCondition.parseLocalPredicate(localPredicate);
        emptyCondition.setLocalPredicate(formula);
        map = emptyCondition.getMap();
        for(int i=0;i<localPredicate.getAtoms().size();i++)
        {
            ArrayList<Atom> arrayList = map.get(localPredicate.getAtoms().get(i).getName());
            assertTrue(!arrayList.isEmpty());
            for(int k =0; k < arrayList.size(); k++)
            {
                String[] values = new String[1];
                values[0]=arrayList.get(k).getValue();
                if(arrayList.get(k).getName().equals("temperature"))
                {
                    arrayList.get(k).setValueType("Double");
                    values[0]= "27";
                }
                else if(arrayList.get(k).getName().equals("RFID"))
                {
                    arrayList.get(k).setValueType("String");
                    values[0]="tag_00001";
                }
                emptyCondition.assign(arrayList.get(k).getName(),values);
                arrayList.get(k).update(values);
            }
            boolean result = compute((Composite)localPredicate.getChildren().get(0));
            assertEquals(formula.getNodeValue(),result);
            /*
            for(int k =0;k<arrayList.size();k++)
            {
                String[] values = new String[1];
                values[0]=arrayList.get(i).getValue()+"1";
                if(arrayList.get(i).getName().equals("temperature"))
                    arrayList.get(i).setValueType("Double");
                else if(arrayList.get(i).getName().equals("RFID"))
                    arrayList.get(i).setValueType("String");
                emptyCondition.assign(arrayList.get(i).getName(),values);
                arrayList.get(i).update(values);
                Composite composite = (Composite)arrayList.get(i).getFather();
                if(((Formula)composite).getConnetor() == null)
                {
                    assertEquals(arrayList.get(i).getNodeValue()
                            ,formula.getNodeValue());
                }
                while(composite!=null)
                {
                    Formula formula = (Formula)composite;
                    if(formula.getConnetor()!=null)
                    {
                        if(formula.getConnetor().getOperator().equals(NodeType.UNIVERSAL))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==false)
                            {
                                formula.setNodeValue(false);
                            }
                        }
                        else if(formula.getConnetor().getOperator().equals(NodeType.EXISTENTIAL))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==true)
                            {
                                formula.setNodeValue(true);
                            }
                        }
                        else if(formula.getConnetor().getOperator().equals(NodeType.CONJUNCTION))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==true
                                    &&((Composite)formula.getChildren().get(0)).getNodeValue()==true)
                            {
                                formula.setNodeValue(true);
                            }
                            else
                            {
                                formula.setNodeValue(false);
                            }
                        }
                        else if(formula.getConnetor().getOperator().equals(NodeType.DISJUNCTION))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==true
                                    ||((Composite)formula.getChildren().get(0)).getNodeValue()==true)
                            {
                                formula.setNodeValue(true);
                            }
                            else
                            {
                                formula.setNodeValue(false);
                            }
                        }
                        else if(formula.getConnetor().getOperator().equals(NodeType.IMPLY))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==false
                                    ||((Composite)formula.getChildren().get(0)).getNodeValue()==true)
                            {
                                formula.setNodeValue(true);
                            }
                            else
                            {
                                formula.setNodeValue(false);
                            }
                        }
                        else if(formula.getConnetor().getOperator().equals(NodeType.NOT))
                        {
                            if(((Composite)formula.getChildren().get(0)).getNodeValue()==false)
                            {
                                formula.setNodeValue(true);
                            }
                            else
                            {
                                formula.setNodeValue(false);
                            }
                        }
                    }
                    composite = (Composite)formula.getFather();
                }
            }
            assertEquals(formula.getNodeValue(),((Composite)localPredicate.getChildren().get(0)).getNodeValue());
            */
        }
    }
    public boolean compute(Composite composite)
    {
        Formula formula = (Formula)composite;
        if(formula.getConnetor()==null)
        {
            return ((Composite)formula.getChildren().get(0)).getNodeValue();
        }
        else
        {
            if(formula.getConnetor().getOperator().equals(NodeType.CONJUNCTION))
            {
                return compute((Composite)formula.getChildren().get(0))
                &&compute((Composite)formula.getChildren().get(1));
            }
            else if(formula.getConnetor().getOperator().equals(NodeType.DISJUNCTION))
            {
                return compute((Composite)formula.getChildren().get(0))
                ||compute((Composite)formula.getChildren().get(1));
            }
            else if(formula.getConnetor().getOperator().equals(NodeType.IMPLY))
            {
                return !compute((Composite)formula.getChildren().get(0))
                ||compute((Composite)formula.getChildren().get(1));
            }
            else if(formula.getConnetor().getOperator().equals(NodeType.UNIVERSAL))
            {
                //to be fixed
                if(compute((Composite)formula.getChildren().get(0))==false)
                    return false; 
            }
            else if(formula.getConnetor().getOperator().equals(NodeType.EXISTENTIAL))
            {
                if(compute((Composite)formula.getChildren().get(0))==true)
                    return true;
            }
            else if(formula.getConnetor().getOperator().equals(NodeType.NOT))
            {
                return !compute((Composite)formula.getChildren().get(0));
            }
        }
        return false;
    }
}
