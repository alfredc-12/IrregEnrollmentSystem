package Classes;

import GettersSetters.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

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
        hoursColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        roomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPreferredRoom()));
        majorColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().getIsMajor()).asObject());

        subjectTable.setItems(subjectList);

        // Set Pane size (750x450)
        Pane.setPrefSize(750, 450);
    }

    @FXML
    private void addSubject() {
        try {
            String subjectName = SubjectTXT.getText();
            int creditHours = Integer.parseInt(CHoursTXT.getText());
            String preferredRoom = RoomTXT.getText();
            boolean isMajor = majorchkbx.isSelected();

            Subject newSubject = new Subject(subjectList.size() + 1, subjectName, creditHours, isMajor, preferredRoom);
            subjectList.add(newSubject);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Credit hours must be a number.");
        }
    }

    @FXML
    private void updateSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            selectedSubject.setSubjectName(SubjectTXT.getText());
            selectedSubject.setCreditHours(Integer.parseInt(CHoursTXT.getText()));
            selectedSubject.setPreferredRoom(RoomTXT.getText());
            selectedSubject.setIsMajor(majorchkbx.isSelected());
            subjectTable.refresh();
            clearFields();
        } else {
            showAlert("No Selection", "Please select a subject to update.");
        }
    }

    @FXML
    private void deleteSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            subjectList.remove(selectedSubject);
        } else {
            showAlert("No Selection", "Please select a subject to delete.");
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
