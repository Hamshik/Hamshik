package in.hamshik;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class GetDay{
	public static void main(String[] args){
		Scanner in  = new Scanner(System.in);
		String format = "MM dd yyyy";
		LocalDate date;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

		while(true){
			 try{
				System.out.println("Enter the Date: ");
			 	String datein = in.nextLine();
            	date = LocalDate.parse(datein, formatter);
              	break;
        	}
        	catch(Exception e){
           		System.out.println("Bad format");
        	}
		}
		System.out.println(getDays(date));
		in.close();
		
	}
	public static String getDays(LocalDate date){
		return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		
	}
}
