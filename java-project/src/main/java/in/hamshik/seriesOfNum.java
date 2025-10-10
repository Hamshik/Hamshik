import java.util.*;

public class seriesOfNum {
    public static void main(String[] args) {
        int a, b, n, query;
        int ans;

        Scanner in = new Scanner(System.in);
        System.out.print("Enter the #(# of queries): ");
        query = in.nextInt();

        ArrayList<ArrayList<Integer>> queries = new ArrayList<>();

        // Read queries
        for (int q = 0; q < query; q++) {
        	System.out.print("Enter the value for a: ");
            a = in.nextInt();

            System.out.print("Enter the value for b: ");
            b = in.nextInt();

            System.out.print("Enter the value for n(where to end): ");
            n = in.nextInt();

            ArrayList<Integer> values = new ArrayList<>();
            values.add(a);
            values.add(b);
            values.add(n);
            queries.add(values);
        }

        // Process each query
        for (int j = 0; j < queries.size(); j++) {
            a = queries.get(j).get(0);
            b = queries.get(j).get(1);
            n = queries.get(j).get(2);

            int sum = a;
            ArrayList<Integer> answers = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                sum += (1 << i) * b; // 2^i * b
                answers.add(sum);
            }

            // Print series for this query
            for (int x : answers) {
                System.out.print(x + " ");
            }
            System.out.println();
        }

        in.close();
    }
}
