package in.hamshik;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.type.TypeReference;
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
    LinkedList<ArrayList<String>> choices_array = new LinkedList<>();
    private int currentIndex = 0;
    boolean isOver = false;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){ Platform.runLater(() -> startInit()); }

    private void startInit()
    {
        Thread readerThread = new Thread(() -> reader());
        try{readerThread.start();}
        catch(Exception e){}
        if(isOver) readerThread.interrupt();
        if(currentIndex < ques_array.size()) showQuestion();
    }

    private void reader()
    {
        InputStream reasourcStream;
        ObjectMapper mapper  = new ObjectMapper();
        JsonNode quizzes;
        String question;
        String answer;
        JsonNode choicesNode;
        ArrayList<String> choiceList = new ArrayList<>();

        try {
            reasourcStream = App.class.getResourceAsStream("/quiz.json");
            if (reasourcStream == null) 
                new Exception("Json file is not found");

            quizzes = mapper.readTree(reasourcStream); // ✅ readTree for JsonNode

            for (JsonNode uni_Node : quizzes) {
                question = uni_Node.get("question").asText();
                answer = uni_Node.get("answer").asText();
                choicesNode = uni_Node.get("choices");
                choiceList = mapper.convertValue(choicesNode, new TypeReference<ArrayList<String>>() {});

                choices_array.add(choiceList);
                ques_array.add(question);
                ans_array.add(answer);
                
            }
        } catch (Exception e) {
            System.out.println("Error reading quiz.json: " + e.getMessage());
        }
        isOver = true;
    }

    private void showQuestion() {

        ques_text.setText(currentIndex +". "+ ques_array.get(currentIndex));

        choice1.setText(choices_array.get(currentIndex).get(0));
        choice2.setText(choices_array.get(currentIndex).get(1));
        choice3.setText(choices_array.get(currentIndex).get(2));
        choice4.setText(choices_array.get(currentIndex).get(3));
    }

    @FXML private void handleNext() {
        if (currentIndex < ques_array.size() - 1) {
            currentIndex++;
            showQuestion();
        }
    }
} 