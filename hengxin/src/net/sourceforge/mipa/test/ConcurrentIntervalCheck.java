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
 * check the happen-before relation of the concurrent intervals
 * 
 * @author Tingting Hua <huatingting0820@gmail.com>
 * 
 */
public class ConcurrentIntervalCheck {

	/** store the concurrent interval information of groups of processes */
	private ArrayList<ArrayList<PhysicalTimeInterval>> queueOfConcurrentInterval;

	private int numOfChecker;

	/** store the number of happen-before relation of the concurrent interval */
	private int count;

	/** the array of concurrent checker name */
	private String[] checkerArray;

	/**
	 * constructor
	 * 
	 * @param n
	 *            the number of group
	 */
	public ConcurrentIntervalCheck(int n) {

		queueOfConcurrentInterval = new ArrayList<ArrayList<PhysicalTimeInterval>>();

		checkerArray = new String[n];

		numOfChecker = n;

		count = 0;

		for (int i = 0; i < n; i++) {
			queueOfConcurrentInterval
					.add(new ArrayList<PhysicalTimeInterval>());
		}

	}

	/**
	 * read the concurrent interval from the file
	 */
	public void read() {
		try {
			for (int i = 0; i < numOfChecker; i++) {
			    if(DEBUG) {
				System.out.println(checkerArray[i]);
			    }
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(checkerArray[i] + "interval")));
				while (br.ready()) {
					String s = br.readLine();
					String[] str = s.split(" ");
					long lo = Long.valueOf(str[1]);
					long hi = Long.valueOf(str[2]);
					queueOfConcurrentInterval.get(i).add(
							new PhysicalTimeInterval(str[0], lo, hi));
				}
			}
		} catch (Exception ex) {
			//System.out.println("read from file error " + ex);
		    ex.printStackTrace();
		}

	}

	/**
	 * compute the happen-before relation of the concurrent intervals
	 */
	public void checkSequence() {
		boolean[] flag = new boolean[numOfChecker];
		for (int i = 0; i < numOfChecker; i++) {
			flag[i] = false;
		}
		int number;
		while (true) {
			number = 0;
			while (number < numOfChecker - 1) {
				if ((queueOfConcurrentInterval.get(number).size() > 0)
						&& (queueOfConcurrentInterval.get(number + 1).size() > 0)) {
					long hi = queueOfConcurrentInterval.get(number).get(0)
							.getpTimeHi();
					long lo = queueOfConcurrentInterval.get(number + 1).get(0)
							.getpTimeLo();
					while (hi > lo) {
						queueOfConcurrentInterval.get(number + 1).remove(0);
						if (queueOfConcurrentInterval.get(number + 1).size() > 0) {
							lo = queueOfConcurrentInterval.get(number + 1).get(
									0).getpTimeLo();
						} else {
							System.out.print("queue empty!");
							return;
						}
					}
					number++;
				} else {
					System.out.print("queue empty!!");
					return;
				}
			}
			if (number == numOfChecker - 1) {
				count++;
				for (int i = 0; i < numOfChecker; i++) {
					queueOfConcurrentInterval.get(i).remove(0);
				}
			}
		}
	}

	public static void main(String args[]) {
		String checkerList = "log/Checker1,log/Checker2";
		String fileList = "log/NormalProcess0,log/NormalProcess1,log/NormalProcess2,log/NormalProcess3";

		ConcurrentIntervalExtract cie = new ConcurrentIntervalExtract(
				checkerList, fileList);
		cie.readfile();
		cie.getIntervalSequence();

		ConcurrentIntervalCheck cic = new ConcurrentIntervalCheck(cie
				.getNumOfChecker());
		cic.checkerArray = cie.getCheckerArray();
		cic.read();
		cic.checkSequence();

		System.out.println(cic.count);

	}

}
