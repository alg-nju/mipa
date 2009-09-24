
import java.util.*;

public class LatticeConstructor {
	
	private int dimension;
	
	private State[] globalState;
	
	private ArrayList<ArrayList<State>> stateSet;
	
	private ArrayList<State> max;
	
	private Map<String, Integer> nameToID;
	
	private LatticeNode startNode;
	
	private LatticeNode currentNode;
	
	
	public LatticeConstructor(String[] process,State[] state){
		dimension=process.length;
		globalState=state;
		stateSet=new ArrayList<ArrayList<State>>();
		max=new ArrayList<State>();
		nameToID=new HashMap<String, Integer>();
		
		for(int i=0;i<dimension;i++){
			ArrayList<State> list=new ArrayList<State>();
			list.add(globalState[i]);
			stateSet.add(list);
			nameToID.put(process[i], new Integer(i));
		}
		
		startNode=new LatticeNode(globalState);
		currentNode=startNode;
		
	}

	public boolean compare(LatticeNode node1,LatticeNode node2){
		int position=-1;
		boolean b=true;
		for(int i=0;i<dimension;i++){
			if(node1.cgs[i]!=node2.cgs[i]){
				if(position==-1){
					position=i;
				}else{
					b=false;
					break;
				}
			}
		}
		if(b&&(position>-1)){
			b=false;
			for(int j=0;j<stateSet.get(position).size()-1;j++){
				if((node1.cgs[position]==stateSet.get(position).get(j))&&(node2.cgs[position]==stateSet.get(position).get(j+1))){
					b=true;
				}
			}
		}else{
			b=false;
		}
		return b;
	}

	public void generate(ArrayList<State> list){
		Stack<State> code= new Stack<State>();
		Stack<LatticeNode> stack=new Stack<LatticeNode>();
		Stack<Integer> point=new Stack<Integer>();
		point.push(new Integer(list.size()));
		
		while(!point.empty()){
			int value=point.pop().intValue();
			int i=list.size()-value;
			if(i<list.size()){
				State st=list.get(i);
				code.push(st);
				stack.push(currentNode);
				
				//get new_s
				int processID=nameToID.get(st.processName).intValue();
				ArrayList<State> stateList= stateSet.get(processID);
				Iterator<State> iter=stateList.iterator();
				while(iter.hasNext()){
					State s=iter.next();
					if(s==st){
						if(iter.hasNext()){
							State news=iter.next();
							
							globalState[processID]=news;
							LatticeNode newnode=new LatticeNode(globalState);
							currentNode.previous.add(newnode);
							newnode.next.add(currentNode);
							for(int j=0;j<currentNode.previous.size()-1;j++){
								LatticeNode pnode=currentNode.previous.get(j);
								for(int k=0;k<pnode.previous.size();k++){
									LatticeNode ppnode=pnode.previous.get(k);
									if(compare(ppnode,newnode)){
										ppnode.next.add(newnode);
										newnode.previous.add(ppnode);
									}
								}
							}
							currentNode=newnode;
							
							boolean flag=true;
							if(iter.hasNext()){
								State comps=iter.next();
								for(int j=0;j<dimension;j++){
									if((j!=processID)&&(comps.vc.lessThan(globalState[j].vc))){
										flag=false;
									}
								}
							}else{
								flag=false;
							}
							if(flag){
								list.add(i+1, news);
								point.push(value-1);
								point.push(value);
							}else{
								point.push(0);
							}
							
						}else{
							//???
							System.out.println("error! no previous state.");
							return;
						}
						break;
					}
				}
			}else{
				State s=code.pop();
				globalState[nameToID.get(s.processName).intValue()]=s;
				currentNode=stack.pop();
			}
		}
	}
	
	public void updateMax(State s){
		
		int processID=nameToID.get(s.processName).intValue();
		
		ArrayList<State> pred=new ArrayList<State>();
		for(int i=0;i<dimension;i++){
			if(i!=processID){
				State state=stateSet.get(i).get(1);
				if(state.vc.lessThan(s.vc)){
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
		
		LatticeNode node=new LatticeNode(globalState);
		currentNode.next.add(node);
		node.previous.add(currentNode);
		currentNode=node;
		
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
