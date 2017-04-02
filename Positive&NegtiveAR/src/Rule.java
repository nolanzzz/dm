import java.util.Set;

public class Rule {
	private double supp_rate; 
	private double conf_rate;
	private Set<String> antecedent;
	private Set<String> consequent;
	private String sign;
	private boolean signFront = false;
	private boolean signEnd = false;
	
	public boolean isSignFront() {
		return signFront;
	}

	public void setSignFront(boolean signFront) {
		this.signFront = signFront;
	}

	public boolean isSignEnd() {
		return signEnd;
	}

	public void setSignEnd(boolean signEnd) {
		this.signEnd = signEnd;
	}

	public Rule(double supp, double conf, Set<String> antecedent, Set<String> consequent){
		this.supp_rate = supp; 
		this.conf_rate = conf;
		this.antecedent = antecedent;
		this.consequent = consequent;
	}

	public double getSupp_rate() {
		return supp_rate;
	}

	public void setSupp_rate(double supp_rate) {
		this.supp_rate = supp_rate;
	}

	public double getConf_rate() {
		return conf_rate;
	}

	public void setConf_rate(double conf_rate) {
		this.conf_rate = conf_rate;
	}

	public Set<String> getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(Set<String> antecedent) {
		this.antecedent = antecedent;
	}

	public Set<String> getConsequent() {
		return consequent;
	}

	public void setConsequent(Set<String> consequent) {
		this.consequent = consequent;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
