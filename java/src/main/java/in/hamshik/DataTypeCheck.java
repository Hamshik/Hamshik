package in.hamshik;
import java.util.Scanner;
public class DataTypeCheck{
	public static void main(String[] args){
	Scanner in = new Scanner(System.in);
	int query = in.nextInt();

	for(int i = 0; i < query; i++){

		try{
				System.out.print("Enter the #(to check the # fits to primitive datatype): ");
				long n = in.nextLong();
				System.out.println(n + " can be fitted in: ");
				if(n >= Byte.MIN_VALUE && n <= Byte.MAX_VALUE)System.out.println("* byte");
				if(n >= Short.MIN_VALUE && n <= Short.MAX_VALUE)System.out.println("* short");
				if(n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE)System.out.println("* int");
				if(n >= Long.MIN_VALUE && n <= Long.MAX_VALUE)System.out.println("* long");
			}
			catch(Exception e){
				System.out.println(in.next() +  " can't be fitted anywhere.");
				continue;
			}
		}
		in.close();
	}
}
