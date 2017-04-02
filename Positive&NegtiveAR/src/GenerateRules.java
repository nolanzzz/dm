import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GenerateRules {
	private List<Rule> positiveRules = new ArrayList<>();
	private List<Rule> negativeRules = new ArrayList<>();
	
	/**
	 * generate positive interesting rules
	 * @param positiveItemSet
	 * @param map
	 * @param mc
	 * @param mi
	 * @param ms
	 */
	public void generatePositiveRules(List<Set<String>> positiveItemSet, Map<Set<String>, Double> map, double mc, double mi, double ms){
		Collections.reverse(positiveItemSet);
		for(Set<String> itemset: positiveItemSet){
			if(itemset.size() > 1) {
				Queue<Set<String>> subset = new LinkedList<>();
				subset.addAll(this.subsetItemSet(itemset));
				double suppRate = map.get(itemset);
				while(subset.size() > 0){
					Set<String> antecedent = subset.poll();
					Set<String> consequent = new HashSet<String>(itemset);
					consequent.removeAll(antecedent);
					
					double antecedentSuppRate = map.get(antecedent);
					double consequentSuppRate = map.get(consequent);
					if(fipis(ms, mc, mi, antecedentSuppRate, consequentSuppRate, suppRate, getDouble(suppRate, antecedentSuppRate)) == 1.00){
						if(this.crip(suppRate, antecedentSuppRate, consequentSuppRate) >= mc){
							Rule r = new Rule( suppRate, this.crip(suppRate, antecedentSuppRate, consequentSuppRate), antecedent, consequent);
							this.positiveRules.add(r);
						}
						
						if(this.crip(suppRate, consequentSuppRate, antecedentSuppRate) >= mc){
							Rule r = new Rule(suppRate, this.crip(suppRate, consequentSuppRate, antecedentSuppRate), consequent, antecedent);
							this.positiveRules.add(r);
						}
						
						subset.addAll(this.subsetItemSet(antecedent));
					}
				}
			}
		}
	}
	
	/**
	 * generate negative interesting rules
	 * @param negativeItemSet
	 * @param map
	 * @param mc
	 * @param mi
	 * @param ms
	 */
	public void generateNegativeRules(List<Set<String>> negativeItemSet, Map<Set<String>, Double> map, double mc, double mi, double ms){
		Collections.reverse(negativeItemSet);
		for(Set<String> itemset: negativeItemSet){
			if(itemset.size() > 0){
				Queue<Set<String>> subset = new LinkedList<>();
				subset.addAll(this.subsetItemSet(itemset));
				double supp_xy = map.get(itemset);
				while(subset.size() > 0){
					Set<String> X = subset.poll();
					Set<String> Y = new HashSet<String>(itemset);
					Y.removeAll(X);
					
					double supp_x = map.get(X);
					double supp_y = map.get(Y);
					double supp_not_x = 1 - supp_x;
					double supp_not_y = 1 - supp_y;
					
					if(this.iipis(ms, mc, mi, supp_x, supp_y, supp_xy, getDouble(supp_xy, supp_x)) == 2.00 ){
						double supp_x_not_y = supp_x - supp_xy;
						double supp_y_not_x = supp_y - supp_xy;
						double supp_not_y_not_x = 1 - supp_xy;
						double supp_not_x_under_y = this.getDouble(supp_y_not_x, supp_not_x);
						double supp_x_under_not_y = this.getDouble(supp_x_not_y, supp_x);
						double supp_not_x_under_not_y = this.getDouble(supp_not_y_not_x, supp_not_x);
						
						if(this.crip(supp_y_not_x, supp_y, supp_not_x) >= mc){
							Rule r = new Rule(supp_not_x_under_y, this.crip(supp_y_not_x, supp_y, supp_not_x), X, Y);
							r.setSign("not");
							r.setSignFront(true);
							this.negativeRules.add(r);
						}
						
						if(this.crip(supp_y_not_x, supp_not_x, supp_y) >= mc){
							Rule r = new Rule(supp_y_not_x, this.crip(supp_y_not_x, supp_not_x, supp_y), Y, X);
							r.setSign("not");
							r.setSignEnd(true);
							this.negativeRules.add(r);
						}
						
						if(this.crip(supp_x_not_y, supp_not_y, supp_x) >= mc){
							Rule r = new Rule(supp_x_under_not_y, this.crip(supp_x_not_y, supp_not_y, supp_x), X, Y);
							r.setSign("not");
							r.setSignEnd(true);
							this.negativeRules.add(r);
						}
						
						if(this.crip(supp_x_not_y, supp_x, supp_not_y) >= mc){
							Rule r = new Rule(supp_x_not_y, this.crip(supp_x_not_y, supp_x, supp_not_y), Y, X);
							r.setSign("not");
							r.setSignFront(true);
							this.negativeRules.add(r);
						}
						
						if(this.crip(supp_not_y_not_x, supp_not_y, supp_not_x) >= mc){
							Rule r = new Rule(supp_not_x_under_not_y, this.crip(supp_not_y_not_x, supp_not_y, supp_not_x), X, Y);
							r.setSign("not");
							r.setSignEnd(true);
							r.setSignFront(true);
							this.negativeRules.add(r);
						}
						
						if(this.crip(supp_not_y_not_x, supp_not_x, supp_not_y) >= mc){
							Rule r = new Rule(supp_not_y_not_x, this.crip(supp_not_y_not_x, supp_not_x, supp_not_y), Y, X);
							r.setSign("not");
							r.setSignEnd(true);
							r.setSignFront(true);
							this.negativeRules.add(r);
						}
						
						subset.addAll(this.subsetItemSet(X));
					}
					
				}
			}
		}
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
	
	public void printRule(Rule r){
		System.out.println("Support Rate: " + r.getSupp_rate() + "  , Confidence Rate: " + r.getConf_rate());
		if(r.isSignFront()){
			System.out.print(r.getSign() + " ");
		}
	}
}
