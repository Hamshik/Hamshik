package in.hamshik;

import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> choices;
    private int answerIndex; // store as 0-based index

    public QuizQuestion() { }

    public QuizQuestion(String question, List<String> choices, int answerIndex) {
        this.question = question;
        this.choices = choices;
        this.answerIndex = answerIndex;
    }

    public String getQuestion() { return question; }
    public List<String> getChoices() { return choices; }
    public int getAnswerIndex() { return answerIndex; }

    public void setQuestion(String question) { this.question = question; }
    public void setChoices(List<String> choices) { this.choices = choices; }
    public void setAnswerIndex(int answerIndex) { this.answerIndex = answerIndex; }
}
