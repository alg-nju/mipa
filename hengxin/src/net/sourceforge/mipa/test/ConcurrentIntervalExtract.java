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

import static config.Debug.DEBUG;
import java.util.*;
import java.io.*;

/**
 * extract the concurrent interval and output to file
 * 
 * @author Tingting Hua <huatingting0820@gmail.com>
 *
 */
public class ConcurrentIntervalExtract {

	/** store the interval information of all normal processes*/
	private ArrayList<ArrayList<PhysicalTimeInterval>> queue;

	/** the array of concurrent checker name */
	private String[] checkerArray;

	/** the array of normal process name*/
	private String[] fileArray;

	/** number of concurrent checker*/
	private int numOfChecker;

	/** element is the number of normal process of each concurrent checker*/
	private int[] numOfFile;

	/** element is the ID of normal process of each concurrent checker*/
	private ArrayList<ArrayList<Integer>> fileArrayList;

	/** store the concurrent interval information of groups of processes*/
	private ArrayList<ArrayList<PhysicalTimeInterval>> sequence;

	/**
	 * constructor
	 * 
	 * @param checkerList the string list of checker name
	 * @param fileList the string list of normal process name
	 */
	public ConcurrentIntervalExtract(String checkerList, String fileList) {

		checkerArray = checkerList.split(",");

		fileArray = fileList.split(",");

		numOfChecker = checkerArray.length;

		numOfFile = new int[checkerArray.length];

		queue = new ArrayList<ArrayList<PhysicalTimeInterval>>();

		fileArrayList = new ArrayList<ArrayList<Integer>>();

		sequence = new ArrayList<ArrayList<PhysicalTimeInterval>>();

		for (int i = 0; i < checkerArray.length; i++) {
			numOfFile[i] = 0;
			fileArrayList.add(new ArrayList<Integer>());
			sequence.add(new ArrayList<PhysicalTimeInterval>());
		}
		for (int i = 0; i < fileArray.length; i++) {
			queue.add(new ArrayList<PhysicalTimeInterval>());
		}

	}

	/**
	 * read the interval information of all normal processes form the file
	 * 
	 */
	public void readfile() {
		try {
			for (int i = 0; i < fileArray.length; i++) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileArray[i])));
				if (DEBUG) {
					System.out.println("open file " + fileArray[i]);
				}
				
				String checkerName = br.readLine();
				for (int j = 0; j < numOfChecker; j++) {
				    String[] checkerActualNames = checkerArray[j].split("/");
				    String checkerActualName = checkerActualNames[checkerActualNames.length - 1];
				    
					if (checkerName.compareTo(checkerActualName) == 0) {
						fileArrayList.get(j).add(new Integer(i));
						numOfFile[j]++;
					}
				}
				
				String s = null;
				while ((s = br.readLine()) != null) {
					if (DEBUG) {
						//System.out.println(s);
					}
					String[] str = s.split(" ");
					long lo = Long.valueOf(str[1]);
					long hi = Long.valueOf(str[2]);
					queue.get(i).add(new PhysicalTimeInterval(str[0], lo, hi));
				}
				if (DEBUG) {
					System.out.println("success " + i + " process");
				}
				br.close();
			}
		} catch (Exception ex) {
			//System.out.println("read file error! " + ex);
		    ex.printStackTrace();
		}
	}

	/**
	 * compute the concurrent interval from a group of normal processes
	 * 
	 * @param queue the interval information of the group
	 * @param number the number of normal process of the group
	 * @param id the ID of the group
	 */
	public void getIntersection(
			ArrayList<ArrayList<PhysicalTimeInterval>> queue, int number, int id) {
		String[] currentId = new String[number];
		for (int i = 0; i < number; i++) {
			currentId[i] = null;
		}
		boolean result = false;
		int index = 0;
		try {
		    if(DEBUG) {
			System.out.println(checkerArray[id]);
		    }
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(checkerArray[id])));
			BufferedWriter bwc = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(checkerArray[id] + "interval")));
			while (queue.get(index).size() > 0) {
				if ((currentId[index] == null) && (queue.get(index).size() > 0)) {
					currentId[index] = queue.get(index).get(0).getIntervalID();
					for (int i = 0; i < number; i++) {
						if ((i != index) && (currentId[i] != null)) {
							if (!(queue.get(i).get(0).getpTimeLo() < queue.get(
									index).get(0).getpTimeHi())) {
								currentId[index] = null;
								queue.get(index).remove(0);
								break;
							}
							if (!(queue.get(index).get(0).getpTimeLo() < queue
									.get(i).get(0).getpTimeHi())) {
								currentId[i] = null;
								queue.get(i).remove(0);
								continue;
							}
						}
					}
					result = true;
					for (int i = 0; i < number; i++) {
						if (currentId[i] == null) {
							result = false;
						}
					}

					if (result) {
						long largestLo = queue.get(0).get(0).getpTimeLo();
						long smallestHi = queue.get(0).get(0).getpTimeHi();
						
						for (int i = 1; i < number; i++) {
							if (largestLo < queue.get(i).get(0).getpTimeLo()) {
								largestLo = queue.get(i).get(0).getpTimeLo();
							}
							if (smallestHi > queue.get(i).get(0).getpTimeHi()) {
								smallestHi = queue.get(i).get(0).getpTimeHi();
							}
						}
						
						if(smallestHi-largestLo>0){
							sequence.get(id).add(
									new PhysicalTimeInterval(" ", largestLo,
											smallestHi));
							bwc.write("id " + largestLo + " " + smallestHi + "\n");
							bwc.flush();
							
							for (int i = 0; i < number; i++) {
								String end = i + 1 == number ? "\n" : " ";
								bw.write(queue.get(i).get(0).getIntervalID() + " "
										+ queue.get(i).get(0).getpTimeLo() + " "
										+ queue.get(i).get(0).getpTimeHi() + end);
								
							}
						}
						
						/*sequence.get(id).add(
								new PhysicalTimeInterval(" ", largestLo,
										smallestHi));
						bwc.write("id " + largestLo + " " + smallestHi + "\n");
						bwc.flush();
						*/
						for (int i = 0; i < number; i++) {
							queue.get(i).remove(0);
							currentId[i] = null;
						}
						bw.flush();
					}
				}
				index = (index + 1) % number;
			}
			bwc.close();
			bw.close();
		} catch (Exception ex) {
			//System.out.println("output file error! " + ex);
		    ex.printStackTrace();
		}

	}

	/**
	 * compute the concurrent interval from all group of normal processes
	 */
	public void getIntervalSequence() {

		for (int i = 0; i < numOfChecker; i++) {
			ArrayList<ArrayList<PhysicalTimeInterval>> que = new ArrayList<ArrayList<PhysicalTimeInterval>>();
			for (int j = 0; j < numOfFile[i]; j++) {
				que.add(queue.get(fileArrayList.get(i).get(j).intValue()));
			}
			getIntersection(que, numOfFile[i], i);
		}

	}

	/**
	 * 
	 * @return the numOfChecker
	 */
	public int getNumOfChecker() {
		return numOfChecker;
	}

	/**
	 * 
	 * @return the checkerArray
	 */
	public String[] getCheckerArray() {
		return checkerArray;
	}
	
	/*
	 * public static void main(String args[]) { String checkerList =
	 * "Checker1,Checker2"; String fileList =
	 * "NormalProcess0,NormalProcess1,NormalProcess2,NormalProcess3";
	 * 
	 * ConcurrentIntervalExtract cie = new
	 * ConcurrentIntervalExtract(checkerList,fileList);
	 * 
	 * cie.readfile();
	 * 
	 * cie.getIntervalSequence();
	 * 
	 * 
	 * }
	 */

}
