package net.sourceforge.mipa.test;

import static config.Config.LOG_DIRECTORY;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class SCPLatticePhysicalcompare {

	public static void main(String[] args){
		boolean flag=true;
		try {
			PrintWriter out = new PrintWriter(LOG_DIRECTORY + "/SCP_Lattice_Physical.log");
			//BufferedReader br_p=new BufferedReader(new FileReader(LOG_DIRECTORY + "/result_re.log"));
			//BufferedReader br_l=new BufferedReader(new FileReader(LOG_DIRECTORY + "/SCP_re.log"));
			
			BufferedReader br_p=new BufferedReader(new FileReader(LOG_DIRECTORY + "/result.txt"));
			BufferedReader br_l=new BufferedReader(new FileReader(LOG_DIRECTORY + "/SCP.log"));
			br_l.readLine();
			
			ArrayList<String> arrayp=new ArrayList<String>();
			while(br_p.ready()){
				String sp=br_p.readLine();
				String[] strp=sp.split(" ");
				arrayp.add(strp[0]+" "+strp[3]);
			}
			
			ArrayList<String> arrayl=new ArrayList<String>();
			while(br_l.ready()){
				String sl=br_l.readLine();
				String[] strl=sl.split(" ");
				arrayl.add(strl[8]+" "+strl[9]);
			}
			
			Iterator<String> iter= arrayp.iterator();
			while(iter.hasNext()){
				String s=iter.next();
				Iterator<String> it=arrayl.iterator();
				while(it.hasNext()){
					String ss=it.next();
					if(s.compareTo(ss)==0){
						out.println(s);
						out.flush();
						int i=arrayp.indexOf(s);
						arrayp.set(i, s+ "   " + i);
						int j=arrayl.indexOf(ss);
						arrayl.set(j, ss+ "   " + j);
						break;
					}
				}
			}
			
			Iterator<String> iterp= arrayp.iterator();
			out.println("Physical:");
			while(iterp.hasNext()){
				String s=iterp.next();
				out.println(s);
			}
			out.flush();
			
			Iterator<String> iterl= arrayl.iterator();
			out.println("Lattice:");
			while(iterl.hasNext()){
				String s=iterl.next();
				out.println(s);
			}
			out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
