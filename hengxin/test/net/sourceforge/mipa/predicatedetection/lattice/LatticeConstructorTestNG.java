package net.sourceforge.mipa.predicatedetection.lattice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.mipa.MIPAInitialize;
import net.sourceforge.mipa.components.Communication;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.naming.NamingService;
import net.sourceforge.mipa.predicatedetection.CheckerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * test for {@link LatticeChecker}
 * @author hengxin
 *
 */
public class LatticeConstructorTestNG /* implements ResultCallback */
{
	private Naming server = null;
	private String[] normalProcesses = null;
	private int dimension = 0;
	private String appId = null;
	private String predicateID = null;
	private String checker = null;

	/**
	 * create and provide checking messages to drive the lattice constructor
	 * 
	 * @return list of checking messages
	 */
	private List<Message> messageDataProvider()
	{
		List<Message> messageList = new ArrayList<Message>();
		
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader("test/messages/messages0.txt"));
			String line = null;
			while((line = in.readLine()) != null)
			{
				Message msg = new Message();
				
				msg.setReceiverID(checker);
				msg.setType(MessageType.Detection);
				
				// lattice vector clock
				LatticeVectorClock lvc = new LatticeVectorClock(this.dimension);
				
				ArrayList<Long> clock = new ArrayList<Long>();
				StringTokenizer tokenizer = new StringTokenizer(line);
				
				// sender id
				int sendId = Integer.parseInt(tokenizer.nextToken());
				msg.setSenderID(normalProcesses[sendId]);
				System.out.println("Sender id: " + msg.getSenderID());
				
//				// vc
//				while(tokenizer.hasMoreTokens())
//				{
//					clock.add(Long.parseLong(tokenizer.nextToken()));
//				}
				
				for(int i=0;i<dimension;i++)
				{
					clock.add(Long.parseLong(tokenizer.nextToken()));
				}

				lvc.setVectorClock(clock);
				
				boolean predicate = Boolean.parseBoolean(tokenizer.nextToken());
				msg.setMessageContent(new LatticeMessageContent(lvc,predicate));
				
				messageList.add(msg);
				
				// for debug
				System.out.println(clock + "\t" + predicate);
			}
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		return messageList;
	}

	/**
	 * preparation for lattice consturctor
	 */
	@BeforeClass
	public void initialize()
	{
		new NamingService().startService();
        new MIPAInitialize().initialize();
        
		try
		{
			MIPAResource.setNamingAddress("rmi://127.0.0.1:1099/");
			server = (Naming) java.rmi.Naming.lookup("rmi://127.0.0.1/"
					+ "Naming");

			IDManager idManager = (IDManager) server.lookup("IDManager");

//			appId = idManager.getID(Catalog.Application);
			predicateID = idManager.getID(Catalog.Predicate);
			checker = idManager.getID(Catalog.Checker);

//			LatticeConstructorTestNG tc = new LatticeConstructorTestNG();

//			ResultCallback appStub = (ResultCallback) UnicastRemoteObject
//					.exportObject(tc, 0);

//			server.bind(appId, appStub);

			this.dimension = 2;
			normalProcesses = new String[dimension];
			for(int i=0;i<this.dimension;i++)
			{
				normalProcesses[i] = idManager.getID(Catalog.NormalProcess);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * preparation and test for lattice constructor
	 * 
	 * FIXME: test the exceptions
	 */
	@Test
	public void testLatticeConstructor()
	{
			// there is no need to provide specification to check(the last argument)
			CheckerFactory.createCTLChecker(appId, predicateID, checker, normalProcesses, null);
			
			Communication checkerProcess;
			try
			{
				checkerProcess = (Communication) server.lookup(checker);

				List<Message> messages = this.messageDataProvider();
				for(Message msg : messages)
				{
					checkerProcess.receive(msg);
				}
			} catch (AccessException e)
			{
				e.printStackTrace();
			} catch (RemoteException e)
			{
				e.printStackTrace();
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (NotBoundException e)
			{
				e.printStackTrace();
			}
	}

}
