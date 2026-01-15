package in.hamshik;


import java.util.List;

public class QuizManager {

    private final List<QuizEntry> questions;
    private int currentIndex = 0;
    private int score;

    public QuizManager(List<QuizEntry> questions) {
        this.questions = questions;
        this.score = questions.size();
    }

    public QuizEntry getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public boolean checkAnswer(String userAns) {
        boolean isCorrect = (userAns.equals(getCurrentQuestion().answer()));
        if (!isCorrect) score--;
        return isCorrect;
    }

    public boolean hasNext() {
        return currentIndex < questions.size() - 1;
    }

    public void nextQuestion() {
        if (hasNext()) currentIndex++;
    }

    public int getScore() { return score; }
    public int getCurrentIndex() { return currentIndex + 1; }
    public int getTotalQuestions() { return questions.size(); }
}
