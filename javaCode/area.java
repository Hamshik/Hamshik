import java.util.Scanner;
public class area {

    static Scanner input = new Scanner(System.in);
    static boolean isEntered = true;
    static char choice;
    static double area;
    static double length;
    static double breath;
    static double width;

    public static void main(String[] args) {
        System.out.println("""
                 Supported shapes:
                 1. kite
                 2. parallelogram
                 3. Square
                 4. Rectangle
                 5. cube
                 6. cubiod
                 7. circle
                 """);
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
        area = length * breath * 0.5;
        System.out.println("area is :" + area);
        return area;
    }
    
    static double parallelogram(){
        System.out.print("Enter the base: ");
        length = input.nextDouble();
        System.out.print("Enter the heigth: ");
        breath = input.nextDouble();
        area = length * breath * 0.5;
        System.out.println("area is :" + area);
        return area;
    }

    static double square(){
        System.out.print("Enter the diagonal (short): ");
        length = input.nextDouble();
        area = Math.pow(length,2);
        System.out.println("area is :" + area);
        return area;
    }

    static double rectangle(){
        System.out.print("Enter the length: ");
        length = input.nextDouble();
        System.out.print("Enter the breath: ");
        breath = input.nextDouble();
        area = length * breath * 1;
        System.out.println("area is :" + area);
        return area;
    }

    static double cube(){
        System.out.print("Enter the length: ");
        length = input.nextDouble();
        area = Math.pow(length,3);
        System.out.println("area is :" + area);
        return area;
    }

    static double cubiod(){
        System.out.print("Enter the breath: ");
        length = input.nextDouble();
        System.out.print("Enter the length: ");
        breath = input.nextDouble();
        System.out.print("Enter the heigth: ");
        area = length * breath * width;
        System.out.println("area is :" + area);
        return area;
    }

    static double circle(){
        System.out.print("Enter the radius: ");
        double r  = input.nextDouble();
        area = Math.PI * r * r;
        System.out.println("area is :" + area);
        return area;
    }
}