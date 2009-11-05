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

import static config.Debug.DEBUG;

import java.rmi.server.UnicastRemoteObject;

import net.sourceforge.mipa.ResultCallback;
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.oga.OGASubChecker;
import net.sourceforge.mipa.predicatedetection.oga.OGATopChecker;
import net.sourceforge.mipa.predicatedetection.scp.SCPChecker;
import net.sourceforge.mipa.predicatedetection.wcp.WCPChecker;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class CheckerFactory {

    private static Naming server;
    static {
        try {
            server = MIPAResource.getNamingServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FIXME checker factory should contain different predicate type checker
    // generating functions.
    /**
     * 
     * @param level the level of checker in OGA, 0 represents top.
     */
    public static void ogaChecker(String callback, 
                                     String checkerName,
                                     String[] fathers, 
                                     String[] children, 
                                     int level) {
        if (DEBUG) {
            System.out.println("CHeckerFactory: ogaChecker\n\tlevel: " + level);
        }
        try {
            ResultCallback application = null;
            if (level == 0) {
                application = (ResultCallback) server.lookup(callback);
                OGATopChecker checker = new OGATopChecker(application,
                                                          checkerName, children);
                Communication checkerStub 
                                    = (Communication) UnicastRemoteObject
                                                            .exportObject(checker,
                                                                           0);
                server.bind(checkerName, checkerStub);

                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
            } else if (level == 1) {
                OGASubChecker checker = new OGASubChecker(application,
                                                          checkerName, fathers,
                                                          children);
                Communication checkerStub 
                                    = (Communication) UnicastRemoteObject
                                                            .exportObject(checker,
                                                                           0);
                server.bind(checkerName, checkerStub);

                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
            } else {
                System.out.println("invalid level.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FIXME this function should split to scpChecker, wcpChecker, LPChecker,
    // etc.
    public static void newChecker(String callback, String checkerName,
                                  String[] normalProcesses,
                                  PredicateType type) {
        if (DEBUG) {
            System.out.println("CheckerFactory: newChecker");
        }
        try {
            ResultCallback application = (ResultCallback) server
                                                                .lookup(callback);

            switch (type) {
            case SCP:
                SCPChecker checker = new SCPChecker(application, checkerName,
                                                    normalProcesses);
                Communication checkerStub 
                                    = (Communication) UnicastRemoteObject
                                                            .exportObject(checker,
                                                                           0);
                server.bind(checkerName, checkerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
                break;
            case LP:

            case WCP:
                WCPChecker wcpChecker = new WCPChecker(application, checkerName,
                                                    normalProcesses);
                Communication wcpCheckerStub 
                                    = (Communication) UnicastRemoteObject
                                                            .exportObject(wcpChecker,
                                                                           0);
                server.bind(checkerName, wcpCheckerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
                break;
            default:
                System.out.println("Type has not been defined.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
