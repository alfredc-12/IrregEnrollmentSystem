package Classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

import ExtraSources.*;
import GettersSetters.*;

public class Section {

    @FXML
    private TableView<SectionModel> tableView;

    @FXML
    private TableColumn<SectionModel, String> colSection;

    @FXML
    private TableColumn<SectionModel, String> colDepartment;

    @FXML
    private TextField secname;

    @FXML
    private ComboBox<String> depcombo; // ComboBox for department

    @FXML
    private Button Add, Update;

    private ObservableList<SectionModel> sectionList = FXCollections.observableArrayList();
    private ObservableList<String> departmentList = FXCollections.observableArrayList(
            "CICS", "CAS", "CABEIHM", "CHS", "CTE", "CCJE"
    );

    private SectionModel selectedSection = null; // Store selected row
    private Connection kon;
    @FXML
    public void initialize() {
        kon = DBConnect.getConnection();
        colSection.setCellValueFactory(new PropertyValueFactory<>("section"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));

        depcombo.setItems(departmentList); // Set departments in ComboBox

        loadData();

        // Click event to populate fields
        tableView.setOnMouseClicked(event -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                handleRowClick();
            }
        });
    }

    @FXML
    private void deleteSection() {
        // Get selected item from the table
        SectionModel selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No section selected for deletion.");
            return;
        }

        // SQL Query to delete the selected section
        String query = "DELETE FROM section WHERE section_id = ?";

        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setString(1, selected.getSection()); // Use the section name as the identifier

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Section deleted successfully!");
                loadData(); // Refresh TableView after deletion
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRowClick() {
        selectedSection = tableView.getSelectionModel().getSelectedItem();
        if (selectedSection != null) {
            secname.setText(selectedSection.getSection());
            depcombo.setValue(selectedSection.getDepartment());
        }
    }

    private void loadData() {
        sectionList.clear(); // Clear existing data before reloading

        String query = "SELECT section_name, department FROM section";
        try (PreparedStatement stmt = kon.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sectionList.add(new SectionModel(rs.getString("section_name"), rs.getString("department")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(sectionList);
    }

    @FXML
    private void addSection() {
        String section = secname.getText().trim();
        String department = depcombo.getValue();

        if (section.isEmpty() || department == null) {
            System.out.println("Please fill in all fields.");
            return;
        }

        String query = "INSERT INTO section (section_name, department) VALUES (?, ?)";
        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setString(1, section);
            stmt.setString(2, department);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Section added successfully!");
                secname.clear();
                depcombo.getSelectionModel().clearSelection();
                loadData(); // Refresh TableView
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateSection() {
        if (selectedSection == null) {
            System.out.println("No section selected for update.");
            return;
        }

        String newSection = secname.getText().trim();
        String newDepartment = depcombo.getValue();

        if (newSection.isEmpty() || newDepartment == null) {
            System.out.println("Please fill in all fields.");
            return;
        }

        String query = "UPDATE section SET section_name = ?, department = ? WHERE section_id = ?";
        try (PreparedStatement stmt = kon.prepareStatement(query)) {
            stmt.setString(1, newSection);
            stmt.setString(2, newDepartment);
            stmt.setString(3, selectedSection.getSection()); // Update using old section name

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Section updated successfully!");
                secname.clear();
                depcombo.getSelectionModel().clearSelection();
                loadData();
                selectedSection = null; // Reset selection after update
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
