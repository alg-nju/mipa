
import java.util.*;

public class LatticeNode {
	
	protected String ID;
	
	protected State[] cgs;
	
	protected ArrayList<LatticeNode> next;
	
	protected ArrayList<LatticeNode> previous;
	

	public LatticeNode(State[] gs,String s){
		ID=s;
		cgs=new State[gs.length];
		for(int i=0;i<gs.length;i++){
			cgs[i]=gs[i];
		}
		next=new ArrayList<LatticeNode>();
		previous=new ArrayList<LatticeNode>();
	}
	

}
