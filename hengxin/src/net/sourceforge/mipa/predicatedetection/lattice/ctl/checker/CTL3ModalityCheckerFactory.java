package net.sourceforge.mipa.predicatedetection.lattice.ctl.checker;

import net.sourceforge.mipa.predicatedetection.NodeType;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;

import org.apache.log4j.Logger;

/**
 * Factory for CTL3 modality checker
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class CTL3ModalityCheckerFactory
{
	private static Logger logger = Logger.getLogger(CTL3ModalityCheckerFactory.class);
	
	/**
	 * // FIXME: if null, throws exception! 
	 * 
	 * @param connector  	the type of ctl(ctl3) modality
	 * @param specification the formula needs to be checked
	 * @param startNode 	the start node of lattice to be checked on
	 * @param endNode   	the end node of lattice to be checked on
	 * @return
	 */
	public static CTLCheckerFramework buildModalityChecker(NodeType connector,
			Structure specification, AbstractLatticeNode startNode,
			AbstractLatticeNode endNode)
	{
		switch(connector)
		{
		case NOT:
			return (new CTL3NotChecker(specification, startNode, endNode));
		case EX:
//			return (new CTL3EXChecker(specification, startNode, endNode));
			break;
		case EU:
			return (new CTL3EUChecker(specification, startNode, endNode));
		case EF:
//			return (new CTL3EFChecker(specification, startNode, endNode));
			break;
		case EG:
//			return (new CTL3EGChecker(specification, startNode, endNode));
			break;
		case AX:
//			return (new CTL3AXChecker(specification, startNode, endNode));
			break;
		case AU:
			return (new CTL3AUChecker(specification, startNode, endNode));
		case AF:
//			return (new CTL3AFChecker(specification, startNode, endNode));
			break;
		case AG:
//			return (new CTL3AGChecker(specification, startNode, endNode));
			break;
		default:
			System.err.println("Modality [" + connector + "] is not defined.");
			logger.error("Modality [" + connector + "] is not defined.");
		}
		
		return null;
	}		
}
