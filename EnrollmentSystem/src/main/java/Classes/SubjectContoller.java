/*package Classes;

import GettersSetters.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import ExtraSources.*;

import java.sql.*;

public class SubjectContoller {

    @FXML
    private TextField CHoursTXT;

    @FXML
    private TextField RoomTXT;

    @FXML
    private TextField SubjectTXT;

    @FXML
    private CheckBox majorchkbx;

    @FXML
    private TableView<Subject> subjectTable; // Define TableView to hold Subject objects

    @FXML
    private TableColumn<Subject, String> subjectColumn;

    @FXML
    private TableColumn<Subject, Integer> hoursColumn;

    @FXML
    private TableColumn<Subject, String> roomColumn;

    @FXML
    private TableColumn<Subject, Boolean> majorColumn;

    @FXML
    private Button addBtn, updateBtn, DeleteBtn;

    @FXML
    private Pane Pane;

    private ObservableList<Subject> subjectList = FXCollections.observableArrayList(); // List to store subjects

    @FXML
    public void initialize() {
        // Initialize TableView columns
        subjectColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubjectName()));
        //hoursColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        //roomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPreferredRoom()));
        //majorColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().getIsMajor()).asObject());

        subjectTable.setItems(subjectList);

        // Set Pane size (750x450)
        Pane.setPrefSize(750, 450);
        loadSubjects();
    }

    private void loadSubjects() {
        subjectList.clear();
        Connection kon = DBConnect.getConnection();
        String query = "SELECT subject_name, credit_hours, is_major FROM subjects";

        try (PreparedStatement stmt = kon.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
              /*  subjectList.add(new Subject(
                        0, // Set a default or dummy ID since we're not retrieving sub_id
                        rs.getString("subject_name"),
                        rs.getInt("credit_hours"),
                        rs.getBoolean("is_major"),
                        "" // You can store preferredRoom if needed
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load subjects.");
        }

        subjectTable.setItems(subjectList);
    }


    @FXML
    private void addSubject() {
        String subjectName = SubjectTXT.getText();
        String creditHoursStr = CHoursTXT.getText();
        String preferredRoom = RoomTXT.getText();
        boolean isMajor = majorchkbx.isSelected();

        if (subjectName.isEmpty() || creditHoursStr.isEmpty() || preferredRoom.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all fields.");
            return;
        }

        try {
            int creditHours = Integer.parseInt(creditHoursStr);
            Connection kon = DBConnect.getConnection();

            String query = "INSERT INTO subjects (subject_name, credit_hours, is_major) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = kon.prepareStatement(query)) {
                stmt.setString(1, subjectName);
                stmt.setInt(2, creditHours);
                stmt.setBoolean(3, isMajor);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "Subject added successfully!");
                    loadSubjects(); // Refresh table
                    clearFields();
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Credit hours must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add subject.");
        }
    }

    @FXML
    private void updateSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            showAlert("No Selection", "Please select a subject to update.");
            return;
        }

        String subjectName = SubjectTXT.getText();
        String creditHoursStr = CHoursTXT.getText();
        String preferredRoom = RoomTXT.getText();
        boolean isMajor = majorchkbx.isSelected();

        if (subjectName.isEmpty() || creditHoursStr.isEmpty() || preferredRoom.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all fields.");
            return;
        }

        try {
            int creditHours = Integer.parseInt(creditHoursStr);
            Connection kon = DBConnect.getConnection();

            String query = "UPDATE subjects SET subject_name = ?, credit_hours = ?, is_major = ? WHERE sub_id = ?";
            try (PreparedStatement stmt = kon.prepareStatement(query)) {
                stmt.setString(1, subjectName);
                stmt.setInt(2, creditHours);
                stmt.setBoolean(3, isMajor);
                //stmt.setInt(4, selectedSubject.getId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert("Success", "Subject updated successfully!");
                    loadSubjects(); // Refresh table
                    clearFields();
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Credit hours must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update subject.");
        }
    }

    @FXML
    private void deleteSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            showAlert("No Selection", "Please select a subject to delete.");
            return;
        }

        Connection kon = DBConnect.getConnection();
        String query = "DELETE FROM subjects WHERE sub_id = ?";

        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            //stmt.setInt(1, selectedSubject.getId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Subject deleted successfully!");
                loadSubjects(); // Refresh table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete subject.");
        }
    }


    private void clearFields() {
        SubjectTXT.clear();
        CHoursTXT.clear();
        RoomTXT.clear();
        majorchkbx.setSelected(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
*/