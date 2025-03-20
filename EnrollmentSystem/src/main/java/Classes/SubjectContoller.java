package Classes;

import ExtraSources.*;
import GettersSetters.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubjectContoller {

    @FXML
    private TextField CodeTXT;

    @FXML
    private Button DeleteBtn;

    @FXML
    private TextField DepartmentTXT;

    @FXML
    private Pane Pane;

    @FXML
    private TextField SubjectTXT;

    @FXML
    private Button addBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private TableView<Subject> subjectTable;
    @FXML
    private TableColumn<Subject, Integer> colId;
    @FXML
    private TableColumn<Subject, String> colSubjectName;
    @FXML
    private TableColumn<Subject, String> colCode;
    @FXML
    private TableColumn<Subject, String> colDepartment;

    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    private Connection kon;
    @FXML
    private void initialize() {
        kon = DBConnect.getConnection();
        // Bind columns to Subject properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSubjectName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Load subjects automatically on startup
        loadSubjects();

        // Set up button actions
        addBtn.setOnAction(event -> addSubject());
        DeleteBtn.setOnAction(event -> deleteSubject());
        updateBtn.setOnAction(event -> updateSubject());
    }

    private void loadSubjects() {
        subjectList.clear(); // Clear existing data

        String query = "SELECT * FROM subjects";

        try (PreparedStatement stmt = kon.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subjectList.add(new Subject(
                        rs.getInt("SSID"),
                        rs.getString("SubjectName"),
                        rs.getString("Code"),
                        rs.getString("Department")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        subjectTable.setItems(subjectList);
    }

    private void addSubject() {
        String subjectName = SubjectTXT.getText();
        String code = CodeTXT.getText();
        String department = DepartmentTXT.getText();

        if (subjectName.isEmpty() || code.isEmpty() || department.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        String query = "INSERT INTO subjects (SubjectName, Code, Department) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setString(1, subjectName);
            stmt.setString(2, code);
            stmt.setString(3, department);
            stmt.executeUpdate();
            System.out.println("Subject added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding subject: " + e.getMessage());
        }

        loadSubjects();
        clearFields();
    }

    private void deleteSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            System.out.println("Please select a subject to delete.");
            return;
        }

        String query = "DELETE FROM subjects WHERE SSID = ?";

        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setInt(1, selectedSubject.getId());
            stmt.executeUpdate();
            System.out.println("Subject deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting subject: " + e.getMessage());
        }

        loadSubjects();
    }

    private void updateSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            System.out.println("Please select a subject to update.");
            return;
        }

        String newSubjectName = SubjectTXT.getText();
        String newCode = CodeTXT.getText();
        String newDepartment = DepartmentTXT.getText();

        if (newSubjectName.isEmpty() || newCode.isEmpty() || newDepartment.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        String query = "UPDATE subjects SET SubjectName = ?, Code = ?, Department = ? WHERE SSID = ?";

        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setString(1, newSubjectName);
            stmt.setString(2, newCode);
            stmt.setString(3, newDepartment);
            stmt.setInt(4, selectedSubject.getId());
            stmt.executeUpdate();
            System.out.println("Subject updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating subject: " + e.getMessage());
        }

        loadSubjects();
        clearFields();
    }

    private void clearFields() {
        SubjectTXT.clear();
        CodeTXT.clear();
        DepartmentTXT.clear();
    }
}
