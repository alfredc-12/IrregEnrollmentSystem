package Classes;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DeanController implements Initializable {

    @FXML
    private Button btnHymn;

    @FXML
    private MenuButton btnMissionVision;

    @FXML
    private Label courseYearLevel;

    @FXML
    private Label date;

    @FXML
    private ScrollPane deanNavigationScroll;

    @FXML
    private ImageView deanPhoto;

    @FXML
    private Label enrollent;

    @FXML
    private Label semester;

    @FXML
    private Label semester1;

    @FXML
    private Label studentName;

    @FXML
    private Label time;

    @FXML
    private AnchorPane deanNavigation;

    @FXML
    private DeanNavigationController deanNavigationController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the date and time display
        setupDateTimeDisplay();

        // Add a listener to trigger initialization after the scene is loaded
        Platform.runLater(() -> {
            if (deanNavigationController != null) {
                // Load CSS for dean navigation
                deanNavigation.getStylesheets().add(
                        getClass().getResource("/CSS/dean-navigation.css").toExternalForm());

                // Load data
                deanNavigationController.loadStudentSectionData();
                deanNavigationController.loadFilterOptions();
            }
        });
    }

    private void setupDateTimeDisplay() {
        // Create a timeline to update the time every second
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();

            // Format date: mm-dd-yyyy (Friday, April 11, 2025)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            date.setText(dateFormatter.format(now));

            // Format time: 12-hour format with AM/PM (05:10:13 PM)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
            time.setText(timeFormatter.format(now));
        }));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}