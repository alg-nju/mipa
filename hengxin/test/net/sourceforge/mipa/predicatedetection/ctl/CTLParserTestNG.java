/**
 * @author hengxin(hengxin0912@gmail.com)
 * 
 * JUnit test for <code>CTLParser</code>
 */
package net.sourceforge.mipa.predicatedetection.ctl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.predicatedetection.StructureParser;

import org.junit.After;
import org.junit.Before;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * test for <code>CTLParser</code>
 * @author hengxin(hengxin0912@gmail.com)
 * 
 */
public class CTLParserTestNG 
{
//	private String ctlFileName = "config/predicate/ctl/predicate_ctl.xml";
	
	private String ctlFileName = "config/predicate/ctl/predicate_ctl_au_experiment0.xml";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception 
	{
	}

	/**
	 * Test method for 
	 * {@link net.sourceforge.mipa.predicatedetection.ctl.CTLParser#parseCTLPart(org.w3c.dom.Document, net.sourceforge.mipa.predicatedetection.Structure, net.sourceforge.mipa.predicatedetection.StructureParser)}.
	 * 
	 * just for ctl parser, regardless of mipa system
	 * 
	 * FIXME: the test is not complete!
	 */
	@Test
	public void testParseCTLPart() 
	{
//		MIPAAllInOne.getInstance("ctl/predicate_ctl.xml").runMIPAWithoutECA();
		
		new StructureParser().parseStructure(this.parseXml(this.ctlFileName));
		
		assertTrue(true);
	}

    /**
     * parse predicate to Document Object(Dom).
     * 
     * @param fileName
     *            a string
     * @return predicate document
     */
    private Document parseXml(String fileName) {
        Document doc = null;
        try {
            File f = new File(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory
                                                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(f);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
}
