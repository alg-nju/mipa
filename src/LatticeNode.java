
import java.util.*;

public class LatticeNode {
	
	private State[] cgs;
	
	private ArrayList<LatticeNode> next;
	

	public LatticeNode(State[] gs){
		State[] cgs=new State[gs.length];
		next=new ArrayList<LatticeNode>();
	}
	

}
