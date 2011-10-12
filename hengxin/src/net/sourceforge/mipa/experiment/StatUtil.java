package net.sourceforge.mipa.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class StatUtil
{
	public void stat4Experiment1()
	{
		BufferedReader br = null;
		PrintWriter bw = null;
		try
		{
			bw = new PrintWriter("log4j/experiment data/exp");
		} catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		
		File file = null;
		int number = 0;
		String temp = null;
		for (int i = 0; i < 20; i++)
		{
			file = new File("log4j/experiment data/exp" + i);
			
			number = 0;
			try
			{
				br = new BufferedReader(new FileReader(file));
				
				temp = br.readLine();
				while(temp != null)
				{
					number += Integer.parseInt(temp); 
					temp = br.readLine();
				}
				bw.println(number);
			} catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
		
		try
		{
			br.close();
			bw.close();
		} catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}	
	
	public void stat4Experiment2()
	{
		BufferedReader br = null;
		PrintWriter pwTime = null;
		PrintWriter pwSpace = null;
		
		try
		{
			pwTime = new PrintWriter("log4j/experiment data/experiment2/exptime");
			pwSpace = new PrintWriter("log4j/experiment data/experiment2/expspace");
		} catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		
		try
		{
			br = new BufferedReader(new FileReader(new File("log4j/experiment data/experiment2/exporigin")));
			
			String temp = null;
			int line = 0;
			long timeTmp = 0;
			long spaceTmp = 0;
			int timeAvg = 0;
			int spaceAvg = 0;
			long timeSum = 0;
			long spaceSum = 0;
			int num = 50;
			
			StringTokenizer st = null;
			
			temp = br.readLine();
			while(temp != null)
			{
				// parse and compute the cumulative sum of time and space
				st = new StringTokenizer(temp);
				timeTmp = Long.parseLong(st.nextToken());
				spaceTmp = Long.parseLong(st.nextToken());
				timeSum += timeTmp;
				spaceSum += spaceTmp;
				
				line++;
				
				// process
				if(line % num == 0)
				{
					// compute the average
					timeAvg = (int) (timeSum / (double) num);
					spaceAvg = (int) (spaceSum / (double) num);
					
					// store
					pwTime.println(timeAvg);
					pwSpace.println(spaceAvg);
					
					// reset; to process the next iteration
					line = 0;
				}
				
				temp = br.readLine();
			}
		} catch (FileNotFoundException fnfe)
		{
			System.out.println(fnfe.getMessage());
		} catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		
		try
		{
			br.close();
			pwTime.close();
			pwSpace.close();
		} catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
//		new StatUtil().stat4Experiment1();
		new StatUtil().stat4Experiment2();
	}
}
