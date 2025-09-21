import java.util.Scanner;
import java.util.ArrayList;
public class oddEvencont{
	public static void main (String[] args){
		Scanner input = new Scanner(System.in);
		String rawData;
		int data;
		String[] parts;
		int[] numbers;
		ArrayList<Integer> even = new ArrayList<>();
		ArrayList<Integer> odd = new ArrayList<>(); 

		while(true){
			try{
				System.out.print("Enter the Number(Seprated with space): ");
				rawData = input.nextLine();
				break;
			}
			catch(Exception e){
				System.out.println("Err!! of this " + e.getMessage());
			}
		}

		parts = rawData.trim().split(" "); 
		numbers = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Integer.parseInt(parts[i]); // Convert each to int
            if(numbers[i] % 2 == 0){
				even.add(numbers[i]);
			}
			else{
				odd.add(numbers[i]);
			}
        }

		System.out.println(Conter(even, odd));

	}
	static String Conter (ArrayList<Integer> even, ArrayList<Integer> odd){
		return "Number of even Number:" + even + " Conuter: " + even.size() + "\nNumber of odd Number:" + odd + " Conuter: " + odd.size();
	}
}