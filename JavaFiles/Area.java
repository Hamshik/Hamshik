import java.util.Scanner;

public class Area {
    static Scanner input = new Scanner(System.in);
    static double length = 0;
    static double width = 0;
    static double area = 0;
    static double d1 = 0;
    static double d2 = 0;
    static double r = 0;
    static double d = 0;
    static double height = 0;
    static String choice;
    static boolean isEntered = false;
    public static void main(String[] args) {
        
        System.out.println("Supported shapes: \n kite \n square \n rectangle \n triangle \n parallelogram \n cube \n cubiod \n ");
        System.out.print("Which area of shape do you want: ");
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
            case "ploygon" -> polygon();
            default -> System.out.print("Invailid shape or this shape doesnt support");
        }

        input.close();
    }


    static double square(){
        System.out.print("enter the length: ");
        length = input.nextDouble();

        area = Math.pow(length, 2);
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double rectangle(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        System.out.print("enter the height: ");
        height = input.nextDouble();

        area = length * width;
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double kite(){
        System.out.print("enter the diagonal1 (short): ");
        d1 = input.nextDouble();

        System.out.print("enter the diagonal2(long): ");
        d2 = input.nextDouble();

        area = 0.5 * d1 * d2;
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double triangle(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        System.out.print("enter the height: ");
        height = input.nextDouble();

        area = 0.5 * height * length;
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double cube(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        area = Math.pow(length,3);
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double cubiod(){
        System.out.print("enter the length: ");
        length = input.nextDouble();

        System.out.print("enter the width: ");
        width = input.nextDouble();

        System.out.print("Enter the height: ");
        height = input.nextDouble();

        area = length * width * height ;
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double circle(){
        System.out.print("enter the radius: ");
        length = input.nextDouble();

        area =  3.14*r*r;
        System.out.print("The area is " + area + "cm^2");
        return area;
    }


    static double polygon(){
        System.out.print("enter the base: ");
        length = input.nextDouble();

        System.out.print("enter the height: ");
        height = input.nextDouble();

        area = length * height;
        
        System.out.print("the area is" + area + "cm");
        return area;
        
    }
}
