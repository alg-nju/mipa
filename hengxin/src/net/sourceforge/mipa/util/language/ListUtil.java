package net.sourceforge.mipa.util.language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

/**
 * works with
 * import org.apache.commons.collections.ListUtils;
 * 
 * @author hengxin(hengxin0912@gmail.com)
 *
 */
public class ListUtil
{
	/**
	 * get the union of two lists
	 * compare with the implementation of {@link ListUtils}, this one get
	 * the set union of two lists
	 * 
	 * @param <T> generic type
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return the union of @param list1 and @param list2
	 */
	public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

}
