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
package net.sourceforge.mipa.predicatedetection.lattice;

import static config.Debug.DEBUG;

import java.util.ArrayList;

import net.sourceforge.mipa.predicatedetection.VectorClock;

/**
 * 
 * @author tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class LatticeVectorClock extends VectorClock {

	private static final long serialVersionUID = -367397072945972758L;

	public LatticeVectorClock(int number) {
		super(number);
	}

	public LatticeVectorClock(VectorClock clock) {
		super(clock);
	}

	@Override
	public void update(VectorClock timestamp) {
		// TODO Auto-generated method stub
		ArrayList<Long> clock = timestamp.getVectorClock();

		assert (vectorClock.size() == clock.size());

		for (int i = 0; i < vectorClock.size(); i++) {
			long clockValue = vectorClock.get(i).longValue();
			long msgClockValue = clock.get(i).longValue();

			Long newValue = new Long(clockValue > msgClockValue ? clockValue
					: msgClockValue);
			vectorClock.set(i, newValue);
		}
	}

	@Override
	public void increment(int id) {
		assert (id < getVectorClock().size());

		Long clock = getVectorClock().get(id);
		getVectorClock().set(id, new Long(clock.longValue() + 1));

		if (DEBUG) {
			System.out.print("Normal Process " + id + ":\n\t");
			ArrayList<Long> list = getVectorClock();
			for (int i = 0; i < list.size(); i++) {
				System.out.print(list.get(i) + ", ");
			}
			System.out.println();
		}
	}

	@Override
	public boolean notLessThan(VectorClock timestamp) {
		ArrayList<Long> right = timestamp.getVectorClock();
		ArrayList<Long> left = vectorClock;

		assert (right.size() == left.size());
		boolean result = true, first = false;
		for (int i = 0; i < right.size(); i++) {
			long rightValue = right.get(i).longValue();
			long leftValue = left.get(i).longValue();
			if (leftValue > rightValue)
				result = false;
			else if (leftValue < rightValue)
				first = true;
		}
		return !(result && first);
	}

	public boolean lessThan(VectorClock timestamp) {
		ArrayList<Long> right = timestamp.getVectorClock();
		ArrayList<Long> left = vectorClock;

		assert (right.size() == left.size());
		boolean result = true, first = true;
		for (int i = 0; i < right.size(); i++) {
			long rightValue = right.get(i).longValue();
			long leftValue = left.get(i).longValue();
			if (leftValue > rightValue)
				result = false;
			else if (leftValue < rightValue)
				first = false;
		}
		if (result && first) {
			return false;
		} else {
			return result;
		}
	}

	public String toString() {
		String string = "";
		for (int i = 0; i < vectorClock.size(); i++) {
			String clock = String.valueOf(vectorClock.get(i).longValue());
			string = string + clock + " ";
		}
		return string.trim();
	}

	public boolean lessOrEqual(VectorClock timestamp) {
		ArrayList<Long> right = timestamp.getVectorClock();
		ArrayList<Long> left = vectorClock;

		assert (right.size() == left.size());
		boolean result = true;
		for (int i = 0; i < right.size(); i++) {
			long rightValue = right.get(i).longValue();
			long leftValue = left.get(i).longValue();
			if (leftValue > rightValue)
				result = false;
		}
		return result;
	}
}
