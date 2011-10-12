package net.sourceforge.mipa.ui.predicate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTL3CheckerTestNG;
import net.sourceforge.mipa.util.ui.graphviz.GraphViz;

public class PredicateDot
{
	public static PredicateDot dot = null;
	
	private PredicateDot()
	{
		try
		{
			dotPredicateWriter = new PrintWriter(dotPredicateFile);
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		dotPredicateWriter.println("digraph Predicate {");
	}
	
	public static PredicateDot getInstance()
	{
		if(dot == null)
			dot = new PredicateDot();
		
		return dot;
	}
	
	// write into .dot file
	private PrintWriter dotPredicateWriter = null; 
	private String dotPredicateFile = "log4j/graphviz/predicate" + CTL3CheckerTestNG.postfix + ".dot";
	
	private String type = "png";
	private String predicateGraphFile = "log4j/graphviz/predicate" + CTL3CheckerTestNG.postfix + "." + type;
	
	/**
	 * write link between @param formula and @param subFormula into .dot file
	 * 
	 * @param formula formula
	 * @param subFormula subformula of @param formula
	 */
	public void write2PredicateDotLink(Structure formula, Structure subFormula)
	{
		this.dotPredicateWriter.println("\"" + formula + "\"" + " -> " + "\"" + subFormula + "\"" + ";");
		this.dotPredicateWriter.flush();
	}
	
	/**
	 * write EOF (end of file)
	 * 
	 * completes the '{' with '}'
	 */
	public void write2PredicateDotOver()
	{
		this.dotPredicateWriter.println("}");
		this.dotPredicateWriter.close();
		
		this.drawGraph2File(dotPredicateFile,predicateGraphFile);
	}
	
	/**
	 * call graphviz to draw graph and store it
	 * make use of third-party java api for graphviz
	 */
	public void drawGraph2File(String dotSourceFile,String toGraphFile)
	{
		GraphViz gv = new GraphViz();
		
		gv.writeGraphToFile(gv.get_img_stream(new File(dotSourceFile), this.type), toGraphFile);
	}
}
