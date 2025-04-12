package Classes;

import ExtraSources.DBConnect;
import GettersSetters.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javax.swing.*;

import static com.sun.glass.ui.Cursor.setVisible;

public class SubjectContoller {
    private final javafx.collections.ObservableList<Subject> subjectList = javafx.collections.FXCollections.observableArrayList();

    @FXML
    private TextField AcadTrackTXT;

    @FXML
    private Button Add;

    @FXML
    private Button delete;

    @FXML
    private TextField LabTXT;

    @FXML
    private TextField LectureTXT;

    @FXML
    private TextField PrerequisiteTXT;

    @FXML
    private TextField SCodeTXT;

    @FXML
    private ComboBox<String> SemCmbBx;

    @FXML
    private TextField SubjectTXT;

    @FXML
    private TextField UnitTXT;

    @FXML
    private ComboBox<String> YLevelCmbBx;

    @FXML
    private javafx.scene.control.TableView<Subject> subjectTable;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> subjCodeCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> subjectNameCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> yearLevelCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> semesterCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, Integer> lectureCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, Integer> labCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, Integer> unitsCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> prerequisiteCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, String> acadTrackCol;

    @FXML
    private javafx.scene.control.TableColumn<Subject, Void> acadTrackCol1;



    // Initialize method to populate combo boxes
    @FXML
    public void initialize() {
        YLevelCmbBx.getItems().addAll("First Year", "Second Year", "Third Year", "Fourth Year");
        SemCmbBx.getItems().addAll("First Semester", "Second Semester", "Midterm");

        subjCodeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubjCode()));
        subjectNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubjectName()));
        lectureCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getLecture()).asObject());
        labCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getLab()).asObject());
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUnits()).asObject());
        yearLevelCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getYearLevel()));
        semesterCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSemester()));
        prerequisiteCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrerequisite()));
        acadTrackCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAcadTrack()));

        // Add key press event listeners to TextFields and ComboBoxes
        setEnterKeyListener(SCodeTXT);
        setEnterKeyListener(SubjectTXT);
        setEnterKeyListener(LectureTXT);
        setEnterKeyListener(LabTXT);
        setEnterKeyListener(UnitTXT);
        setEnterKeyListener(PrerequisiteTXT);
        setEnterKeyListener(AcadTrackTXT);
        setEnterKeyListener(YLevelCmbBx);
        setEnterKeyListener(SemCmbBx);

        // Initial load
        loadSubjectTable();
        addUpdateButtonToTable();

    }

    private void setEnterKeyListener(javafx.scene.control.Control control) {
        control.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleUpdateConfirmation();
            }
        });
    }


    private void handleUpdateConfirmation() {
        // Create an Alert for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Subject");
        alert.setHeaderText("Are you sure you want to update this subject?");
        alert.setContentText("Changes will be saved.");

        // Wait for user response
        Optional<ButtonType> result = alert.showAndWait();

        // If user clicks YES, proceed with the update
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateSubjectInDatabase();
        } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            // Clear all the fields if user clicks Cancel
            clearFields();
        }
    }

    private void clearFields() {
        // Clear TextFields
        SCodeTXT.clear();
        SubjectTXT.clear();
        LectureTXT.clear();
        LabTXT.clear();
        UnitTXT.clear();
        PrerequisiteTXT.clear();
        AcadTrackTXT.clear();

        // Clear ComboBoxes
        YLevelCmbBx.getSelectionModel().clearSelection();
        SemCmbBx.getSelectionModel().clearSelection();
    }



    private void loadSubjectTable() {
        subjectList.clear(); // Clear existing data

        try (Connection connection = DBConnect.getConnection()) {
            String query = "SELECT subj_code, subject_name, lecture, lab, units, year_level, semester, prerequisite, acad_track FROM subjects";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setSubjCode(rs.getString("subj_code"));
                subject.setSubjectName(rs.getString("subject_name"));
                subject.setLecture(rs.getInt("lecture"));
                subject.setLab(rs.getInt("lab"));
                subject.setUnits(rs.getInt("units"));
                subject.setYearLevel(rs.getString("year_level"));
                subject.setSemester(rs.getString("semester"));
                subject.setPrerequisite(rs.getString("prerequisite"));
                subject.setAcadTrack(rs.getString("acad_track"));

                subjectList.add(subject);
            }


            subjectTable.setItems(subjectList); // Set list to table

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUpdateButtonToTable() {
        Callback<TableColumn<Subject, Void>, TableCell<Subject, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Subject, Void> call(final TableColumn<Subject, Void> param) {
                return new TableCell<>() {

                    // Create a button to hold the image icon
                    private final Button updateButton = new Button();
                    private final ImageView updateIcon = new ImageView(new Image(getClass().getResourceAsStream("/Images/icons8-edit-30.png")));

                    {
                        updateIcon.setFitWidth(20);
                        updateIcon.setFitHeight(20);
                        updateButton.setGraphic(updateIcon); // Set the ImageView as the button's graphic
                        updateButton.setStyle("-fx-cursor: hand;"); // Set cursor to hand to indicate clickability

                        // Add click event to button
                        updateButton.setOnAction(event -> {
                            Subject subject = getTableView().getItems().get(getIndex());
                            // Pre-fill the form with the subject data
                            SCodeTXT.setText(subject.getSubjCode());
                            SubjectTXT.setText(subject.getSubjectName());
                            LectureTXT.setText(String.valueOf(subject.getLecture()));
                            LabTXT.setText(String.valueOf(subject.getLab()));
                            UnitTXT.setText(String.valueOf(subject.getUnits()));
                            YLevelCmbBx.setValue(subject.getYearLevel());
                            SemCmbBx.setValue(subject.getSemester());
                            PrerequisiteTXT.setText(subject.getPrerequisite());
                            AcadTrackTXT.setText(subject.getAcadTrack());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(updateButton);  // Set the button in the cell
                        }
                    }
                };
            }
        };

        acadTrackCol1.setCellFactory(cellFactory); // Apply the cell factory to the column
    }




    // Add event handler for the "Add" button
    @FXML
    public void addSubject() {
        // Retrieve input values from the form
        String subjCode = SCodeTXT.getText();
        String subjectName = SubjectTXT.getText();
        Integer lecture = null;
        Integer lab = null;
        int units = Integer.valueOf(UnitTXT.getText());

        // If the lecture field is not empty, parse it
        if (!LectureTXT.getText().isEmpty()) {
            lecture = Integer.valueOf(LectureTXT.getText());
        }

        // If the lab field is not empty, parse it
        if (!LabTXT.getText().isEmpty()) {
            lab = Integer.valueOf(LabTXT.getText());
        }

        // If the prerequisite field is not empty, use the value
        String prerequisite = PrerequisiteTXT.getText().isEmpty() ? null : PrerequisiteTXT.getText();

        // Trim the combo box selections to ensure there are no leading/trailing spaces
        String yearLevel = YLevelCmbBx.getValue() != null ? YLevelCmbBx.getValue().trim() : null;
        String semester = SemCmbBx.getValue() != null ? SemCmbBx.getValue().trim() : null;

        // Validate Year Level and Semester selections
        if (yearLevel == null || yearLevel.isEmpty()) {
            System.out.println("Please select a valid Year Level.");
            return; // Prevent insertion if Year Level is not selected
        }

        if (semester == null || semester.isEmpty()) {
            System.out.println("Please select a valid Semester.");
            return; // Prevent insertion if Semester is not selected
        }

        // Create a Subject object with the retrieved values
        Subject subject = new Subject();
        subject.setSubjCode(subjCode);
        subject.setSubjectName(subjectName);
        subject.setLecture(lecture);  // May be null
        subject.setLab(lab);  // May be null
        subject.setUnits(units);
        subject.setYearLevel(yearLevel);
        subject.setSemester(semester);
        subject.setPrerequisite(prerequisite);  // May be null
        subject.setAcadTrack(AcadTrackTXT.getText());

        // Insert the subject into the database
        insertSubject(subject);
        loadSubjectTable();
        clearFields();
    }

    private void insertSubject(Subject subject) {
        String query = "INSERT INTO subjects (subj_code, subject_name, lecture, lab, units, year_level, semester, prerequisite, acad_track) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the parameters for the query
            statement.setString(1, subject.getSubjCode());
            statement.setString(2, subject.getSubjectName());

            // Check if lecture is null before setting
            if (subject.getLecture() != null) {
                statement.setInt(3, subject.getLecture());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);  // Set as NULL if lecture is not provided
            }

            // Check if lab is null before setting
            if (subject.getLab() != null) {
                statement.setInt(4, subject.getLab());
            } else {
                statement.setNull(4, java.sql.Types.INTEGER);  // Set as NULL if lab is not provided
            }

            statement.setInt(5, subject.getUnits());

            // Check if year level is valid
            statement.setString(6, subject.getYearLevel());

            // Check if semester is valid
            statement.setString(7, subject.getSemester());

            // Check if prerequisite is null before setting
            if (subject.getPrerequisite() != null) {
                statement.setString(8, subject.getPrerequisite());
            } else {
                statement.setNull(8, java.sql.Types.VARCHAR);  // Set as NULL if prerequisite is not provided
            }

            statement.setString(9, subject.getAcadTrack());

            // Execute the query
            int result = statement.executeUpdate();
            if (result > 0) {
                System.out.println("Subject added successfully!");
            } else {
                System.out.println("Failed to add subject.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateSubjectInDatabase() {
        // Retrieve updated values from the form fields
        String subjCode = SCodeTXT.getText();
        String subjectName = SubjectTXT.getText();
        Integer lecture = Integer.valueOf(LectureTXT.getText());
        Integer lab = Integer.valueOf(LabTXT.getText());
        int units = Integer.valueOf(UnitTXT.getText());

        String yearLevel = YLevelCmbBx.getValue();
        String semester = SemCmbBx.getValue();
        String prerequisite = PrerequisiteTXT.getText();
        String acadTrack = AcadTrackTXT.getText();

        // Update the subject in the database
        String query = "UPDATE subjects SET subject_name = ?, lecture = ?, lab = ?, units = ?, year_level = ?, semester = ?, prerequisite = ?, acad_track = ? WHERE subj_code = ?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set parameters for the query
            statement.setString(1, subjectName);
            statement.setInt(2, lecture);
            statement.setInt(3, lab);
            statement.setInt(4, units);
            statement.setString(5, yearLevel);
            statement.setString(6, semester);
            statement.setString(7, prerequisite);
            statement.setString(8, acadTrack);
            statement.setString(9, subjCode);

            // Execute the query
            int result = statement.executeUpdate();
            if (result > 0) {
                System.out.println("Subject updated successfully!");
            } else {
                System.out.println("Failed to update subject.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleDeleteButtonAction() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this subject?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                softDeleteSubject(selectedSubject.getSubId()); // replace with the actual getter method for ID
                subjectTable.getItems().remove(selectedSubject); // visually remove from table
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a subject to delete.");
            alert.showAndWait();
        }
    }

    private void softDeleteSubject(int subjectId) {
        String query = "UPDATE subjects SET isDeleted = 0 WHERE subjectId = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, subjectId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Subject soft-deleted successfully.");
            } else {
                System.out.println("Subject not found or already deleted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
