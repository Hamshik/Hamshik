import java.util.Scanner;

public class perimeter {
    static Scanner input = new Scanner(System.in);
    static double length = 0;
    static double width = 0;
    static double perimeter = 0;
    static double d1 = 0;
    static double d2 = 0;
    static double r = 0;
    static double d = 0;
    static double height = 0;
    static String choice;
    static boolean isEntered = false;
    public static void main(String[] args) {

        System.out.println("Supported shapes: \n kite \n square \n rectangle \n triangle \n parallelogram \n cube \n cubiod \n ");
        System.out.print("Which perimeter of shape do you want: ");
        choice = input.nextLine();

        do{
            if(choice.isEmpty()){
                System.out.print("Enter the choice!! :");
                choice = input.nextLine();
                    }
                else{
                isEntered = false;
                }
            }while(isEntered);

        switch(choice.toLowerCase()){
            case "kite" -> kite();
            case "rectangle" -> rectangle();
            case "square" -> square();
            case "triangle" -> triangle();
            case "cube" -> cube();
            case "cubiod" -> cubiod();
            case "circle" -> circle();
            case "parallelogram" -> polygon();
            default -> System.out.print("Invailid shape or this shape doesnt support");
        }

        input.close();
    }


    static double square(){
        System.out.print("enter the length: ");
        length = input.nextDouble();

        perimeter = length * 4;
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double rectangle(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        System.out.print("enter the height: ");
        height = input.nextDouble();

        perimeter = 2*(length + width);
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double kite(){
        System.out.print("enter the diagonal1 (short): ");
        d1 = input.nextDouble();

        System.out.print("enter the diagonal2(long): ");
        d2 = input.nextDouble();

        perimeter = 2*(d1 + d2);
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double triangle(){
        System.out.print("enter the length: ");
        length = input.nextDouble();

        perimeter =  3 * length;
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double cube(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        perimeter = 12 * length;
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double cubiod(){
        System.out.print("enter the length: ");
        length = input.nextDouble();

        System.out.print("enter the width: ");
        width = input.nextDouble();

        System.out.print("Enter the height: ");
        height = input.nextDouble();

        perimeter = 4 * (length + width + height) ;
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double circle(){
        System.out.print("enter the radius: ");
        length = input.nextDouble();

        perimeter =  2 * 3.14 * r;
        System.out.print("The perimeter is " + perimeter + "cm");
        return perimeter;
    }


    static double polygon(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        System.out.print("enter the height: ");
        height = input.nextDouble();

        perimeter = 2 * (length + height);
        System.out.print("the perimeter is" + perimeter + "cm");
        return perimeter;
    }
}

