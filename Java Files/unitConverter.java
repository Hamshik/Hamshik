import java.util.Scanner;
public class unitConverter {
    String choice;
    boolean isEntered = true;
    static Scanner input = new Scanner(System.in);
    static public void main(String[]args){

        String choice;
        boolean isEntered = true;
        double kg;
        double g;
        double mg;
        double t;

        System.out.println("******************************");
        System.out.println("Welcome to Java unit converter");
        System.out.println("******************************");
        System.out.println("""
                *************************
                Supported unit converter:
                1. kg to g
                2. kg to t
                3. g to kg
                4. g to mg
                5. mg to g
                6. t to kg
                *************************
                """);
        System.out.print("Enter the #: ");
        choice = input.nextLine();

        do{
            if(choice.isBlank()){
                System.out.print("Enter the choice!! :");
                choice = input.nextLine();
            }
            else{
                isEntered = false;
            }
        }while(isEntered);

        switch(choice){
            case "1" ->{
                System.out.print("Enter the # to convert kilo to gram: ");
                kg = input.nextDouble();
                System.out.println(kg2g(kg));
            }
            case "2" ->{
                System.out.print("Enter the # to convert kilo to Ton: ");
                kg = input.nextDouble();
                System.out.println(kg2t(kg));
            }
            case "3" ->{
                System.out.print("Enter the # to convert gram to kilo: ");
                g = input.nextDouble();
                System.out.println(g2kg(g));
            }
            case "4" ->{
                System.out.print("Enter the # to convert gram to mg: ");
                g = input.nextDouble();
                System.out.println(g2mg(g));
            }
            case "5" -> {
                System.out.print("Enter the # to convert gram to mg: ");
                mg = input.nextDouble();
                System.out.println(mg2g(mg));
            }
            case "6" ->{
                System.out.print("Enter the # to convert tone to kilo: ");
                t = input.nextDouble();
                System.out.println(t2kg(t));
            }
        }

        input.close();
    }

    static double kg2g(double kg){
        return kg*1000;
    }

    static double kg2t(double kg){
        return kg/1000.0;
    }

    static double g2kg(double g){
        return g/1000.0;
    }

    static double g2mg(double g){
        return g*1000;
    }

    static double mg2g(double mg){
        return mg/1000.0;
    }

    static double t2kg(double t){
        return t*1000;
    }
}
