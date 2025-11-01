package in.hamshik;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import com.fasterxml.jackson.databind.*;

public class Main {
    static ArrayList<String> ques = new ArrayList<>();
    static ArrayList<String> ans = new ArrayList<>();
    static ArrayList<String> choice = new ArrayList<>();
    static ArrayList<String> uniQChoice = new ArrayList<>();
    static JsonNode obj;
    static String question;
    static JsonNode choices;
    static String answer;

    public static void main(String[] args) {
        int i = 0;
        int inCorrectChoice = 0;
        String userAnswer;
        Scanner input = new Scanner(System.in);
        int inCorrect = 0;

        Readers();

        double score = ques.size();

        while (i < ques.size()) {
            System.out.printf("\nQ%d: %s\n", i + 1, ques.get(i));

            String[] options = choice.get(i).split("\n");
            for (String opt : options) {
                System.out.println(opt);
            }

            System.out.print("Your answer (don't write numbers): ");
            userAnswer = input.nextLine().trim();

            if(isCorrect(userAnswer , i)){
            	System.out.println("correct!!");
            	i++;
            }

            else{
            	inCorrect++;
                inCorrectChoice++;
                if (inCorrectChoice < 2) {
                    System.out.println("❌ Wrong Answer! Try again.");
                } else {
                    System.out.println("❌ Incorrect choice twice — skipping this question.");
                    score--;
                    i++;
                    inCorrectChoice = 0;
                }
            }
            
        }
        System.out.println(scoreCalculator(score) + "\nIncorrect:" + inCorrect);
        input.close();
    } 

    static void Readers() {
        try {
            InputStream is = Main.class.getResourceAsStream("/quiz.json");
            if (is == null) {
                System.out.println("quiz.json not found!");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode quizzes = mapper.readTree(is); // ✅ readTree for JsonNode

            for (JsonNode q : quizzes) {
                String question = q.get("question").asText();
                String answer = q.get("answer").asText();
                JsonNode choicesNode = q.get("choices");

                ques.add(question);
                ans.add(answer);

                StringBuilder choiceBlock = new StringBuilder();
                for (JsonNode choiceNode : choicesNode) {
                    choiceBlock.append(choiceNode.asText()).append("\n");
                }

                choice.add(choiceBlock.toString());
            }
        } catch (Exception e) {
            System.out.println("Error reading quiz.json: " + e.getMessage());
        }
    }



    static boolean isCorrect(String userAnswer, int i) {
        return userAnswer.trim().equals(ans.get(i).trim());
    }

    static String scoreCalculator(double score) {
        int lenQues = ques.size();
        double percentage = (score / lenQues) * 100;

        if (percentage >= 90)
            return String.format("Excellent!! You scored %.1f\nYour percentage: %.2f%%\nGrade: A+", score, percentage);
        else if (percentage >= 80) 
            return String.format("Excellent!! You scored %.1f\nYour percentage: %.2f%%\nGrade: A", score, percentage);
        else if (percentage >= 70) 
            return String.format("Very Good!! You scored %.1f\nYour percentage: %.2f%%\nGrade: B+", score, percentage);
        else if (percentage >= 60) 
            return String.format("Good!! You scored %.1f\nYour percentage: %.2f%%\nGrade: B", score, percentage);
        else if (percentage >= 50) 
            return String.format("Average!! You scored %.1f\nYour percentage: %.2f%%\nGrade: C", score, percentage);
        else if (percentage >= 40) 
            return String.format("Pass!! You scored %.1f\nYour percentage: %.2f%%\nGrade: D", score, percentage);
        else 
            return String.format("Fail!! You scored %.1f\nYour percentage: %.2f%%\nGrade: F", score, percentage);
    }
}
