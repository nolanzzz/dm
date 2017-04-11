import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

//import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;

public class GenerateRules {
	private List<Rule> positiveRules = new ArrayList<>();
	private List<Rule> negativeRules = new ArrayList<>();
	private Map<Set<String>, Set<String>> recordedSubsetOfCurrentPL = null;
	private Map<Set<String>, Set<String>> recordedSubsetOfCurrentNL = null;
	
	/**
	 * generate positive interesting rules
	 * @param positiveItemSet
	 * @param map
	 * @param mc
	 * @param mi
	 * @param ms
	 */
	public List<Rule> generatePositiveRules(List<Set<String>> positiveItemSet, Map<Set<String>, Integer> map,  double mc, double mi, double ms, ReadData rd){
		Collections.reverse(positiveItemSet);
		for(Set<String> itemset: positiveItemSet){
			if(itemset.size() > 1) {
				Queue<Set<String>> subset = new LinkedList<>();
				recordedSubsetOfCurrentPL = new HashMap<>();
				this.addSubsetIntoMap(recordedSubsetOfCurrentPL, itemset, this.subsetItemSet(itemset), subset);
				double suppRate = this.getDouble(map.get(itemset), rd.rowCount());
				while(subset.size() > 0){
					Set<String> antecedent = subset.poll();
					Set<String> consequent = new HashSet<String>(itemset);
					consequent.removeAll(antecedent);
					if(map.get(antecedent) != null && map.get(consequent) != null){
						double antecedentSuppRate = this.getDouble(map.get(antecedent), rd.rowCount());
						double consequentSuppRate = this.getDouble(map.get(consequent), rd.rowCount());
						
						if(this.crip(suppRate, antecedentSuppRate, consequentSuppRate) >= mc){
							if(fipis(ms, mc, mi, antecedentSuppRate, consequentSuppRate, suppRate, this.crip(suppRate, antecedentSuppRate, consequentSuppRate)) == 1.00){
								Rule r = new Rule( suppRate, this.crip(suppRate, antecedentSuppRate, consequentSuppRate), antecedent, consequent);
								this.positiveRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentPL, itemset, this.subsetItemSet(antecedent), subset);
							}
						}
						
						if(this.crip(suppRate, consequentSuppRate, antecedentSuppRate) >= mc){
							if(fipis(ms, mc, mi, consequentSuppRate, antecedentSuppRate, suppRate, this.crip(suppRate, consequentSuppRate, antecedentSuppRate)) == 1.00){
								Rule r = new Rule(suppRate, this.crip(suppRate, consequentSuppRate, antecedentSuppRate), consequent, antecedent);
								this.positiveRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentPL, itemset, this.subsetItemSet(antecedent), subset);
							}
						}
							
					}
				}
			}
		}
		return this.positiveRules;
	}
	
	/**
	 * generate negative interesting rules
	 * @param negativeItemSet
	 * @param map
	 * @param mc
	 * @param mi
	 * @param ms
	 */
	public List<Rule> generateNegativeRules(List<Set<String>> negativeItemSet, Map<Set<String>, Integer> map,  double mc, double mi, double ms, ReadData rd){
		Collections.reverse(negativeItemSet);
		for(Set<String> itemset: negativeItemSet){
			if(itemset.size() > 0){
				Queue<Set<String>> subset = new LinkedList<>();
				recordedSubsetOfCurrentNL = new HashMap<>();
				this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(itemset), subset);
				double supp_xy = this.getDouble(map.get(itemset), rd.rowCount());
				while(subset.size() > 0){
					Set<String> X = subset.poll();
					Set<String> Y = new HashSet<String>(itemset);
					Y.removeAll(X);
					
					if(map.get(X) != null && map.get(Y) != null && supp_xy > 0){
						double supp_x = this.getDouble(map.get(X), rd.rowCount());
						double supp_y = this.getDouble(map.get(Y), rd.rowCount());
						double supp_not_x = 1 - supp_x;
						double supp_not_y = 1 - supp_y;
						double supp_x_not_y = supp_x - supp_xy;
						double supp_y_not_x = supp_y - supp_xy;
						double supp_not_y_not_x = 1 - supp_xy;
						double supp_not_x_under_y = this.getDouble(supp_y_not_x, supp_not_x);
						double supp_x_under_not_y = this.getDouble(supp_x_not_y, supp_x);
						double supp_not_x_under_not_y = this.getDouble(supp_not_y_not_x, supp_not_x);
						
						if(this.crip(supp_y_not_x, supp_y, supp_not_x) >= mc){
							if(this.iipis(ms, mc, mi, supp_y_not_x, supp_y, supp_not_x_under_y,  this.crip(supp_y_not_x, supp_y, supp_not_x)) == 2.00 ){
								Rule r = new Rule(supp_not_x_under_y, this.crip(supp_y_not_x, supp_y, supp_not_x), X, Y);
								r.setSign("not");
								r.setSignFront(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(X), subset);
							}
						}
						
						if(this.crip(supp_y_not_x, supp_not_x, supp_y) >= mc){
							if(this.iipis(ms, mc, mi, supp_not_x, supp_y, supp_y_not_x,  this.crip(supp_y_not_x, supp_not_x, supp_y)) == 2.00 ){
								Rule r = new Rule(supp_y_not_x, this.crip(supp_y_not_x, supp_not_x, supp_y), Y, X);
								r.setSign("not");
								r.setSignEnd(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(Y), subset);
							}
						}
						
						if(this.crip(supp_x_not_y, supp_not_y, supp_x) >= mc){
							if(this.iipis(ms, mc, mi, supp_not_y, supp_x, supp_x_under_not_y,  this.crip(supp_x_not_y, supp_not_y, supp_x)) == 2.00 ){
								Rule r = new Rule(supp_x_under_not_y, this.crip(supp_x_not_y, supp_not_y, supp_x), X, Y);
								r.setSign("not");
								r.setSignEnd(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(X), subset);
							}
						}
						
						if(this.crip(supp_x_not_y, supp_x, supp_not_y) >= mc){
							if(this.iipis(ms, mc, mi, supp_x, supp_not_y, supp_x_not_y,  this.crip(supp_x_not_y, supp_x, supp_not_y)) == 2.00 ){
								Rule r = new Rule(supp_x_not_y, this.crip(supp_x_not_y, supp_x, supp_not_y), Y, X);
								r.setSign("not");
								r.setSignFront(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(Y), subset);
							}
						}
						
						if(this.crip(supp_not_y_not_x, supp_not_y, supp_not_x) >= mc){
							if(this.iipis(ms, mc, mi, supp_not_y, supp_not_x, supp_not_x_under_not_y,  this.crip(supp_not_y_not_x, supp_not_y, supp_not_x)) == 2.00 ){
								Rule r = new Rule(supp_not_x_under_not_y, this.crip(supp_not_y_not_x, supp_not_y, supp_not_x), X, Y);
								r.setSign("not");
								r.setSignEnd(true);
								r.setSignFront(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(X), subset);
							}
						}
						
						if(this.crip(supp_not_y_not_x, supp_not_x, supp_not_y) >= mc){
							if(this.iipis(ms, mc, mi, supp_not_x, supp_not_y, supp_not_y_not_x,  this.crip(supp_not_y_not_x, supp_not_x, supp_not_y)) == 2.00 ){
								Rule r = new Rule(supp_not_y_not_x, this.crip(supp_not_y_not_x, supp_not_x, supp_not_y), Y, X);
								r.setSign("not");
								r.setSignEnd(true);
								r.setSignFront(true);
								this.negativeRules.add(r);
								this.addSubsetIntoMap(recordedSubsetOfCurrentNL, itemset, this.subsetItemSet(Y), subset);
							}
						}
					}

					
				}
			}
		}
		return this.negativeRules;
	}
	
	/**
	 * generate subset of an itemset
	 * @param itemSet
	 * @return
	 */
	public List<Set<String>> subsetItemSet(Set<String> itemSet){
		List<Set<String>> ans = new ArrayList<>();
		if(itemSet.size() > 1){
			Set<String> oneElem = null;
			Set<String> restElem = null;
			for(String item: itemSet){
				oneElem = new HashSet<String>();
				oneElem.add(item);
				restElem = new HashSet<String>(itemSet);
				restElem.removeAll(oneElem);
				ans.add(restElem);
			}
		}
		return ans;
	}
	
	/**
	 * frequent itemset of potential interest set
	 * @param ms
	 * @param mc
	 * @param mi
	 * @param supp_x
	 * @param supp_y
	 * @param supp_xy
	 * @param conf
	 * @return
	 */
	public double fipis(double ms, double mc, double mi, double supp_x, double supp_y, double supp_xy, double conf){
		double numerator = supp_xy + conf + interest(supp_x, supp_y, supp_xy) - ( ms + mc + mi) + 1;
		double denominator = Math.abs(supp_xy - ms) + Math.abs(conf - mc) + Math.abs(interest(supp_x, supp_y, supp_xy) - mi) + 1;
		return getDouble(numerator, denominator);
	}
	/**
	 * return double value 
	 * @param a
	 * @param b
	 * @return
	 */
	public double getDouble(double a, double b){
		return BigDecimal.valueOf(a/b).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * interest function
	 * @param supp_x
	 * @param supp_y
	 * @param supp_xy
	 * @return
	 */
	public double interest(double supp_x, double supp_y, double supp_xy){
		return Math.abs(supp_xy - supp_x * supp_y);
	}
	
	/**
	 * conditional-probability increment ratio 
	 * @param supp_xy
	 * @param supp_x
	 * @param supp_y
	 * @return
	 */
	public double crip(double supp_xy, double supp_x, double supp_y){
		double n = supp_xy - supp_xy * supp_y;
		double m  = supp_x *(  1 - supp_y);
		return getDouble(n, m);
	}
	
	/**
	 * infrequent itemset of potential interest set
	 * @param ms
	 * @param mc
	 * @param mi
	 * @param supp_x
	 * @param supp_y
	 * @param supp_xy
	 * @param conf
	 * @return
	 */
	public double iipis(double ms, double mc, double mi, double supp_x, double supp_y, double supp_xy, double conf){
		double n = supp_x + supp_y - 2 * ms  + 1;
		double m = Math.abs(supp_x - ms) + Math.abs(supp_y - ms) + 1;
		double not_supp_y = 1 - supp_y;
		double not_supp_xy = supp_xy - supp_x * not_supp_y;
		double conf_x_not_y = getDouble(not_supp_xy, supp_x);
		return this.fipis(ms, mc, mi, supp_x, not_supp_y, not_supp_xy, conf_x_not_y) + this.getDouble(n, m);
	}
	
	/**
	 * print rules
	 * @param r
	 */
	public void printRule(Rule r){
		System.out.println("Support Rate: " + r.getSupp_rate() + "  , Confidence Rate: " + r.getConf_rate());
		if(r.isSignFront()){
			System.out.print(r.getSign() + " ");
		}
	}
	
	public void saveRules(List<Rule> rules, String filename, String flag, double ms, double mc, double mi,  ReadData rd){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
			StringBuffer buffer = new StringBuffer();
			
			String str = "Summary: \n";
			str += "Total rows in the original set: " + rd.rowCount() + "\n";
			str += "Total" + flag + " rules discovered: " +  rules.size() + "\n";
			str += "The selected measures: Support = " + ms + " Confidence = " + mc + " Interest: " + mi +"\n";
			str += "----------------------------------------------------------------\n\n Rules:\n\n";
			buffer.append(str);
			for(int i = 0; i < rules.size(); i++){
				str = new String();
				Rule rule = rules.get(i);
				str = "Rules#" + (i + 1) + ":";
				str += "(Support=" + rule.getSupp_rate() + ",";
				str += " Confidence=" + rule.getConf_rate() +")\n";
				str += "{ ";
				
				for(String s: rule.getAntecedent()){
					str += rd.getAttrValue(s) + ", ";
				}
				str += " }\n ---->";
				str += "{ ";
				
				for(String s: rule.getConsequent()){
					str += rd.getAttrValue(s) + ", ";
				}
				str += " }\n\n";
				buffer.append(str);
			}
			
			
			bw.write(buffer.toString());
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean checkSubsetExist(Map<Set<String>, Set<String>> itemSetMap, Set<String> itemset, Set<String> subset){
		return itemSetMap.get(itemset).equals(subset);
	}
	
	/**
	 * use to check whether the subset have been added to the queue yet
	 * @param map
	 * @param itemset
	 * @param subsets
	 */
	public void addSubsetIntoMap(Map<Set<String>, Set<String>> map, Set<String> itemset, List<Set<String>> subsets, Queue<Set<String>> queue){
		for(Set<String> s: subsets){
			if(!map.containsKey(s)){
				map.put(s, itemset);
				queue.add(s);
			}
		}
	}
}

