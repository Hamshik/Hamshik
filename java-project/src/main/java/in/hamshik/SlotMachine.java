package in.hamshik;
import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;

public class SlotMachine {
    static public void main(String[]args) {
        String[] symbol = {"🍔","🍕","🍟","🍎","🍏"};
        String[] symbol_out = new String[3];
        Random random = new Random();
        Scanner input = new Scanner(System.in);
        double balance = 5000;
        double currentBlance = 5000;
        double bet;
        boolean isExited = false;
        
        do{
            System.out.print("Enter the bet: ");
            bet = input.nextDouble();
                input.nextLine();
            if(bet > balance){
                System.out.println("Insuffeint balance");
                continue;
            }
            else if (bet < 0){
                System.out.println("bet cannot be negative");
                continue;
            }
            else{
                balance -= bet;
                currentBlance -= bet;
                break;
            }
        }while(true);
        do{
            try {
                for(int i = 0; i < 3; i++){
                    System.out.print("\rSpining.  ");
                    Thread.sleep(150);
                    System.out.print("\rSpining.. ");
                    Thread.sleep(150);
                    System.out.print("\rSpining...");
                    Thread.sleep(150);
                }
                System.out.println();
            } catch (InterruptedException e) {
                System.err.println("Thread is interrupted and still continuning.");
                continue;
            }

            
            for(int i = 0; i < 3; i++){
                symbol_out[i] = symbol[random.nextInt(0,4)];
            }
            System.out.println(String.join("||",symbol_out));
            if (symbol_out[0].equals(symbol_out[1]) && symbol_out[2].equals(symbol_out[2])){
                switch(symbol_out[1]){
                    case "1" -> balance *= 20;
                    case "2" -> balance *= 10;
                    case "3" -> balance *= 5;
                }
                System.out.printf("You won (%f) & current balence is ₹(%f)",balance - currentBlance,balance);
            }
            else if(symbol_out[0].equals(symbol_out[1])){
                switch(symbol_out[1]){
                    case "1" -> balance += 24;
                    case "2" -> balance += 12;
                    case "3" -> balance += 6;
                }
                System.out.printf("You won %f.2 & current balence is ₹%f.2",balance - currentBlance,balance);
            }
            else if(symbol_out[2].equals(symbol_out[1])){
                switch(symbol_out[1]){
                    case "1" -> balance += 24;
                    case "2" -> balance += 12;
                    case "3" -> balance += 6;
                }
                System.out.printf("You won (%f) & current balence is ₹(%f)",balance - currentBlance,balance);
            }
            else{
                System.out.println("Sorry You lost " + "₹" + bet);
                System.out.println("Current balence is "+ "₹" + balance);
            }

            System.out.print("do u want to play again (Y/N) : ");
            String choice = input.nextLine().toUpperCase();
            System.out.println(choice);
            switch(choice){
                case "YES", "Y" ->  System.out.println("Continuing");
                case "NO", "N" ->{
                    System.out.println("Bye");
                    isExited = true;
                }
                default  -> System.out.println("Enter Y/N!!");
            }
            
        }while(!isExited);
        input.close();
    }

}
