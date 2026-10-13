import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        BigInteger n =  in.nextBigInteger();
        System.out.println(isPrime(n) ? "prime":"not prime");
        in.close();
    }
    
    public static boolean isPrime(BigInteger n){
        if(n.isProbablePrime(50)) return true;
        else return false;
    }
}
