/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the term of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.mipa.test;

import java.util.ArrayList;
import java.io.*;

/**
 * 
 * @author Tingting Hua <huatingting0820@163.com>
 */
public class PhysicalTimeCheck {
	
	private ArrayList<ArrayList<PhysicalTimeInterval>> queue;
	
	private String[] currentId;
	
	private int numOfNormalProcess;
	 /**
     * @param n the numOfNormalProcess to set
     */
	public PhysicalTimeCheck(int n){
		numOfNormalProcess=n;
		
		queue = new ArrayList<ArrayList<PhysicalTimeInterval>>();
		
		currentId=new String[numOfNormalProcess];
		
	    for (int i = 0; i < numOfNormalProcess; i++) {
	    	 queue.add(new ArrayList<PhysicalTimeInterval>());
	    	 currentId[i]=null;
	    }
	}
	
	/**
	 * @param fileList normal process file name list
     * @read read the physical time interval of normal process from the file and store in queue
     */
	public void read(ArrayList<String> fileList) {
		try {
			for(int i=0;i<numOfNormalProcess;i++) {
				BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileList.get(i))));
				while(br.ready()) {
					String s=br.readLine();
					String id="";
					long lo=0;
					long hi=0;
					for(int j=0;j<s.length();j++) {
						if(s.charAt(j)==' ') {
							id=s.substring(0, j);
							s=s.substring(j+1);
							for(int k=0;k<s.length();k++) {
								if(s.charAt(k)==' ') {
									String s1=s.substring(0,k);
									String s2=s.substring(k+1);
									int t=0;
									while(t<s1.length()) {
										lo=lo*10+s1.charAt(t)-48;
										t++;
									}
									t=0;
									while(t<s2.length()) {
										hi=hi*10+s2.charAt(t)-48;
										t++;
									}
								}
							}
							break;
						}
					}
					queue.get(i).add(new PhysicalTimeInterval(id,lo,hi));
				}
			}
		}catch(Exception ex) {
			System.out.println("read from file error "+ex);
		}
		
	}
	
	/**
	 * @param outFileName output file name
     * @check check out the overlap interval and output to file
     */
	public void check(String outFileName) {
		boolean result=false;
		int index=0;
		try{
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName)));
			while(true) {
				if((currentId[index]==null)&&(queue.get(index).size()>0)) {
					currentId[index]=queue.get(index).get(0).getIntervalID();
					for(int i=0;i<numOfNormalProcess;i++) {
						if((i!=index)&&(currentId[i]!=null)) {
							if(!(queue.get(i).get(0).getpTimeLo()<queue.get(index).get(0).getpTimeHi())) {
								currentId[index]=null;
								queue.get(index).remove(0);
								break;
							}
							if(!(queue.get(index).get(0).getpTimeLo()<queue.get(i).get(0).getpTimeHi())) {
								currentId[i]=null;
								queue.get(i).remove(0);
								continue;
							}
						}
					}
					result=true;
					for(int i=0;i<numOfNormalProcess;i++) {
						if(currentId[i]==null) {
							result=false;
						}
					}
					if(result) {
						long hi=queue.get(0).get(0).getpTimeHi();
						int id=0;
						for(int i=0;i<numOfNormalProcess;i++) {
							bw.write(queue.get(i).get(0).getIntervalID()+" "+queue.get(i).get(0).getpTimeLo()+" "+queue.get(i).get(0).getpTimeHi()+" ");
							currentId[i]=null;
							if(hi>queue.get(i).get(0).getpTimeHi()){ 
								hi=queue.get(i).get(0).getpTimeHi();
								id=i;
							}
						}
						queue.get(id).remove(0);
						bw.newLine();
						bw.flush();
					}
				}
				
				if((currentId[index]==null)&&(queue.get(index).size()==0)) {
					bw.close();
					return;
				}
				index=(index+1)%numOfNormalProcess;
			}
		}catch(Exception ex){
			System.out.println("output to file error "+ex);
		}
		
	}
	
	public static void main(String args[]) {
		String normalProcessFileList="NormalProcess0.txt,NormalProcess1.txt";
		
		int k=0,n=1;
		ArrayList<String> fileList=new ArrayList<String>();
		for(int i=0;i<normalProcessFileList.length();i++) {
			if(normalProcessFileList.charAt(i)==',') {
				n++;
				fileList.add(normalProcessFileList.substring(k, i));
				k=i+1;
			}
		}
		fileList.add(normalProcessFileList.substring(k));
		
		PhysicalTimeCheck ptc=new PhysicalTimeCheck(n);
		ptc.read(fileList);
		ptc.check("result.txt");
	}

}
