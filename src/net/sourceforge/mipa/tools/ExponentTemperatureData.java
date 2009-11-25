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
public class ExponentTemperatureData {

    public static void main(String[] args) {
        int timeGap = 200;
        
        String outputFile = "data/temperature_temp";
        String outputFile_1 = "data/temperature_1_temp";
        PrintWriter output = null;
        PrintWriter output_1 = null;
        double intervalLambda = 1.0 / 10;
        double intervalGapLambda = 1.0 / 5;
        
        // double threshold = 30;
        // The two values should be auto adaptive.
        double falseValue = 25;
        double trueValue = 35;
        
        try {
            output = new PrintWriter(outputFile);
            output_1 = new PrintWriter(outputFile_1);
            output.println(timeGap);
            output_1.println(timeGap);
            
            int count = 2000;
            for(int i = 0; i < count; i++) {
                double gapLength = ExponentDistribution.exponent(intervalGapLambda);
                for(int j = 0; j < (int) gapLength; j++) 
                    output.println(falseValue);
                double intervalLength = ExponentDistribution.exponent(intervalLambda);
                for(int j = 0; j < (int) intervalLength; j++) 
                    output.println(trueValue);
            }
            output.flush();
            output.close();
            for(int i = 0; i < count; i++) {
                double intervalLength = ExponentDistribution.exponent(intervalGapLambda);
                for(int j = 0; j < (int) intervalLength; j++) {
                    output_1.println(trueValue);
                }
                double gapLength = ExponentDistribution.exponent(intervalLambda);
                for(int j = 0; j < (int) gapLength; j++) {
                    output_1.println(falseValue);
                }
            }
            output_1.flush();
            output_1.close();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
