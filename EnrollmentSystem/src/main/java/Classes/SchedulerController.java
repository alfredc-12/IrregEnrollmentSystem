// File: EnrollmentSystem/src/main/java/Classes/SchedulerController.java
// Language: java
package Classes;

import ExtraSources.DBConnect;
import ExtraSources.ORToolsScheduler;
import GettersSetters.ScheduleModel;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;

public class SchedulerController {

    @FXML
    private ComboBox<String> filterSchedCb;

    @FXML
    private TableView<ScheduleModel> subjectScheduleTbl;

    @FXML
    private TableColumn<ScheduleModel, Integer> schedIdColumn;

    @FXML
    private TableColumn<ScheduleModel, String> subjectColumn;

    @FXML
    private TableColumn<ScheduleModel, String> daysColumn;

    @FXML
    private TableColumn<ScheduleModel, String> timeinColumn;

    @FXML
    private TableColumn<ScheduleModel, String> timeoutColumn;

    @FXML
    private TableColumn<ScheduleModel, String> roomNameColumn;

    @FXML
    private TableColumn<ScheduleModel, String> instructorColumn;

    @FXML
    private TableColumn<ScheduleModel, String> sectionColumn;

    @FXML
    private TableColumn<ScheduleModel, String> yearLevelColumn;

    @FXML
    private TableColumn<ScheduleModel, String> majorColumn;

    @FXML
    private ToggleButton btnPull;

    @FXML
    private StackPane mainFrame; // Main content pane

    @FXML
    private AnchorPane popUpContainer; // Dedicated overlay container

    @FXML
    private Pane contentPane; // Content pane to be shifted


    @FXML
    private Label scheduleLbl;



    private ObservableList<String> allSubjectItems;
    private ObservableList<String> allFacultyItems;

    private Parent popupRoot;

    @FXML
    public void initialize() {
        schedIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSchedId()).asObject());
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
        daysColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDay()));
        timeinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeIn()));
        timeoutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeOut()));
        roomNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFaculty()));
        sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSection()));
        yearLevelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getYearLevel()));
        majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMajor()));

        // Set event handlers for the toggle button
        btnPull.setOnAction(e -> showPopUp());



        loadScheduleData();
    }

    public ScheduleModel getSelectedSchedule() {
        return subjectScheduleTbl.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void showPopUp() {
        try {
            if (popupRoot == null || !popUpContainer.getChildren().contains(popupRoot)) {
                // Load the pop‑up FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Pop-upSchedControl.fxml"));
                popupRoot = loader.load();

                // Retrieve the pop\-up controller and pass the parent reference.
                PopUpSchedController popUpCtrl = loader.getController();
                popUpCtrl.setParentController(this);

                popupRoot.getStylesheets().add(getClass().getResource("/CSS/popUpSched.css").toExternalForm());

                Platform.runLater(() -> {
                    // Hide the schedule label and filter combo box while the popup is active
                    scheduleLbl.setVisible(false);
                    filterSchedCb.setVisible(false);

                    // Set custom positions for the pop‑up
                    double customX = 0;
                    double customY = 220;
                    popupRoot.setLayoutX(customX);
                    popupRoot.setLayoutY(customY);

                    // Manually set a popup height and set its initial translate offset (slides in from below)
                    double popupHeight = 150;
                    popupRoot.setTranslateY(popupHeight);

                    // Add the pop‑up to the dedicated container
                    popUpContainer.getChildren().add(popupRoot);

                    // Hide the btnPull button temporarily
                    btnPull.setVisible(false);

                    // Transition for the pop‑up sliding in
                    TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), popupRoot);
                    slideIn.setInterpolator(Interpolator.EASE_BOTH);
                    slideIn.setFromY(popupHeight);
                    slideIn.setToY(0);

                    // Transition to shift the table upward by the pop‑up's height
                    TranslateTransition tableShift = new TranslateTransition(Duration.millis(300), subjectScheduleTbl);
                    tableShift.setInterpolator(Interpolator.EASE_BOTH);
                    tableShift.setToY(-70);

                    // Animation to adjust the preferred height of the table view
                    double manualTablePrefHeight = 210; // <-- Adjust this value manually
                    double initialPrefHeight = subjectScheduleTbl.getPrefHeight();
                    // Animate from the current prefHeight to the new value
                    KeyValue kv = new KeyValue(subjectScheduleTbl.prefHeightProperty(), manualTablePrefHeight, Interpolator.EASE_BOTH);
                    KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
                    Timeline heightAnimation = new Timeline(kf);

                    // Run all animations in parallel
                    ParallelTransition parallelTransition = new ParallelTransition(slideIn, tableShift, heightAnimation);
                    parallelTransition.play();
                    parallelTransition.setOnFinished(ev -> {
                        // Bring the table view to front and apply a clipping rectangle based on mainFrame's bounds
                        Bounds mainBounds = mainFrame.getLayoutBounds();
                        javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle(mainBounds.getWidth(), mainBounds.getHeight());
                        subjectScheduleTbl.setClip(clipRect);
                        subjectScheduleTbl.toFront();
                    });

                    // Create and add a click event filter to detect clicks outside the popup
                    EventHandler<MouseEvent> clickOutsideHandler = new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // Check if the clicked node is inside the table view
                            boolean clickedInsideTable = false;
                            if (event.getTarget() instanceof javafx.scene.Node) {
                                javafx.scene.Node target = (javafx.scene.Node) event.getTarget();
                                while (target != null) {
                                    if (target == subjectScheduleTbl) {
                                        clickedInsideTable = true;
                                        break;
                                    }
                                    target = target.getParent();
                                }
                            }
                            // Get the popup's bounds in the scene
                            Bounds popupBounds = popupRoot.localToScene(popupRoot.getBoundsInLocal());
                            // Close the popup only if the click is outside the popup's bounds and not inside the table view
                            if (!popupBounds.contains(event.getSceneX(), event.getSceneY()) && !clickedInsideTable) {
                                // Reverse the table shift animation
                                TranslateTransition reverseTableShift = new TranslateTransition(Duration.millis(300), subjectScheduleTbl);
                                reverseTableShift.setInterpolator(Interpolator.EASE_BOTH);
                                reverseTableShift.setFromY(-70);
                                reverseTableShift.setToY(0);

                                // Reset the preferred height animation back to original value
                                KeyValue kvReset = new KeyValue(subjectScheduleTbl.prefHeightProperty(), 314, Interpolator.EASE_BOTH);
                                KeyFrame kfReset = new KeyFrame(Duration.millis(300), kvReset);
                                Timeline heightResetAnimation = new Timeline(kfReset);

                                // Animate the popup container to shrink using scale transition
                                ScaleTransition shrinkPopup = new ScaleTransition(Duration.millis(300), popupRoot);
                                shrinkPopup.setInterpolator(Interpolator.EASE_BOTH);
                                shrinkPopup.setFromX(1);
                                shrinkPopup.setFromY(1);
                                shrinkPopup.setToX(0.0);
                                shrinkPopup.setToY(0.0);

                                ParallelTransition exitTransition = new ParallelTransition(reverseTableShift, heightResetAnimation, shrinkPopup);
                                exitTransition.play();
                                exitTransition.setOnFinished(ev -> {
                                    popUpContainer.getChildren().remove(popupRoot);
                                    popupRoot = null;
                                    btnPull.setVisible(true);
                                    scheduleLbl.setVisible(true);
                                    filterSchedCb.setVisible(true);
                                    subjectScheduleTbl.setClip(null);
                                    mainFrame.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                                });
                            }
                        }
                    };
                    mainFrame.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, clickOutsideHandler);
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void loadScheduleData() {
        ObservableList<ScheduleModel> scheduleData = FXCollections.observableArrayList();
        String sql = "SELECT s.sched_id, sub.subj_code, s.days, s.time_in, s.time_out, " +
                "CONCAT(r.room_name, ' - ', r.room_type) AS room, " +
                "CONCAT(f.first_name, ' ', IFNULL(f.middle_name, ''), ' ', f.last_name) AS faculty_name, " +
                "sec.section_name, sec.year_level, sec.track AS major " +  // Added year_level and track
                "FROM subsched s " +
                "JOIN subjects sub ON s.subject_id = sub.sub_id " +
                "JOIN faculty f ON s.faculty_id = f.faculty_id " +
                "JOIN section sec ON s.section_id = sec.section_id " +
                "JOIN rooms r ON s.room_id = r.room_id " +
                "ORDER BY s.sched_id ASC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            while (rs.next()) {
                Time timeInValue = rs.getTime("time_in");
                Time timeOutValue = rs.getTime("time_out");
                String timeInFormatted = sdf.format(timeInValue);
                String timeOutFormatted = sdf.format(timeOutValue);

                ScheduleModel schedule = new ScheduleModel(
                        rs.getInt("sched_id"),
                        rs.getString("subj_code"),
                        rs.getString("days"),
                        timeInFormatted,
                        timeOutFormatted,
                        rs.getString("room"),
                        rs.getString("faculty_name"),
                        rs.getString("section_name"),
                        rs.getString("year_level"),    // Added year_level
                        rs.getString("major")          // Added major (track)
                );
                scheduleData.add(schedule);
            }
            subjectScheduleTbl.setItems(scheduleData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}