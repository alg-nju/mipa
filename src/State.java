
public class State {

	protected LatticeVectorClock vc;
	
	protected String processName;
	
	//protected boolean localPredicate;
	
	public State(LatticeVectorClock lvc,String pname){
		vc.setVectorClock(lvc.getVectorClock());
		processName=pname;
	}
	
	public void setVC(LatticeVectorClock lvc){
		
		vc.setVectorClock(lvc.getVectorClock());
		
	}
	
	
	
}
