
import java.util.*;
import java.io.*;

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
		String s="";
		for(int i=0;i<dimension;i++){
			ArrayList<State> list=new ArrayList<State>();
			list.add(globalState[i]);
			max.add(globalState[i]);
			stateSet.add(list);
			nameToID.put(process[i], new Integer(i));
			s=s+0;
		}
		
		startNode=new LatticeNode(globalState,s);
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
							String str="";
							for(int j=0;j<dimension;j++){
								int temp=stateSet.get(j).size()-stateSet.get(j).indexOf(globalState[j])-1;
								str=str+temp;
							}
							LatticeNode newnode=new LatticeNode(globalState,str);
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
				if(stateSet.get(i).size()<2){
					break;
				}
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
		String str="";
		for(int j=0;j<dimension;j++){
			int temp=stateSet.get(j).size()-stateSet.get(j).indexOf(globalState[j])-1;
			str=str+temp;
		}
		
		LatticeNode node=new LatticeNode(globalState,str);
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
		
	
	public void construct(String filename){
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			while(br.ready()){
				String s=br.readLine();
				String[] string=s.split(" ");
				String pname=string[0];
				LatticeVectorClock vc=new LatticeVectorClock(dimension);
				ArrayList<Long> clock=new ArrayList<Long>();
				for(int i=1;i<string.length;i++){
					clock.add(new Long(string[i]));
				}
				vc.setVectorClock(clock);
				State state=new State(vc,pname);
				grow(state);
			}
			
		}catch(Exception ex){
			System.out.println(ex);
		}
		
		traversal(startNode);
	}
	
	
	public void traversal(LatticeNode node){
		System.out.print(node.ID+" ");
		Iterator<LatticeNode> it=node.next.iterator();
		while(it.hasNext()){
			LatticeNode nextnode=it.next();
			traversal(nextnode);
		}
		
	}
	
	
	public static void main(String[] args){
		String s="process1,process2";
		String[] string=s.split(",");
		State[] state=new State[string.length];
		for(int i=0;i<string.length;i++){
			LatticeVectorClock vc=new LatticeVectorClock(string.length);
			vc.increment(i);
			state[i]=new State(vc,string[i]);
		}
		
		LatticeConstructor lc=new LatticeConstructor(string,state);
		
		String filename="input";
		lc.construct(filename);
	}
	

}
