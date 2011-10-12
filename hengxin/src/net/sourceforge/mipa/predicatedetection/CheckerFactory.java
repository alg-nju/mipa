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

import org.apache.log4j.Logger;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.CheckMode;
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.CTLLatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.scp.SCPLatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.sequence.SequenceLatticeChecker;
import net.sourceforge.mipa.predicatedetection.normal.cada.CADAChecker;
import net.sourceforge.mipa.predicatedetection.lattice.wcp.WCPLatticeChecker;
import net.sourceforge.mipa.predicatedetection.normal.oga.OGASubChecker;
import net.sourceforge.mipa.predicatedetection.normal.oga.OGATopChecker;
import net.sourceforge.mipa.predicatedetection.normal.scp.SCPChecker;
import net.sourceforge.mipa.predicatedetection.normal.wcp.WCPChecker;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class CheckerFactory {

	private static Logger logger = Logger.getLogger(CheckerFactory.class);
	
    private static Naming server;
    static {
        try {
            server = MIPAResource.getNamingServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param level
     *            the level of checker in OGA, 0 represents top.
     */
    public static void createOGAChecker(String callback, String predicateID, String checkerName,
            String[] fathers, String[] children, int level) {
        try {
            ResultCallback application = null;
            if (level == 0) {
                application = (ResultCallback) server.lookup(callback);
                OGATopChecker checker = new OGATopChecker(application, predicateID,
                        checkerName, children);
                Communication checkerStub = (Communication) UnicastRemoteObject
                        .exportObject(checker, 0);
                server.bind(checkerName, checkerStub);

            } else if (level == 1) {
                OGASubChecker checker = new OGASubChecker(application, predicateID,
                        checkerName, fathers, children);
                Communication checkerStub = (Communication) UnicastRemoteObject
                        .exportObject(checker, 0);
                server.bind(checkerName, checkerStub);

            } else {
                System.out.println("invalid level.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSCPChecker(String callback, String predicateID, String checkerName,
            String[] normalProcesses) {
        try {
            ResultCallback application = (ResultCallback) server
                    .lookup(callback);

            CheckMode checkMode = MIPAResource.getCheckMode();
            if (checkMode == CheckMode.NORMAL) {
                // NORMAL mode code puts here!
                SCPChecker checker = new SCPChecker(application, predicateID, 
                		checkerName, normalProcesses);
                Communication checkerStub = (Communication) UnicastRemoteObject
                        .exportObject(checker, 0);
                server.bind(checkerName, checkerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
            } else if (checkMode == CheckMode.LATTICE) {
                SCPLatticeChecker checker = new SCPLatticeChecker(application, predicateID,
                        checkerName, normalProcesses);
                Communication checkerStub = (Communication) UnicastRemoteObject
                        .exportObject(checker, 0);
                server.bind(checkerName, checkerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }

            } else {
                System.out.println("Check Mode " + checkMode
                        + "has not been defined.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createWCPChecker(String callback, String predicateID, String checkerName,
            String[] normalProcesses) {
        try {
            ResultCallback application = (ResultCallback) server
                    .lookup(callback);

            CheckMode checkMode = MIPAResource.getCheckMode();
            if (checkMode == CheckMode.NORMAL) {
                // NORMAL mode code puts here!
                WCPChecker wcpChecker = new WCPChecker(application, predicateID,
                        checkerName, normalProcesses);
                Communication wcpCheckerStub = (Communication) UnicastRemoteObject
                        .exportObject(wcpChecker, 0);
                server.bind(checkerName, wcpCheckerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
            } else if (checkMode == CheckMode.LATTICE) {
                // LATTICE mode code puts here!
                WCPLatticeChecker wcpLatticeChecker = new WCPLatticeChecker(
                        application, predicateID, checkerName, normalProcesses);
                Communication wcpLatticecheckerStub = (Communication) UnicastRemoteObject
                        .exportObject(wcpLatticeChecker, 0);
                server.bind(checkerName, wcpLatticecheckerStub);
            } else {
                System.out.println("Check Mode " + checkMode
                        + "has not been defined.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createCADAChecker(String callback, String predicateID, String checkerName,
            String[] normalProcesses) {
        // TODO Auto-generated method stub
        try {
            ResultCallback application = (ResultCallback) server
                    .lookup(callback);

            CheckMode checkMode = MIPAResource.getCheckMode();
            if (checkMode == CheckMode.NORMAL) {
                // NORMAL mode code puts here!
                CADAChecker checker = new CADAChecker(application, predicateID, checkerName,
                        normalProcesses);
                Communication checkerStub = (Communication) UnicastRemoteObject
                        .exportObject(checker, 0);
                server.bind(checkerName, checkerStub);
                if (DEBUG) {
                    System.out.println("binding checker " + checkerName);
                }
            } else if (checkMode == CheckMode.LATTICE) {

            } else {
                System.out.println("Check Mode " + checkMode
                        + "has not been defined.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSequenceChecker(String callback, String predicateID, 
            String checkerName, String[] normalProcesses, Structure specification) {
        // TODO Auto-generated method stub
        try {
            ResultCallback application = (ResultCallback) server
                    .lookup(callback);

            CheckMode checkMode = MIPAResource.getCheckMode();
            if (checkMode == CheckMode.NORMAL) {
                // NORMAL mode code puts here!
            } else if (checkMode == CheckMode.LATTICE) {
                SequenceLatticeChecker sequenceLatticeChecker = new SequenceLatticeChecker(
                        application, predicateID, checkerName, normalProcesses,specification);
                Communication sequenceLatticecheckerStub = (Communication) UnicastRemoteObject
                        .exportObject(sequenceLatticeChecker, 0);
                server.bind(checkerName, sequenceLatticecheckerStub);
            } else {
                System.out.println("Check Mode " + checkMode
                        + "has not been defined.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author hengxin(hengxin0912@gmail.com)
     * 
     * @param callback application
     * @param predicateID id of predicate to be checked
     * @param checkerName name of check process
     * @param normalProcesses normal processes
     * @param specification internal data structure of predicate to be checked
     */
    public static void createCTLChecker(String callback, String predicateID,
    		String checkerName, String[] normalProcesses, Structure specification)
    {
		try
		{
			ResultCallback application = null;
			if(callback != null)
			{
				application = (ResultCallback) server.lookup(callback);
			}

			CheckMode checkMode = MIPAResource.getCheckMode();
			if (checkMode == CheckMode.NORMAL)
			{
				// NORMAL mode code puts here!
			} else if (checkMode == CheckMode.LATTICE)
			{
				CTLLatticeChecker ctlLatticeChecker = new CTLLatticeChecker(
						application, predicateID, checkerName, normalProcesses,
						specification);
				Communication ctlLatticeCheckerStub = (Communication) UnicastRemoteObject
						.exportObject(ctlLatticeChecker, 0);
				server.bind(checkerName, ctlLatticeCheckerStub);
			} else
			{
				logger.error("The check mode of " + checkMode + " is not defined.");
			}

		} catch (Exception e)
		{
			logger.fatal(e.getMessage());
			e.printStackTrace();
		}
    }
    /*
     * public static void newChecker(String callback, String checkerName,
     * String[] normalProcesses, PredicateType type) { try { ResultCallback
     * application = (ResultCallback) server .lookup(callback);
     * 
     * CheckMode checkMode = MIPAResource.getCheckMode(); if(checkMode ==
     * CheckMode.NORMAL) { // NORMAL mode code puts here! switch (type) { case
     * SCP: SCPChecker checker = new SCPChecker(application, checkerName,
     * normalProcesses); Communication checkerStub = (Communication)
     * UnicastRemoteObject .exportObject(checker, 0); server.bind(checkerName,
     * checkerStub); if (DEBUG) { System.out.println("binding checker " +
     * checkerName); } break; case LP:
     * 
     * break; case WCP: WCPChecker wcpChecker = new WCPChecker(application,
     * checkerName, normalProcesses); Communication wcpCheckerStub =
     * (Communication) UnicastRemoteObject .exportObject(wcpChecker, 0);
     * server.bind(checkerName, wcpCheckerStub); if (DEBUG) {
     * System.out.println("binding checker " + checkerName); } break; default:
     * System.out.println("Type " + type + " has not been defined."); } } else
     * if(checkMode == CheckMode.LATTICE) { // LATTICE mode code puts here!
     * switch(type) { case WCP: WCPLatticeChecker wcpLatticeChecker = new
     * WCPLatticeChecker(application, checkerName, normalProcesses);
     * Communication wcpLatticecheckerStub = (Communication) UnicastRemoteObject
     * .exportObject(wcpLatticeChecker, 0); server.bind(checkerName,
     * wcpLatticecheckerStub); break; case SCP:
     * 
     * break; case LP:
     * 
     * break; default: System.out.println("Type " + type +
     * " has not been defined."); } } else { System.out.println("Check Mode " +
     * checkMode + "has not been defined."); }
     * 
     * } catch (Exception e) { e.printStackTrace(); } }
     */
}
