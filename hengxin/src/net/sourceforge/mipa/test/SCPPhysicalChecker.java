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
 * Calculate overlap interval.
 * 
 * @author Tingting Hua <huatingting0820@gmail.com>
 */
public class SCPPhysicalChecker {

	private ArrayList<ArrayList<PhysicalTimeInterval>> queue;

	private String[] currentId;

	private int numOfNormalProcess;

	private int result;

	/**
	 * @param n
	 *            the numOfNormalProcess to set
	 */
	public SCPPhysicalChecker(int n) {

		result = 0;

		numOfNormalProcess = n;

		queue = new ArrayList<ArrayList<PhysicalTimeInterval>>();

		currentId = new String[numOfNormalProcess];

		for (int i = 0; i < numOfNormalProcess; i++) {
			queue.add(new ArrayList<PhysicalTimeInterval>());
			currentId[i] = null;
		}
	}

	/**
	 * read the physical time interval of normal process from the file and store
	 * in queue.
	 * 
	 * @param fileList
	 *            normal process file's name list
	 */
	public void read(String[] fileList) {
		try {
			for (int i = 0; i < numOfNormalProcess; i++) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileList[i])));
				br.readLine();
				while (br.ready()) {
					String s=br.readLine();
					String[] str = s.split(" ");
					long lo = Long.valueOf(str[1]);
					long hi = Long.valueOf(str[2]);
					queue.get(i).add(new PhysicalTimeInterval(str[0], lo, hi));
				}
			}
		} catch (Exception ex) {
			System.out.println("read from file error " + ex);
		}

	}

	/**
	 * check out the overlap intervals and output the number.
	 * 
	 * @param outFileName
	 *            output file name
	 */
	public void check(String outFileName) {
		boolean flag = false;
		int index = 0;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFileName)));
			while (true) {
				if ((currentId[index] == null) && (queue.get(index).size() > 0)) {
					currentId[index] = queue.get(index).get(0).getIntervalID();
					for (int i = 0; i < numOfNormalProcess; i++) {
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
					flag = true;
					for (int i = 0; i < numOfNormalProcess; i++) {
						if (currentId[i] == null) {
							flag = false;
						}
					}
					if (flag) {
						result++;
						for (int i = 0; i < numOfNormalProcess; i++) {
							// String end = i + 1 == numOfNormalProcess ? "\n" :
							// " ";
							// bw.write(queue.get(i).get(0).getIntervalID()+" "+queue.get(i).get(0).getpTimeLo()+" "+queue.get(i).get(0).getpTimeHi()+end);
							currentId[i] = null;
							queue.get(i).remove(0);
						}
						// bw.flush();
					}
				}
				if ((currentId[index] == null)
						&& (queue.get(index).size() == 0)) {
					System.out.println(result);
					bw.write(new Integer(result).toString());
					bw.close();
					return;
				}
				index = (index + 1) % numOfNormalProcess;
			}
		} catch (Exception ex) {
			System.out.println("output to file error " + ex);
		}

	}

	public static void main(String args[]) {
		String normalProcessFileList = "log/NormalProcess0,log/NormalProcess1";

		String[] s = normalProcessFileList.split(",");
		SCPPhysicalChecker spc = new SCPPhysicalChecker(s.length);
		spc.read(s);
		spc.check("log/result.txt");
	}

}
