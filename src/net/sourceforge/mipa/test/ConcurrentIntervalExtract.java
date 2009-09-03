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

import java.util.*;
import java.io.*;

public class ConcurrentIntervalExtract {

	private int numOfChecker;

	private int[] numOfFile;

	private ArrayList<ArrayList<String>> fileArrayList;

	private ArrayList<ArrayList<PhysicalTimeInterval>> sequence;

	/**
	 * 
	 * @param n
	 */
	public ConcurrentIntervalExtract(int n) {

		numOfChecker = n;
		numOfFile = new int[n];
		fileArrayList = new ArrayList<ArrayList<String>>();
		sequence = new ArrayList<ArrayList<PhysicalTimeInterval>>();

		for (int i = 0; i < n; i++) {
			numOfFile[i] = 0;
			fileArrayList.add(new ArrayList<String>());
			sequence.add(new ArrayList<PhysicalTimeInterval>());
		}

	}

	/**
	 * separate the file according to checker information and store the result
	 * in fileArrayList
	 * 
	 * @param checkerArrayList
	 *            the name list of checker
	 * @param fileList
	 *            the name list of all normal process
	 */
	public void partition(ArrayList<String> checkerArrayList, String fileList) {

		try {
			int k = 0;
			for (int i = 0; i < fileList.length(); i++) {
				if (fileList.charAt(i) == ',') {
					String s = fileList.substring(k, i);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(s)));
					String name = br.readLine();
					for (int j = 0; j < numOfChecker; j++) {
						if (name.compareTo(checkerArrayList.get(j)) == 0) {
							fileArrayList.get(j).add(s);
							numOfFile[j]++;
						}
					}
					k = i + 1;
				}
			}
			String s = fileList.substring(k);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(s)));
			String name = br.readLine();
			for (int j = 0; j < numOfChecker; j++) {
				if (name.compareTo(checkerArrayList.get(j)) == 0) {
					fileArrayList.get(j).add(s);
					numOfFile[j]++;
				}
			}
		} catch (Exception ex) {
			System.out.println("file read error! " + ex);
		}

	}

	/**
	 * 
	 * @param fileList
	 * @param num
	 * @param array
	 */
	public void read(ArrayList<String> fileList, int num,
			ArrayList<ArrayList<PhysicalTimeInterval>> array) {
		try {
			for (int i = 0; i < num; i++) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileList.get(i))));
				while (br.ready()) {
					String s = br.readLine();
					String id = "";
					long lo = 0;
					long hi = 0;
					for (int j = 0; j < s.length(); j++) {
						if (s.charAt(j) == ' ') {
							id = s.substring(0, j);
							s = s.substring(j + 1);
							for (int k = 0; k < s.length(); k++) {
								if (s.charAt(k) == ' ') {
									String s1 = s.substring(0, k);
									String s2 = s.substring(k + 1);
									int t = 0;
									while (t < s1.length()) {
										lo = lo * 10 + s1.charAt(t) - 48;
										t++;
									}
									t = 0;
									while (t < s2.length()) {
										hi = hi * 10 + s2.charAt(t) - 48;
										t++;
									}
								}
							}
							break;
						}
					}
					array.get(i).add(new PhysicalTimeInterval(id, lo, hi));
				}
			}
		} catch (Exception ex) {
			System.out.println("read from file error " + ex);
		}

	}

	/**
	 * comtpute the overlap interval sequence from the queue and store in
	 * sequence
	 * 
	 * @param queue
	 * @param number
	 * @param id
	 */
	public void getIntersection(
			ArrayList<ArrayList<PhysicalTimeInterval>> queue, int number, int id) {
		String[] currentId = new String[number];
		for (int i = 0; i < number; i++) {
			currentId[i] = null;
		}
		boolean result = false;
		int index = 0;
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
						if (!(queue.get(index).get(0).getpTimeLo() < queue.get(
								i).get(0).getpTimeHi())) {
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
					queue.get(0).remove(0);
					currentId[0] = null;
					for (int i = 1; i < number; i++) {
						if (largestLo < queue.get(i).get(0).getpTimeLo()) {
							largestLo = queue.get(i).get(0).getpTimeLo();
						}
						if (smallestHi > queue.get(i).get(0).getpTimeHi()) {
							smallestHi = queue.get(i).get(0).getpTimeHi();
						}
						queue.get(i).remove(0);
						currentId[i] = null;
					}
					sequence.get(id)
							.add(
									new PhysicalTimeInterval(" ", largestLo,
											smallestHi));
				}
			}
			index = (index + 1) % number;
		}
	}

	/**
	 * 
	 */
	public void getIntervalSequence() {
		ArrayList<ArrayList<PhysicalTimeInterval>> queue = new ArrayList<ArrayList<PhysicalTimeInterval>>();
		for (int i = 0; i < numOfChecker; i++) {
			queue.add(new ArrayList<PhysicalTimeInterval>());
		}
		for (int i = 0; i < numOfChecker; i++) {
			read(fileArrayList.get(i), numOfFile[i], queue);
			getIntersection(queue, numOfFile[i], i);
		}

	}

	public void output(ArrayList<String> checkerArrayList) {
		try {
			for (int i = 0; i < numOfChecker; i++) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(checkerArrayList.get(i))));
				int k = 1;
				while (sequence.get(i).size() > 0) {
					bw.write(k + " " + sequence.get(i).get(0).getpTimeLo()
							+ " " + sequence.get(i).get(0).getpTimeHi() + "\n");
					sequence.get(i).remove(0);
					k++;
					bw.flush();
					System.out.println("aa"+i);
				}
				
				bw.close();
			}
		} catch (Exception ex) {
			System.out.println("output to file error " + ex);
		}
	}

	public static void main(String args[]) {
		String checkerList = "Checker1,Checker2";
		String fileList = "NormalProcess0,NormalProcess1,NormalProcess2,NormalProcess3";
		ArrayList<String> checkerArrayList = new ArrayList<String>();
		int k = 0, n = 1;
		for (int i = 0; i < checkerList.length(); i++) {
			if (checkerList.charAt(i) == ',') {
				n++;
				checkerArrayList.add(checkerList.substring(k, i));
				k = i + 1;
			}
		}
		checkerArrayList.add(checkerList.substring(k));
		ConcurrentIntervalExtract cie = new ConcurrentIntervalExtract(n);

		cie.partition(checkerArrayList, fileList);

		cie.getIntervalSequence();

		cie.output(checkerArrayList);

	}

}
