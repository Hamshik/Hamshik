import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;

public class OddEvenCounter {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<Integer> even = new ArrayList<>();
        ArrayList<Integer> odd = new ArrayList<>();
        ArrayList<Integer> prime = new ArrayList<>();

        System.out.print("Enter numbers separated by spaces: ");
        String rawData = input.nextLine();
        String[] parts = rawData.trim().split(" ");
        int[] numbers = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Integer.parseInt(parts[i]);

            if (numbers[i] % 2 == 0) {
                even.add(numbers[i]);
            } else {
                odd.add(numbers[i]);
            }

            if (isPrime(numbers[i])) {
                prime.add(numbers[i]);
            }
        }

        System.out.println(displayResults(even, odd, prime));
    }

    static String displayResults(ArrayList<Integer> even, ArrayList<Integer> odd, ArrayList<Integer> prime) {
        Collections.sort(even);
        Collections.sort(odd);
        Collections.sort(prime);

        return "Even Numbers: " + even + " | Count: " + even.size() +
             "\nOdd Numbers: " + odd + " | Count: " + odd.size() +
             "\nPrime Numbers: " + prime + " | Count: " + prime.size();
    }

    static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
