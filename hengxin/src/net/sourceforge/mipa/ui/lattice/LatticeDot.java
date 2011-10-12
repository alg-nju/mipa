package net.sourceforge.mipa.ui.lattice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.mipa.predicatedetection.Composite;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTL3CheckerTestNG;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeNode;
import net.sourceforge.mipa.util.algorithm.bfs.BFSFramework;
import net.sourceforge.mipa.util.algorithm.bfs.IBFS;
import net.sourceforge.mipa.util.ui.graphviz.GraphViz;

/**
 * graphviz
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class LatticeDot implements IBFS
{
	public static LatticeDot dot = null;
	
	private LatticeDot()
	{
		try
		{
			dotLatticeWriter = new PrintWriter(dotLatticeFile);
			dotCheckWriter = new PrintWriter(dotCheckFile);
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		dotLatticeWriter.println("digraph Lattice {");
		dotCheckWriter.println("digraph Check {");
	}
	
	public static LatticeDot getInstance()
	{
		if(dot == null)
			dot = new LatticeDot();
		
		return dot;
	}
	
	// write into .dot file
	private PrintWriter dotLatticeWriter = null; 
	private PrintWriter dotCheckWriter = null;
	private String dotLatticeFile = "log4j/graphviz/lattice" + CTL3CheckerTestNG.postfix + ".dot";
	private String dotCheckFile = "log4j/graphviz/check" + CTL3CheckerTestNG.postfix + ".dot";
	
	private String type = "png";
//	private String latticeGraphFile = "log4j/graphviz/lattice." + type;
	private String checkGraphFile = "log4j/graphviz/check" + CTL3CheckerTestNG.postfix + "." + type;
	
	private Set<String> definedVirtualNodeSet = new HashSet<String>();
	
	/**
	 * write the VIRTUAL NODE @param id into lattice.dot file
	 * 
	 * @param id id of non-lattice node
	 */
	public void write2LatticeDotVirtualNode(String id)
	{
		this.dotLatticeWriter.println(this.getLatticeVirtualNodeDotString(id) + "\t [shape=box,style=filled];");
		this.dotLatticeWriter.flush();
	}
	/**
	 * write the LINK between @param preNode and @param postNode into lattice.dot file
	 * 
	 * @param preNode previous node
	 * @param postNode post node
	 * @param inTree is the link in LE(linear extension)
	 */
	public void write2LatticeDotLink(AbstractLatticeNode preNode, AbstractLatticeNode postNode, boolean inLinearExtension)
	{
		if(inLinearExtension)
			this.dotLatticeWriter.println(this.getLatticeNodeDotString(preNode) + "\t -> \t" + this.getLatticeNodeDotString(postNode) + "[color = red]" + ";");
		else
			this.dotLatticeWriter.println(this.getLatticeNodeDotString(preNode) + "\t -> \t" + this.getLatticeNodeDotString(postNode) + ";");
		
		this.dotLatticeWriter.flush();
	}
	
	/**
	 * write the VIRTUAL LINK between @param aln and @param alnId into lattce.dot file
	 * 
	 * @param aln lattice node
	 * @param alnId id of non-lattice node
	 */
	public void write2LatticeDotVirtualLink(AbstractLatticeNode aln, String alnId)
	{
		this.dotLatticeWriter.println(this.getLatticeNodeDotString(aln) + "\t -> \t" + this.getLatticeVirtualNodeDotString(alnId) + "\t [style = dotted];");
		this.dotLatticeWriter.flush();
	}
	
	public void write2LatticeDotVirtualLink(String preId, String postId)
	{
		this.dotLatticeWriter.println(this.getLatticeVirtualNodeDotString(preId) + "\t -> \t" + this.getLatticeVirtualNodeDotString(postId) + "\t [style = dotted];");
		this.dotLatticeWriter.flush();
	}
	
	public void write2LatticeDotVirtualLink(String alnId, AbstractLatticeNode aln)
	{
		this.dotLatticeWriter.println(this.getLatticeVirtualNodeDotString(alnId) + "\t -> \t" + this.getLatticeNodeDotString(aln) + "\t [style = dotted];");
		this.dotLatticeWriter.flush();
	}
	/**
	 * get the string representing the lattice node
	 * 
	 * @param aln lattice node
	 * @return string which conforms to the syntax of .dot file
	 */
	private String getLatticeNodeDotString(AbstractLatticeNode aln)
	{
		String[] id = aln.getID();
		StringBuilder sb = new StringBuilder();
		sb.append("Node_");
		
		sb.append(this.getIdDotString(id));
		
		return sb.toString();
	}
	
	/**
	 * get the string representing the non-lattice node
	 * 
	 * @param id id of non-lattice node
	 * @return string which conforms to the syntax of .dot file
	 */
	private String getLatticeVirtualNodeDotString(String id)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Non_");
		
		sb.append(id);
		
		return sb.toString();
	}
	
	/**
	 * write the CHECKING information into checking.dot file 
	 * @param aln start lattice node
	 */
	public void write2CheckDot(AbstractLatticeNode root)
	{
		BFSFramework bfs = new BFSFramework(this);
		bfs.breadthFirstSearchRev(root);
		
		this.dotCheckWriter.println('}');
		this.dotCheckWriter.flush();
		
		this.dotCheckWriter.close();
		
		this.drawGraph2File(dotCheckFile,checkGraphFile);
	}
	
	private String getCheckDotString(AbstractLatticeNode aln)
	{
		StringBuilder sb = new StringBuilder();
		
		if(aln instanceof CTLLatticeNode)
		{
			List<Composite> satSubFormulae = ((CTLLatticeNode) aln).getSatSubFormulae();
			List<Composite> falSubFormulae = ((CTLLatticeNode) aln).getFalSubFormulae();
			
			sb.append('\"');
			
			sb.append(this.getLatticeNodeDotString(aln));
			sb.append("\\nSat_");
			
			for(Composite subFormula : satSubFormulae)
			{
				sb.append(subFormula.toString()).append('_');
			}
			
			sb.append("\\nFal_");
			
			for(Composite subFormula : falSubFormulae)
			{
				sb.append(subFormula.toString()).append('_');
			}
			
			sb.append('\"');
		}
		
		return sb.toString();
	}

	@Override
	public boolean toVisit(AbstractLatticeNode perNode)
	{
		return true;
	}

	@Override
	public void processNode(AbstractLatticeNode node)
	{
		
	}

	@Override
	public void processLink(AbstractLatticeNode preNode,
			AbstractLatticeNode node)
	{
		String preNodeDotString = this.getCheckDotString(preNode);
		String postNodeDotString = this.getCheckDotString(node);
		
		this.dotCheckWriter.println(preNodeDotString + "\t -> \t" + postNodeDotString + ";");
		this.dotCheckWriter.flush();
	}

	private String getIdDotString(String[] id)
	{
		StringBuilder sb = new StringBuilder();
		
		for(String localId : id)
		{
			sb.append(localId).append('_');
		}
		
		return sb.toString();
	}

	public boolean isDefinedVirtualNode(String preNodeId)
	{
		return this.definedVirtualNodeSet.contains(preNodeId);
	}

	public void add2DefinedVirtualNode(String preNodeId)
	{
		this.definedVirtualNodeSet.add(preNodeId);
	}
	
	/**
	 * call graphviz to draw graph and store it
	 * make use of third-party java api for graphviz
	 */
	
	
	private void drawGraph2File(String dotSourceFile,String toGraphFile)
	{
		GraphViz gv = new GraphViz();
		gv.writeGraphToFile(gv.get_img_stream(new File(dotSourceFile), this.type), toGraphFile);
	}
}
