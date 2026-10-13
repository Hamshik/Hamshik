package in.hamshik;

import java.util.List;

import javafx.scene.control.Button;

public class MControllerVar {
    public  boolean transitionRunning;
    public  boolean shouldGONext;
    public  boolean isCorrect;
    public  String userAnswer;
    public  List<Button> buttons;
    public  int numberOfUserAttempts;

    public static boolean shouldStart = false;
    public static boolean isThereErr = false;

    public static String causedBy = null;

}