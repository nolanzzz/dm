import java.util.*;
class Attribute {
	private String attrName;
	private ArrayList<String> attrValues;
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
