package Classes; //Hello - Marlo

import Application.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard {

    @FXML
    private Label adminLabel;

    @FXML
    private ImageView facultyBut;

    @FXML
    private AnchorPane leftpane;

    @FXML
    private ImageView pictureBut;

    @FXML
    private AnchorPane rightpane;

    @FXML
    private ImageView sectionBut;

    @FXML
    private ImageView studentBut;

    @FXML
    private void openFaculty() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/FXML/Faculty.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Faculty!");
        stage.setScene(scene);
        stage.show();
        // Close the current stage
        Stage currentStage = (Stage) adminLabel.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void openStudent() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/FXML/student-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Student!");
        stage.setScene(scene);
        stage.show();
        // Close the current stage
        Stage currentStage = (Stage) adminLabel.getScene().getWindow();
        currentStage.close();
    }
}

