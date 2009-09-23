
import java.util.*;

public class LatticeConstructor {
	
	private int dimension;
	
	private State[] globalState;
	
	private ArrayList<ArrayList<State>> stateSet;
	
	private ArrayList<State> max;
	
	private Map<String, Integer> nameToID;
	
	
	public LatticeConstructor(){
		
	}

	public void generate(ArrayList<State> list){
		Stack<State> code= new Stack<State>();
		int i=1;
		while(i>0){
			if(){
				
			}else{
				
			}
		}
	}
	
	public void updateMax(State s){
		
		int processID=nameToID.get(s.processName).intValue();
		
		ArrayList<State> pred=new ArrayList<State>();
		for(int i=0;i<dimension;i++){
			if(i!=processID){
				State state=stateSet.get(i).get(1);
				if(state.vc.nolessThan(s.vc)){
					pred.add(stateSet.get(i).get(0));
				}
			}else {
				pred.add(stateSet.get(i).get(1));
			}
		}
		
		Iterator<State> iter=pred.iterator();
		while(iter.hasNext()){
			max.remove(iter.next());
		}
		
		//max.add(0, s);
	}
	
	public void grow(State s){
		int processID=nameToID.get(s.processName).intValue();
		globalState[processID]=s;
		stateSet.get(processID).add(0, s);
		
		updateMax(s);
		ArrayList<State> sList=new ArrayList<State>();
		Iterator<State> iter=max.iterator();
		while(iter.hasNext()){
			sList.add(iter.next());
		}
		max.add(0, s);
		
		generate(sList);
	}
		
	
	public void construct(){
		
	}
	

}
