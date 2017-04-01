import java.io.*;
import java.util.*;

public class readData {
	public static void main(String [] args) throws IOException {
		Scanner kbd = new Scanner(System.in);
		System.out.print("Enter the filename: ");
		String filename = kbd.nextLine();
		File data = new File(filename);
	}
}
