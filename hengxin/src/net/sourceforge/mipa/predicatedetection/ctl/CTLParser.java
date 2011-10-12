/**
 * CTLParser is used to parse the ctl formula given in the form of xml file.
 * 
 * @see net.sourceforge.mipa.predicatedetection.StrctureParser#parseStructure.
 */
package net.sourceforge.mipa.predicatedetection.ctl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.Connector;
import net.sourceforge.mipa.predicatedetection.Formula;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;
import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.StructureParser;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTL3CheckerTestNG;
import net.sourceforge.mipa.ui.predicate.PredicateDot;
import net.sourceforge.mipa.ui.predicate.PredicateTabUI;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author hengxin
 *
 * Design pattern: Singleton
 */
public class CTLParser implements IParser,IFormulaTreeTraversal
{
	private static Logger logger = Logger.getLogger(CTLParser.class);
	
	// ctlSpecification has been parsed
	private Structure ctlSpecification = null;
	private Formula formula = null;
	private List<Composite> subFormulae = null;
	
	// related to cgs(es) which will be used as atomic propositions in ctl formulae 
	private HashMap<String, ArrayList<String>> cgs2nps = null;
	
	private static CTLParser instance = null;
	private CTLParser()
	{
		
	}
	public static CTLParser getInstance()
	{
		if(instance == null)
			instance = new CTLParser();
		
		return instance;
	}
	
	/**
	 * complete the parser of ctl formula which is in the form of @see Document
	 * 
	 * @param predicate the predicate need to be parsed which is in the form of @see Document.
	 * @param partStructure the part @see Structure obtained by parsing Document partially.
	 * @param parser  <code>StructureParser</code> which contains some useful context information for parsing work
	 * 
	 * @return the whole @see Structure obtained by parsing Document completely which includes 
	 *   the CTL part.
	 */
	public Structure parseCTLPart(Document predicate,Structure partStructure,StructureParser parser)
	{
		this.beforeParse();
		
		NodeList elements = predicate.getElementsByTagName("specification");
		if(elements != null)
		{
			// for log and debug
			logger.debug("--specification");
			
			Node specification = elements.item(0);
			
			Node node = specification.getFirstChild();
			node = node.getNextSibling();
			if(node != null && node.getNodeName().equals("CTLFormula"))
			{
				logger.info("begin to parse ctl formula.");
				logger.debug("----ctlformula");
				
				// parse ctl formula recursively
				Structure ctlFormulaNode = this.parseCTLFormula(node,partStructure,parser);
				partStructure.add(ctlFormulaNode);
				
				// write UI information into predicate.dot file
				PredicateDot.getInstance().write2PredicateDotOver();
			}
		}
		
		this.ctlSpecification = partStructure;
		
		this.afterParse();
		
		return partStructure;
	}
	
	/**
	 * parse ctl formula recursively
	 * 
	 * @param element element of XML document which represents CTLFORMULA node(<code>NodeType</code>)
	 * @param partStructure the part @see Structure obtained by parsing Document partially.
	 * @param parser  <code>StructureParser</code> which contains some useful context information for parsing work
	 * 
	 * @return the <code>Structure</code>(actually, it is of type <code>Formula</code>)
	 *    represents CTLFORMULA which is parsed wholly.
	 */
	private Structure parseCTLFormula(Node element,Structure partStructure,StructureParser parser)
	{
		logger.debug("-------CTLFormula");
	
		Formula ctlFormulaNode = new Formula(NodeType.CTLFORMULA,"ctlformula");
		
		for(Node node = element.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				// it is local predicate which is regarded as atomic proposition in ctl formula.
				if(node.getNodeName().equals("cgs"))
				{
					String name = node.getAttributes().getNamedItem("name").getNodeValue();
					Structure CGS = parser.getName2CGS().get(name);
					
					logger.debug("--------------------------CGS:" + name);
					ctlFormulaNode.setNodeName(name);

					return CGS;
				}
				// it is ctl formula in the form of (CTLFormula, binary, CTLFormula) which should be parsed recursively.
				else if(node.getNodeName().equals("CTLFormula"))
				{
					// parse left sub-formula(lsf) recursively.
					Structure lsfNode = this.parseCTLFormula(node, partStructure, parser);
					
					// binary operator
					node = node.getNextSibling().getNextSibling();
					assert (node.getNodeName().equals("binary"));
					Connector binaryNode = new Connector(NodeType.BINARY,"binary");
					String operator = node.getAttributes().getNamedItem("value").getNodeValue();
					binaryNode.setOperator(operator);
						
					logger.debug("-----------------------binary:" + operator);
					
					// parse right sub-formula(rsf) recursively.
					node = node.getNextSibling().getNextSibling();
					Structure rsfNode = this.parseCTLFormula(node, partStructure, parser);
					
					// combine left sub-formula, binary operator, and right sub-formula. 
					ctlFormulaNode.setConnetor(binaryNode);
					ctlFormulaNode.add(lsfNode);
					ctlFormulaNode.add(rsfNode);
					
					// write UI information into predicate.dot file
					PredicateDot.getInstance().write2PredicateDotLink(ctlFormulaNode, lsfNode);
					PredicateDot.getInstance().write2PredicateDotLink(ctlFormulaNode, rsfNode);
				}
				// it is ctl formula in the form of (unary, CTLFormula) which should be parsed recursively.
				else if(node.getNodeName().equals("unary"))
				{
					Connector unaryNode = new Connector(NodeType.UNARY,"unary");
					String operator = node.getAttributes().getNamedItem("value").getNodeValue();
					unaryNode.setOperator(operator);
					
					logger.debug("-------------------------unary:" + operator);
					
					// parse sub-formula(sf) recursively.
					node = node.getNextSibling().getNextSibling();
					Structure sfNode = this.parseCTLFormula(node, partStructure, parser);
					
					// combine unary operator and sub-formula.
					ctlFormulaNode.setConnetor(unaryNode);
					ctlFormulaNode.add(sfNode);
					
					// write UI information into predicate.dot file
					PredicateDot.getInstance().write2PredicateDotLink(ctlFormulaNode, sfNode);
				}
				else
				{
					logger.error("The xml file of ctl predicate is wrong. Node " + node.getNodeName() + " is illegal here!");
				}
			}
		}
		
		return ctlFormulaNode;
	}
	
	/**
	 * @return ctlSpecification has been parsed
	 */
	public Structure getCTLSpecification()
	{
		return this.ctlSpecification;
	}
	
	/**
	 * get Formula type of ctlSpecification
	 * @return Formula type of ctlSpecification
	 */
	public Formula getCtlFormula()
	{
		if(this.ctlSpecification == null)
			return null;
		
		if(this.formula == null)
		{
			Structure ctlFormula = this.ctlSpecification.getChildren().get(1);
			
			if(! (ctlFormula instanceof Formula))
			{
				logger.error("Fail to recognize ctl formula from " + this.ctlSpecification);
				return null;
			}
			else
				this.formula = (Formula) ctlFormula;
		}
		
		return this.formula;
	}
	
	/**
	 * get sub-formulae of ctlSpecification(or formula)
	 * @return list of sub-formulae of ctlSpecification
	 */
	public List<Composite> getSubFormulae()
	{
		if (this.subFormulae == null)
		{
			this.subFormulae = new ArrayList<Composite>();
			
			if (this.formula == null)
				this.getCtlFormula();
			if (this.formula != null)
			{
				PostOrderTraversal pot = new PostOrderTraversal();
				pot.postOrder(this.formula, this);
			}
		}
		
		return this.subFormulae;
	}
	
	public boolean isEUSubFormula(Composite formula)
	{
		return (! CTLParser.getInstance().isLeaf(formula)) && 
				((Formula) formula).getConnetor().getNodeType() == NodeType.BINARY
			&&
			((Formula) formula).getConnetor().getOperator() == NodeType.EU;
	}
	
	public boolean isAUSubFormula(Composite formula)
	{
		return (! CTLParser.getInstance().isLeaf(formula)) && 
				((Formula) formula).getConnetor().getNodeType() == NodeType.BINARY
			&&
			((Formula) formula).getConnetor().getOperator() == NodeType.AU;
	}
	
	private void calculateCgs2NPs(/* Structure specification */)
	{
		if(ctlSpecification != null)
        {
			Structure CGSs = ctlSpecification.getChildren().get(0);
			
	        for (int i = 0; i < CGSs.getChildren().size(); i++) 
	        {
	            ArrayList<String> NPs = new ArrayList<String>();
	            Structure CGS = CGSs.getChildren().get(i);
	            String name = ((Composite) CGS).getNodeName();
	            for (int j = 0; j < CGS.getChildren().size(); j++) 
	            {
	                NPs.add(((LocalPredicate) CGS.getChildren().get(j)).getNormalProcess());
	            }
	            this.cgs2nps.put(name, NPs);
	        }
	        
	        // for debug
	        logger.info("The hashmap of cgs2nps are :" + this.cgs2nps.toString());
        }
	}
	
	public HashMap<String, ArrayList<String>> getCgs2NPs()
	{
		if(this.cgs2nps == null)
		{
			this.cgs2nps = new HashMap<String, ArrayList<String>>();
			this.calculateCgs2NPs();
		}
		
		return this.cgs2nps;
	}
	
	/**
	 * used for debug(set cgs <-> np manually)
	 * 
	 * {@link CTL3CheckerTestNG}
	 */
	public void setCgs2Nps(HashMap<String, ArrayList<String>> cgs2nps)
	{
		this.cgs2nps = cgs2nps;
	}
	/*************************** IParser ***************************/
	
	/**
	 * do nothing now
	 */
	@Override
	public void beforeParse()
	{
		
	}
	
	/**
	 * update the ui (treeviewer for predicate) when parsing is done.
	 */
	@Override
	public void afterParse()
	{
		if(PredicateTabUI.getInstance(null) != null)
			PredicateTabUI.getInstance(null).update(this.ctlSpecification);
	}
	
	
	/*************************** IFormulaTreeTraversal ***************************/

	@Override
	public boolean isLeaf(Composite node)
	{
		return node.getNodeType()  == NodeType.CGS;
	}
	
	@Override
	public void processLeaf(Composite node)
	{
		this.subFormulae.add(node);
	}
	
	@Override
	public void processNonLeaf(Composite node)
	{
		this.subFormulae.add(node);
	}
}
