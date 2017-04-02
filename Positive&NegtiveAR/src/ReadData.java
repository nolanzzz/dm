import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.*;

public class ReadData {
	
	/* variables */
	//create a value-tag dictionary
	private HashMap<String, HashMap<String, String>> attrMap = new HashMap<String, HashMap<String, String>>();
	
	//2-d ArrayList to store tuples: {{1.1, 2.3, 3.1, 4.1, 5.2}, ...}
	private ArrayList<ArrayList<String>> tuples = new ArrayList<ArrayList<String>>();
	
	//create list to store attributes
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	//create supporting variables
	private String filename;
	private StringTokenizer token;
	private String line, value, name;
	private File data;
	
	

	
	//new scanner to scan file
	private Scanner readFile;
	
	/* constructor */
	public ReadData(String filename) throws IOException {
		this.filename = filename;
		data = new File(filename);
		readFile = new Scanner(data);
		retrieveAttr();
		generateData();
	}
	
	
	
	/* retrieve attributes */
	public void retrieveAttr() {
		//retrieve first line of data
		line = readFile.nextLine();
		token = new StringTokenizer(line, " ");
		while (token.hasMoreTokens()) {
			name = token.nextToken();
			attributes.add(new Attribute(name));
		}
	}
	
	/* generate dictionary and tuples */
	public void generateData() {
		while (readFile.hasNextLine()) {
			line = readFile.nextLine();
			token = new StringTokenizer(line, " ");
			//ArrayList to store each tuple
			ArrayList<String> tuple = new ArrayList<String>();
			int i = 0;
			while (token.hasMoreTokens()) {
				//HashMap: value-tag
				HashMap<String, String> tagMap = new HashMap<String, String>();
				value = token.nextToken();
				if (!attributes.get(i).getValues().contains(value))
					attributes.get(i).addValue(value);

				int attrTag = i+1;
				int valueTag = attributes.get(i).getValues().indexOf(value)+1;
				String tag = attrTag + "." + valueTag;

				//if attribute not in dictionary, add it
				if (attrMap.get(attributes.get(i).getAttrName())==null) {
					attrMap.put(attributes.get(i).getAttrName(), tagMap);
				}
				else {
					attrMap.get(attributes.get(i).getAttrName()).put(value, tag);
				}

				tuple.add(tag);
				i++;
			}
			//add tuple to the dataset:tuples
			tuples.add(tuple);	
		}
		readFile.close();
	}
	
	/* get occurence of a given itemset */
	public int getOccurence(ArrayList<String> set) {
		int count = 0;
		int size = set.size();
		for (int i=0;i<tuples.size();i++) {		
			int occur = 0;
			for (int j=0;j<size;j++) {
				String tag = set.get(j);
				if (!tuples.get(i).contains(tag)) {
					break;
				}
				else {
					occur++;
				}
			}
			if (occur==size) {
				count++;
			}
		}
		return count;
	}
	
	/* transform from tag to attribute-value */
	public String getAttrValue(String tag) {
		String attribute;
		String value;
		//transfer from char to an integer index
		int attrIndex = tag.charAt(0) - '0' - 1;
		attribute = attributes.get(attrIndex).getAttrName();
		int valueIndex = tag.charAt(2) - '0' - 1;
		value = attributes.get(attrIndex).getValues().get(valueIndex);
		String attr_value = attribute + "=" +value;
		return attr_value;
	}
	
	/* get attributes */
	public ArrayList<Attribute> getAttr() {
		return attributes;
	}
	
	/* get tuples */
	public ArrayList<ArrayList<String>> getTuples() {
		return tuples;
	}
	
	/* enumerate tuples */
	public void enumerate() {
		for (int i=0;i<tuples.size();i++) 
			System.out.println(tuples.get(i));
	}
}
		
	

