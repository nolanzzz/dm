import java.util.*;
public class Attribute {
	private String attrName;
	ArrayList<String> attrValues;
	Attribute(String attrName) {
		this.attrName = attrName;
		attrValues = new ArrayList<String>();
	}
	String getAttrName() {
		return attrName;
	}
	int getValueNum() {
		return attrValues.size();
	}
	void addValue(String value) {
		attrValues.add(value);
	}
	ArrayList<String> getValues() {
		return attrValues;
	}
	
}
