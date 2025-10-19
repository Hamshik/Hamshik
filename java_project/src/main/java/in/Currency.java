package in;


import java.util.*;
import java.text.*;
public class Currency{
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		try{
			double pay = in.nextDouble();
			in.close();
			Locale LocaleIn = Locale.forLanguageTag("en-IN");
			String us = NumberFormat.getCurrencyInstance(Locale.US).format(pay);
			String china = NumberFormat.getCurrencyInstance(Locale.CHINA).format(pay);
			String india = NumberFormat.getCurrencyInstance(LocaleIn).format(pay);
			String France = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(pay);

			System.out.println("US: " + us);
			System.out.println("India: " + india);
			System.out.println("China: " + china);
			System.out.println("France: " + France);
		}
		catch(Exception e){}
		
	}
}
