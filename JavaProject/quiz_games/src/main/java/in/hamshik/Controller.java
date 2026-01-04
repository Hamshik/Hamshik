package in.hamshik;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller implements Initializable{

    @FXML private Button choice1, choice2, choice3, choice4, nextBut;
    @FXML private Text ques_text;

    private ArrayList<String> ans_array = new ArrayList<>();
    private ArrayList<String> ques_array = new ArrayList<>();
    private ArrayList<String> choices_array = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){ Platform.runLater(() -> startInit()); }

    private void startInit(){
        reader();
        System.out.print(choices_array.toString());
        for(int i = 0; i < ques_array.size();i++){
            ques_text.setText(ques_array.get(i));
            choice1.setText(choices_array.get(i));
        }
    }

    private void reader()
    {
        try {
            InputStream is = App.class.getResourceAsStream("/quiz.json");
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

                ques_array.add(question);
                ans_array.add(answer);

                StringBuilder choiceBlock = new StringBuilder();
                for (JsonNode choiceNode : choicesNode) {
                    choiceBlock.append(choiceNode.asText()).append("\n");
                }

                
                choices_array.add("");
            }
        } catch (Exception e) {
            System.out.println("Error reading quiz.json: " + e.getMessage());
        }
    }

} 