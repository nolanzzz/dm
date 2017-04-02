import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateItemset {
	

	public List<String> firstItemSet(ArrayList<Map<String, HashMap<String, String>>> attrmap)
	{
		List<String> itemset = new ArrayList<String>();
		for(int i = 0; i <attrmap.size(); i++)
		{
		    Map<String, HashMap<String, String>> attrs = attrmap.get(i);	
		    for(String key : attrs.keySet())
		    {
		    	Map<String, String> tags = attrs.get(key);
		    	for(String value : tags.keySet())
		    	{
		    		String tag = tags.get(value);
		    		if(!itemset.contains(tag))
		    			itemset.add(tag);
		    	}
		    }
		}
		return itemset;
	}
	
	
	
}
