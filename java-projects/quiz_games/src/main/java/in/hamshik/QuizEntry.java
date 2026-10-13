package in.hamshik;
import java.util.List;
public record QuizEntry(String question, List<String> choices, String answer){}