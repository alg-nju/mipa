package net.sourceforge.mipa.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.StructureParser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StructureParserTest {
    
    private static StructureParser structureParser;
    private static Document doc;
    private static LocalPredicate localPredicate;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        structureParser = new StructureParser();
        File f = new File("config/predicate/predicate_wcp.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(f);
        localPredicate = new LocalPredicate();
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
    public void testParseAtom() {
        NodeList nodeList = doc.getElementsByTagName("atom");
        if(nodeList != null)
        {
            for(int i = 0; i < nodeList.getLength(); i++)
            {
                try
                {
                    Atom atom = (Atom) structureParser.parseAtom(nodeList.item(i));
                    String name = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
                    assertEquals(name,atom.getName());
                    String operator = nodeList.item(i).getAttributes().getNamedItem("operator").getNodeValue();
                    assertEquals(operator,atom.getOperator());
                    String value = nodeList.item(i).getAttributes().getNamedItem("value").getNodeValue();
                    assertEquals(value,atom.getValue());
                }
                catch(ClassCastException e)
                {
                    fail("Cannot be here!");
                }

            }
        }
        else
        {
            fail("Cannot be here!");
        }
    }
    
    @Test
    public void testParseLocalPredicate() {
        NodeList nodeList = doc.getElementsByTagName("LP");
        if(nodeList != null)
        {
            for(int i=0;i<nodeList.getLength();i++)
            {
                localPredicate = structureParser.parseLocalPredicate(nodeList.item(i));
                if(localPredicate.getChildren().get(0)!= null)
                {
                    try
                    {
                        Formula formula = (Formula)localPredicate.getChildren().get(0);
                    }
                    catch(ClassCastException e)
                    {
                        fail("Cannot be here!");
                    }
                }
                else
                {
                    fail("Cannot be here!");
                }
            }
        }
        else
        {
            fail("Cannot be here!");
        }
    }

    @Test
    public void testParseFormula() {
        NodeList nodeList = doc.getElementsByTagName("formula");
        if(nodeList != null)
        {
            for(int i = 0; i < nodeList.getLength(); i++)
            {
                
                try
                {
                    Formula formula = (Formula) structureParser.parseFormula(localPredicate,nodeList.item(i));
                    for(Node node = nodeList.item(i).getFirstChild();
                        node!=null;
                        node = node.getNextSibling())
                    {
                        if(node.getNodeType() == Node.ELEMENT_NODE)
                        {
                            if(node.getNodeName().equals("quantifier")
                                    ||node.getNodeName().equals("binary")
                                    ||node.getNodeName().equals("unary"))
                            {
                                assertEquals(node.getAttributes().getNamedItem("value").getNodeValue()
                                            ,formula.getConnetor().getOperator().toString().toLowerCase());
                            }
                            else if(node.getNodeName().equals("atom"))
                            {
                                String name = node.getAttributes().getNamedItem("name").getNodeValue();
                                assertEquals(name,((Atom)formula.getChildren().get(0)).getName());
                                String operator = node.getAttributes().getNamedItem("operator").getNodeValue();
                                assertEquals(operator,((Atom)formula.getChildren().get(0)).getOperator());
                                String value = node.getAttributes().getNamedItem("value").getNodeValue();
                                assertEquals(value,((Atom)formula.getChildren().get(0)).getValue());
                                
                                ArrayList<Atom> arrayList = localPredicate.getAtoms();
                                boolean found = false;
                                for(int j = 0;j<arrayList.size();j++)
                                {
                                    if(arrayList.get(j).getName().equals(name)
                                            &&arrayList.get(j).getOperator().equals(operator)
                                            &&arrayList.get(j).getValue().equals(value))
                                    {
                                        found = true;
                                        break;
                                    }
                                }
                                assertTrue(found);
                            }
                            else if(node.getNodeName().equals("formula"))
                            {
                                //do nothing
                            }
                            else
                            {
                                fail("cannot be here!");
                            }
                        }
                    }
                }
                catch(ClassCastException e)
                {
                    fail("Cannot be here!");
                }
            }
        }
        else
        {
            fail("Cannot be here!");
        }
    }

    

}
