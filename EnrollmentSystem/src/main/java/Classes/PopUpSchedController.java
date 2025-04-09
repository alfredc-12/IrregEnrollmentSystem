// File: EnrollmentSystem/src/main/java/Classes/PopUpSchedController.java
package Classes;

import ExtraSources.ORToolsScheduler;
import GettersSetters.ScheduleModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ExtraSources.DBConnect;

public class PopUpSchedController {

    private SchedulerController parentController;

    // Setter to pass the parent controller reference
    public void setParentController(SchedulerController parentController) {
        this.parentController = parentController;
    }

    // Now you can call loadScheduleData() on the parent controller when needed.
    private void loadSched() {
        if (parentController != null) {
            parentController.loadScheduleData();
        }
    }

    @FXML
    private ListView<String> subjectListView;

    @FXML
    private Button btnDeleteSched;

    @FXML
    private Button btnGenerateSched;

    @FXML
    private ListView<String> facultyListView;

    @FXML
    private ComboBox<String> subjectCb;

    @FXML
    private ComboBox<String> facultyCb;

    @FXML
    private TextField searchSubject;

    @FXML
    private TextField searchFaculty;

    // ObservableLists for list and combo boxes
    private ObservableList<String> allSubjectItems;
    private ObservableList<String> allFacultyItems;


    @FXML
    public void initialize() {
        // Load list view and combo box data
        loadSubjects();
        loadFaculties();
        loadSubjectsComboBox();
        loadFacultyComboBox();
        listViewSelections();
        initializeSubjectSearch();
        initializeFacultySearch();

        btnGenerateSched.setOnAction(e -> generateSchedule());

        // Delete schedule record when btnDeleteSched is clicked
        btnDeleteSched.setOnAction(e -> deleteScheduleRecord());

    }

    // Handler to delete record from the database based on the selected item in the parent's table view.
    private void deleteScheduleRecord() {
        // Get the selected schedule record from the parent controller.
        ScheduleModel selected = parentController.getSelectedSchedule();
        if (selected == null) {
            System.out.println("No record selected to delete.");
            return;
        }

        // Display a confirmation dialog asking the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Record");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this record?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            int schedId = selected.getSchedId();
            String sql = "DELETE FROM subsched WHERE sched_id = ?";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, schedId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Record deleted successfully.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            // Refresh table view in the parent controller
            parentController.loadScheduleData();
        }
    }

    private int getRoomIdByType(String roomType) {
        int roomId = -1;
        String sql = "SELECT room_id FROM rooms WHERE room_type = ? LIMIT 1";

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, roomType);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                roomId = rs.getInt("room_id");
            }

            // Close resources individually, not through try-with-resources
            rs.close();
            ps.close();
            // Don't close the connection here
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    private String convertYearLevel(String yearLevel) {
        if (yearLevel == null) return null;

        return switch (yearLevel.toLowerCase()) {
            case "first year" -> "1st Year";
            case "second year" -> "2nd Year";
            case "third year" -> "3rd Year";
            case "fourth year" -> "4th Year";
            default -> yearLevel; // Return as-is if no match
        };
    }

    @FXML
    private void generateSchedule() {
        // Get selected subject and faculty from combo boxes
        String selectedSubject = subjectCb.getValue();
        String selectedFaculty = facultyCb.getValue();

        if (selectedSubject == null || selectedFaculty == null) {
            showAlert("Error", "Please select both subject and faculty.");
            return;
        }

        // Extract IDs from the formatted strings
        int subjectId = Integer.parseInt(selectedSubject.substring(1, selectedSubject.indexOf(",")));
        int facultyId = Integer.parseInt(selectedFaculty.substring(1, selectedFaculty.indexOf(",")));

        // Declare variables outside try block
        int lectureDuration = 0;
        int labDuration = 0;
        String yearLevel = null;
        int lectureRoomId = -1;
        int labRoomId = -1;
        List<ORToolsScheduler.TimeSlot> existingTimeSlots = new ArrayList<>();
        List<Integer> sectionIds = new ArrayList<>();

        // Get subject details in a separate connection
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT year_level, lecture, lab FROM subjects WHERE sub_id = ?")) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    yearLevel = rs.getString("year_level");
                    lectureDuration = rs.getInt("lecture") * 60; // Convert hours to minutes
                    Integer lab = rs.getObject("lab", Integer.class);
                    if (lab != null) {
                        labDuration = lab * 60; // Convert hours to minutes
                    }
                } else {
                    showAlert("Error", "Subject information not found.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
            return;
        }

        // Debug output
        System.out.println("Subject year level from database: " + yearLevel);
        String convertedYearLevel = convertYearLevel(yearLevel);
        System.out.println("Converted year level for section query: " + convertedYearLevel);
        System.out.println("Lecture duration: " + lectureDuration/60 + " hours (" + lectureDuration + " minutes)");
        System.out.println("Lab duration: " + labDuration/60 + " hours (" + labDuration + " minutes)");

        // Get room IDs in a separate connection
        try (Connection conn = DBConnect.getConnection()) {
            lectureRoomId = getRoomIdByType("Lecture");
            labRoomId = getRoomIdByType("Lab");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error getting room information: " + e.getMessage());
            return;
        }

        if (lectureRoomId == -1) {
            showAlert("Error", "No lecture room available.");
            return;
        }

        if (labDuration > 0 && labRoomId == -1) {
            showAlert("Error", "No lab room available.");
            return;
        }

        // Get existing schedules in a separate connection
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT time_in, time_out, days, room_id FROM subsched WHERE room_id = ? OR room_id = ?")) {
            ps.setInt(1, lectureRoomId);
            ps.setInt(2, labRoomId > 0 ? labRoomId : lectureRoomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    existingTimeSlots.add(new ORToolsScheduler.TimeSlot(
                            rs.getString("days"),
                            rs.getTime("time_in").toLocalTime(),
                            rs.getTime("time_out").toLocalTime(),
                            rs.getInt("room_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error getting existing schedules: " + e.getMessage());
            return;
        }

        // Get section IDs in a separate connection
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT section_id FROM section WHERE year_level = ?")) {
            ps.setString(1, convertedYearLevel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sectionIds.add(rs.getInt("section_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error getting section information: " + e.getMessage());
            return;
        }

        if (sectionIds.isEmpty()) {
            showAlert("Error", "No suitable sections found for year level: " + convertedYearLevel);
            return;
        }

        // Create separate schedule for each section to prevent conflicts
        boolean success = true;
        for (int i = 0; i < sectionIds.size(); i++) {
            int sectionId = sectionIds.get(i);

            // For each section, create a fresh list of subjects and generate a new schedule
            List<ORToolsScheduler.ScheduleSubject> subjects = new ArrayList<>();
            subjects.add(new ORToolsScheduler.ScheduleSubject(subjectId, lectureDuration, labDuration));

            // Create a new scheduler for each section
            ORToolsScheduler scheduler = new ORToolsScheduler();

            // Generate schedule for this specific section
            Map<Integer, List<ORToolsScheduler.TimeSlot>> schedules = scheduler.generateOptimizedSchedule(
                    subjects, existingTimeSlots, lectureRoomId, labRoomId);

            if (schedules.containsKey(subjectId)) {
                List<ORToolsScheduler.TimeSlot> timeSlots = schedules.get(subjectId);

                if (timeSlots.isEmpty()) {
                    showAlert("Error", "Failed to find suitable time slots for section " + sectionId +
                            ". Try different parameters or check existing schedules.");
                    success = false;
                    break;
                }

                try (Connection conn = DBConnect.getConnection()) {
                    // 1) Fetch current semester & academic year
                    String curSql =
                            "SELECT Semester, AcademicYear " +
                                    "FROM current " +
                                    "ORDER BY currentID DESC " +
                                    "LIMIT 1";
                    String currentSemester = null, currentAcadYear = null;
                    try (PreparedStatement psCur = conn.prepareStatement(curSql);
                         ResultSet rs = psCur.executeQuery()) {
                        if (rs.next()) {
                            currentSemester = rs.getString("Semester");
                            currentAcadYear  = rs.getString("AcademicYear");
                        } else {
                            throw new IllegalStateException("No current semester/academic year defined");
                        }
                    }

                    // 2) Prepare your INSERT (now including semester & acadYear)
                    String insertSql =
                            "INSERT INTO subsched " +
                                    "(subject_id, faculty_id, time_in, time_out, days, room_id, section_id, semester, acadYear) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        for (ORToolsScheduler.TimeSlot slot : timeSlots) {
                            ps.setInt(1, subjectId);
                            ps.setInt(2, facultyId);
                            ps.setTime(3, Time.valueOf(slot.startTime));
                            ps.setTime(4, Time.valueOf(slot.endTime));
                            ps.setString(5, slot.day);
                            ps.setInt(6, slot.roomId);
                            ps.setInt(7, sectionId);
                            ps.setString(8, currentSemester);
                            ps.setString(9, currentAcadYear);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }

                    // 3) Add these new slots to your existingTimeSlots to avoid conflicts
                    for (ORToolsScheduler.TimeSlot slot : timeSlots) {
                        existingTimeSlots.add(slot);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to insert schedule for section " + sectionId + ": " + e.getMessage());
                    success = false;
                    break;
                }
            } else {
                showAlert("Error", "Failed to generate schedule for section " + sectionId + ". Try different parameters.");
                success = false;
                break;
            }
        }

        if (success) {
            showAlert("Success", "Schedules generated successfully for all sections!");
            loadSched(); // Refresh the schedule view
            subjectCb.getSelectionModel().clearSelection();
            facultyCb.getSelectionModel().clearSelection();
            subjectCb.setPromptText("Choose subject");
            facultyCb.setPromptText("Choose faculty");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void listViewSelections() {
        subjectListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String[] parts = newVal.split(" \\- ");
                if (parts.length >= 2) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String comboRecord = "(" + id + ", " + name + ")";
                    subjectCb.getSelectionModel().select(comboRecord);
                }
            }
        });
        facultyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String[] parts = newVal.split(" \\- ");
                if (parts.length >= 2) {
                    String id = parts[0].trim();
                    String fullName = parts[1].trim();
                    String comboRecord = "(" + id + ", " + fullName + ")";
                    facultyCb.getSelectionModel().select(comboRecord);
                }
            }
        });
    }

    private void loadSubjects() {
        allSubjectItems = FXCollections.observableArrayList();

        try (Connection con = DBConnect.getConnection()) {
            // Get current semester directly from the current table
            String currentQuery = "SELECT Semester FROM current LIMIT 1";

            try (PreparedStatement currentPs = con.prepareStatement(currentQuery);
                 ResultSet currentRs = currentPs.executeQuery()) {

                if (currentRs.next()) {
                    String currentSemester = currentRs.getString("Semester");
                    System.out.println("Current semester from database: " + currentSemester);

                    // Get subjects that match the current semester and aren't already scheduled
                    String sql = "SELECT sub_id, subject_name FROM subjects " +
                            "WHERE semester = ? " +
                            "AND sub_id NOT IN (SELECT subject_id FROM subsched)";

                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, currentSemester);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                int id = rs.getInt("sub_id");
                                String name = rs.getString("subject_name");
                                allSubjectItems.add(id + " - " + name);
                            }
                        }
                    }
                } else {
                    System.out.println("No semester found in the current table.");
                }
            }

            subjectListView.setItems(allSubjectItems);
            System.out.println("Loaded " + allSubjectItems.size() + " subjects into listview");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error loading subjects: " + e.getMessage());
        }
    }

    private void loadFaculties() {
        String sql = "SELECT f.faculty_id, f.first_name, f.middle_name, f.last_name, f.max_subjects FROM faculty f " +
                "WHERE (SELECT COUNT(DISTINCT s.subject_id) FROM subsched s WHERE s.faculty_id = f.faculty_id) < f.max_subjects";
        allFacultyItems = FXCollections.observableArrayList();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("faculty_id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String fullName = firstName + (middleName != null && !middleName.trim().isEmpty() ? " " + middleName : "") + " " + lastName;
                allFacultyItems.add(id + " - " + fullName);
            }
            facultyListView.setItems(allFacultyItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeSubjectSearch() {
        searchSubject.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                subjectListView.setItems(allSubjectItems);
                return;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            ObservableList<String> filteredSubjects = FXCollections.observableArrayList();
            for (String subject : allSubjectItems) {
                if (subject.toLowerCase().contains(lowerCaseFilter)) {
                    filteredSubjects.add(subject);
                }
            }
            subjectListView.setItems(filteredSubjects);
        });
    }

    private void initializeFacultySearch() {
        searchFaculty.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                facultyListView.setItems(allFacultyItems);
                return;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            ObservableList<String> filteredFaculty = FXCollections.observableArrayList();
            for (String faculty : allFacultyItems) {
                if (faculty.toLowerCase().contains(lowerCaseFilter)) {
                    filteredFaculty.add(faculty);
                }
            }
            facultyListView.setItems(filteredFaculty);
        });
    }

    private void loadSubjectsComboBox() {
        ObservableList<String> subjectsCombo = FXCollections.observableArrayList();

        try (Connection con = DBConnect.getConnection()) {
            // Get current semester directly from the current table
            String currentQuery = "SELECT Semester FROM current LIMIT 1";

            try (PreparedStatement currentPs = con.prepareStatement(currentQuery);
                 ResultSet currentRs = currentPs.executeQuery()) {

                if (currentRs.next()) {
                    String currentSemester = currentRs.getString("Semester");
                    System.out.println("Current semester from database: " + currentSemester);

                    // Get subjects that match the current semester and aren't already scheduled
                    String sql = "SELECT sub_id, subject_name FROM subjects " +
                            "WHERE semester = ? " +
                            "AND sub_id NOT IN (SELECT subject_id FROM subsched)";

                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, currentSemester);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                int id = rs.getInt("sub_id");
                                String name = rs.getString("subject_name");
                                subjectsCombo.add("(" + id + ", " + name + ")");
                            }
                        }
                    }
                } else {
                    showAlert("Info", "No semester found in the current table.");
                }
            }

            subjectCb.setItems(subjectsCombo);
            System.out.println("Loaded " + subjectsCombo.size() + " subjects into combobox");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void loadFacultyComboBox() {
        String sql = "SELECT f.faculty_id, f.first_name, f.middle_name, f.last_name, f.max_subjects FROM faculty f " +
                "WHERE (SELECT COUNT(DISTINCT s.subject_id) FROM subsched s WHERE s.faculty_id = f.faculty_id) < f.max_subjects";
        ObservableList<String> facultyCombo = FXCollections.observableArrayList();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("faculty_id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String fullName = firstName + (middleName != null && !middleName.trim().isEmpty() ? " " + middleName : "") + " " + lastName;
                facultyCombo.add("(" + id + ", " + fullName + ")");
            }
            facultyCb.setItems(facultyCombo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}