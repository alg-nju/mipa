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
package net.sourceforge.mipa.components;

import static config.Debug.DEBUG;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import net.sourceforge.mipa.tools.ExponentDistribution;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ExponentDelayMessageDispatcher extends GenericMessageDispatcher {

    PrintWriter out;
    
    double lambda;
    
    public ExponentDelayMessageDispatcher() {
        super();

        try {
            out = new PrintWriter("log/MessageDispatcherDelay.log");
            BufferedReader rd 
                = new BufferedReader(
                            new FileReader("config/message_dispatcher_exponent"));
            lambda = Double.parseDouble(rd.readLine());
            
            if(DEBUG) {
                System.out.println("message dispatcher lambda is " + lambda);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.mipa.components.GenericMessageDispatcher#addDispatchTime
     * (net.sourceforge.mipa.components.Message)
     */
    @Override
    public void addDispatchTime(Message message) {
        //double lambda = 0.01;
        int delay = (int) ExponentDistribution.exponent(lambda);
        out.println(delay);
        out.flush();
        message.setDispatchTime(message.getReachTime() + delay);

    }

}
