import java.util.Arrays;
import java.util.Scanner;

public class StringSorting {
    public static String getSmallestAndLargest(String s, int k) {
        
        String smallest = null;
        String largest = null;
        String[] word = new String[s.length() - k + 1];

        for(int i = 0; i <= s.length() - k; i++) word[i] = s.substring(i, i + k);

        Arrays.sort(word);

        smallest = word[0];
        largest = word[word.length - 1];

        return smallest + "\n" + largest;
    }


    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the Str: ");
        String s = scan.nextLine();
        System.out.print("Enter the # to be divided: ");
        int k = scan.nextInt();
        scan.close();
        
        if(s.length() >= k) System.out.println(getSmallestAndLargest(s, k));
        else throw new Exception("# is too large");
    }
}