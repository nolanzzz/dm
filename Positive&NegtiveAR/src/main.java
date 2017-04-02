import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class main {

	public static void main(String[] args) throws IOException
	{
		System.out.println("Enter file name: ");
		Scanner sc = new Scanner(System.in);
		
		String fileName = sc.nextLine();
		
		System.out.println("enter ms");
		double ms = Double.parseDouble(sc.nextLine());
		
		System.out.println("enter mi");
		double mi = Double.parseDouble(sc.nextLine());
		
		ReadData rd = new ReadData(fileName);
		 ArrayList<ArrayList<String>> tuples = rd.getTuples();
		 
		 GenerateItemset gi = new GenerateItemset(fileName, tuples, ms, mi);
		 
	}
}
