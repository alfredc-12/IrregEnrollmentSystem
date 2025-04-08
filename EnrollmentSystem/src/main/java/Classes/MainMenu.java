package Classes;

import ExtraSources.DBConnect;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;


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
    private AnchorPane anchorPane;

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

            // Wrap the studentPortal in a StackPane so it is centered automatically.
            StackPane centeredWrapper = new StackPane(studentPortal);
            centeredWrapper.setAlignment(Pos.CENTER);
            // Let the wrapper use its preferred size.
            centeredWrapper.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            // Clear the anchorPane (the ScrollPane's content) and add the centeredWrapper.
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(centeredWrapper);

            // Force CSS and layout passes to get proper bounds.
            centeredWrapper.applyCss();
            centeredWrapper.layout();
            anchorPane.applyCss();
            anchorPane.layout();

            // Set the AnchorPane's preferred size to match the content's size.
            double wrapperWidth = centeredWrapper.getLayoutBounds().getWidth();
            double wrapperHeight = centeredWrapper.getLayoutBounds().getHeight();
            anchorPane.setPrefWidth(wrapperWidth);
            anchorPane.setPrefHeight(wrapperHeight);

            // A helper method to center the wrapper in the ScrollPane's viewport.
            Runnable centerInViewport = () -> {
                // Recalculate the current content size.
                double contentWidth = centeredWrapper.getLayoutBounds().getWidth();
                double contentHeight = centeredWrapper.getLayoutBounds().getHeight();
                Bounds viewport = mainScrollPane.getViewportBounds();
                double offsetX = (viewport.getWidth() - contentWidth) > 0
                        ? (viewport.getWidth() - contentWidth) / 2
                        : 0;
                double offsetY = (viewport.getHeight() - contentHeight) > 0
                        ? (viewport.getHeight() - contentHeight) / 2
                        : 0;
                centeredWrapper.setLayoutX(offsetX);
                centeredWrapper.setLayoutY(offsetY);
            };

            // Listen to changes in the scroll pane's viewport bounds.
            mainScrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                Platform.runLater(centerInViewport);
            });

            // Listen for changes in the wrapper's layout bounds to update preferred size and re-center.
            centeredWrapper.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                double newWidth = newBounds.getWidth();
                double newHeight = newBounds.getHeight();
                anchorPane.setPrefWidth(newWidth);
                anchorPane.setPrefHeight(newHeight);
                Platform.runLater(centerInViewport);
            });

            // Schedule an initial centering after the layout pass.
            Platform.runLater(centerInViewport);

            // Optional fade transition on studentPortal.
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), studentPortal);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}