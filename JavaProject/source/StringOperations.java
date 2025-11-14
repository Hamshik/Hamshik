import java.util.Scanner;

public class StringOperations {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try{
            String a = in.nextLine();
            String b = in.nextLine();

            // 1. Sum of lengths
            System.out.println(a.length() + b.length());

            // 2. Lexicographic comparison
            System.out.println(a.compareTo(b) > 0 ? "Yes" : "No");

            // 3. Capitalize first letters
            String capA = a.substring(0, 1).toUpperCase() + a.substring(1);
            String capB = b.substring(0, 1).toUpperCase() + b.substring(1);
            System.out.println(capA + " " + capB);
        }
        finally{in.close();}
    }
}
