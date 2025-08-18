import java.util.Scanner;
public class perimeter {

    static Scanner input = new Scanner(System.in);
    static boolean isEntered = true;
    static String choice;
    static double perimeter;
    static double length;
    static double breath;
    static double width;

    public static void main(String[] args) {
        System.out.print("Supported shapes: \n 1. kite \n 2. parallelogram \n 3. Square \n 4. Rectangle \n 5. cube \n6. cubiod \n7. circle ");
        System.out.print("Enter the Shape's Number: ");
        choice = input.nextLine();
        do{
            if(choice.isEmpty()){
                System.out.print("Enter the Shape's number: ");
                choice = input.nextLine();
            }
            else if(!choice.equals("1") && !choice.equals("2") &&!choice.equals("3") &&!choice.equals("4") &&!choice.equals("5") &&!choice.equals("6") && !choice.equals("7")){
               System.out.print("Invailid Shape no. pls enter it again: ");
                System.out.print("Enter the Shape's number: ");
                choice = input.nextLine();
            }
            else{
                isEntered = false;
            }
        }while(isEntered);
        switch(choice){
            case "1" -> kite();
            case "2" -> parallelogram();
            case "3" -> square();
            case "4" -> rectangle();
            case "5" -> cube();
            case "6" -> cubiod();
            case "7" -> circle();
            default -> System.out.println("Error 301 pls try again");
        }
        input.close();
    }

    static double kite(){
        System.out.print("Enter the diagonal (short): ");
        length = input.nextDouble();
        System.out.print("Enter the Diagonals (long): ");
        breath = input.nextDouble();
        perimeter = 2 * (length + breath);
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }
    
    static double parallelogram(){
        System.out.print("Enter the base: ");
        length = input.nextDouble();
        System.out.print("Enter the heigth: ");
        breath = input.nextDouble();
        perimeter = (length + breath) * 2;
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }

    static double square(){
        System.out.print("Enter the diagonal (short): ");
        length = input.nextDouble();
        perimeter = (length * 4) * 1;
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }

    static double rectangle(){
        System.out.print("Enter the length: ");
        length = input.nextDouble();
        System.out.print("Enter the breath: ");
        breath = input.nextDouble();
        perimeter = (length + breath) * 2;
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }

    static double cube(){
        System.out.print("Enter the length: ");
        length = input.nextDouble();
        perimeter = 1 * (length * 12);
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }

    static double cubiod(){
        System.out.print("Enter the breath: ");
        length = input.nextDouble();
        System.out.print("Enter the length: ");
        breath = input.nextDouble();
        System.out.print("Enter the heigth: ");
        perimeter = 4 * (length + breath + width);
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }

    static double circle(){
        System.out.print("Enter the radius: ");
        double r  = input.nextDouble();
        perimeter = 2 * (Math.PI * r);
        System.out.print("perimeter is :" + perimeter);
        return perimeter;
    }
}