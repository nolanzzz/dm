import java.io.*;
import java.util.*;

public class readData {
	public static void main(String [] args) throws IOException {
		Scanner kbd = new Scanner(System.in);
		System.out.print("Enter the filename: ");
		String filename = kbd.nextLine();
		File data = new File(filename);
		Scanner readFile = new Scanner(data);
		StringTokenizer token;
		String line, value, name;
		
		//retrieve attributes
		line = readFile.nextLine();
		//create list to store attributes
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		token = new StringTokenizer(line, " ");
		while (token.hasMoreTokens()) {
			name = token.nextToken();
			attributes.add(new Attribute(name));
		}
		//Map to store attribute value and its tag
		HashMap<String, String> attr_tag = new HashMap<String, String>();
		for (int i=0; i<attributes.size();i++) {
			System.out.println(attributes.get(i).getAttrName());
			for (int j=0;j<attributes.get(i).getValues().size();j++) {
				String valueName = attributes.get(i).getValues().get(j);
				System.out.println(valueName);
				double attrTag = i+1;
				double valueTag = j+1;
				String tag = attrTag + "." + valueTag;
			}
		}
		
		//read tuples
		ArrayList<HashMap<String, HashMap<String, String>>> tuples = new ArrayList<HashMap<String, HashMap<String, String>>>();
		

		while (readFile.hasNextLine()) {
			line = readFile.nextLine();
			token = new StringTokenizer(line, " ");
			//HashMap: attribute-value-tag
			HashMap<String, HashMap<String, String>> attrMap = new HashMap<String, HashMap<String, String>>();
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
				tagMap.put(value, tag);
				System.out.println(value+" "+tag);
				attrMap.put(attributes.get(i).getAttrName(), tagMap);
				i++;
			}
			//add tuple to the dataset (tuples)
			tuples.add(attrMap);
		}
	}
}
