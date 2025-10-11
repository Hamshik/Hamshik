package in.hamshik;
import java.util.*;
import java.text.*;
public class Currency{
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		double pay = in.nextDouble();
		in.close();
		Locale LocaleIn = Locale.forLanguageTag("en-IN");
		NumberFormat usFormat = NumberFormat.getCurrencyInstance(Locale.US);
		NumberFormat chiformat = NumberFormat.getCurrencyInstance(Locale.CHINA);
		NumberFormat informat = NumberFormat.getCurrencyInstance(LocaleIn);
		NumberFormat FraFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

		String india = informat.format(pay);
		String china = chiformat.format(pay);
		String us = usFormat.format(pay);
		String france = FraFormat.format(pay);

		System.out.println("US: " + us); 
		System.out.println("India: " + india); 
		System.out.println("China: " + china); 
		System.out.println("France: " + france);
		
	}
}
