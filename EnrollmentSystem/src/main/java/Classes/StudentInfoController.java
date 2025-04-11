package Classes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentInfoController implements Initializable {

    @FXML
    public Label addressLbl;

    @FXML
    public Button btnExit;

    @FXML
    public Label contactLbl;

    @FXML
    public Label emailLbl;

    @FXML
    public AnchorPane mainFrame;

    @FXML
    public Label nameLbl;

    @FXML
    Pane navigation;

    @FXML
    public Pane pictureFrame;

    @FXML
    public Label semesterLbl;

    @FXML
    public Label sexLbl;

    @FXML
    public Label srcodeLbl;

    @FXML
    public ImageView studentPic;

    @FXML
    public Label yearLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the close button functionality
        setupCloseButton();
    }

    private void setupCloseButton() {
        btnExit.setOnAction(event -> {
            // Get the stage
            Stage stage = (Stage) btnExit.getScene().getWindow();

            // Create fade-out transition for the root element
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(300), mainFrame);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            // Create scale transition (shrink effect)
            javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(
                    javafx.util.Duration.millis(300), mainFrame);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.8);
            scaleOut.setToY(0.8);
            scaleOut.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            // Combine transitions
            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition();
            pt.getChildren().addAll(fadeOut, scaleOut);

            // Close the stage when animation finishes
            pt.setOnFinished(e -> stage.close());

            // Start animation
            pt.play();
        });
    }

}
