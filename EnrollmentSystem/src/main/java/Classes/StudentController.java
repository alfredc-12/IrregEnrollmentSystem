package Classes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import GettersSetters.*;
import ExtraSources.*;

import javafx.scene.layout.GridPane;
import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;



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

    @FXML private TextField firstName, middleName, lastName, picLink, srCode, contact, email, password, address;
    @FXML private ComboBox<String> yearLevel, program, major, status;
    @FXML private Button insertButton, clearButton;
    @FXML private TextField studID; // If needed for the database

    @FXML private Button Delete;
    @FXML private TextField searchField; //search or filter


    private ObservableList<Student> studentList = FXCollections.observableArrayList();


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

        // Load student data
        loadStudents();

        //filter
        setupSearchFilter();

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
                String query = "DELETE FROM student WHERE studID = ?";
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

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchStudentsInDatabase(newValue);
        });
    }

    private void searchStudentsInDatabase(String searchText) {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return;
        }

        ObservableList<Student> filteredStudents = FXCollections.observableArrayList();

        String sql = "SELECT * FROM student WHERE " +
                "FirstName LIKE ? OR MiddleName LIKE ? OR LastName LIKE ? OR " +
                "`SR-Code` LIKE ? OR YearLevel LIKE ? OR Program LIKE ? OR " +
                "Major LIKE ? OR Contact LIKE ? OR Email LIKE ? OR " +
                "Address LIKE ? OR Status LIKE ?";

        try (PreparedStatement stmt = kon.prepareStatement(sql)) {
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 11; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("studID"),
                        rs.getString("FirstName"),
                        rs.getString("MiddleName"),
                        rs.getString("LastName"),
                        rs.getString("SR-Code"),
                        rs.getString("YearLevel"),
                        rs.getString("Program"),
                        rs.getString("Major"),
                        rs.getString("Contact"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("Status")
                );
                filteredStudents.add(student);
            }

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
                        rs.getInt("StudID"),
                        rs.getString("FirstName"),
                        rs.getString("MiddleName"),
                        rs.getString("LastName"),
                        rs.getString("SR-Code"),
                        rs.getString("YearLevel"),
                        rs.getString("Program"),
                        rs.getString("Major"),
                        rs.getString("Contact"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("Status")
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

        // Get guardian details
        Map<String, String> guardianDetails = getGuardianDetails();
        if (guardianDetails.isEmpty()) {
            return; // If the user cancels input, stop insertion
        }

        try {
            kon.setAutoCommit(false); // Start transaction

            // Insert into guardian table
            String guardianSQL = "INSERT INTO guardian (GFName, GMName, GLName, GAddress, GEmail, GContactNo, Relationship) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int guardianID;
            try (PreparedStatement guardianStmt = kon.prepareStatement(guardianSQL, Statement.RETURN_GENERATED_KEYS)) {
                guardianStmt.setString(1, guardianDetails.get("GFName"));
                guardianStmt.setString(2, guardianDetails.get("GMName"));
                guardianStmt.setString(3, guardianDetails.get("GLName"));
                guardianStmt.setString(4, guardianDetails.get("GAddress"));
                guardianStmt.setString(5, guardianDetails.get("GEmail"));
                guardianStmt.setString(6, guardianDetails.get("GContactNo"));
                guardianStmt.setString(7, guardianDetails.get("Relationship"));
                guardianStmt.executeUpdate();

                // Get the generated GID
                ResultSet rs = guardianStmt.getGeneratedKeys();
                if (rs.next()) {
                    guardianID = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve Guardian ID.");
                }
            }

            // Insert into student table
            String studentSQL = "INSERT INTO student (FirstName, MiddleName, LastName, PicLink, `SR-Code`, YearLevel, Program, Major, Contact, Email, Password, Address, Status, isDeleted, GID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement studentStmt = kon.prepareStatement(studentSQL)) {
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
                studentStmt.setInt(15, guardianID); // Assign generated GID

                studentStmt.executeUpdate();
                loadStudents();
                clearFields();
            }

            kon.commit(); // Commit transaction
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
            String checkStudentSQL = "SELECT GID FROM student WHERE `SR-Code` = ? AND Contact = ? AND Email = ?";
            int guardianID = -1;
            try (PreparedStatement checkStmt = kon.prepareStatement(checkStudentSQL)) {
                checkStmt.setString(1, srCode.getText());
                checkStmt.setString(2, contact.getText());
                checkStmt.setString(3, email.getText());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    guardianID = rs.getInt("GID");
                } else {
                    new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                    return;
                }
            }

            // Update guardian details
            String guardianSQL = "UPDATE guardian SET GFName = ?, GMName = ?, GLName = ?, GAddress = ?, GEmail = ?, GContactNo = ?, Relationship = ? WHERE GID = ?";
            try (PreparedStatement guardianStmt = kon.prepareStatement(guardianSQL)) {
                Map<String, String> guardianDetails = getGuardianDetails();
                if (guardianDetails.isEmpty()) {
                    return; // If the user cancels input, stop update
                }

                guardianStmt.setString(1, guardianDetails.get("GFName"));
                guardianStmt.setString(2, guardianDetails.get("GMName"));
                guardianStmt.setString(3, guardianDetails.get("GLName"));
                guardianStmt.setString(4, guardianDetails.get("GAddress"));
                guardianStmt.setString(5, guardianDetails.get("GEmail"));
                guardianStmt.setString(6, guardianDetails.get("GContactNo"));
                guardianStmt.setString(7, guardianDetails.get("Relationship"));
                guardianStmt.setInt(8, guardianID);

                guardianStmt.executeUpdate();
            }

            // Update student details
            String studentSQL = "UPDATE student SET FirstName = ?, MiddleName = ?, LastName = ?, PicLink = ?, YearLevel = ?, Program = ?, Major = ?, Contact = ?, Email = ?, Password = ?, Address = ?, Status = ? WHERE `SR-Code` = ?";
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
                studentStmt.setString(13, srCode.getText());

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
        String sql = "SELECT COUNT(*) FROM student WHERE `SR-Code` = ? OR Email = ? OR Contact = ?";
        try (PreparedStatement stmt = kon.prepareStatement(sql)) {
            stmt.setString(1, srCode);
            stmt.setString(2, email);
            stmt.setString(3, contact);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Student already exists
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
        return false;
    }

    @FXML
    private Map<String, String> getGuardianDetails() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Guardian Information");
        dialog.setHeaderText("Enter Guardian Details:");

        // Create layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create text fields
        TextField gfName = new TextField();
        TextField gmName = new TextField();
        TextField glName = new TextField();
        TextField gAddress = new TextField();
        TextField gEmail = new TextField();
        TextField gContactNo = new TextField();

        // Create ComboBox for Relationship
        ComboBox<String> relationship = new ComboBox<>();
        relationship.getItems().addAll("Father", "Mother", "Guardian");
        relationship.setValue("Father"); // Default selection

        // Add fields to layout
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

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (relationship.getValue() == null) {
                    new Alert(Alert.AlertType.ERROR, "Please select a relationship type!").show();
                    return new HashMap<>(); // Return empty map if no selection
                }

                Map<String, String> guardianData = new HashMap<>();
                guardianData.put("GFName", gfName.getText());
                guardianData.put("GMName", gmName.getText());
                guardianData.put("GLName", glName.getText());
                guardianData.put("GAddress", gAddress.getText());
                guardianData.put("GEmail", gEmail.getText());
                guardianData.put("GContactNo", gContactNo.getText());
                guardianData.put("Relationship", relationship.getValue()); // Get selected value

                return guardianData;
            }
            return new HashMap<>(); // Empty map if canceled
        });

        return dialog.showAndWait().orElse(new HashMap<>()); // Return collected data or empty map
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
