package net.sourceforge.mipa.util.language;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ListUtilTest
{
	List<String> list1 = new ArrayList<String>();
	List<String> list2 = new ArrayList<String>();

	@Before
	public void setUp() throws Exception
	{
		list1.add("hello");
		list1.add("world");
		
		list2.add("hello");
		list2.add("china");
	}

	@Test
	public void testUnion()
	{
		Assert.assertEquals(3, ListUtil.union(list1, list2).size());
	}

}
