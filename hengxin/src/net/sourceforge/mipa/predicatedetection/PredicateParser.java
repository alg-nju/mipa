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
package net.sourceforge.mipa.predicatedetection;

import static config.Config.EXPERIMENT;
import static config.Debug.DEBUG;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import net.sourceforge.mipa.components.GroupManager;
import net.sourceforge.mipa.test.TimeCost;
import net.sourceforge.mipa.test.TimeInfo;

import org.w3c.dom.Document;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class PredicateParser implements PredicateParserMethod {

    /** structure of predicate parser reference */
    private StructureParser structureParser;

    /** checker logic parser reference */
    // private CheckerParser checkerParser;
    
    private GroupManager groupManager;

    private PredicateValidation predicateValidation;

    /**
     * default construction.
     */
    public PredicateParser(GroupManager groupManager) {
        structureParser = new StructureParser();
        predicateValidation = new PredicateValidation();
        this.groupManager = groupManager;
    }

    public synchronized String parsePredicate(String applicationName,
                                                Document predicate)
                                                    throws RemoteException {

        if (DEBUG) {
            System.out.println("parsing predicate...");
        }
        
        if (EXPERIMENT) {
            TimeInfo t = new TimeInfo();
            TimeCost.put(applicationName, t);
            t.item_1_begin = System.nanoTime();
        }
        //only for debug
        //groupManager.setCallback(applicationName);

        PredicateType type = PredicateIdentify.predicateIdentify(predicate);

        Structure predicateStructure = structureParser
                                                      .parseStructure(predicate);
        
        if(EXPERIMENT) {
            TimeInfo t = TimeCost.get(applicationName);
            t.item_1_end = System.nanoTime();
            
            t.item_2_begin = System.nanoTime();
        }
        
        if (! predicateValidation.validate(predicateStructure)) {
            return null;
        }
        
        String predicateID = groupManager.createGroups(predicateStructure, type, applicationName);
        
        if(EXPERIMENT) {
            TimeInfo t = TimeCost.get(applicationName);
            t.item_2_end = System.nanoTime();
        }
        
        if(EXPERIMENT) {
            TimeInfo t = TimeCost.get(applicationName);
            try {
                PrintWriter out = new PrintWriter(new FileWriter("log/time_cost", true), true);
                out.println((t.item_1_end - t.item_1_begin) + " " + (t.item_2_end - t.item_2_begin) + " " + (t.item_2_end - t.item_1_begin));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        return predicateID;
    }
}
