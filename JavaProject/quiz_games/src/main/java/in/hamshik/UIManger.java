package in.hamshik;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class UIManger {

    private QuizManager quizManager;
    private MControllerVar mControllerVar;



    UIManger(QuizManager quizManager, MControllerVar mControllerVar) {
        this.quizManager = quizManager;
        this.mControllerVar = mControllerVar;
    }
    
    public void runAct(Runnable action) {
        mControllerVar.transitionRunning = true;
        quizManager.nextQuestion();
        action.run();
        for (Button btn : mControllerVar.buttons)
            btn.setDisable(false);
        mControllerVar.transitionRunning = false;
    }


    public void resetChoiceButtons(List<Button> buttons) {

        for (Button btn : buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }
    }

    public void showFinalScore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/percentHandler.fxml"));
            Parent root = loader.load();

            EndGameHandler handler = loader.getController();
            handler.setData(quizManager);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeJson(){
        
        final Path HOME = Paths.get(System.getProperty("user.home"));

        final Path PROJECT_PATH = HOME.resolve(
                Paths.get("Documents", "hamshik", "quiz_games",
                        "src", "main", "java", "resource", "in", "hamshik")
        );

        // ✅ Python executable (NOT activate)
        final Path PY_PATH = HOME.resolve(
                Paths.get(".venv", "bin", "python")
        );

        // ✅ Script inside project directory
        final Path SCRIPT_PATH = PROJECT_PATH.resolve("script.py");

        final Path WORKING_DIR = PROJECT_PATH;

        try {
            StaticUtilities.runPy(
                    PY_PATH.toString(),
                    SCRIPT_PATH.toString(),
                    WORKING_DIR.toString()
            );
        } catch (Exception e) {e.getStackTrace();}
    }

    public static Stage createLoadingStage() {
        ProgressIndicator indicator = new ProgressIndicator();
        Label label = new Label("Loading... Please wait");

        VBox box = new VBox(15, indicator, label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(box, 250, 150));

        return stage;
    }


}
