package Classes;

import ExtraSources.DBConnect;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.StringConverter;
import javafx.animation.Interpolator;
import GettersSetters.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.awt.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.util.*;

import javafx.scene.image.Image;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StudentPortal {

    private double origAdvisoryY, origPolicyY, origStudentTabY;
    private double origMainPaneH, origInvisibleH;
    private double textHeight, shiftDistance;

    private final Set<Subject> excluded = new HashSet<>();
    private int studentId;
    private String currentSemester, currentAcadYear;
    private String srCode = MainMenu.userLoggedIn;
    private final Map<SectionModel,Integer> sectionIdMap = new HashMap<>();

    @FXML
    private void initialize() {
        studentPhoto.setOpacity(1.0);
        installSpeechBubbleTooltips(homePane);
        setButtonHoverEffect(buttons);
        setImageHoverInsideButtons(buttons);

        loadStudentInfo();
        startRealTimeClock(date, time);
        setupMissionVisionToggle();
        tabFade();

        // FIRST: load the studentId / currentSemester / currentAcadYear
        loadStudentAndCurrent();
        // THEN: disable the regular pane if already enrolled
        checkStudentStatusAndDisablePane();
        // NOW you can safely populate the sections combo
        loadStudentSectionOptions();
        setupPickMajor();
        // finally wire up the table
        setupTableAndEvents();
        autoFitTableColumns(enrollTable);
    }

    private void setupTableAndEvents() {
        // columns
        codeCol         .setCellValueFactory(new PropertyValueFactory<>("subjCode"));
        subjectNameCol  .setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        unitsCol        .setCellValueFactory(new PropertyValueFactory<>("units"));
        lectureCol      .setCellValueFactory(new PropertyValueFactory<>("lecture"));
        labCol          .setCellValueFactory(new PropertyValueFactory<>("lab"));
        acadTrackCol    .setCellValueFactory(new PropertyValueFactory<>("acadTrack"));
        prerequisiteCol .setCellValueFactory(new PropertyValueFactory<>("prerequisite"));

        // load data
        enrollTable.setItems(getEnrollableSubjects(srCode));

        // double‐click to exclude/include
        enrollTable.setRowFactory(tv -> {
            TableRow<Subject> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    Subject s = row.getItem();
                    if (excluded.remove(s)) {
                        row.setStyle("");
                    } else {
                        excluded.add(s);
                        row.setStyle("-fx-background-color: #cb0600;");
                    }
                    updateTotalUnits();
                }
            });
            return row;
        });

        // total units
        updateTotalUnits();

        // enroll button
        enrollBut.setOnAction(e -> confirmEnroll());
        signOut.setOnAction(e -> signOut(e));
    }

    @FXML
    private void signOut(ActionEvent event) {
        try {
            // Reset logged-in user
            MainMenu.userLoggedIn = null;

            // Get the current stage
            Stage stage = (Stage) signOut.getScene().getWindow();

            // Load the Loading.fxml
            FXMLLoader loadingLoader = new FXMLLoader(getClass().getResource("/FXML/Loading.fxml"));
            Parent loadingScreen = loadingLoader.load();

            // Create a scene with the loading screen
            Scene loadingScene = new Scene(loadingScreen);
            stage.setScene(loadingScene);

            // Set up fade animation on loading screen
            FadeTransition loadingFade = new FadeTransition(Duration.seconds(1), loadingScreen);
            loadingFade.setFromValue(0.0);
            loadingFade.setToValue(1.0);
            loadingFade.setCycleCount(FadeTransition.INDEFINITE);
            loadingFade.setAutoReverse(true);
            loadingFade.setInterpolator(Interpolator.EASE_BOTH);
            loadingFade.play();

            // Load MainMenu.fxml in the background
            Task<Parent> loadMainMenuTask = new Task<>() {
                @Override
                protected Parent call() throws Exception {
                    FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/FXML/MainMenu.fxml"));
                    return mainMenuLoader.load();
                }
            };

            loadMainMenuTask.setOnSucceeded(e -> {
                Parent mainMenuRoot = loadMainMenuTask.getValue();

                // Switch to MainMenu.fxml scene
                Platform.runLater(() -> {
                    // Optional: Stop loading animation
                    loadingFade.stop();

                    Scene mainMenuScene = new Scene(mainMenuRoot);
                    stage.setScene(mainMenuScene);

                    // Fade in the MainMenu
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mainMenuRoot);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.setInterpolator(Interpolator.EASE_BOTH);
                    fadeIn.play();
                });
            });

            loadMainMenuTask.setOnFailed(e -> {
                System.err.println("Failed to load MainMenu.fxml: " + loadMainMenuTask.getException());
            });

            // Start the background task
            new Thread(loadMainMenuTask).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public static void autoFitTableColumns(TableView<?> table) {
        for (TableColumn<?,?> col : table.getColumns()) {
            // Measure header text
            Text header = new Text(col.getText());
            double max = header.getLayoutBounds().getWidth();

            // Measure each cell by row index
            int rowCount = table.getItems().size();
            for (int row = 0; row < rowCount; row++) {
                Object cellValue = col.getCellData(row);  // <-- use index overload
                if (cellValue != null) {
                    Text t = new Text(cellValue.toString());
                    double w = t.getLayoutBounds().getWidth();
                    if (w > max) max = w;
                }
            }

            // add a little padding
            col.setPrefWidth(max + 20);
        }
    }

    @FXML
    private void tabFade() {
        studentTab.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getContent() != null) {
                Node content = newTab.getContent();

                // Make sure the content is visible and opacity starts from 0
                content.setOpacity(0);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), content);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.setInterpolator(Interpolator.EASE_BOTH);
                fadeIn.play();
            }
        });
    }

    @FXML
    private void setupMissionVisionToggle() {
        // capture originals (unchanged) …
        origAdvisoryY   = advisoryPane.getLayoutY();
        origPolicyY     = policyPane.getLayoutY();
        origStudentTabY = studentTab.getLayoutY();
        origMainPaneH   = mainPane.getPrefHeight();
        origInvisibleH  = invisible.getPrefHeight();
        textHeight      = missionText.getPrefHeight();

        double fudge = 210;
        shiftDistance = textHeight
                + (origAdvisoryY - missionText.getLayoutY())
                - fudge;

        // startup: invisible + shifted up
        missionText.setOpacity(0);
        missionText.setVisible(false);
        visionText.setOpacity(0);
        visionText.setVisible(false);
        animateShiftUp();

        missionVision.selectedProperty().addListener((obs, wasSel, isSel) -> {
            if (isSel) {
                // swap to "upload.png"
                control.setImage(new Image(
                        getClass().getResource("/Images/upload.png").toExternalForm()
                ));

                // slide down, then fade in
                animateRestore();
                fadeIn(missionText);
                fadeIn(visionText);

            } else {
                // swap back to arrow-down
                control.setImage(new Image(
                        getClass().getResource("/Images/arrow-down-sign-to-navigate.png").toExternalForm()
                ));

                // fade out, then slide up
                fadeOut(missionText);
                fadeOut(visionText, () -> animateShiftUp());
            }
        });
    }

    // slide up (hide)
    private void animateShiftUp() {
        animateShift(
                origAdvisoryY   - shiftDistance,
                origPolicyY     - shiftDistance,
                origStudentTabY - shiftDistance,
                origMainPaneH   - shiftDistance,
                origInvisibleH  - shiftDistance
        );
    }

    // slide down (show)
    private void animateRestore() {
        animateShift(
                origAdvisoryY,
                origPolicyY,
                origStudentTabY,
                origMainPaneH,
                origInvisibleH
        );
    }

    private void animateShift(double advY, double polY, double tabY, double paneH, double invH) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(advisoryPane.layoutYProperty(), advY, Interpolator.EASE_BOTH),
                new KeyValue(policyPane.layoutYProperty(),   polY, Interpolator.EASE_BOTH),
                new KeyValue(studentTab.layoutYProperty(),   tabY, Interpolator.EASE_BOTH),
                new KeyValue(mainPane.prefHeightProperty(),  paneH, Interpolator.EASE_BOTH),
                new KeyValue(invisible.prefHeightProperty(), invH,  Interpolator.EASE_BOTH)
        ));
        tl.play();
    }

    // simple fade‑in
    private void fadeIn(Node n) {
        n.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), n);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_BOTH);
        ft.play();
    }

    // fade‑out, then optionally run a Runnable on finish
    private void fadeOut(Node n) {
        fadeOut(n, null);
    }

    private void fadeOut(Node n, Runnable onFinished) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), n);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setInterpolator(Interpolator.EASE_BOTH);
        ft.setOnFinished(e -> {
            n.setVisible(false);
            if (onFinished != null) onFinished.run();
        });
        ft.play();
    }

    private void installSpeechBubbleTooltips(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                String text = null;
                if (btn.getTooltip() != null) {
                    text = btn.getTooltip().getText();
                    btn.setTooltip(null);
                }
                if (text != null && !text.isEmpty()) {
                    Popup popup = createSpeechBubblePopup(text);

                    btn.setOnMouseEntered(e -> {
                        Point2D p = btn.localToScreen(btn.getWidth()/2, btn.getHeight());
                        // arrow height = 10px
                        double arrowHeight = 10;
                        double bubbleWidth = popup.getContent().get(0).prefWidth(-1);
                        popup.show(btn, p.getX() - bubbleWidth/2, p.getY() - arrowHeight);
                    });
                    btn.setOnMouseExited(e -> popup.hide());
                }
            }
            if (node instanceof Parent) {
                installSpeechBubbleTooltips((Parent) node);
            }
        }
    }

    private Popup createSpeechBubblePopup(String text) {
        // The black rounded box
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(200);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 13px;");

        VBox bubble = new VBox(label);
        bubble.setAlignment(Pos.CENTER);
        bubble.setStyle(
                "-fx-background-color: black;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8;"
        );

        // The little black arrow pointing up
        Polygon arrow = new Polygon(
                0.0, 10.0,
                10.0, 0.0,
                20.0, 10.0
        );
        arrow.setFill(Color.BLACK);

        // Stack arrow above bubble
        VBox container = new VBox(arrow, bubble);
        container.setAlignment(Pos.TOP_CENTER);

        Popup popup = new Popup();
        popup.getContent().add(container);
        popup.setAutoHide(true);
        return popup;
    }

    private void setButtonHoverEffect(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                // remember original
                Background original = btn.getBackground();

                // drives the left→right fill
                DoubleProperty progress = new SimpleDoubleProperty(0);
                progress.addListener((obs, oldV, newV) -> {
                    double p = newV.doubleValue();
                    LinearGradient lg = new LinearGradient(
                            0, 0, 1, 0,
                            true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#d1d1d1")),
                            new Stop(p, Color.web("#d1d1d1")),
                            new Stop(p, Color.TRANSPARENT),
                            new Stop(1, Color.TRANSPARENT)
                    );
                    btn.setBackground(new Background(new BackgroundFill(
                            lg, new CornerRadii(10), Insets.EMPTY
                    )));
                });

                // ADD handlers instead of setOn...
                btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                    Timeline tl = new Timeline(
                            new KeyFrame(Duration.ZERO,   new KeyValue(progress, 0)),
                            new KeyFrame(Duration.millis(300),
                                    new KeyValue(progress, 1, Interpolator.EASE_BOTH))
                    );
                    tl.play();
                });
                btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                    Timeline tl = new Timeline(
                            new KeyFrame(Duration.ZERO,   new KeyValue(progress, progress.get())),
                            new KeyFrame(Duration.millis(300),
                                    new KeyValue(progress, 0, Interpolator.EASE_BOTH))
                    );
                    tl.setOnFinished(ev -> btn.setBackground(original));
                    tl.play();
                });
            }
            // recurse
            if (node instanceof Parent) {
                setButtonHoverEffect((Parent) node);
            }
        }
    }

    private void setImageHoverInsideButtons(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                // collect any ImageView(s) inside this button's graphic
                List<ImageView> images = new ArrayList<>();
                Node graphic = btn.getGraphic();
                if (graphic != null) {
                    collectImageViews(graphic, images);
                }
                if (!images.isEmpty()) {
                    // on button hover, scale all found images
                    btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                        for (ImageView iv : images) {
                            ScaleTransition st = new ScaleTransition(Duration.millis(200), iv);
                            st.setToX(1.30);
                            st.setToY(1.30);
                            st.setInterpolator(Interpolator.EASE_BOTH);
                            st.play();
                        }
                    });
                    btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                        for (ImageView iv : images) {
                            ScaleTransition st = new ScaleTransition(Duration.millis(200), iv);
                            st.setToX(1.0);
                            st.setToY(1.0);
                            st.setInterpolator(Interpolator.EASE_BOTH);
                            st.play();
                        }
                    });
                }
            }
            // recurse into children
            if (node instanceof Parent) {
                setImageHoverInsideButtons((Parent) node);
            }
        }
    }

    // helper to find ImageViews in a node subtree
    private void collectImageViews(Node node, List<ImageView> out) {
        if (node instanceof ImageView) {
            out.add((ImageView) node);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                collectImageViews(child, out);
            }
        }
    }

    public void loadStudentInfo() {
        String srCode = MainMenu.userLoggedIn;

        String studentQuery = "SELECT first_name, middle_name, last_name, pic_link, year_level, program, status " +
                "FROM student WHERE sr_code = ? AND is_deleted = 0";

        String currentQuery = "SELECT AcademicYear, Semester FROM current LIMIT 1";

        try (
                PreparedStatement psStudent = DBConnect.getConnection().prepareStatement(studentQuery);
                PreparedStatement psCurrent = DBConnect.getConnection().prepareStatement(currentQuery)
        ) {
            psStudent.setString(1, srCode);

            ResultSet rsStudent = psStudent.executeQuery();
            ResultSet rsCurrent = psCurrent.executeQuery();

            if (rsStudent.next()) {
                String middleName = rsStudent.getString("middle_name");
                String middleInitial = (middleName != null && !middleName.isEmpty()) ? middleName.charAt(0) + "." : "";

                String fullName = String.format("%s, %s %s",
                        rsStudent.getString("last_name"),
                        rsStudent.getString("first_name"),
                        middleInitial
                ).replaceAll("\\s+", " ").trim();

                String programCode = rsStudent.getString("program");
                String yearLevel = rsStudent.getString("year_level");
                String status = rsStudent.getString("status");

                // Map program codes to full names
                Map<String, String> programMap = Map.of(
                        "BSIT", "BS Information Technology",
                        "BSCS", "BS Computer Science",
                        "BSCE", "BS Computer Engineering"
                );

                // Map numeric year levels to full words
                Map<String, String> yearLevelMap = Map.of(
                        "1st Year", "FIRST YEAR",
                        "2nd Year", "SECOND YEAR",
                        "3rd Year", "THIRD YEAR",
                        "4th Year", "FOURTH YEAR"
                );

                // Get the full program name
                String fullProgram = programMap.getOrDefault(programCode, programCode);

                // Convert year level to full word
                String fullYearLevel = yearLevelMap.getOrDefault(yearLevel, yearLevel);

                // Set the course and year level to their respective labels
                course.setText(fullProgram);  // Set the program (course) to the 'course' label
                yearLevelLabel.setText(fullYearLevel);  // Set the year level to the 'yearLevel' label

                // Set other labels
                studentName.setText(fullName);
                enrollent.setText(status);

                if ("Not Enrolled".equalsIgnoreCase(status)) {
                    enrollent.setStyle("-fx-text-fill: #cb0600;");
                } else if ("Enrolled".equalsIgnoreCase(status)) {
                    enrollent.setStyle("-fx-text-fill: #4bad58;");
                }

                enrollent2.setText(status);

                if ("Not Enrolled".equalsIgnoreCase(status)) {
                    enrollent2.setStyle("-fx-text-fill: #cb0600;");
                } else if ("Enrolled".equalsIgnoreCase(status)) {
                    enrollent2.setStyle("-fx-text-fill: #4bad58;");
                }

                // Load image from Google Drive link
                String picLink = rsStudent.getString("pic_link");
                if (picLink != null && !picLink.isEmpty()) {
                    Image image = new Image(picLink, true);
                    studentPhoto.setImage(image);
                }
            }

            if (rsCurrent.next()) {
                String semester = rsCurrent.getString("Semester").toUpperCase();
                String academicYear = rsCurrent.getString("AcademicYear");

                semesterLabel.setText(semester + " AY " + academicYear);
                semesterEnroll.setText(semester);
                acadYearEnroll.setText(academicYear);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startRealTimeClock(Label dateLabel, Label timeLabel) {
        // Set the time zone to Philippines (GMT +8)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));

        // Create a Timeline that updates the time every second
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateDateTime(dateLabel, timeLabel)),
                new KeyFrame(Duration.seconds(1), event -> updateDateTime(dateLabel, timeLabel))
        );

        // Set the Timeline to run infinitely
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateDateTime(Label dateLabel, Label timeLabel) {
        // Get the current date and time in LocalDateTime
        LocalDateTime now = LocalDateTime.now();

        // Define the date and time format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        // Format the current date and time
        String formattedDate = now.format(dateFormatter);
        String formattedTime = now.format(timeFormatter);

        // Update the labels
        dateLabel.setText(formattedDate);
        timeLabel.setText(formattedTime);
    }

    private void checkStudentStatusAndDisablePane() {
        String query = "SELECT status FROM student WHERE sr_code = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, MainMenu.userLoggedIn); // Adjust this if your variable name is different
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("Enrolled".equalsIgnoreCase(status)) {
                    regularStudent.setDisable(true);
                    regularStudent.setOpacity(0.9);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Subject> getEnrollableSubjects(String srCode) {
        ObservableList<Subject> list = FXCollections.observableArrayList();

        String studentSql = """
        SELECT id, year_level, major, isIrregular
          FROM student
         WHERE sr_code = ? AND is_deleted = 0
    """;
        String currentSql = "SELECT Semester FROM current LIMIT 1";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement psStu = conn.prepareStatement(studentSql);
             PreparedStatement psCur = conn.prepareStatement(currentSql))
        {
            psStu.setString(1, srCode);
            ResultSet rsStu = psStu.executeQuery();
            if (!rsStu.next()) return list;

            int studentId = rsStu.getInt("id");
            String yearLevel = rsStu.getString("year_level");
            String major = rsStu.getString("major");
            boolean isIrregular = rsStu.getBoolean("isIrregular");

            Map<String,String> toFullYear = Map.of(
                    "1st Year", "First Year",
                    "2nd Year", "Second Year",
                    "3rd Year", "Third Year",
                    "4th Year", "Fourth Year"
            );
            String fullYear = toFullYear.get(yearLevel);

            Map<String,String> prevYear = Map.of(
                    "2nd Year", "First Year",
                    "3rd Year", "Second Year",
                    "4th Year", "Third Year"
            );
            String prevFullYear = prevYear.get(yearLevel);

            ResultSet rsCur = psCur.executeQuery();
            if (!rsCur.next()) return list;
            String currentSemester = rsCur.getString("Semester");

            Set<String> completedCodes = new HashSet<>();
            String compSql = """
            SELECT s.subj_code
              FROM enrolled e
              JOIN subjects s ON e.sub_id = s.sub_id
             WHERE e.student_id = ? AND e.grade IS NOT NULL
        """;
            try (PreparedStatement psComp = conn.prepareStatement(compSql)) {
                psComp.setInt(1, studentId);
                ResultSet rsC = psComp.executeQuery();
                while (rsC.next()) {
                    completedCodes.add(rsC.getString("subj_code"));
                }
            }

            Map<String,Integer> totalByYear = new HashMap<>();
            try (ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT year_level, COUNT(*) AS cnt FROM subjects WHERE isDeleted=0 GROUP BY year_level")) {
                while (rs.next()) {
                    totalByYear.put(rs.getString("year_level"), rs.getInt("cnt"));
                }
            }

            Map<String,Integer> doneByYear = new HashMap<>();
            String compYearSql = """
            SELECT s.year_level, COUNT(DISTINCT e.sub_id) AS cnt
              FROM enrolled e
              JOIN subjects s ON e.sub_id = s.sub_id
             WHERE e.student_id = ? AND e.grade IS NOT NULL
             GROUP BY s.year_level
        """;
            try (PreparedStatement psCY = conn.prepareStatement(compYearSql)) {
                psCY.setInt(1, studentId);
                ResultSet rsY = psCY.executeQuery();
                while (rsY.next()) {
                    doneByYear.put(rsY.getString("year_level"), rsY.getInt("cnt"));
                }
            }

            List<Subject> candidates = new ArrayList<>();
            StringBuilder sb = new StringBuilder("""
            SELECT s.sub_id, s.subj_code, s.subject_name,
                   s.units, s.lecture, s.lab,
                   s.acad_track, s.prerequisite,
                   s.year_level AS subj_year_level,
                   s.semester  AS subj_semester
              FROM subjects s
             WHERE s.isDeleted = 0
               AND s.semester = ?
        """);
            List<Object> params = new ArrayList<>();
            params.add(currentSemester);

            if (!isIrregular) {
                sb.append(" AND s.year_level = ?");
                params.add(fullYear);

                if ("1st Year".equals(yearLevel) || "2nd Year".equals(yearLevel)) {
                    sb.append(" AND s.sub_id <= 32");
                } else {
                    sb.append(" AND s.acad_track = ?");
                    params.add(major);
                }
            } else {
                sb.append(" AND (s.year_level = ?");
                params.add(fullYear);
                if (prevFullYear != null) {
                    sb.append(" OR s.year_level = ?");
                    params.add(prevFullYear);
                }
                sb.append(")");
                if ("3rd Year".equals(yearLevel) || "4th Year".equals(yearLevel)) {
                    sb.append(" AND s.acad_track = ?");
                    params.add(major);
                } else {
                    sb.append(" AND s.sub_id <= 32");
                }
            }

            try (PreparedStatement psSub = conn.prepareStatement(sb.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    psSub.setObject(i + 1, params.get(i));
                }
                ResultSet rs = psSub.executeQuery();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubId(rs.getInt("sub_id"));
                    sub.setSubjCode(rs.getString("subj_code"));
                    sub.setSubjectName(rs.getString("subject_name"));
                    sub.setUnits(rs.getInt("units"));
                    sub.setLecture(rs.getObject("lecture") != null ? rs.getInt("lecture") : null);
                    sub.setLab(rs.getObject("lab") != null ? rs.getInt("lab") : null);
                    sub.setYearLevel(rs.getString("subj_year_level"));
                    sub.setSemester(rs.getString("subj_semester"));

                    if ("First Year".equals(sub.getYearLevel()) || "Second Year".equals(sub.getYearLevel())) {
                        sub.setAcadTrack(null);
                    } else {
                        sub.setAcadTrack(rs.getString("acad_track"));
                    }

                    sub.setPrerequisite(rs.getString("prerequisite"));
                    candidates.add(sub);
                }
            }

            // --- filter by prerequisites ---
            List<Subject> afterPrereq = new ArrayList<>();
            for (Subject sub : candidates) {
                String pre = sub.getPrerequisite();
                if (pre == null || pre.isBlank()) {
                    afterPrereq.add(sub);
                    continue;
                }

                // --- Regular <Nth> Year prerequisite ---
                if (pre.matches("^Regular [1-4].. Year$")) {
                    int requiredYear = switch (pre) {
                        case "Regular 1st Year" -> 1;
                        case "Regular 2nd Year" -> 2;
                        case "Regular 3rd Year" -> 3;
                        case "Regular 4th Year" -> 4;
                        default -> -1;
                    };

                    if (!isIrregular && requiredYear != -1) {
                        boolean allDone = true;
                        for (int y = 1; y < requiredYear; y++) {
                            String yearStr = switch (y) {
                                case 1 -> "First Year";
                                case 2 -> "Second Year";
                                case 3 -> "Third Year";
                                case 4 -> "Fourth Year";
                                default -> "";
                            };
                            int done = doneByYear.getOrDefault(yearStr, 0);
                            int total = totalByYear.getOrDefault(yearStr, Integer.MAX_VALUE);
                            if (done < total) {
                                allDone = false;
                                break;
                            }
                        }
                        if (allDone) {
                            afterPrereq.add(sub);
                        }
                    }
                    continue;
                }

                // Year-level prerequisite (e.g. "Third Year")
                if (totalByYear.containsKey(pre)) {
                    int total = totalByYear.get(pre);
                    int done = doneByYear.getOrDefault(pre, 0);
                    if (done >= total) {
                        afterPrereq.add(sub);
                    }
                    continue;
                }

                // Subject code prerequisites
                String[] codes = pre.split(",\\s*");
                boolean ok = true;
                for (String c : codes) {
                    if (!completedCodes.contains(c)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) afterPrereq.add(sub);
            }

            List<Subject> notYetTaken = new ArrayList<>();
            for (Subject sub : afterPrereq) {
                if (!completedCodes.contains(sub.getSubjCode())) {
                    notYetTaken.add(sub);
                }
            }

            if (isIrregular) {
                int maxUnits;
                switch (yearLevel) {
                    case "1st Year", "2nd Year" -> maxUnits = 23;
                    case "3rd Year" -> maxUnits = "Midterm".equals(currentSemester) ? 6 : 21;
                    case "4th Year" -> maxUnits = "Midterm".equals(currentSemester) ? 6 : 21;
                    default -> maxUnits = Integer.MAX_VALUE;
                }
                int sum = 0;
                for (Subject sub : notYetTaken) {
                    if (sum + sub.getUnits() <= maxUnits) {
                        list.add(sub);
                        sum += sub.getUnits();
                    }
                }
            } else {
                list.addAll(notYetTaken);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void showConfirmationDialog(String message, String confirmText, Runnable onConfirm, String cancelText, Runnable onCancel, String iconType) {
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

    private void confirmEnroll() {
        showConfirmationDialog(
                "Confirm Subjects to be Enrolled",
                "Confirm", this::enrollAllSubjects,
                "Cancel",   ()->{},
                "confirm"
        );
    }

    private void loadStudentAndCurrent() {
        try (Connection conn = DBConnect.getConnection()) {
            // student_id + status
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id,status FROM student WHERE sr_code=? AND is_deleted=0")) {
                ps.setString(1, srCode);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new SQLException("No such student");
                studentId = rs.getInt("id");
                if ("Enrolled".equalsIgnoreCase(rs.getString("status"))) {
                    regularStudent.setDisable(true);
                    regularStudent.setOpacity(0.5);
                }
            }
            // current sem & year
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Semester,AcademicYear FROM current LIMIT 1")) {
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new SQLException("No current set");
                currentSemester = rs.getString("Semester");
                currentAcadYear  = rs.getString("AcademicYear");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateTotalUnits() {
        int total = enrollTable.getItems().stream()
                .filter(s -> !excluded.contains(s))
                .mapToInt(Subject::getUnits)
                .sum();
        units.setText(String.valueOf(total));
    }

    private void enrollAllSubjects() {
        List<Subject> toEnroll = enrollTable.getItems().stream()
                .filter(s -> !excluded.contains(s))
                .collect(Collectors.toList());
        if (toEnroll.isEmpty()) return;

        String insertSql = """
            INSERT INTO enrolled(student_id,sub_id,semester,academic_year)
            VALUES(?,?,?,?)
            """;
        String updStatus = "UPDATE student SET status='Enrolled' WHERE sr_code=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement psIns = conn.prepareStatement(insertSql);
             PreparedStatement psUpd = conn.prepareStatement(updStatus)) {
            conn.setAutoCommit(false);

            for (Subject s : toEnroll) {
                psIns.setInt(1, studentId);
                psIns.setInt(2, s.getSubId());
                psIns.setString(3, currentSemester);
                psIns.setString(4, currentAcadYear);
                psIns.addBatch();
            }
            psIns.executeBatch();

            psUpd.setString(1, srCode);
            psUpd.executeUpdate();

            conn.commit();

            checkStudentStatusAndDisablePane();
            // reload sections (now that they’re enrolled)
            loadStudentSectionOptions();
            loadStudentInfo();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String getStudentYearLevel() {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT year_level FROM student WHERE id=?")) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("year_level");
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    private void loadStudentSectionOptions() {
        // if we haven't loaded the student/current info yet, do nothing
        if (sections == null || studentId <= 0 || currentSemester == null || currentAcadYear == null) {
            return;
        }

        // clear old entries
        sections.getItems().clear();
        sectionIdMap.clear();

        String yearLevel = getStudentYearLevel();
        // hide for 1st‑Year 1st Sem & 3rd‑Year 1st Sem
        boolean hide = "First Semester".equals(currentSemester)
                && ("1st Year".equals(yearLevel) || "3rd Year".equals(yearLevel));
        if (hide) {
            return;
        }

        // display only the .getSection() text
        sections.setConverter(new StringConverter<SectionModel>() {
            @Override public String toString(SectionModel sm) {
                return sm == null ? "" : sm.getSection();
            }
            @Override public SectionModel fromString(String s) {
                return null;
            }
        });

        // figure out what semester & year to pull from
        String prevYearLevel = getPrevYearLevel(yearLevel);
        String prevSemester  = getPrevSemester(currentSemester);

        List<SectionModel> loaded = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection()) {
            // 1) last‑enrolled section (if any)
            String secQuery = """
            SELECT s.section_id, s.section_name, s.department
              FROM section s
              JOIN student_section ss ON ss.section_id = s.section_id
              JOIN student st ON ss.student_id = st.id
             WHERE st.sr_code = ?
             ORDER BY ss.id DESC
             LIMIT 1
        """;
            try (PreparedStatement ps = conn.prepareStatement(secQuery)) {
                ps.setString(1, srCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SectionModel sm = parseSection(
                                rs.getString("section_name"),
                                rs.getString("department")
                        );
                        loaded.add(sm);
                        sectionIdMap.put(sm, rs.getInt("section_id"));
                    }
                }
            }

            // 2) only sections from previous yearLevel & semester
            String allQuery = """
            SELECT section_id, section_name, department
              FROM section
             WHERE year_level = ?
               AND semester   = ?
               AND acadYear   = ?
        """;
            try (PreparedStatement ps = conn.prepareStatement(allQuery)) {
                ps.setString(1, prevYearLevel);
                ps.setString(2, prevSemester);
                ps.setString(3, currentAcadYear);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SectionModel sm = parseSection(
                                rs.getString("section_name"),
                                rs.getString("department")
                        );
                        boolean dup = loaded.stream()
                                .anyMatch(x -> x.getSection().equals(sm.getSection()));
                        if (!dup) {
                            loaded.add(sm);
                            sectionIdMap.put(sm, rs.getInt("section_id"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // push into combo & select first
        sections.getItems().addAll(loaded);
        if (!loaded.isEmpty()) {
            sections.getSelectionModel().selectFirst();
        }

        // when they pick a new one, update population & student_section
        sections.getSelectionModel().selectedItemProperty().addListener((obs, old, ne) -> {
            if (ne == null || ne.equals(old)) return;
            int newId = sectionIdMap.get(ne);
            int oldId = old != null ? sectionIdMap.getOrDefault(old, -1) : -1;

            try (Connection conn = DBConnect.getConnection()) {
                conn.setAutoCommit(false);

                // decrement old
                if (oldId != -1) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE section SET population = population - 1 WHERE section_id = ?"
                    )) {
                        ps.setInt(1, oldId);
                        ps.executeUpdate();
                    }
                }

                // increment new
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE section SET population = population + 1 WHERE section_id = ?"
                )) {
                    ps.setInt(1, newId);
                    ps.executeUpdate();
                }

                // record student_section
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO student_section(student_id, section_id) VALUES(?, ?)"
                )) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, newId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private String getPrevYearLevel(String yl) {
        return switch (yl) {
            case "2nd Year" -> "1st Year";
            case "3rd Year" -> "2nd Year";
            case "4th Year" -> "3rd Year";
            default         -> yl;
        };
    }

    private String getPrevSemester(String sem) {
        return switch (sem) {
            case "First Semester"  -> "Second Semester";
            case "Second Semester" -> "First Semester";
            case "Midterm"         -> "First Semester";
            default                -> sem;
        };
    }

    private SectionModel parseSection(String raw, String dept) {
        // split off the numeric code
        String[] main = raw.split("\\s-\\s", 2);
        String left = main[0];                // "BSIT-NT"
        String code = main.length>1 ? main[1] : "";

        // break "BSIT-NT" → ["BSIT","NT"]
        String[] pt = left.split("-",2);
        String prog  = pt[0];                 // "BSIT"
        String track = pt.length>1?pt[1]:"";  // "NT"

        // last two digits of "3201" → "01"
        String suffix = code.length()>2
                ? code.substring(code.length()-2)
                : code;

        // drop leading "BS"
        String dispProg = prog.startsWith("BS")
                ? prog.substring(2)
                : prog;

        // assemble "IT 01-NT"
        String display = dispProg + " " + suffix
                + (track.isEmpty()?"":"-" + track);

        return new SectionModel(display, dept);
    }

    private void setupPickMajor() {
        // hyperlink to the BSIT PDF
        linkMajor.setTextFill(Color.BLUE);
        linkMajor.setUnderline(true);
        linkMajor.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(new URI(
                        "https://batstate-u.edu.ph/wp-content/uploads/2023/03/" +
                                "Bachelor-of-Science-in-Information-Technology-BSIT.pdf"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Define full names and acronyms
        Map<String, String> majorMap = new HashMap<>();
        majorMap.put("Business Analytics", "BA");
        majorMap.put("Network and Security", "NT");
        majorMap.put("Service Management", "SM");

        // fetch year_level & major
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT year_level, major FROM student WHERE sr_code = ?")) {

            ps.setString(1, srCode);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return;

            String yearLevel = rs.getString("year_level");
            String major     = rs.getString("major");

            // show picker only for 3rd‑Year & no existing major
            boolean show = "3rd Year".equals(yearLevel) && (major == null || major.isEmpty());
            pickMajor.setVisible(show);
            pickMajor.setDisable(!show);

            if (show) {
                // use full names in ComboBox
                majoringCombo.setItems(FXCollections.observableArrayList(majorMap.keySet()));
                majoringCombo.getSelectionModel().selectFirst();

                confirmBut.setOnAction(ev -> {
                    String selectedMajor = majoringCombo.getValue();
                    if (selectedMajor == null || selectedMajor.isEmpty()) return;

                    String code = majorMap.get(selectedMajor);
                    if (code == null) return;

                    // Use a new connection for update
                    try (Connection conn2 = DBConnect.getConnection();
                         PreparedStatement ps2 = conn2.prepareStatement(
                                 "UPDATE student SET major = ? WHERE sr_code = ?")) {

                        ps2.setString(1, code);
                        ps2.setString(2, srCode);
                        ps2.executeUpdate();

                        // Hide and disable the pickMajor pane
                        pickMajor.setDisable(true);
                        pickMajor.setVisible(false);

                        // Refresh info and table
                        loadStudentInfo();
                        setupTableAndEvents();
                        autoFitTableColumns(enrollTable);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private Label SAS;

    @FXML
    private TableColumn<?, ?> acadTrackCol;

    @FXML
    private Label acadYearEnroll;

    @FXML
    private Pane advisoryPane;

    @FXML
    private Button allGradeBut;

    @FXML
    private Button assessBut;

    @FXML
    private Pane buttons;

    @FXML
    private Button changePassword;

    @FXML
    private TableColumn<?, ?> codeCol;

    @FXML
    private Button completeBut;

    @FXML
    private Button confirmBut;

    @FXML
    private Tab contacts;

    @FXML
    private ImageView control;

    @FXML
    private Button corBut;

    @FXML
    private Text course;

    @FXML
    private Button curriculumBut;

    @FXML
    private Label date;

    @FXML
    private Tab downloads;

    @FXML
    private Button enrollBut;

    @FXML
    private Label enrollent;

    @FXML
    private Label enrollent2;

    @FXML
    private Tab enrollment;

    @FXML
    private Button gradeBut;

    @FXML
    private Tab home;

    @FXML
    private AnchorPane homePane;

    @FXML
    private Button hymn;

    @FXML
    private Button idBut;

    @FXML
    private AnchorPane invisible;

    @FXML
    private TableColumn<?, ?> labCol;

    @FXML
    private TableColumn<?, ?> lectureCol;

    @FXML
    private Button liabilitiesBut;

    @FXML
    private Label linkMajor;

    @FXML
    private Tab links;

    @FXML
    private Pane mainPane;

    @FXML
    private Pane missionText;

    @FXML
    private ToggleButton missionVision;

    @FXML
    private Button paymentBut;

    @FXML
    private StackPane pickMajor;

    @FXML
    private Label policies;

    @FXML
    private Pane policyPane;

    @FXML
    private TableColumn<?, ?> prerequisiteCol;

    @FXML
    private Button printAllGradesBut;

    @FXML
    private Button printGradesBut;

    @FXML
    private MenuButton readFirst;

    @FXML
    private Pane regularStudent;

    @FXML
    private Tab schedule;

    @FXML
    private Button scholarshipBut;

    @FXML
    private Label semesterEnroll;

    @FXML
    private Label semesterLabel;

    @FXML
    private Button signOut;

    @FXML
    private Button sscBut;

    @FXML
    private Label studentName;

    @FXML
    private ImageView studentPhoto;

    @FXML
    private TabPane studentTab;

    @FXML
    private TableColumn<?, ?> subjectNameCol;

    @FXML
    private Button subjectsBut;

    @FXML
    private Label time;

    @FXML
    private Label units;

    @FXML
    private TableColumn<?, ?> unitsCol;

    @FXML
    private Pane visionText;

    @FXML
    private Text yearLevelLabel;

    @FXML
    private ComboBox<String> majoringCombo;
    @FXML
    private TableView<Subject> enrollTable;

    @FXML
    private ComboBox<SectionModel> sections;

}
