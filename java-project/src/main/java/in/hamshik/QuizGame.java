package in.hamshik;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;

public class QuizGame {
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
    } 

    static void Readers() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/quiz.json");
            JsonNode quizArray = mapper.readTree(file);

            for (JsonNode value : quizArray) {
                obj = value;
                question = obj.get("question").asText();
                choices = obj.get("choices");
                answer = obj.get("answer").asText();

                ques.add(question);
                ans.add(answer);

                StringBuilder choiceBlock = new StringBuilder();
                for (JsonNode choice : choices) {
                    choiceBlock.append(choice.asText()).append("\n");
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

        if (percentage >= 90) {
            return String.format("Excellent!! You scored %.1f\nYour percentage: %.2f%%\nGrade: A+", score, percentage);
        } else if (percentage >= 80) {
            return String.format("Excellent!! You scored %.1f\nYour percentage: %.2f%%\nGrade: A", score, percentage);
        } else if (percentage >= 70) {
            return String.format("Very Good!! You scored %.1f\nYour percentage: %.2f%%\nGrade: B+", score, percentage);
        } else if (percentage >= 60) {
            return String.format("Good!! You scored %.1f\nYour percentage: %.2f%%\nGrade: B", score, percentage);
        } else if (percentage >= 50) {
            return String.format("Average!! You scored %.1f\nYour percentage: %.2f%%\nGrade: C", score, percentage);
        } else if (percentage >= 40) {
            return String.format("Pass!! You scored %.1f\nYour percentage: %.2f%%\nGrade: D", score, percentage);
        } else {
            return String.format("Fail!! You scored %.1f\nYour percentage: %.2f%%\nGrade: F", score, percentage);
        }
    }
}
