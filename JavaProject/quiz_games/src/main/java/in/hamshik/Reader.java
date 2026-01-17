package in.hamshik;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reader {

    public static List<QuizEntry> loadQuiz(String path) {
        List<QuizEntry> quizzes = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream stream = App.class.getResourceAsStream(path)) {
            if (stream == null) throw new RuntimeException("JSON file not found: " + path);

            JsonNode nodes = mapper.readTree(stream);
            for (JsonNode node : nodes) {
                String question = node.get("question").asText();
                String answerStr = node.get("answer").asText();

                List<String> choices = mapper.convertValue(
                        node.get("choices"),
                        new TypeReference<List<String>>() {}
                );
                Collections.shuffle(choices);
                quizzes.add(new QuizEntry(question, choices, answerStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quizzes;
    }

    public static List<PerformEntry> loadUserStatus(String path) {
        List<PerformEntry> performs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream stream = App.class.getResourceAsStream(path)) {
            if (stream == null) throw new RuntimeException("JSON file not found: " + path);

            JsonNode nodes = mapper.readTree(stream);
            for (JsonNode node : nodes) {
                // Scoring
                int totalScore = node.get("totalScore").asInt();
                int bestScore = node.get("bestScore").asInt();
                double averageScore = node.get("averageScore").asDouble();
                double accuracy = node.get("accuracy").asDouble();


                performs.add(new PerformEntry(totalScore, bestScore, averageScore, accuracy));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return performs;
    }

}
