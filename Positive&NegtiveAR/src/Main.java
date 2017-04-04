 import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws IOException
	{
		System.out.println("Enter file name: ");
		Scanner sc = new Scanner(System.in);
		
//		C:\Users\Liu\Desktop\courses\CSCI 4144\Ass\Ass5\data2
		String fileName = sc.nextLine();
		
		System.out.println("enter ms:");
		double ms = Double.parseDouble(sc.nextLine());
		
		System.out.println("enter mi:");
		double mi = Double.parseDouble(sc.nextLine());
		
		System.out.println("enter mc:");
		double mc = Double.parseDouble(sc.nextLine());
		
		ReadData rd = new ReadData(fileName);
		ArrayList<ArrayList<String>> tuples = rd.getTuples();
		 
		GenerateItemset gi = new GenerateItemset(rd, tuples, ms, mi);
		
		System.out.println("Print all Positive Itemset: ");
		gi.printItemSet(gi.getPL());
		
		System.out.println("Print all negative Itemset: ");
		gi.printItemSet(gi.getNL());
		
		GenerateRules gr = new GenerateRules();
		List<Rule> positiveRules = gr.generatePositiveRules(gi.getPL(), gi.getOccurrenceMap(), mc, mi, ms, rd);
		gr.saveRules(positiveRules, "Positive Rules", "Positive", ms, mc, mi, rd);
		
		List<Rule> negativeRules = gr.generateNegativeRules(gi.getNL(), gi.getOccurrenceMap(),  mc, mi, ms, rd);
		gr.saveRules(negativeRules, "Negative Rules", "Negative", ms, mc, mi, rd);
		
		System.out.println("Rules are saved to files.");
	}
}
