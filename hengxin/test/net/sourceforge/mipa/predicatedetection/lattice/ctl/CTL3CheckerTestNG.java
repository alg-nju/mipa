package net.sourceforge.mipa.predicatedetection.lattice.ctl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import net.sourceforge.mipa.predicatedetection.StructureParser;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParser;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParserTestNG;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeConstructorTestNG;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeMessageContent;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeVectorClock;
import net.sourceforge.mipa.predicatedetection.lattice.ctl.checker.CTL3CheckerController;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * test for {@link CTL3CheckerController} This test is based on that of
 * {@link LatticeChecker} and {@link CTLParser} (see
 * {@link LatticeConstructorTestNG} and {@link CTLParserTestNG})
 * 
 * FIXME: Can we combine {@link LatticeConstructorTestNG} and
 * {@link CTLParserTestNG} in TestNG instead of doing them again.
 * 
 * @author hengxin(hengxin0912@gmail.com)
 * 
 */
public class CTL3CheckerTestNG
{
	/**
	 * part of lattice constructor
	 */
	private Naming server = null;
	private String[] normalProcesses = null;
	private int dimension = 0;
	private String appId = null;
	private String predicateID = null;
	private String checker = null;

//	public static String postfix = "_dc_fal_notcomplete";
//	public static String postfix = "_dc_au";
//	public static String postfix = "_dc_eu";
//	public static String postfix = "_dc_eu_long";
	public static String postfix = "au_experiment0";
	private String msgFile = "test/messages/messages";
	
	/**
	 * part of ctl parser
	 */
//	private String ctlFileName = "config/predicate/ctl/predicate_ctl_eu.xml";
	private String ctlFileName = "config/predicate/ctl/predicate_ctl_au_experiment0.xml";
//	private String ctlFileName = "config/predicate/ctl/predicate_ctl_au.xml";
	
	/**
	 * just for ctl parser, regardless of mipa system
	 * 
	 * FIXME: the test is not complete!
	 */
	public void parseCTL()
	{
		new StructureParser().parseStructure(this.parseXml(this.ctlFileName));
	}

	/**
	 * parse predicate to Document Object(DOM).
	 * 
	 * @param fileName
	 *            a string
	 * @return predicate document
	 */
	private Document parseXml(String fileName)
	{
		Document doc = null;
		try
		{
			File f = new File(fileName);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(f);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return doc;
	}

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
			in = new BufferedReader(new FileReader(this.msgFile + postfix + ".txt"));
			String line = null;
			while ((line = in.readLine()) != null)
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

				// vc
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

			predicateID = idManager.getID(Catalog.Predicate);
			checker = idManager.getID(Catalog.Checker);

			this.dimension = 2;
			
			// actually, they are normalProcess0, normalProcess1,...
			normalProcesses = new String[dimension];
			for (int i = 0; i < this.dimension; i++)
			{
				normalProcesses[i] = idManager.getID(Catalog.NormalProcess);
			}
			
			// set cgs<-> np manually for debug
			this.setCgs2Nps();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setCgs2Nps()
	{
		HashMap<String,ArrayList<String>> cgs2nps = new HashMap<String,ArrayList<String>>();
		ArrayList<String> nps_a = new ArrayList<String>();
		nps_a.add(normalProcesses[0]);
		cgs2nps.put("a", nps_a);
		ArrayList<String> nps_b = new ArrayList<String>();
		nps_b.add(normalProcesses[1]);
		cgs2nps.put("b", nps_b);
		CTLParser.getInstance().setCgs2Nps(cgs2nps);
	}
	
	/**
	 * preparation and test for lattice constructor
	 * 
	 * FIXME: test the exceptions
	 */
	@Test
	public void testCTL3Checker()
	{
		this.parseCTL();
		
		// check the ctl specification
		CheckerFactory.createCTLChecker(appId, predicateID, checker,
				normalProcesses, CTLParser.getInstance().getCTLSpecification());

		try
		{
			Communication checkerProcess = (Communication) server.lookup(checker);

			List<Message> messages = this.messageDataProvider();
			for (Message msg : messages)
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

		// wait for other unfinished threads
		try
		{
			Thread.sleep(3000);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
