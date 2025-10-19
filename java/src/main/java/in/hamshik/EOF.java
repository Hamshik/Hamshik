package in.hamshik;
import java.util.Scanner;
import java.util.ArrayList;
public class EOF{
	public static void main(String[] args){
		Scanner in  = new Scanner(System.in);
		ArrayList<String> lines = new ArrayList<>();
		int j = 0;
		System.out.println("Wirte anything!!");
		while(in.hasNext()){
			String line = in.nextLine();
			lines.add(line);
		}

		for(String i: lines){
			j++;
			System.out.println(j + " " + i);
		}
		in.close();
	}
}
