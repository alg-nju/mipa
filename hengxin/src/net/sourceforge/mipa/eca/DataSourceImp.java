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
package net.sourceforge.mipa.eca;

import static config.Debug.DEBUG;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mipa.eca.exception.ConditionNotFoundException;

/**
 * Data Source implementation.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class DataSourceImp implements DataSource {

    /** map event name to <code>Condition</code> list */
    private Map<String, ArrayList<Condition>> map;

    public DataSourceImp() {
        map = new HashMap<String, ArrayList<Condition>>();
    }

    @Override
    public synchronized void attach(Condition condition, String eventName)
                                                             throws RemoteException {

        if(DEBUG) {
            System.out.println("attaching condtion to data source...");
        }
        
        if (map.containsKey(eventName) == false) {
            if(DEBUG) {
                System.out.println("New event in data source...");
            }
            ArrayList<Condition> list = new ArrayList<Condition>();
            list.add(condition);
            map.put(eventName, list);
        } else {
            if(DEBUG) {
                System.out.println("already contained event in data source...");
            }
            ArrayList<Condition> list = map.get(eventName);
            list.add(condition);
        }
    }

    @Override
    public synchronized void detach(Condition condition, String eventName)
                                                             throws RemoteException,
                                                             ConditionNotFoundException {
        if (map.containsKey(eventName) == true) {
            ArrayList<Condition> list = map.get(eventName);

            if (list.remove(condition) == false) {
                throw new ConditionNotFoundException(
                                                     "Condition not in event list");
            }
        } else {
            throw new ConditionNotFoundException("Can't find event name");
        }
    }

    //FIXME: This method may not need synchronized prefix
    @Override
    public synchronized void notifyCondition(String eventName, String[] values)
                                                               throws RemoteException {
        if (map.containsKey(eventName) == false)
            //throw new EventNotFoundException("Event not found.");
            return;

        ArrayList<Condition> list = map.get(eventName);
        for (int i = 0; i < list.size(); i++) {
            Condition con = list.get(i);
            con.update(eventName, values);
        }
    }

    @Override
    public synchronized void update(String eventName, String[] values)
                                                                   throws RemoteException {

        notifyCondition(eventName, values);
    }
}
