package in.hamshik;
import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Number_Guessing_Game{
	public static void main(String[] args){
		Random random = new Random();
		int target = random.nextInt(0,100);
		Scanner input = new Scanner(System.in);
		int userGuess = 0;
		int WorngGuess = 0;
		boolean userGuessed = false;
		System.out.println("Guess the Number between 0-100");
		do{
			try{
				System.out.print("Guess the #: ");
				userGuess = input.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Err!! because of this Exception " + e.getMessage());
			}
			if(userGuess > target){
				System.out.println("Too HIGH!!");
				WorngGuess++;
			}
			else if(userGuess < target){
				System.out.println("Too LOW!!");
				WorngGuess++;
			}
			else{
				System.out.println("You Won!!");
				System.out.printf("# of Worng Guess: %d",WorngGuess);
				userGuessed = true;
			}

			if(WorngGuess >= 10){
				System.out.println("You have reached limit of WorngGuess thus abroting");
				userGuessed = true;
			}

		}while(!userGuessed);
		input.close();
	}

}