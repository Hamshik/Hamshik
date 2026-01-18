package in.hamshik;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static List<LeaderBoardEntry> loadPlayers(String path) {
        List<LeaderBoardEntry> performs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream stream = App.class.getResourceAsStream(path)) {
            if (stream == null) throw new RuntimeException("JSON file not found: " + path);

            JsonNode nodes = mapper.readTree(stream);
            for (JsonNode node : nodes) {
                // Scoring
                int score = node.get("score").asInt();
                String name = node.get("name").asText();

                performs.add(new LeaderBoardEntry(name, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return performs;
    }

    public static void writeJson(String pythonPath, String scriptPath, String workingDic) throws Exception {

        // Array of all paths to check
        String[] pathsToCheck = {pythonPath, scriptPath, workingDic};

        // Loop through each path and check if it exists
        for (String p : pathsToCheck) {
            Path path = Paths.get(p);
            if (!Files.exists(path)) throw new Exception("Path does not exist: " + p);
            else System.out.println("Exists");
        }

        // At this point, all paths exist, we can run Python
        ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
        pb.directory(new File(workingDic));

        Process process = pb.start();

        // Read Python output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            System.out.println("Python Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // Wait for Python to finish
        int exitCode = process.waitFor();
        System.out.println("Python process finished with exit code: " + exitCode);

        if (exitCode != 0) throw new Exception("Python failed to execute.");
        
    }

}