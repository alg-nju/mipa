package net.sourceforge.mipa.test;

import static config.Config.LOG_DIRECTORY;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class SCPPhysicalTimeCheckerResultreduce {

	public static void main(String[] args){
		String s1="000";
		String s2="000";
		try {
			PrintWriter out = new PrintWriter(LOG_DIRECTORY + "/result_re.log");
			BufferedReader br=new BufferedReader(new FileReader(LOG_DIRECTORY + "/result.txt"));
			
			while(br.ready()){
				String s=br.readLine();
				String[] str=s.split(" ");
				/*for(int i=0;i<str.length;i++){
					out.print(str[i]+",");
					
				}
				out.println("");*/
				if((s1.compareTo(str[0])==0)||(s2.compareTo(str[3])==0)){
					
					
				}else{
					s1=str[0];
					s2=str[3];
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
