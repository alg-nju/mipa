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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class FileDelayMessageDispatcher extends GenericMessageDispatcher {

    /** delay in millisecond */
    ArrayList<Long> delays;

    int indicator;

    /**
     * 
     */
    public FileDelayMessageDispatcher(String delayFile) {
        super();
        indicator = 0;
        delays = new ArrayList<Long>();
        try {
            BufferedReader reader = new BufferedReader(
                                               new FileReader(delayFile));
            String str;
            while ((str = reader.readLine()) != null) {
                delays.add(new Long(Long.parseLong(str)));
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
        int delay = delays.get(indicator).intValue();
        indicator++;
        indicator %= delays.size();
        message.setDispatchTime(message.getReachTime() + delay);
    }

}
