package Classes;

import Application.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import GettersSetters.*;
import ExtraSources.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.sql.*;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Window X
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class StudentController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> studIDColumn;
    @FXML private TableColumn<Student, String> firstNameColumn;
    @FXML private TableColumn<Student, String> middleNameColumn;
    @FXML private TableColumn<Student, String> lastNameColumn;
    @FXML private TableColumn<Student, String> srCodeColumn;
    @FXML private TableColumn<Student, String> yearLevelColumn;
    @FXML private TableColumn<Student, String> programColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TableColumn<Student, String> contactColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> addressColumn;
    @FXML private TableColumn<Student, String> statusColumn;
    @FXML private TableColumn<Student, String> picLinkColumn;
    @FXML private TableColumn<Student, String> passwordColumn;

    @FXML private TextField firstName, middleName, lastName, picLink, srCode, contact, email, password, address;
    @FXML private ComboBox<String> yearLevel, program, major, status;
    @FXML private Button insertButton, clearButton;
    @FXML private TextField studID; // If needed for the database

    @FXML private Button Delete;
    @FXML private TextField searchField; //search or filter

    @FXML private Pane signaturePanel; // Signature

    @FXML private Pane imagePanel; // Image


    private Connection kon;

    // Mapping of programs to their respective majors
    private final Map<String, String[]> majorsMap = new HashMap<>();


    @FXML
    private void initialize() {

        // Get database connection from DBConnect
        kon = DBConnect.getConnection();

        // Populate ComboBox options
        yearLevel.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");
        program.getItems().addAll("BS Computer Science", "BS Information Technology", "BS Computer Engineering");
        status.getItems().addAll("Enrolled", "Not Enrolled");

        // Define major options based on the selected program
        majorsMap.put("BS Computer Science", new String[]{"None", "Data Science", "Web Development"});
        majorsMap.put("BS Information Technology", new String[]{"None", "Business Analytics", "Network Technology"});
        majorsMap.put("BS Computer Engineering", new String[]{"None", "Robotics", "VLSI Design"});

        // Assign event handlers to buttons
        insertButton.setOnAction(event -> insertStudent());
        clearButton.setOnAction(event -> clearFields());
        studentTable.setOnMouseClicked(event -> populateFieldsFromTable()); // mouseclick

        // Assign event handlers to combo boxes
        program.setOnAction(event -> updateMajorOptions());

        studIDColumn.setCellValueFactory(cellData -> cellData.getValue().studIDProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        middleNameColumn.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        srCodeColumn.setCellValueFactory(cellData -> cellData.getValue().srCodeProperty());
        yearLevelColumn.setCellValueFactory(cellData -> cellData.getValue().yearLevelProperty());
        programColumn.setCellValueFactory(cellData -> cellData.getValue().programProperty());
        majorColumn.setCellValueFactory(cellData -> cellData.getValue().majorProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        picLinkColumn.setCellValueFactory(cellData -> cellData.getValue().picLinkProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());


        // Load student data
        loadStudents();

        //filter
        setupSearchFilter();

        Platform.runLater(() -> {
            Stage stage = (Stage) studentTable.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // Prevents the window from closing
                returnToDashboard();
            });
        });

    }

    // Method to return to the dashboard
    private void returnToDashboard() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/FXML/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Faculty!");
            stage.setScene(scene);
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) studentTable.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPictureDialog() {
        try {
            // Load the PictureDialog FXML file (adjust the resource path as needed)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/PictureDialog.fxml"));
            Parent root = loader.load();

            // Get the PictureDialog controller instance
            PictureDialog dialogController = loader.getController();

            // Create and display the dialog stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Capture Picture");

            // Ensure camera stops when the window is closed
            stage.setOnHidden(event -> dialogController.stopCamera());

            stage.showAndWait();

            // After the dialog closes, retrieve the captured image
            Image capturedImage = dialogController.getCapturedImage();
            if (capturedImage != null) {
                ImageView iv = new ImageView(capturedImage);

                double imageWidth = capturedImage.getWidth();
                double imageHeight = capturedImage.getHeight();
                double panelWidth = imagePanel.getPrefWidth();
                double panelHeight = imagePanel.getPrefHeight();

                double targetRatio = panelWidth / panelHeight;
                double imageRatio = imageWidth / imageHeight;

                double viewportX, viewportY, viewportWidth, viewportHeight;

                if (imageRatio > targetRatio) {
                    viewportHeight = imageHeight;
                    viewportWidth = imageHeight * targetRatio;
                    viewportX = (imageWidth - viewportWidth) / 2;
                    viewportY = 0;
                } else {
                    viewportWidth = imageWidth;
                    viewportHeight = imageWidth / targetRatio;
                    viewportX = 0;
                    viewportY = (imageHeight - viewportHeight) / 2;
                }

                iv.setViewport(new javafx.geometry.Rectangle2D(viewportX, viewportY, viewportWidth, viewportHeight));
                iv.setFitWidth(panelWidth);
                iv.setFitHeight(panelHeight);
                iv.setPreserveRatio(true);

                imagePanel.getChildren().clear();
                imagePanel.getChildren().add(iv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openSignatureDialog() {
        try {
            // Load the signature dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SignatureDialog.fxml"));
            Parent root = loader.load();

            // Get the dialog controller
            SignatureDialog dialogController = loader.getController();

            // Create a new stage for the dialog
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Signature Dialog");
            stage.showAndWait();

            // After the dialog is closed, retrieve the signature image
            Image sigImage = dialogController.getSignatureImage();
            if (sigImage != null) {
                // Display the signature image on the main signaturePanel.
                ImageView iv = new ImageView(sigImage);
                iv.setFitWidth(signaturePanel.getPrefWidth());
                iv.setFitHeight(signaturePanel.getPrefHeight());
                signaturePanel.getChildren().clear();
                signaturePanel.getChildren().add(iv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a student to delete.");
            return;
        }

        // Confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this student?");
        confirmationAlert.setContentText("This action cannot be undone.");

        // Show confirmation and wait for user response
        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            try {
                String query = "DELETE FROM student WHERE id = ?";
                PreparedStatement pstmt = kon.prepareStatement(query);
                pstmt.setInt(1, selectedStudent.getStudID());
                pstmt.executeUpdate();

                // Refresh student list
                loadStudents();

                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student.");
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Call this method during initialization
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchStudentsInDatabase(newValue);
            showSuggestions(newValue);
        });
    }

    // Declare ContextMenu at class level to keep it persistent
    private ContextMenu suggestionsMenu = new ContextMenu();

    private void showSuggestions(String input) {
        if (input == null || input.isEmpty()) {
            suggestionsMenu.hide(); // Hide if input is empty
            return;
        }

        ObservableList<String> suggestions = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT first_name FROM student WHERE first_name LIKE ? " +
                "UNION SELECT DISTINCT last_name FROM student WHERE last_name LIKE ? " +
                "UNION SELECT DISTINCT middle_name FROM student WHERE middle_name LIKE ? " +
                "UNION SELECT DISTINCT address FROM student WHERE address LIKE ? " +
                "UNION SELECT DISTINCT major FROM student WHERE major LIKE ? " +
                "UNION SELECT DISTINCT program FROM student WHERE program LIKE ? " +
                "UNION SELECT DISTINCT status FROM student WHERE status LIKE ? " +
                "UNION SELECT DISTINCT year_level FROM student WHERE year_level LIKE ? " +
                "UNION SELECT DISTINCT sr_code FROM student WHERE sr_code LIKE ?";

        try (PreparedStatement stmt = kon.prepareStatement(sql)) {
            String searchPattern = input + "%";
            for (int i = 1; i <= 9; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next() && suggestions.size() < 10) { // Limit to 10 suggestions
                suggestions.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch suggestions: " + e.getMessage());
        }

        // Remove duplicates & ensure the first suggestion doesn't disappear
        suggestions = suggestions.stream().distinct().collect(Collectors.toCollection(FXCollections::observableArrayList));

        // If there are no suggestions, hide the menu
        if (suggestions.isEmpty()) {
            suggestionsMenu.hide();
            return;
        }

        // Clear only items (not the ContextMenu itself) to keep it open
        suggestionsMenu.getItems().clear();

        // Populate new suggestions
        for (String suggestion : suggestions) {
            MenuItem item = new MenuItem(suggestion);
            item.setStyle("-fx-padding: 5px 10px; -fx-font-size: 13px; -fx-font-family: Arial;");
            item.setOnAction(e -> searchField.setText(suggestion)); // Set selected text
            suggestionsMenu.getItems().add(item);
        }

        // Position dropdown below the searchField
        Bounds boundsInScreen = searchField.localToScreen(searchField.getBoundsInLocal());
        if (boundsInScreen != null) {
            suggestionsMenu.show(searchField, boundsInScreen.getMinX(), boundsInScreen.getMaxY() + 2);
        }
    }



    private void searchStudentsInDatabase(String searchText) {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return;
        }

        ObservableList<Student> filteredStudents = FXCollections.observableArrayList();

        String sql = "SELECT * FROM student WHERE " +
                "first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ? OR " +
                "sr_code LIKE ? OR year_level LIKE ? OR program LIKE ? OR " +
                "major LIKE ? OR contact LIKE ? OR email LIKE ? OR " +
                "address LIKE ? OR status LIKE ?";


        try (PreparedStatement stmt = kon.prepareStatement(sql)) {
            String searchPattern = "%" + searchText + "%"; // Allow partial matching with wildcards
            for (int i = 1; i <= 11; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),  // Changed "studID" to "id" (as per your table)
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("sr_code"),
                        rs.getString("year_level"),
                        rs.getString("program"),
                        rs.getString("major"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("status"),  // Ensure enum handling as needed
                        rs.getString("pic_link"),
                        rs.getString("password")
                );
                filteredStudents.add(student);
            }

            // Bind the filtered list to the TableView
            studentTable.setItems(filteredStudents);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
    }


    @FXML
    private void loadStudents() {
        ObservableList<Student> studentList = FXCollections.observableArrayList();
        String query = "SELECT * FROM student"; // Only fetch active students

        try (
                PreparedStatement stmt = kon.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                studentList.add(new Student(
                        rs.getInt("id"),  // Changed "studID" to "id" (as per your table)
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("sr_code"),
                        rs.getString("year_level"),
                        rs.getString("program"),
                        rs.getString("major"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("status"), // Ensure enum handling as needed
                        rs.getString("pic_link"),
                        rs.getString("password")
                ));
            }
            studentTable.setItems(studentList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading students: " + e.getMessage()).show();
        }
    }

    @FXML
    private void populateFieldsFromTable() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studID.setText(String.valueOf(selectedStudent.getStudID()));
            firstName.setText(selectedStudent.getFirstName());
            middleName.setText(selectedStudent.getMiddleName());
            lastName.setText(selectedStudent.getLastName());
            srCode.setText(selectedStudent.getSrCode());
            yearLevel.setValue(selectedStudent.getYearLevel());
            program.setValue(selectedStudent.getProgram());
            major.setValue(selectedStudent.getMajor());
            contact.setText(selectedStudent.getContact());
            email.setText(selectedStudent.getEmail());
            address.setText(selectedStudent.getAddress());
            status.setValue(selectedStudent.getStatus());
            picLink.setText(selectedStudent.getPicLink());
            password.setText(selectedStudent.getPassword());
        }
    }

    //Updates the "Major" ComboBox based on the selected "Program".
    @FXML
    private void updateMajorOptions() {
        String selectedProgram = program.getValue();
        major.getItems().clear(); // Clear previous major options

        if (selectedProgram != null && majorsMap.containsKey(selectedProgram)) {
            major.getItems().addAll(majorsMap.get(selectedProgram));
            major.setValue(null); // Reset major selection
        }
    }

    @FXML
    private void insertStudent() {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return;
        }

        // Validate input fields
        if (isInputInvalid()) {
            return; // Stop execution if validation fails
        }

        // Check if student already exists
        if (isStudentExists(srCode.getText(), email.getText(), contact.getText())) {
            new Alert(Alert.AlertType.ERROR, "Student with the same SR-Code, Email, or Contact already exists!").show();
            return;
        }

        try {
            kon.setAutoCommit(false); // Start transaction

            // Insert into student table first
            String studentSQL = "INSERT INTO student (first_name, middle_name, last_name, pic_link, sr_code, year_level, program, major, contact, email, password, address, status, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int studentID;
            try (PreparedStatement studentStmt = kon.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS)) {
                studentStmt.setString(1, firstName.getText());
                studentStmt.setString(2, middleName.getText());
                studentStmt.setString(3, lastName.getText());
                studentStmt.setString(4, picLink.getText());
                studentStmt.setString(5, srCode.getText());
                studentStmt.setString(6, yearLevel.getValue());
                studentStmt.setString(7, program.getValue());
                studentStmt.setString(8, major.getValue());
                studentStmt.setString(9, contact.getText());
                studentStmt.setString(10, email.getText());
                studentStmt.setString(11, password.getText());
                studentStmt.setString(12, address.getText());
                studentStmt.setString(13, status.getValue());
                studentStmt.setInt(14, 0); // isDeleted = 0 (not deleted)

                studentStmt.executeUpdate();

                // Get the generated student ID
                try (ResultSet rs = studentStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        studentID = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve Student ID.");
                    }
                }
            }

            // Get guardian details
            Map<String, String> guardianDetails = getGuardianDetails();
            if (guardianDetails.isEmpty()) {
                kon.rollback(); // Rollback if user cancels input
                return;
            }

            // Insert into guardian table (now referencing `student_id`)
            String guardianSQL = "INSERT INTO guardian (student_id, first_name, middle_name, last_name, address, email, contact_no, relationship) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement guardianStmt = kon.prepareStatement(guardianSQL)) {
                guardianStmt.setInt(1, studentID); // Use generated student_id
                guardianStmt.setString(2, guardianDetails.get("GFName"));
                guardianStmt.setString(3, guardianDetails.get("GMName"));
                guardianStmt.setString(4, guardianDetails.get("GLName"));
                guardianStmt.setString(5, guardianDetails.get("GAddress"));
                guardianStmt.setString(6, guardianDetails.get("GEmail"));
                guardianStmt.setString(7, guardianDetails.get("GContactNo"));
                guardianStmt.setString(8, guardianDetails.get("Relationship"));

                guardianStmt.executeUpdate();
            }

            kon.commit(); // Commit transaction
            loadStudents(); // Refresh student list
            clearFields(); // Clear form fields
            new Alert(Alert.AlertType.INFORMATION, "Student and Guardian inserted successfully!").show();

        } catch (SQLException ex) {
            try {
                kon.rollback(); // Rollback transaction on error
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        } finally {
            try {
                kon.setAutoCommit(true); // Restore default mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void updateStudent() {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return;
        }

        // Validate input fields
        if (isInputInvalid()) {
            return; // Stop execution if validation fails
        }

        try {
            kon.setAutoCommit(false); // Start transaction

            // Check if student exists
            String checkStudentSQL = "SELECT id FROM student WHERE sr_code = ? AND contact = ? AND email = ?";
            int studentID = -1;

            try (PreparedStatement checkStmt = kon.prepareStatement(checkStudentSQL)) {
                checkStmt.setString(1, srCode.getText());
                checkStmt.setString(2, contact.getText());
                checkStmt.setString(3, email.getText());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        studentID = rs.getInt("id"); // Get student ID instead of GID
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                        return;
                    }
                }
            }

            // Update guardian details
            String guardianSQL = "UPDATE guardian SET first_name = ?, middle_name = ?, last_name = ?, address = ?, email = ?, contact_no = ?, relationship = ? WHERE student_id = ?";
            try (PreparedStatement guardianStmt = kon.prepareStatement(guardianSQL)) {
                Map<String, String> guardianDetails = getGuardianDetails();
                if (guardianDetails.isEmpty()) {
                    return; // Stop update if user cancels input
                }

                guardianStmt.setString(1, guardianDetails.get("GFName"));
                guardianStmt.setString(2, guardianDetails.get("GMName"));
                guardianStmt.setString(3, guardianDetails.get("GLName"));
                guardianStmt.setString(4, guardianDetails.get("GAddress"));
                guardianStmt.setString(5, guardianDetails.get("GEmail"));
                guardianStmt.setString(6, guardianDetails.get("GContactNo"));
                guardianStmt.setString(7, guardianDetails.get("Relationship"));
                guardianStmt.setInt(8, studentID); // Use student_id as foreign key

                guardianStmt.executeUpdate();
            }

            // Update student details
            String studentSQL = "UPDATE student SET first_name = ?, middle_name = ?, last_name = ?, pic_link = ?, year_level = ?, program = ?, major = ?, contact = ?, email = ?, password = ?, address = ?, status = ? WHERE id = ?";
            try (PreparedStatement studentStmt = kon.prepareStatement(studentSQL)) {
                studentStmt.setString(1, firstName.getText());
                studentStmt.setString(2, middleName.getText());
                studentStmt.setString(3, lastName.getText());
                studentStmt.setString(4, picLink.getText());
                studentStmt.setString(5, yearLevel.getValue());
                studentStmt.setString(6, program.getValue());
                studentStmt.setString(7, major.getValue());
                studentStmt.setString(8, contact.getText());
                studentStmt.setString(9, email.getText());
                studentStmt.setString(10, password.getText());
                studentStmt.setString(11, address.getText());
                studentStmt.setString(12, status.getValue());
                studentStmt.setInt(13, studentID); // Update using student ID

                studentStmt.executeUpdate();
            }

            kon.commit(); // Commit transaction
            loadStudents(); // Refresh student list
            clearFields(); // Clear form fields
            new Alert(Alert.AlertType.INFORMATION, "Student details updated successfully!").show();

        } catch (SQLException ex) {
            try {
                kon.rollback(); // Rollback transaction on error
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        } finally {
            try {
                kon.setAutoCommit(true); // Restore default mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private boolean isStudentExists(String srCode, String email, String contact) {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return false;
        }

        String sql = "SELECT COUNT(*) FROM student WHERE sr_code = ? OR email = ? OR contact = ?";

        try (PreparedStatement stmt = kon.prepareStatement(sql)) {
            stmt.setString(1, srCode);
            stmt.setString(2, email);
            stmt.setString(3, contact);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
        return false;
    }


    @FXML
    private Map<String, String> getGuardianDetails() {
        Map<String, String> guardianData = new HashMap<>();

        // Get selected student from TableView
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.ERROR, "No student selected! Please select a student to update.").show();
            return new HashMap<>();
        }

        int studentID = selectedStudent.getStudID(); // Get student ID from the selected row

        // Query to fetch guardian details
        String fetchGuardianSQL = "SELECT first_name, middle_name, last_name, address, email, contact_no, relationship FROM guardian WHERE student_id = ?";

        try (PreparedStatement stmt = kon.prepareStatement(fetchGuardianSQL)) {
            stmt.setInt(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    guardianData.put("GFName", rs.getString("first_name"));
                    guardianData.put("GMName", rs.getString("middle_name"));
                    guardianData.put("GLName", rs.getString("last_name"));
                    guardianData.put("GAddress", rs.getString("address"));
                    guardianData.put("GEmail", rs.getString("email"));
                    guardianData.put("GContactNo", rs.getString("contact_no"));
                    guardianData.put("Relationship", rs.getString("relationship"));
                }
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error fetching guardian details: " + ex.getMessage()).show();
            return new HashMap<>(); // Return empty map on error
        }

        // Display the input dialog with pre-filled values
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Guardian Information");
        dialog.setHeaderText("Update Guardian Details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField gfName = new TextField(guardianData.getOrDefault("GFName", ""));
        TextField gmName = new TextField(guardianData.getOrDefault("GMName", ""));
        TextField glName = new TextField(guardianData.getOrDefault("GLName", ""));
        TextField gAddress = new TextField(guardianData.getOrDefault("GAddress", ""));
        TextField gEmail = new TextField(guardianData.getOrDefault("GEmail", ""));
        TextField gContactNo = new TextField(guardianData.getOrDefault("GContactNo", ""));

        ComboBox<String> relationship = new ComboBox<>();
        relationship.getItems().addAll("Father", "Mother", "Guardian");
        relationship.setValue(guardianData.getOrDefault("Relationship", "Father"));

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(gfName, 1, 0);
        grid.add(new Label("Middle Name:"), 0, 1);
        grid.add(gmName, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(glName, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(gAddress, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(gEmail, 1, 4);
        grid.add(new Label("Contact No.:"), 0, 5);
        grid.add(gContactNo, 1, 5);
        grid.add(new Label("Relationship:"), 0, 6);
        grid.add(relationship, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Map<String, String> result = new HashMap<>();
                result.put("GFName", gfName.getText());
                result.put("GMName", gmName.getText());
                result.put("GLName", glName.getText());
                result.put("GAddress", gAddress.getText());
                result.put("GEmail", gEmail.getText());
                result.put("GContactNo", gContactNo.getText());
                result.put("Relationship", relationship.getValue());
                return result;
            }
            return new HashMap<>();
        });

        return dialog.showAndWait().orElse(new HashMap<>());
    }


    /**
     * Validates if any required field is empty and displays an error message.
     * @return true if input is invalid, false if all fields are filled
     */

    @FXML
    private boolean isInputInvalid() {
        StringBuilder errorMessage = new StringBuilder();

        // Check for empty fields
        if (firstName.getText().trim().isEmpty()) errorMessage.append("First Name is required.\n");
        if (lastName.getText().trim().isEmpty()) errorMessage.append("Last Name is required.\n");
        if (srCode.getText().trim().isEmpty()) errorMessage.append("SR-Code is required.\n");
        if (yearLevel.getValue() == null) errorMessage.append("Year Level is required.\n");
        if (program.getValue() == null) errorMessage.append("Program is required.\n");
        if (major.getValue() == null) errorMessage.append("Major is required.\n");
        if (contact.getText().trim().isEmpty()) errorMessage.append("Contact is required.\n");
        if (email.getText().trim().isEmpty()) errorMessage.append("Email is required.\n");
        if (password.getText().trim().isEmpty()) errorMessage.append("Password is required.\n");
        if (address.getText().trim().isEmpty()) errorMessage.append("Address is required.\n");
        if (status.getValue() == null) errorMessage.append("Status is required.\n");

        // Show error message if any field is empty
        if (errorMessage.length() > 0) {
            new Alert(Alert.AlertType.ERROR, errorMessage.toString()).show();
            return true; // Input is invalid
        }

        return false; // Input is valid
    }


    @FXML
    private void clearFields() {
        Arrays.asList(studID, firstName, middleName, lastName, picLink, srCode, contact, email, password, address)
                .forEach(TextField::clear);

        Arrays.asList(yearLevel, program, major, status)
                .forEach(comboBox -> comboBox.setValue(null));
    }
}
