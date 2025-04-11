package Classes;

import ExtraSources.DBConnect;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;


public class MainMenu {

    public static String userLoggedIn;

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

    private static MainMenu instance;

    @FXML
    private void initialize() {
        instance = this;
        signIn.setOnAction(e -> verifyLogin());
    }

    public static ScrollPane getMainScrollPane() {
        return instance.mainScrollPane;
    }

    private void showConfirmationDialog(String message,
                                        String confirmText, Runnable onConfirm,
                                        String cancelText, Runnable onCancel,
                                        String iconType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Notification.fxml"));
            Parent root = loader.load();
            ExtraSources.Notification notification = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            notification.setStage(dialogStage);
            notification.setMessage(message);
            notification.setButtons(confirmText, () -> {
                dialogStage.close();
                onConfirm.run();
            }, cancelText, () -> {
                dialogStage.close();
                onCancel.run();
            });
            notification.setIcon(iconType);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyLogin() {

        if (username.getText().trim().isEmpty() ||
                password.getText().trim().isEmpty()) {

            // Show a warning dialog with only one button ("Ok").
            showConfirmationDialog(
                    "Warning: Required fields are empty. Please fill in all required fields.",
                    "Ok",    // Confirm button text (only one button will be shown)
                    () -> { /* No action on confirm */ },
                    "",      // Cancel button text is empty so it won't show
                    () -> { /* Do nothing on cancel */ },
                    "warning"
            );
            return;
        }

        String enteredUsername = username.getText().trim();
        String enteredPassword = password.getText().trim();

        String query = "SELECT * FROM student WHERE sr_code = ? AND password = ? AND is_deleted = '0'";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {

            stmt.setString(1, enteredUsername);
            stmt.setString(2, enteredPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userLoggedIn = enteredUsername;
                handleStudentButtonAction(); // login successful
            } else {
                showConfirmationDialog(
                        "Authentication Error! Check Details Inputted!",
                        "Ok",    // Confirm button text (only one button will be shown)
                        () -> { /* No action on confirm */ },
                        "",      // Cancel button text is empty so it won't show
                        () -> { /* Do nothing on cancel */ },
                        "warning"
                );
                System.out.println("Invalid credentials"); // Or show dialog
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleStudentButtonAction() {
        try {
            // Show the loading screen first
            FXMLLoader loadingLoader = new FXMLLoader(getClass().getResource("/FXML/Loading.fxml"));
            Parent loadingScreen = loadingLoader.load();

            // Wrap the loadingScreen in a StackPane to center it
            StackPane loadingWrapper = new StackPane(loadingScreen);
            loadingWrapper.setAlignment(Pos.CENTER);

            // Clear the anchorPane (the ScrollPane's content) and add the loading screen
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(loadingWrapper);

            // Create FadeTransition for the loading screen
            FadeTransition loadingFade = new FadeTransition(Duration.millis(1000), loadingScreen);
            loadingFade.setFromValue(0.0);
            loadingFade.setToValue(1.0);
            loadingFade.setCycleCount(FadeTransition.INDEFINITE); // Looping fade in and out
            loadingFade.setInterpolator(Interpolator.EASE_BOTH); // Use ease interpolator for smooth transition
            loadingFade.setAutoReverse(true); // Fade in and out
            loadingFade.play(); // Start fade transition

            // Start loading the StudentPortal.fxml in the background
            Task<Parent> loadStudentPortalTask = new Task<Parent>() {
                @Override
                protected Parent call() throws Exception {
                    FXMLLoader studentPortalLoader = new FXMLLoader(getClass().getResource("/FXML/StudentPortal.fxml"));
                    return studentPortalLoader.load();  // Load the StudentPortal.fxml
                }
            };

            loadStudentPortalTask.setOnSucceeded(event -> {
                // When loading is complete, switch to the StudentPortal.fxml
                Parent studentPortal = loadStudentPortalTask.getValue();

                // Clear the loading screen and add the StudentPortal to the anchorPane
                anchorPane.getChildren().clear();
                StackPane centeredWrapper = new StackPane(studentPortal);
                centeredWrapper.setAlignment(Pos.CENTER);
                // Let the wrapper use its preferred size.
                centeredWrapper.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

                // Add the centered wrapper to anchorPane
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

                // Create a fade transition for the StudentPortal to fade in
                FadeTransition studentPortalFade = new FadeTransition(Duration.millis(1000), studentPortal);
                studentPortalFade.setFromValue(0.0);
                studentPortalFade.setToValue(1.0);
                studentPortalFade.setInterpolator(Interpolator.EASE_BOTH); // Smooth transition
                studentPortalFade.play(); // Start the fade in effect

            });

            loadStudentPortalTask.setOnFailed(event -> {
                // Handle loading error (e.g., show a dialog or log the error)
                System.out.println("Failed to load StudentPortal.fxml");
            });

            // Start the background task
            new Thread(loadStudentPortalTask).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}