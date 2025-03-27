// File: EnrollmentSystem/src/main/java/Classes/PopUpSchedController.java
package Classes;

import ExtraSources.ORToolsScheduler;
import GettersSetters.ScheduleModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roomType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roomId = rs.getInt("room_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    @FXML
    private void generateSchedule() {
        String subjectRecord = subjectCb.getSelectionModel().getSelectedItem();
        String facultyRecord = facultyCb.getSelectionModel().getSelectedItem();
        if (subjectRecord == null || facultyRecord == null) {
            System.out.println("Please select both a subject and an instructor.");
            return;
        }
        String subjectIdStr = subjectRecord.substring(1, subjectRecord.indexOf(",")).trim();
        int subjectId = Integer.parseInt(subjectIdStr);
        String facultyIdStr = facultyRecord.substring(1, facultyRecord.indexOf(",")).trim();
        int facultyId = Integer.parseInt(facultyIdStr);

        int creditHours = 0;
        String subjectName = "";
        boolean isMajor = false;
        String subjectSql = "SELECT credit_hours, subject_name, is_major FROM subjects WHERE sub_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(subjectSql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                creditHours = rs.getInt("credit_hours");
                subjectName = rs.getString("subject_name");
                isMajor = rs.getInt("is_major") == 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        ObservableList<Integer> sectionIds = FXCollections.observableArrayList();
        String sectionSql = "SELECT section_id FROM section";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sectionSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sectionIds.add(rs.getInt("section_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        if (sectionIds.isEmpty()) {
            System.out.println("No sections available.");
            return;
        }

        ORToolsScheduler scheduler = new ORToolsScheduler();
        if (isMajor && creditHours > 2) {
            int duration = (creditHours * 60) / 2;
            int lectureRoomId = getRoomIdByType("Lecture");
            int labRoomId = getRoomIdByType("Lab");
            if (lectureRoomId == -1 || labRoomId == -1) {
                System.out.println("Appropriate room(s) not found.");
                return;
            }
            ORToolsScheduler.DualSchedule dualSchedule = scheduler.generateDualSchedule(duration, sectionIds);
            if (dualSchedule == null) {
                System.out.println("No feasible schedule found.");
                return;
            }
            String insertSql = "INSERT INTO subsched (subject_id, time_in, time_out, days, room_id, faculty_id, section_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(insertSql)) {
                // Insert lecture sessions.
                for (ORToolsScheduler.ScheduleItem item : dualSchedule.lectureSchedule) {
                    ps.setInt(1, subjectId);
                    ps.setString(2, item.timeIn);
                    ps.setString(3, item.timeOut);
                    ps.setString(4, item.day);
                    ps.setInt(5, lectureRoomId);
                    ps.setInt(6, facultyId);
                    ps.setInt(7, item.sectionId);
                    ps.addBatch();
                }
                // Insert lab sessions.
                for (ORToolsScheduler.ScheduleItem item : dualSchedule.labSchedule) {
                    ps.setInt(1, subjectId);
                    ps.setString(2, item.timeIn);
                    ps.setString(3, item.timeOut);
                    ps.setString(4, item.day);
                    ps.setInt(5, labRoomId);
                    ps.setInt(6, facultyId);
                    ps.setInt(7, item.sectionId);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            }
        } else {
            int duration = creditHours * 60;
            int lectureRoomId = getRoomIdByType("Lecture");
            if (lectureRoomId == -1) {
                System.out.println("Lecture room not found.");
                return;
            }
            ObservableList<ORToolsScheduler.ScheduleItem> scheduleItems = scheduler.generateSchedule(duration, sectionIds);
            String insertSql = "INSERT INTO subsched (subject_id, time_in, time_out, days, room_id, faculty_id, section_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(insertSql)) {
                for (ORToolsScheduler.ScheduleItem item : scheduleItems) {
                    ps.setInt(1, subjectId);
                    ps.setString(2, item.timeIn);
                    ps.setString(3, item.timeOut);
                    ps.setString(4, item.day);
                    ps.setInt(5, lectureRoomId);
                    ps.setInt(6, facultyId);
                    ps.setInt(7, item.sectionId);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            }
        }
        parentController.loadScheduleData();
        loadFaculties();
        loadSubjects();
        loadFacultyComboBox();
        loadSubjectsComboBox();
        facultyCb.getSelectionModel().clearSelection();
        subjectCb.getSelectionModel().clearSelection();
        facultyCb.setPromptText("Choose instructor");
        subjectCb.setPromptText("Choose subject");
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
        String sql = "SELECT sub_id, subject_name FROM subjects WHERE sub_id NOT IN (SELECT subject_id FROM subsched)";
        allSubjectItems = FXCollections.observableArrayList();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("sub_id");
                String name = rs.getString("subject_name");
                allSubjectItems.add(id + " - " + name);
            }
            subjectListView.setItems(allSubjectItems);
        } catch (SQLException e) {
            e.printStackTrace();
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
        String sql = "SELECT sub_id, subject_name FROM subjects WHERE sub_id NOT IN (SELECT subject_id FROM subsched)";
        ObservableList<String> subjectsCombo = FXCollections.observableArrayList();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("sub_id");
                String name = rs.getString("subject_name");
                subjectsCombo.add("(" + id + ", " + name + ")");
            }
            subjectCb.setItems(subjectsCombo);
        } catch (SQLException e) {
            e.printStackTrace();
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