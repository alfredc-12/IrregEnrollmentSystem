package Classes;

import ExtraSources.ClassListGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import ExtraSources.DBConnect;

public class ClassListController implements Initializable {

    @FXML private ComboBox<String> subjectComboBox;
    @FXML private ComboBox<String> sectionComboBox;
    @FXML private Button generateButton;
    @FXML private AnchorPane mainPane;
    @FXML private Label statusLabel;
    @FXML private Button btnExit;
    @FXML private ListView<String> sectionList;
    @FXML private ListView<String> subjectList;
    @FXML private TextField searchSec;
    @FXML private TextField searchSub;

    @FXML
    public Pane navigation;

    private ObservableList<String> allSubjects = FXCollections.observableArrayList();
    private ObservableList<String> allSections = FXCollections.observableArrayList();
    private FilteredList<String> filteredSubjects;
    private FilteredList<String> filteredSections;
    private Map<String, String> subjectDisplayToCode = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set prompt text for the combo boxes
        subjectComboBox.setPromptText("Select a subject");
        sectionComboBox.setPromptText("Select a section");

        loadSubjects();
        loadSections();
        setupSearchFilters();
        setupListViewListeners();

        generateButton.setOnAction(event -> generateClassList());
        btnExit.setOnAction(event -> closeWindow());
    }

    private void loadSubjects() {
        try (Connection conn = DBConnect.getConnection()) {
            // Clear existing data
            allSubjects.clear();
            subjectDisplayToCode.clear();
            ObservableList<String> subjectCodes = FXCollections.observableArrayList();

            // First get the current semester from the current table
            String currentQuery = "SELECT Semester FROM current ORDER BY currentID DESC LIMIT 1";

            try (PreparedStatement currentPs = conn.prepareStatement(currentQuery);
                 ResultSet currentRs = currentPs.executeQuery()) {

                if (currentRs.next()) {
                    String currentSemester = currentRs.getString("Semester");
                    System.out.println("Current semester from database: " + currentSemester);

                    // Only load subjects for the current semester
                    String sql = "SELECT subj_code, subject_name FROM subjects WHERE semester = ? ORDER BY subj_code";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, currentSemester);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                String code = rs.getString("subj_code");
                                String name = rs.getString("subject_name");
                                String displayText = code + " " + name;

                                // Add to maps for lookup
                                subjectDisplayToCode.put(displayText, code);

                                // Add to collections
                                subjectCodes.add(code);
                                allSubjects.add(displayText);
                            }
                        }
                    }
                } else {
                    System.out.println("No semester found in the current table.");
                    showAlert("No current semester is set in the system");
                }
            }

            // Setup ComboBox without selecting first item
            subjectComboBox.setItems(subjectCodes);

            // Setup ListView with filtered list
            filteredSubjects = new FilteredList<>(allSubjects, p -> true);
            subjectList.setItems(filteredSubjects);

            System.out.println("Loaded " + allSubjects.size() + " subjects for current semester");

        } catch (SQLException e) {
            showAlert("Error loading subjects: " + e.getMessage());
        }
    }

    private void loadSections() {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT DISTINCT section_name FROM section ORDER BY section_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> sections = FXCollections.observableArrayList();

            while (rs.next()) {
                String section = rs.getString("section_name");
                sections.add(section);
                allSections.add(section);
            }

            // Setup ComboBox without selecting first item
            sectionComboBox.setItems(sections);

            // Setup ListView with filtered list
            filteredSections = new FilteredList<>(allSections, p -> true);
            sectionList.setItems(filteredSections);

        } catch (SQLException e) {
            showAlert("Error loading sections: " + e.getMessage());
        }
    }

    private void setupSearchFilters() {
        // Subject search filter
        searchSub.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSubjects.setPredicate(subject -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return subject.toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Section search filter
        searchSec.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSections.setPredicate(section -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return section.toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private String convertSubjectYearLevelToSectionYear(String subjectYearLevel) {
        if (subjectYearLevel == null) return null;

        // Map subject year levels to section year numbers
        switch (subjectYearLevel) {
            case "First Year":
                return "1st Year";
            case "Second Year":
                return "2nd Year";
            case "Third Year":
                return "3rd Year";
            case "Fourth Year":
                return "4th Year";
            default:
                return null;
        }
    }

    private void setupListViewListeners() {
        // When a subject is selected in the ListView, update the ComboBox and filter sections
        subjectList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String subjectCode = subjectDisplayToCode.get(newValue);
                subjectComboBox.getSelectionModel().select(subjectCode);

                // Filter sections based on the year level of the selected subject
                filterSectionsBySubjectYearLevel(subjectCode);
            }
        });

        // When a section is selected in the ListView, update the ComboBox
        sectionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sectionComboBox.getSelectionModel().select(newValue);
            }
        });
    }

    private void filterSectionsBySubjectYearLevel(String subjectCode) {
        try (Connection conn = DBConnect.getConnection()) {
            // Get the year level of the selected subject
            String yearLevelQuery = "SELECT year_level FROM subjects WHERE subj_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(yearLevelQuery)) {
                stmt.setString(1, subjectCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String subjectYearLevel = rs.getString("year_level");

                        // Convert subject year level to section year level format
                        String sectionYearLevel = convertSubjectYearLevelToSectionYear(subjectYearLevel);

                        // Filter sections by year level
                        filteredSections.setPredicate(section -> {
                            if (sectionYearLevel == null) {
                                return true; // Show all if we can't determine the year level
                            }

                            try {
                                String sectionQuery = "SELECT year_level FROM section WHERE section_name = ?";
                                try (PreparedStatement sectionStmt = conn.prepareStatement(sectionQuery)) {
                                    sectionStmt.setString(1, section);
                                    try (ResultSet sectionRs = sectionStmt.executeQuery()) {
                                        if (sectionRs.next()) {
                                            String secYearLevel = sectionRs.getString("year_level");
                                            return secYearLevel.equals(sectionYearLevel);
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            return false;
                        });
                    } else {
                        // If subject not found, show all sections
                        filteredSections.setPredicate(s -> true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error filtering sections: " + e.getMessage());
            // On error, show all sections
            filteredSections.setPredicate(s -> true);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    private void generateClassList() {
        String subject = subjectComboBox.getValue();
        String section = sectionComboBox.getValue();

        if (subject == null || section == null) {
            showAlert("Please select both subject and section");
            return;
        }

        try {
            statusLabel.setText("Generating class list...");

            // Create a suggested filename
            String suggestedFileName = subject + "_" + section.replace(" ", "_") + "_ClassList.xlsx";

            // Create and configure file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Class List");
            fileChooser.setInitialFileName(suggestedFileName);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            // Show save dialog
            File file = fileChooser.showSaveDialog(btnExit.getScene().getWindow());

            if (file != null) {
                // User selected a location, generate and save the file
                ClassListGenerator.generateClassList(subject, section, file);
                statusLabel.setText("Class list generated successfully: " + file.getName());

                // Ask if user wants to open the file
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Class list generated successfully!\n\nFile: " + file.getName() +
                                "\n\nWould you like to open it now?",
                        ButtonType.YES, ButtonType.NO);
                alert.setTitle("Success");
                alert.setHeaderText("Class List Generated");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            // Open the file with the default application
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            showAlert("Error opening file: " + e.getMessage());
                        }
                    }
                });
            } else {
                // User canceled the save dialog
                statusLabel.setText("Class list generation canceled");
            }
        } catch (SQLException | IOException e) {
            statusLabel.setText("Error: " + e.getMessage());
            showAlert("Error generating class list: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}