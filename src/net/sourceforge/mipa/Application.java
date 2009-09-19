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
package net.sourceforge.mipa;

import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * MIPA application.
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Application extends AbstractApplication {
    
    int count;
    
    PrintWriter out;
    /**
     * <code>Application</code> construction.
     * 
     * @param fileName
     *            a file contains predicate
     */
    public Application(String fileName) {
        super(fileName);
        count = 0;
        try {
            out = new PrintWriter("log/application_count");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public synchronized void callback(String value) throws RemoteException {
        //TODO implements application logic
        System.out.println("Result returns:");
        System.out.println("\t" + value);
        count++;
        System.out.println("count is " + count);
        try {
            out.println(count);
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application app = new Application("config/predicate/predicate_scp.xml");
        app.start("config/config.xml");
    }
}
