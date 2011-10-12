package util;

import net.sourceforge.mipa.Application;
import net.sourceforge.mipa.ECAInitialize;
import net.sourceforge.mipa.MIPAInitialize;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.naming.NamingService;
import net.sourceforge.mipa.predicatedetection.ctl.CTLParserTestNG;

/**
 * run mipa all in one, including <code>MIPAInitialize</code>, 
 * <code>ECAInitialize</code>, and
 * <code>Application</code>.
 * 
 * @author hengxin
 *
 */

/**
 * run mipa all in one.
 * 
 * Design pattern: Singleton
 */
public class MIPAAllInOne 
{
	private String predicateXml = null;
	
	private static MIPAAllInOne instance = null;
	private MIPAAllInOne(String predicateXml) 
	{ 
		this.predicateXml = predicateXml;
	}
	public static MIPAAllInOne getInstance(String predicateXml)
	{
		if(instance == null)
			instance = new MIPAAllInOne(predicateXml);
		
		return instance;
	}
	
	/**
	 * run mipa all in one including mipa infrastructure, 
	 * eca infrastructure, and mipa application
	 */
	public void runMIPAAllInOne()
	{
		try
		{
			this.mipaInitialize();
			Thread.sleep(2000);
			this.ecaInitialize();
			Thread.sleep(2000);
			this.runMipaApplication();
		} catch (InterruptedException inte)
		{
			inte.getStackTrace();
		}
	}
	
	/**
	 * start mipa infrastructure and mipa application 
	 * but not with ECA infrastructure
	 * 
	 * this configuration can be used to test 
	 * the parser module (@see {@link CTLParserTestNG})
	 */
	public void runMIPAWithoutECA()
	{
		try
		{
			this.mipaInitialize();
			Thread.sleep(5000);
			this.runMipaApplication();
		} catch(InterruptedException inte)
		{
			inte.getStackTrace();
		}
	}
	
	/**
	 * initialize mipa
	 * 
	 * {@link net.sourceforge.mipa.MIPAInitialize}
	 */
	private void mipaInitialize()
	{
        // start naming service
        NamingService service = new NamingService();
        System.out.println("MIPA system Naming Service starts sucessfully.");
        service.startService();
        
        boolean result = new MIPAInitialize().initialize();
        if (result) 
        {
            System.out.println("Initialization finished.");
        } else 
        {
            System.out.println("Error occurs when initializing");
        }

        System.out.println(MIPAResource.getNamingAddress());
	}
	
	/**
	 * initialize eca
	 * 
	 * {@link net.sourceforge.mipa.ECAInitialize}
	 */
	private void ecaInitialize()
	{
		new ECAInitialize().initialize();
	}
	
	/**
	 * run mipa application
	 * 
	 * {@link net.sourceforge.mipa.Application}
	 */
	private void runMipaApplication()
	{
		Application app = new Application("config/config.xml");
		app.register(this.getFullPath(this.predicateXml));
	}
	
	private String getFullPath(String filePath)
	{
		// it is full path
		if(filePath.charAt(1) == ':')
			return filePath;
		else // it is relative path
			return "config/predicate/" + filePath;
	}
}
