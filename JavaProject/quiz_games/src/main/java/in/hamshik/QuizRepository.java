package in.hamshik;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizRepository {

    public static List<QuizEntry> loadQuizzes(String path) {
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


}
