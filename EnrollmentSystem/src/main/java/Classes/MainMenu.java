package Classes;

import ExtraSources.DBConnect;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;

import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;


public class MainMenu {

    @FXML
    private ImageView image1;

    @FXML
    private Pane mainPane;

    @FXML
    private TextField password;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ToggleButton showPass;

    @FXML
    private Button signIn;

    @FXML
    private TextField username;

    @FXML
    private ScrollPane mainScrollPane;


    @FXML
    private void initialize() {
        signIn.setOnAction(e -> verifyLogin());

    }

    private void verifyLogin() {
        String enteredUsername = username.getText().trim();
        String enteredPassword = password.getText().trim();

        String query = "SELECT * FROM student WHERE sr_code = ? AND password = ? AND is_deleted = '0'";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {

            stmt.setString(1, enteredUsername);
            stmt.setString(2, enteredPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                handleStudentButtonAction(); // login successful
            } else {
                System.out.println("Invalid credentials"); // Or show dialog
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleStudentButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/StudentPortal.fxml"));
            Parent studentPortal = loader.load();

            // Wrap the studentPortal content in a ScrollPane
            ScrollPane scrollableStudentPortal = new ScrollPane();
            scrollableStudentPortal.setContent(studentPortal);

            // Enable scrolling in both directions
            scrollableStudentPortal.setFitToWidth(false);
            scrollableStudentPortal.setFitToHeight(false);
            scrollableStudentPortal.setPannable(true); // Allows dragging
            scrollableStudentPortal.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollableStudentPortal.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Optional: style the scrollpane
            scrollableStudentPortal.setStyle("-fx-background-color: transparent;");

            // Clear the mainPane and add the ScrollPane containing studentPortal
            mainPane.getChildren().clear();
            mainPane.getChildren().add(scrollableStudentPortal);

            // Make the ScrollPane fill the Pane (centered inside mainPane)
            scrollableStudentPortal.prefWidthProperty().bind(mainPane.widthProperty());
            scrollableStudentPortal.prefHeightProperty().bind(mainPane.heightProperty());

            // Optional fade transition effect
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), scrollableStudentPortal);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}