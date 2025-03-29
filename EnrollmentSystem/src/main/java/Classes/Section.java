package Classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.sql.*;

import ExtraSources.*;
import GettersSetters.*;

public class Section {

    @FXML private TableView<SectionModel> tableView;
    @FXML private TableColumn<SectionModel, String> colSection;
    @FXML private TableColumn<SectionModel, String> colDepartment;
    @FXML private TableColumn<SectionModel, Void> colActions;
    @FXML private TextField secname;
    @FXML private TextField filter;
    @FXML private ComboBox<String> depcombo;
    @FXML private Button Add; // This will switch between "Add" and "Update"

    private ObservableList<SectionModel> sectionList =
            FXCollections.observableArrayList();
    private ObservableList<String> departmentList =
            FXCollections.observableArrayList("CICS", "CAS", "CABEIHM", "CHS", "CTE",
                    "CCJE");
    private Connection kon;
    private ContextMenu searching = new ContextMenu();

    private String originalSection = ""; // Store original section name
    private String originalDepartment = ""; // Store original department

    @FXML
    public void initialize() {
        kon = DBConnect.getConnection();
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection failed!").show();
            return;
        }

        colSection.setCellValueFactory(cellData ->
                cellData.getValue().sectionProperty());
        colDepartment.setCellValueFactory(cellData ->
                cellData.getValue().departmentProperty());

        depcombo.setItems(departmentList);

        loadData();
        setupSearchFilter();
        setupActionButtons();
    }

    private void setupSearchFilter() {
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                searching.hide();
            } else {
                loadSuggestions(newValue);
            }
        });
    }

    private void loadSuggestions(String query) {
        searching.getItems().clear();
        String sql = "SELECT section_name FROM section WHERE section_name LIKE ? LIMIT 10";

        try (PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sectionName = rs.getString("section_name").trim();
                MenuItem item = new MenuItem(sectionName);

                item.setOnAction(e -> {
                    filter.setText(sectionName);
                    searching.hide();
                    searchSectionsInDatabase(sectionName);
                });

                searching.getItems().add(item);
            }

            if (!searching.getItems().isEmpty()) {
                searching.show(filter,
                        javafx.stage.Window.getWindows().get(0).getX() + filter.getLayoutX(),
                        javafx.stage.Window.getWindows().get(0).getY() +
                                filter.getLayoutY() + filter.getHeight());
            } else {
                searching.hide();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchSectionsInDatabase(String searchText) {
        ObservableList<SectionModel> filteredSections =
                FXCollections.observableArrayList();
        String sql = "SELECT * FROM section WHERE section_name LIKE ? OR department LIKE ?";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filteredSections.add(new
                        SectionModel(rs.getString("section_name"), rs.getString("department")));
            }
            tableView.setItems(filteredSections);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " +
                    ex.getMessage()).show();
        }
    }

    private void setupActionButtons() {
        colActions.setCellFactory(tc -> new TableCell<SectionModel, Void>() {
            private final Button editButton =
                    createImageButton("/Images/edit.png");
            private final Button deleteButton =
                    createImageButton("/Images/delete.png");

            {
                editButton.setOnAction(event -> {
                    SectionModel section =
                            getTableView().getItems().get(getIndex());
                    editSection(section);
                });

                deleteButton.setOnAction(event -> {
                    SectionModel section =
                            getTableView().getItems().get(getIndex());
                    confirmDelete(section);
                });

                HBox buttons = new HBox(editButton, deleteButton);
                buttons.setSpacing(5);
                setGraphic(buttons);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(editButton, deleteButton));
            }
        });
    }

    private Button createImageButton(String imagePath) {
        ImageView imageView = new ImageView(new
                Image(getClass().getResourceAsStream(imagePath)));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }

    private void loadData() {
        sectionList.clear();
        String query = "SELECT section_name, department FROM section";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sectionList.add(new SectionModel(rs.getString("section_name"),
                        rs.getString("department")));
            }
            tableView.setItems(sectionList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading data: " +
                    e.getMessage()).show();
        }
    }

    private void confirmDelete(SectionModel section) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this section?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                deleteSection(section);
            }
        });
    }

    private void deleteSection(SectionModel section) {
        String query = "DELETE FROM section WHERE section_name = ? AND department = ?";
        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {
            stmt.setString(1, section.getSection());
            stmt.setString(2, section.getDepartment()); // Include department!
            stmt.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Section deleted successfully!").show();
            loadData();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error deleting section: " +
                    e.getMessage()).show();
        }
    }


    @FXML
    private void addSection() {
        String section = secname.getText().trim();
        String department = depcombo.getValue();

        if (section.isEmpty() || department == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields.").show();
            return;
        }

        // CHECK IF SECTION ALREADY EXISTS WITH SAME NAME & DEPARTMENT
        if (isSectionDuplicate(section, department)) {
            new Alert(Alert.AlertType.WARNING, "Section already exists in this department!").show();
            return;
        }

        // IF NO DUPLICATE, PROCEED WITH INSERTION
        String query = "INSERT INTO section (section_name, department) VALUES (?, ?)";
        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {
            stmt.setString(1, section);
            stmt.setString(2, department);
            stmt.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Section added successfully!").show();
            resetForm();
            loadData();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error adding section: " +
                    e.getMessage()).show();
        }
    }


    // FUNCTION TO CHECK DUPLICATES
    private boolean isSectionDuplicate(String section, String department) {
        String query = "SELECT COUNT(*) FROM section WHERE section_name = ? AND department = ?";
        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {
            stmt.setString(1, section);
            stmt.setString(2, department);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // May duplicate (same section at department)
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error checking duplicate: " +
                    e.getMessage()).show();
        }
        return false; // Walang duplicate
    }


    private void updateSection() {
        String newSection = secname.getText().trim();
        String newDepartment = depcombo.getValue();

        if (newSection.isEmpty() || newDepartment == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields.").show();
            return;
        }

        // CHECK IF THE NEW VALUES ALREADY EXIST
        if (isSectionDuplicate(newSection, newDepartment) &&
                (!newSection.equals(originalSection) ||
                        !newDepartment.equals(originalDepartment))) {
            new Alert(Alert.AlertType.WARNING, "Section already exists in this department!").show();
            return;
        }

        if (newSection.equals(originalSection) &&
                newDepartment.equals(originalDepartment)) {
            new Alert(Alert.AlertType.INFORMATION, "No changes detected!").show();
            resetForm();
            return;
        }

        // UPDATE ONLY THE SPECIFIC SECTION WITH ITS DEPARTMENT
        String query = "UPDATE section SET section_name = ?, department = ? WHERE section_name = ? AND department = ?";
        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {
            stmt.setString(1, newSection);
            stmt.setString(2, newDepartment);
            stmt.setString(3, originalSection);
            stmt.setString(4, originalDepartment);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Section updated successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error: Section not found or unchanged!").show();
            }

            loadData();
            resetForm();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error updating section: " +
                    e.getMessage()).show();
        }
    }




    private void editSection(SectionModel section) {
        secname.setText(section.getSection());
        depcombo.setValue(section.getDepartment());
        Add.setText("Update");
        originalSection = section.getSection();
        originalDepartment = section.getDepartment();

        // Update the button action to perform an update instead of an add
        Add.setOnAction(e -> updateSection());
    }

    private void resetForm() {
        secname.clear();
        depcombo.getSelectionModel().clearSelection();
        Add.setText("Add");
    }
} 