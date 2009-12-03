package net.sourceforge.mipa.test;

import static org.junit.Assert.*;

import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.NodeType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AtomTest {
    private static Atom atom;
    private int n;
    private String[] values;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        atom = new Atom(NodeType.ATOM,"atom");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        atom.setType(NodeType.ATOM);
        atom.setNodeName("atom");
        atom.setFather(null);
        atom.setName(null);
        atom.setOperator(null);
        atom.setValue(null);
        atom.setValueType(null);
        n = 10;
        values = new String[n];
        for(int i=0;i<n;i++)
            values[i] = String.valueOf(i);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUpdateDouble() {
        atom.setValueType("Double");
        atom.setName("temperature");
        atom.setOperator("great-than");
        atom.setValue("20");
        atom.update(values);
        assertFalse(atom.getNodeValue());
        atom.setValue("-1");
        atom.update(values);
        assertTrue(atom.getNodeValue());
        atom.setOperator("less-than");
        atom.setValue("20");
        atom.update(values);
        assertTrue(atom.getNodeValue());
        atom.setValue("-1");
        atom.update(values);
        assertFalse(atom.getNodeValue());
        atom.setOperator("equals");
        atom.setValue("20");
        atom.update(values);
        assertFalse(atom.getNodeValue());
        values[0]="20";
        atom.update(values);
        assertTrue(atom.getNodeValue());
    }
    
    public void testUpdateString() {
        atom.setValueType("String");
        atom.setName("RFID");
        atom.setOperator("contain");
        atom.setValue("0");
        atom.update(values);
        assertTrue(atom.getNodeValue());
        atom.setValue("11");
        atom.update(values);
        assertFalse(atom.getNodeValue());
        atom.setOperator("not-contain");
        atom.setValue("0");
        atom.update(values);
        assertFalse(atom.getNodeValue());
        atom.setValue("11");
        atom.update(values);
        assertTrue(atom.getNodeValue());
        
    }
    
    @Test(expected = NumberFormatException.class)
    public void testUpdateException() {
        values[0]="a";
        atom.setValueType("Double");
        atom.setName("temperature");
        atom.setOperator("greater-than");
        atom.setValue("20");
        atom.update(values);
    }
}
