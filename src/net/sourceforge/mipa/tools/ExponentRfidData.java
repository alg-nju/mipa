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
import java.util.ArrayList;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ExponentRfidData {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ArrayList<String> tags = new ArrayList<String>();
        
        int timeGap = 200;
        String concernedTag = "tag_00001";
        String outputFile = "data/RFID_temp";
        String outputFile_1 = "data/RFID_1_temp";
        PrintWriter output = null;
        PrintWriter output_1 = null;
        double intervalLambda = 1.0 / 10;
        double intervalGapLambda = 1.0 / 5;
        
        
        tags.add(concernedTag);
        
        try {
            output = new PrintWriter(outputFile);
            output_1 = new PrintWriter(outputFile_1);
            output.println(timeGap);
            output.println(tags.size());
            output_1.println(timeGap);
            output_1.println(tags.size());
            
            for(int i = 0; i < tags.size(); i++) {
                output.println(tags.get(i));
                output_1.println(tags.get(i));
            }
            for(int i = 0; i < tags.size(); i++) {
                int count = 2000;
                String stream = "";
                for(int j = 0; j < count; j++) {
                    double gapLength = ExponentDistribution.exponent(intervalGapLambda);
                    for(int k = 0; k < (int) gapLength; k++) {
                        stream += "0";
                    }
                    double intervalLength = ExponentDistribution.exponent(intervalLambda);
                    for(int k = 0; k < (int) intervalLength; k++) {
                        stream += "1";
                    }
                    //System.out.println(j);
                }
                output.println(stream);
                
                stream = "";
                for(int j = 0; j < count; j++) {
                    double intervalLength = ExponentDistribution.exponent(intervalGapLambda);
                    for(int k = 0; k < (int) intervalLength; k++) {
                        stream += "1";
                    }
                    double gapLength = ExponentDistribution.exponent(intervalLambda);
                    for(int k = 0; k < (int) gapLength; k++) {
                        stream += "0";
                    }
                }
                output_1.println(stream);
            }
            output.flush();
            output.close();
            
            output_1.flush();
            output_1.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
