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

import java.util.ArrayList;

import net.sourceforge.mipa.eca.ECAManager;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.Atom;
import net.sourceforge.mipa.predicatedetection.LocalPredicate;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Broker {
    
    private ContextModeling contextModeling;
    
    private ContextRetrieving contextRetrieving;
    
    
    public Broker(ContextModeling modeling, ContextRetrieving retrieving) {
        contextModeling = modeling;
        contextRetrieving = retrieving;
    }
    
    
    public void registerLocalPredicate(LocalPredicate lp, String normalProcessId, Group g) {
        try {
                ArrayList<Atom> arrayList = lp.getAtoms();
                String ecaManagerID = contextRetrieving.getEntityId(contextModeling.getLowContext(arrayList.get(0).getName()));
                for(int i=0;i<arrayList.size();i++)
                {
                    Atom atom = arrayList.get(i);
                    String lowContext = contextModeling.getLowContext(atom.getName());
                    String ecaManagerIDNew = contextRetrieving.getEntityId(lowContext);
                    if(!ecaManagerIDNew.equals(ecaManagerID))
                    {
                        System.out.println("The sensors "+arrayList.get(0).getName()
                                                         +" and "
                                                         +arrayList.get(i).getName()
                                                         +" are in different ECA.");
                    }
                    // reset the name of local predicate from high level context to low level context.
                    atom.setName(lowContext);
                    atom.setValueType(contextModeling.getValueType(lowContext));
                }

                Naming server = MIPAResource.getNamingServer();
                ECAManager ecaManager = (ECAManager) server.lookup(ecaManagerID);

                System.out.println("find eca manager successfully.");
                System.out.println(ecaManagerID);
                ecaManager.registerLocalPredicate(lp, normalProcessId, g);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
