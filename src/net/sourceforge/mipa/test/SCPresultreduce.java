package net.sourceforge.mipa.test;

import static config.Config.LOG_DIRECTORY;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class SCPresultreduce {
	
	public static void main(String[] args){
		String s1="11111";
		String s2="11111";
		try {
			PrintWriter out = new PrintWriter(LOG_DIRECTORY + "/SCP_re.log");
			BufferedReader br=new BufferedReader(new FileReader(LOG_DIRECTORY + "/SCP.log"));
			br.readLine();
			while(br.ready()){
				String s=br.readLine();
				String[] str=s.split(" ");
				/*for(int i=0;i<str.length;i++){
					out.print(str[i]+",");
					
				}
				out.println("");*/
				if((s1.compareTo(str[8])!=0)&&(s2.compareTo(str[9])!=0)){
					s1=str[8];
					s2=str[9];
					out.println(s);
					out.flush();
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
