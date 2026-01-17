package in.hamshik;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class UIManger {

    public static boolean transitionRunning = false;

    public UIManger(boolean transitionRunning) {UIManger.transitionRunning = transitionRunning;}
    
    public static void crossFade(Image newImage, QuizManager quizManager, 
        ImageView resultImage, List<Button> buttons, Runnable showQuestion) {

        transitionRunning = true;

        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(150), resultImage);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            resultImage.setImage(newImage);

            FadeTransition fadeIn =
                    new FadeTransition(Duration.millis(150), resultImage);
            fadeIn.setToValue(1);

            fadeIn.setOnFinished(ev -> {

                // ⏳ Small pause AFTER animation
                PauseTransition pause =
                        new PauseTransition(Duration.millis(200));
                pause.setOnFinished(event -> {

                    quizManager.nextQuestion();
                    showQuestion.run();

                    for (Button btn : buttons) {
                        btn.setDisable(false);
                    }

                    transitionRunning = false;
                });

                pause.play();
            });

            fadeIn.play();
        });

        fadeOut.play();
    }


    public static void resetChoiceButtons(List<Button> buttons) {

        for (Button btn : buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }
    }
}
