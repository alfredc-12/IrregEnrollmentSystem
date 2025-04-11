package Classes;

import ExtraSources.DBConnect;
import GettersSetters.StudentSectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeanNavigationController implements Initializable {

    @FXML
    private TableColumn<StudentSectionModel, Integer> IdColumn;

    @FXML
    private TableColumn<StudentSectionModel, String> actionsColumn;

    @FXML
    private ComboBox<String> filterBatch;

    @FXML
    private ToggleButton btnSwitch;

    @FXML
    private ComboBox<String> filterSemester;

    @FXML
    private TableColumn<StudentSectionModel, String> nameColumn;

    @FXML
    private TextField searchStudent;

    @FXML
    private ContextMenu contextMenuGen;

    @FXML
    private MenuItem generateClass;

    @FXML
    private TableColumn<StudentSectionModel, String> sectionColumn;

    @FXML
    private ComboBox<String> sectionFilter;

    @FXML
    private TableView<StudentSectionModel> sectionTbl;

    @FXML
    private TableColumn<StudentSectionModel, String> sexColumn;

    @FXML
    private TableColumn<StudentSectionModel, String> srcodeColumn;

    @FXML
    private TableColumn<StudentSectionModel, String> yearColumn;

    private ObservableList<StudentSectionModel> masterData = FXCollections.observableArrayList();
    private FilteredList<StudentSectionModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the table columns
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        srcodeColumn.setCellValueFactory(new PropertyValueFactory<>("srCode"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));

        // Set up action column with eye icon buttons
        setupActionColumn();

        // Set up toggle button for semester/batch filtering
        setupToggleButton();

        // Load data from the database
        loadStudentSectionData();

        // Set up filtering
        setupFiltering();

        // Load filter options
        loadFilterOptions();

        generateClass.setOnAction(event -> openClassListGenerator());
    }

    @FXML
    private void openClassListGenerator() {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/class-list-generator.fxml"));
            AnchorPane root = loader.load();

            // Get the controller to access navigation pane
            ClassListController controller = loader.getController();

            // Apply styling to navigation pane (curved only at top corners)
            double cornerRadius = 15.0;
            if (controller.navigation != null) {
                controller.navigation.setStyle("-fx-background-color: red; " +
                        "-fx-background-radius: " + cornerRadius + "px " + cornerRadius + "px 0 0;");
            }

            // Apply styling with rounded corners to main root
            root.setStyle("-fx-background-radius: " + cornerRadius + "px; " +
                    "-fx-background-color: #FAF9F6; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

            // Create a new stage with transparent style
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.centerOnScreen();

            // Create scene with transparent background
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/class-list-generator.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            // Make the window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = stage.getX() - e.getScreenX();
                dragDelta.y = stage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() + dragDelta.x);
                stage.setY(e.getScreenY() + dragDelta.y);
            });

            // Configure the stage
            stage.setScene(scene);
            stage.initOwner(sectionTbl.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Class List Generator");

            // Set up entrance animation
            root.setScaleX(0.8);
            root.setScaleY(0.8);
            root.setOpacity(0);

            // Show the stage
            stage.show();

            // Create and play the entrance animation
            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition();

            // Scale transition
            javafx.animation.ScaleTransition scaleTransition = new javafx.animation.ScaleTransition(
                    javafx.util.Duration.millis(350), root);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            // Fade transition
            javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(350), root);
            fadeTransition.setToValue(1.0);
            fadeTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            // Play animations
            pt.getChildren().addAll(scaleTransition, fadeTransition);
            pt.play();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading class-list-generator.fxml: " + e.getMessage());
        }
    }

    public void loadStudentSectionData() {
        String query = "SELECT s.id, s.first_name, s.middle_name, s.last_name, s.sex, s.sr_code, " +
                "s.year_level, sec.section_name " +
                "FROM student s " +
                "LEFT JOIN student_section ss ON s.id = ss.student_id " +
                "LEFT JOIN section sec ON ss.section_id = sec.section_id " +
                "WHERE s.is_deleted = 0 AND s.status = 'Enrolled'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            masterData.clear();

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String sex = rs.getString("sex");
                String srCode = rs.getString("sr_code");
                String yearLevel = rs.getString("year_level");
                String section = rs.getString("section_name");

                // If section is null, display "Not Assigned"
                if (section == null) {
                    section = "Not Assigned";
                }

                StudentSectionModel student = new StudentSectionModel(
                        id, lastName, firstName, middleName, sex, srCode, section, yearLevel
                );

                masterData.add(student);
            }

            // Set the items on the table
            filteredData = new FilteredList<>(masterData, p -> true);
            sectionTbl.setItems(filteredData);

        } catch (SQLException e) {
            System.err.println("Error loading student data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFiltering() {
        filteredData = new FilteredList<>(masterData, p -> true);

        // Create filter properties to track current filter values
        final String[] currentSection = {null};
        final String[] currentYearLevel = {null};
        final String[] currentSemester = {null};
        final String[] currentSearchText = {null};

        // Configure search by student name or SR code
        searchStudent.textProperty().addListener((observable, oldValue, newValue) -> {
            currentSearchText[0] = newValue;
            if (btnSwitch.isSelected()) {
                applyAllFilters(currentSection[0], null, currentSemester[0], currentSearchText[0]);
            } else {
                applyAllFilters(currentSection[0], currentYearLevel[0], null, currentSearchText[0]);
            }
        });

        // Configure section filter
        sectionFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSection[0] = "All Sections".equals(newValue) ? null : newValue;
                if (btnSwitch.isSelected()) {
                    applyAllFilters(currentSection[0], null, currentSemester[0], currentSearchText[0]);
                } else {
                    applyAllFilters(currentSection[0], currentYearLevel[0], null, currentSearchText[0]);
                }
            }
        });

        // Configure batch (year level) filter
        filterBatch.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentYearLevel[0] = "All Batches".equals(newValue) ? null : newValue;
                applyAllFilters(currentSection[0], currentYearLevel[0], null, currentSearchText[0]);
            }
        });

        // Configure semester filter
        filterSemester.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSemester[0] = "All Semesters".equals(newValue) ? null : newValue;
                applyAllFilters(currentSection[0], null, currentSemester[0], currentSearchText[0]);
            }
        });

        // Set up toggle button listener to refresh filters when toggled
        btnSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {  // Semester mode
                applyAllFilters(currentSection[0], null, currentSemester[0], currentSearchText[0]);
            } else {  // Batch/Year level mode
                applyAllFilters(currentSection[0], currentYearLevel[0], null, currentSearchText[0]);
            }
        });
    }

    private void setupToggleButton() {
        // Initially hide semester filter as batch filter is shown by default
        filterSemester.setVisible(false);
        filterSemester.setManaged(false);

        btnSwitch.setOnAction(event -> {
            boolean isToggled = btnSwitch.isSelected();

            // Toggle visibility between batch filter and semester filter
            filterBatch.setVisible(!isToggled);
            filterBatch.setManaged(!isToggled);
            filterSemester.setVisible(isToggled);
            filterSemester.setManaged(isToggled);

            // Reset filters when switching
            if (isToggled) {
                filterBatch.getSelectionModel().selectFirst();
                if (filterSemester.getItems() != null && !filterSemester.getItems().isEmpty()) {
                    filterSemester.getSelectionModel().selectFirst();
                }
            } else {
                filterSemester.getSelectionModel().clearSelection();
                if (filterBatch.getItems() != null && !filterBatch.getItems().isEmpty()) {
                    filterBatch.getSelectionModel().selectFirst();
                }
            }
        });
    }

    private void applyAllFilters(String section, String yearLevel, String semester, String searchText) {
        filteredData.setPredicate(student -> {
            boolean matchesSection = (section == null) || student.getSection().equals(section);
            boolean matchesYearLevel = (yearLevel == null) || student.getYearLevel().equals(yearLevel);

            // Updated semester matching logic
            boolean matchesSemester = (semester == null);
            if (semester != null && !semester.equals("All Semesters")) {
                try (Connection conn = DBConnect.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT semester FROM student WHERE id = ?")) {
                    stmt.setInt(1, student.getId());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String studentSemester = rs.getString("semester");
                        matchesSemester = semester.equals(studentSemester);
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking semester: " + e.getMessage());
                }
            }

            boolean matchesSearch = (searchText == null || searchText.isEmpty()) ||
                    student.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    student.getSrCode().toLowerCase().contains(searchText.toLowerCase());

            // Determine which year/semester filter to apply based on toggle state
            boolean matchesYearOrSemester;
            if (btnSwitch.isSelected()) {
                matchesYearOrSemester = matchesSemester;
            } else {
                matchesYearOrSemester = matchesYearLevel;
            }

            // Student must match ALL active filters
            return matchesSection && matchesYearOrSemester && matchesSearch;
        });
    }

    private void setupActionColumn() {
        actionsColumn.setCellFactory(column -> {
            return new TableCell<>() {
                private final Button viewButton = new Button();

                {
                    ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/Images/eye.png")));
                    eyeIcon.setFitHeight(16);
                    eyeIcon.setFitWidth(16);

                    viewButton.setGraphic(eyeIcon);
                    viewButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-radius: 4px; -fx-cursor: hand;");

                    // Hover effect with interpolator for smooth animation
                    viewButton.setOnMouseEntered(e -> {
                        viewButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #2980b9; -fx-border-radius: 4px; -fx-cursor: hand;");
                        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), viewButton);
                        st.setToX(1.1);
                        st.setToY(1.1);
                        st.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
                        st.play();
                    });

                    viewButton.setOnMouseExited(e -> {
                        viewButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-radius: 4px; -fx-cursor: hand;");
                        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), viewButton);
                        st.setToX(1.0);
                        st.setToY(1.0);
                        st.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
                        st.play();
                    });

                    viewButton.setOnAction(event -> {
                        StudentSectionModel student = getTableView().getItems().get(getIndex());
                        try {
                            // Load the FXML
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/student-info.fxml"));
                            AnchorPane root = loader.load();

                            // Get the controller and set the student data
                            StudentInfoController controller = loader.getController();

                            // Get student details from database based on student id
                            loadStudentDetails(student.getId(), controller);

                            // Apply rounded corners to top of navigation pane
                            double cornerRadius = 15.0;
                            if (controller.navigation != null) {
                                controller.navigation.setStyle("-fx-background-color: red; " +
                                        "-fx-background-radius: " + cornerRadius + "px " + cornerRadius + "px 0 0;");
                            }

                            // Create a new stage
                            Stage infoStage = new Stage();
                            infoStage.initStyle(StageStyle.TRANSPARENT);
                            infoStage.centerOnScreen();

                            // Create scene with transparent background
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/CSS/student-info.css").toExternalForm());
                            scene.setFill(Color.TRANSPARENT);

                            // Apply corner radius to main frame (matching the nav pane radius)
                            root.setStyle("-fx-background-radius: " + cornerRadius + "px; " +
                                    "-fx-background-color: #FAF9F6; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

                            // Make the window draggable
                            final Delta dragDelta = new Delta();
                            root.setOnMousePressed(e -> {
                                dragDelta.x = infoStage.getX() - e.getScreenX();
                                dragDelta.y = infoStage.getY() - e.getScreenY();
                            });
                            root.setOnMouseDragged(e -> {
                                infoStage.setX(e.getScreenX() + dragDelta.x);
                                infoStage.setY(e.getScreenY() + dragDelta.y);
                            });

                            infoStage.setScene(scene);
                            infoStage.initOwner(sectionTbl.getScene().getWindow());
                            infoStage.initModality(Modality.APPLICATION_MODAL);
                            infoStage.centerOnScreen();

                            // Set up fade-in transition
                            // Initial state: smaller scale and zero opacity
                            root.setScaleX(0.8);
                            root.setScaleY(0.8);
                            root.setOpacity(0);

                            // Show the stage
                            infoStage.show();

                            // Create parallel transition for scale and fade
                            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition();

                            // Add scale transition
                            javafx.animation.ScaleTransition scaleTransition = new javafx.animation.ScaleTransition(
                                    javafx.util.Duration.millis(350), root);
                            scaleTransition.setToX(1.0);
                            scaleTransition.setToY(1.0);
                            scaleTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

                            // Add fade transition
                            javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(
                                    javafx.util.Duration.millis(350), root);
                            fadeTransition.setToValue(1.0);
                            fadeTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

                            // Combine both transitions
                            pt.getChildren().addAll(scaleTransition, fadeTransition);
                            pt.play();

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("Error loading student-info.fxml: " + e.getMessage());
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewButton);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            };
        });
    }

    // Method to load student details from database
    private void loadStudentDetails(int studentId, StudentInfoController controller) {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT first_name, middle_name, last_name, sex, sr_code, year_level, " +
                             "semester, contact, email, address, pic_link FROM student WHERE id = ?")) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String middleName = rs.getString("middle_name");
                    String lastName = rs.getString("last_name");
                    String fullName = lastName + ", " + firstName + " " +
                            (middleName != null && !middleName.isEmpty() ? middleName.charAt(0) + "." : "");

                    // Set values to the student info controller
                    controller.nameLbl.setText(fullName);
                    controller.sexLbl.setText(rs.getString("sex"));
                    controller.srcodeLbl.setText(rs.getString("sr_code"));
                    controller.contactLbl.setText(rs.getString("contact"));
                    controller.emailLbl.setText(rs.getString("email"));
                    controller.addressLbl.setText(rs.getString("address"));
                    controller.yearLbl.setText(rs.getString("year_level"));
                    controller.semesterLbl.setText(rs.getString("semester"));

                    // Load student image if available
                    String picLink = rs.getString("pic_link");
                    if (picLink != null && !picLink.isEmpty()) {
                        String directImageUrl = convertToDirectLink(picLink);
                        Image studentImage = new Image(directImageUrl, true);
                        try {
                            if (studentImage == null) {
                                System.out.println("Student image is null");
                                // Set a default placeholder image
                                controller.studentPic.setImage(new Image(getClass().getResourceAsStream("/Images/default_profile.png")));
                            } else {
                                controller.studentPic.setImage(studentImage);
                                // Adjust the image to fit properly
                                controller.studentPic.setPreserveRatio(true);
                                controller.studentPic.setFitWidth(controller.studentPic.getFitWidth());
                                controller.studentPic.setFitHeight(controller.studentPic.getFitHeight());
                                controller.studentPic.setSmooth(true);
                                controller.studentPic.setCache(true);
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading student image: " + e.getMessage());
                            e.printStackTrace();
                            // Set a default placeholder image on error
                            controller.studentPic.setImage(new Image(getClass().getResourceAsStream("/Images/default_profile.png")));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String convertToDirectLink(String shareLink) {
        // Regular expression to capture the file ID from a typical Google Drive share URL
        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(shareLink);
        if (matcher.find()) {
            String fileId = matcher.group(1);
            // Construct the direct link URL
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }
        // Fallback: if the shareLink does not match the expected pattern, return it unchanged.
        return shareLink;
    }

    // Helper class for draggable window
    private static class Delta {
        double x, y;
    }

    public void loadFilterOptions() {
        // Load sections for section filter
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT section_name FROM section ORDER BY section_name");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> sections = FXCollections.observableArrayList();
            sections.add("All Sections");

            while (rs.next()) {
                sections.add(rs.getString("section_name"));
            }

            sectionFilter.setItems(sections);
            sectionFilter.getSelectionModel().selectFirst();

            // Set prompt text color to white
            sectionFilter.setStyle("-fx-prompt-text-fill: white;" + "-fx-background-color:transparent");

        } catch (SQLException e) {
            System.err.println("Error loading section options: " + e.getMessage());
        }

        // Load year levels for batch filter
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT year_level FROM student ORDER BY year_level");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> years = FXCollections.observableArrayList();
            years.add("All Batches");

            while (rs.next()) {
                years.add(rs.getString("year_level"));
            }

            filterBatch.setItems(years);
            filterBatch.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            System.err.println("Error loading year level options: " + e.getMessage());
        }

        // Populate semester filter with only 1st and 2nd semester options
        ObservableList<String> semesters = FXCollections.observableArrayList();
        semesters.add("All Semesters");
        semesters.add("1st Sem");
        semesters.add("2nd Sem");
        filterSemester.setItems(semesters);
        filterSemester.getSelectionModel().selectFirst();
    }
}