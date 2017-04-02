import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GenerateItemset {
	
     Map<Set<String>, Integer> occurence = new HashMap<Set<String>, Integer>();
     List<Set<String>> PL = null;
     List<Set<String>> NL = null;
     ArrayList<ArrayList<String>> tuples = null;
     ReadData rd = null;
     int tuplesSize = 0;
     double ms = 0;
     double mi = 0;
     
     
     public GenerateItemset(String fileName, ArrayList<ArrayList<String>> tuples, double ms, double mi) throws IOException
     {
    	 rd = new ReadData(fileName);
    	 this.tuples = tuples;
    	 tuplesSize = tuples.size();
    	 this.ms = ms;
    	 this.mi = mi;   	 
    	 pruneItemset();
     }
     
	public List<ArrayList<String>> firstItemSet()
	{
		Set<ArrayList<String>> itemset = new HashSet<ArrayList<String>>();
	    
		for(int i = 0; i<tuples.size(); i++)
		{
			ArrayList<String> tuple = tuples.get(i);
			for(int j = 0; j < tuple.size(); j++)
			{
				String value = tuple.get(j);
				ArrayList<String> listValue = new ArrayList<String>();
				listValue.add(value);
				if(rd.getOccurence(listValue) > 0)
				{
					itemset.add(listValue);
				}
			}				
		}	
		List<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>(itemset);
	  return returnList;
	}
	//generate all posible combainations that are greater than mi 
	public Map<Integer, Set<Set<String>>> generateItemset()
	{	
		List<ArrayList<String>>itemset = firstItemSet();
		
		Map<Integer, Set<Set<String>>> allJoinList = new HashMap<Integer, Set<Set<String>>>();
		int k = 2;
		int size = itemset.size();
		while( size > 1)
        {
			Set<Set<String>> joinItem = new HashSet<Set<String>>();			
			for(int i = 0; i<itemset.size(); i++)
        	{
        		ArrayList<String> list1 =  itemset.get(i);
        		Set<String> set1 = new HashSet(list1);
        		for(int j = i+1; j <itemset.size(); j++)
        		{
        			ArrayList<String> list2 = itemset.get(j);
        			Set<String> set2 = new HashSet(list2);
        			if(allowJoin(set1, set2))
        			{
        				ArrayList<String>  joinList = new ArrayList<String>(Join(set1, set2));
        				if(getInterest(joinList, list1, list2) > mi)
        				      joinItem.add(Join(set1, set2)); 
        			}
        		}
        	}
			itemset = new ArrayList<ArrayList<String>>();
            for(Set<String> item: joinItem)
            {
            	itemset.add(new ArrayList(item));
            }
            size = itemset.size();
            if(joinItem.size() != 0)
        	     allJoinList.put(k, joinItem);      	
        	k++;   		
        }
		return allJoinList;
	}
	
	// all interest itemsets are divided into PL and NL
	public void pruneItemset()
	{
		Map<Integer, Set<Set<String>>> allItemset = generateItemset();
		PL = new ArrayList<Set<String>>();
		NL = new ArrayList<Set<String>>();
		
		for(int k : allItemset.keySet())
		{
			Set<Set<String>> k_itemset = allItemset.get(k);
			for(Set<String> item : k_itemset)
			{
				ArrayList<String> listItem = new ArrayList<String>(item);
				if(getSupport(listItem) > ms)
					PL.add(item);
				else 
					NL.add(item);
			}
		}
	}
	
	public List<Set<String>> getNL()
	{
	    return NL;
	}
	
	public List<Set<String>> getPL()
	{
		return PL;
	}
	
	public boolean allowJoin(Set<String> set1, Set<String> set2)
	{
	   Set<String> set12 = new HashSet<String>();	
	   for(String value : set1)
		   set12.add(value);
	   for(String value : set2)
		   set12.add(value);
	   Set<Integer> decimal = new HashSet<Integer>();
	   for(String joinValue : set12)
	   {
		   int attr = (int) Double.parseDouble(joinValue);
		   decimal.add(attr);
	   }
	   if(decimal.size() == set12.size() && set12.size() == set2.size() +1 && set1.size() == set2.size())
	     return true;
	   else
		 return false;
		
	}
	
	public Set<String> Join(Set<String> set1, Set<String> set2)
	{
		Set<String> joinSet = new HashSet<String>();
		for(String value1: set1)
			joinSet.add(value1);
		for(String value2: set2)
			joinSet.add(value2);
		
		return joinSet;
	}

	public double getSupport(ArrayList<String> values)
	{
		ArrayList<String> valueList = new ArrayList<String>(values);
	    int occur = rd.getOccurence(valueList);
	    double support = (double) occur/tuplesSize;
	    return support;
	}

   public double getInterest(ArrayList<String> joinset, ArrayList<String>set1, ArrayList<String>set2)
   {
	   
	   double value = Math.abs(getSupport(joinset) - getSupport(set1)*getSupport(set2));
	   return value;
   }
	
	
	
}
