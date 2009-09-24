
import java.util.*;

public class LatticeNode {
	
	protected State[] cgs;
	
	protected ArrayList<LatticeNode> next;
	
	protected ArrayList<LatticeNode> previous;
	

	public LatticeNode(State[] gs){
		cgs=new State[gs.length];
		next=new ArrayList<LatticeNode>();
		previous=new ArrayList<LatticeNode>();
	}
	

}
