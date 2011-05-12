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
package net.sourceforge.mipa.tools;

import java.io.PrintWriter;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ExponentDelayData {
    
    public static void main(String[] args) {
        String fileName = "data/delay_temp";
        long count = 1000000000 / 10;
        PrintWriter output = null;
        double lambda = 0.1 / 100;
        
        try {
            output = new PrintWriter(fileName);
            for(long i = 0; i < count; i++) {
                output.println((int) ExponentDistribution.exponent(lambda));
            }
            output.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
