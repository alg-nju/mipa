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

import static config.Config.EXPERIMENT;
import static config.Debug.DEBUG;

import java.io.File;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sourceforge.mipa.components.BrokerInterface;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.eca.DataSource;
import net.sourceforge.mipa.eca.DataSourceImp;
import net.sourceforge.mipa.eca.ECAManager;
import net.sourceforge.mipa.eca.ECAManagerImp;
import net.sourceforge.mipa.eca.SensorAgent;
import net.sourceforge.mipa.eca.SensorPlugin;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.tools.GCRunner;

/**
 * initialize ECA mechanism.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class ECAInitialize {
    /**
     * initialize method.
     */
    public void initialize() {
        if(EXPERIMENT) {
            GCRunner r = new GCRunner();
            Thread t = new Thread(r);
            t.start();
        }

        String namingAddress = MIPAResource.getNamingAddress();
        try {
            Naming server = (Naming) java.rmi.Naming.lookup(namingAddress
                                                                + "Naming");

            IDManager idManager = (IDManager) server.lookup("IDManager");
            //ContextRegister contextRegister 
            //                    = (ContextRegister) server
            //                                             .lookup("ContextRegister");
            BrokerInterface broker = (BrokerInterface) MIPAResource.getBroker();
            
            // binding data source
            String dataSourceId = idManager.getID(Catalog.DataSource);
            DataSourceImp dataSource = new DataSourceImp();
            DataSource dataSourceStub 
                            = (DataSource) UnicastRemoteObject
                                                 .exportObject(dataSource,
                                                                0);
            server.bind(dataSourceId, dataSourceStub);

            String ecaManagerId = idManager.getID(Catalog.ECAManager);
            ECAManagerImp ecaManager = new ECAManagerImp(broker,
                                                                 dataSource,
                                                                 ecaManagerId);
            // ecaManager.setECAManagerName(ecaManagerId);

            ECAManager ecaManagerStub 
                            = (ECAManager) UnicastRemoteObject
                                                  .exportObject(ecaManager,
                                                                0);
            server.bind(ecaManagerId, ecaManagerStub);

            
            SensorPlugin sensorPlugin = new SensorPlugin(dataSourceStub);

            // add resources to list for registering resources.
            ArrayList<SensorAgent> resources = new ArrayList<SensorAgent>();
            
            String[] files = new File(config.Config.SENSORS_CONFIG_DIRECTORY).list();
            for(int i = 0; i < files.length; i++) {
                String pattern = ".*xml";
                if(Pattern.matches(pattern, files[i])) {
                    System.out.println(files[i]);
                    resources.add(sensorPlugin.load(config.Config.SENSORS_CONFIG_DIRECTORY + files[i]));
                }
            }
            
            if (DEBUG) {
                System.out.println("resources value: ");
                for (int i = 0; i < resources.size(); i++) {
                    System.out.println(resources.get(i).getName());
                }
                System.out.println();
            }

            ecaManager.registerResources(resources);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new ECAInitialize().initialize();
    }
}
