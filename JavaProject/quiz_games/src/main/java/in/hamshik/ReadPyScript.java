package in.hamshik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class ReadPyScript {

    private String userAnswer;
    private String currentQuestion;
    private Text resultText;

    public ReadPyScript(String userAnswer, String currentQuestion, Text resultText) {
        this.userAnswer = userAnswer;
        this.currentQuestion = currentQuestion;
        this.resultText = resultText;
    }

    public void checkAnswer() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("python", "script_ai.py", currentQuestion, userAnswer);
        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String result = reader.readLine(); // Python prints "Correct" or "Incorrect"
        p.waitFor();

        Platform.runLater(() -> {
            resultText.setText(result);
            resultText.getStyleClass().removeAll("correct", "incorrect");
            resultText.getStyleClass().add(result.equals("Correct") ? "correct" : "incorrect");
        });

    }
}