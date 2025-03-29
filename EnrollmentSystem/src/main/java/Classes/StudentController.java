package Classes;

import Application.HelloApplication;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import GettersSetters.*;
import ExtraSources.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Window X
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.control.TableColumn;

import javax.imageio.ImageIO;
import java.sql.ResultSet;
import java.io.InputStream;
import java.io.FileNotFoundException;


public class StudentController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> studIDColumn;

    @FXML private TableColumn<Student, String> srCodeColumn;
    @FXML private TableColumn<Student, String> yearLevelColumn;
    @FXML private TableColumn<Student, String> programColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TableColumn<Student, String> contactColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> addressColumn;
    @FXML private TableColumn<Student, String> statusColumn;
    @FXML private TableColumn<Student, String> picLinkColumn;
    @FXML private TableColumn<Student, String> signLinkColumn;
    @FXML private TableColumn<Student, String> passwordColumn;

    @FXML private TextField firstName, middleName, lastName, picLink, signLink, srCode, contact, email, password, address;
    @FXML private ComboBox<String> yearLevel, program, major, status;
    @FXML private Button insertButton, clearButton;
    @FXML private TextField studID; // If needed for the database

    @FXML private Button Delete;
    @FXML private TextField searchField; //search or filter
    @FXML private RadioButton showDeleted;

    @FXML private Pane signaturePanel; // Signature
    @FXML private Pane imagePanel; // Image
    private Integer currentUpdateFacultyId = null;

    private double originalStudentTableHeight;
    private double originalStudentPaneHeight;
    @FXML private Pane hidePane;
    @FXML private AnchorPane rightpane;
    @FXML private Button btnHide; //For pop UP


    @FXML private TableColumn<Student, String> studentNameColumn; //for fullname

    private java.io.File photoFile; //upload ng picture
    private java.io.File signatureFile; //upload ng signature

    private final double originalSearchLayoutX = 577.0;  // Updated to match FXML
    private final double originalSearchPrefWidth = 150.0;
    private final double originalShowDeletedLayoutX = 460.0;  // Updated to match FXML

    private final double targetSearchLayoutX = 498.0;
    private final double targetSearchPrefWidth = 230.0;
    private final double targetShowDeletedLayoutX = 385.0;



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


        // Load student data
        loadStudents();
        initializeTableColumns();
        studIDColumn.setVisible(false);
        signLinkColumn.setVisible(false);

        originalStudentPaneHeight = rightpane.getPrefHeight();
        originalStudentTableHeight = studentTable.getPrefHeight();
        //filter
        setupSearchFilter();


        adjustPanelAndColumns();
        originalStudentPaneHeight = rightpane.getPrefHeight();
        originalStudentTableHeight = studentTable.getPrefHeight();

        togglePaneAndResize();

    }


    @FXML
    private String convertToDirectLink(String shareLink) {
        // Regular expression to capture the file ID from a typical Google Drive share URL
        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(shareLink);
        if (matcher.find()) {
            String fileId = matcher.group(1);
            // Construct the direct link URL
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }
        // Fallback: if the shareLink does not match the expected pattern, return it unchanged.
        return shareLink;
    }



    private Drive getDriveService() throws Exception {
        // Load credentials from the resources folder.
        InputStream in = getClass().getResourceAsStream("/OtherFiles/Credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Credentials file not found at /OtherFiles/Credentials.json");
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                new GsonFactory(),   // Use GsonFactory instead of JacksonFactory.
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("EnrollmentSystem")
                .build();
    }

    private String uploadFileToDrive(Drive driveService, File localFile, String folderId, String mimeType) throws IOException {
        // Create metadata using Google Drive's File class.
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(localFile.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent(mimeType, localFile);
        com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
    }


    private String getImageUrlFromPane(Pane pane) {
        // Loop through the children of the pane.
        for (Node node : pane.getChildren()) {
            if (node instanceof ImageView) {
                ImageView iv = (ImageView) node;
                Image img = iv.getImage();
                // If the image exists and its URL is not null, return it.
                if (img != null && img.getUrl() != null && !img.getUrl().isEmpty()) {
                    return img.getUrl();
                }
            }
        }
        // Return null if no valid image URL is found.
        return null;
    }

    @FXML
    private void initializeTableColumns() {
        studIDColumn.setCellValueFactory(cellData -> cellData.getValue().studIDProperty().asObject());
        srCodeColumn.setCellValueFactory(cellData -> cellData.getValue().srCodeProperty());
        yearLevelColumn.setCellValueFactory(cellData -> cellData.getValue().yearLevelProperty());
        programColumn.setCellValueFactory(cellData -> cellData.getValue().programProperty());
        majorColumn.setCellValueFactory(cellData -> cellData.getValue().majorProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        picLinkColumn.setCellValueFactory(cellData -> cellData.getValue().picLinkProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        picLinkColumn.setCellFactory(column -> new TableCell<Student, String>() {
            private final ImageView imageView = new ImageView();
            {
                // Set the ImageView's display size. Adjust these as needed.
                imageView.setFitWidth(90);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true); // Enables smoother scaling
                imageView.setCache(true);
            }

            @Override
            protected void updateItem(String picLink, boolean empty) {
                super.updateItem(picLink, empty);
                if (empty || picLink == null || picLink.trim().isEmpty()) {
                    setGraphic(null);
                } else {
                    // Convert share link to direct link if needed.
                    String directLink = convertToDirectLink(picLink);
                    try {
                        // Request a higher-resolution image (e.g., 200x200) to avoid pixelation on resize.
                        Image image = new Image(directLink, 200, 200, true, true, true);
                        if (image.isError()) {
                            System.err.println("Error loading image: " + image.getException());
                            setGraphic(null);
                        } else {
                            imageView.setImage(image);
                            setGraphic(imageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        setGraphic(null);
                    }
                }
            }
        });

    }



    // Method to return to the dashboard
    private void returnToDashboard() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/FXML/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Faculty!");
            stage.setScene(scene);
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) studentTable.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPictureDialog() {
        try {
            // Load the PictureDialog FXML file (adjust resource path as needed)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/PictureDialog.fxml"));
            Parent root = loader.load();

            // Get the PictureDialog controller instance
            PictureDialog dialogController = loader.getController();

            // Create and display the dialog stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Capture Picture");

            // Ensure camera stops when the window is closed
            stage.setOnHidden(event -> dialogController.stopCamera());
            stage.showAndWait();

            // After the dialog closes, retrieve the captured image
            Image capturedImage = dialogController.getCapturedImage();
            if (capturedImage != null) {
                // Create an ImageView with the captured image
                ImageView iv = new ImageView(capturedImage);

                // Get dimensions of the captured image and target panel
                double imageWidth = capturedImage.getWidth();
                double imageHeight = capturedImage.getHeight();
                double panelWidth = imagePanel.getPrefWidth();
                double panelHeight = imagePanel.getPrefHeight();

                // Compute target aspect ratio (assuming centered cropping)
                double targetRatio = panelWidth / panelHeight;
                double imageRatio = imageWidth / imageHeight;
                double viewportX, viewportY, viewportWidth, viewportHeight;
                if (imageRatio > targetRatio) {
                    // Crop left and right sides
                    viewportHeight = imageHeight;
                    viewportWidth = imageHeight * targetRatio;
                    viewportX = (imageWidth - viewportWidth) / 2;
                    viewportY = 0;
                } else {
                    // Crop top and bottom
                    viewportWidth = imageWidth;
                    viewportHeight = imageWidth / targetRatio;
                    viewportX = 0;
                    viewportY = (imageHeight - viewportHeight) / 2;
                }

                // Apply the computed viewport (center crop)
                iv.setViewport(new Rectangle2D(viewportX, viewportY, viewportWidth, viewportHeight));
                iv.setFitWidth(panelWidth);
                iv.setFitHeight(panelHeight);
                iv.setPreserveRatio(true);

                // Display the image in the panel
                imagePanel.getChildren().clear();
                imagePanel.getChildren().add(iv);

                // Save the captured image to a temporary file for later upload.
                java.io.File tempPhoto = java.io.File.createTempFile("captured_", ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(capturedImage, null), "png", tempPhoto);
                photoFile = tempPhoto;
                System.out.println("Photo saved to temporary file: " + tempPhoto.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSignatureDialog() {
        try {
            // Load the SignatureDialog FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SignatureDialog.fxml"));
            Parent root = loader.load();

            // Get the dialog controller
            SignatureDialog dialogController = loader.getController();

            // Create a new stage for the dialog
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Signature Dialog");
            stage.showAndWait();

            // After the dialog is closed, retrieve the signature image
            Image sigImage = dialogController.getSignatureImage();
            if (sigImage != null) {
                // Display the signature image on the main signaturePanel.
                ImageView iv = new ImageView(sigImage);
                iv.setFitWidth(signaturePanel.getPrefWidth());
                iv.setFitHeight(signaturePanel.getPrefHeight());
                iv.setPreserveRatio(true);
                signaturePanel.getChildren().clear();
                signaturePanel.getChildren().add(iv);

                // Save the signature image to a temporary file for later upload.
                java.io.File tempSignature = java.io.File.createTempFile("signature_", ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(sigImage, null), "png", tempSignature);
                signatureFile = tempSignature;
                System.out.println("Signature saved to temporary file: " + tempSignature.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void uploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(imagePanel.getScene().getWindow());
        if (file != null) {
            // Save the selected file for later use (Google Drive upload)
            photoFile = file;
            try (FileInputStream fis = new FileInputStream(file)) {
                // Load the image
                Image image = new Image(fis);
                double imgWidth = image.getWidth();
                double imgHeight = image.getHeight();
                // We want a square crop (target ratio = 1)
                double targetRatio = 1.0;
                double cropWidth, cropHeight, x, y;
                double imgRatio = imgWidth / imgHeight;
                if (imgRatio > targetRatio) {
                    // Image is wider than it is tall, crop horizontally.
                    cropHeight = imgHeight;
                    cropWidth = cropHeight * targetRatio;
                    x = (imgWidth - cropWidth) / 2;
                    y = 0;
                } else {
                    // Image is taller than it is wide, crop vertically.
                    cropWidth = imgWidth;
                    cropHeight = cropWidth / targetRatio;
                    x = 0;
                    y = (imgHeight - cropHeight) / 2;
                }
                // Create an ImageView for the loaded image.
                ImageView iv = new ImageView(image);
                // Set the viewport to crop the image to a centered square.
                iv.setViewport(new Rectangle2D(x, y, cropWidth, cropHeight));
                // Resize the ImageView to fill the imagePanel while preserving aspect ratio.
                iv.setFitWidth(imagePanel.getPrefWidth());
                iv.setFitHeight(imagePanel.getPrefHeight());
                iv.setPreserveRatio(true);
                // Clear any existing content and add the ImageView.
                imagePanel.getChildren().clear();
                imagePanel.getChildren().add(iv);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void uploadSignature() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Signature Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(signaturePanel.getScene().getWindow());
        if (file != null) {
            // Save the selected file for later use (Google Drive upload)
            signatureFile = file;
            try (FileInputStream fis = new FileInputStream(file)) {
                // Load the image from the file
                Image image = new Image(fis);
                double imgWidth = image.getWidth();
                double imgHeight = image.getHeight();
                // We want a square crop (aspect ratio = 1.0)
                double targetRatio = 1.0;
                double cropWidth, cropHeight, x, y;
                double imgRatio = imgWidth / imgHeight;
                if (imgRatio > targetRatio) {
                    // Image is wider than it is tall: crop the left and right sides.
                    cropHeight = imgHeight;
                    cropWidth = cropHeight * targetRatio;
                    x = (imgWidth - cropWidth) / 2;
                    y = 0;
                } else {
                    // Image is taller than it is wide: crop the top and bottom.
                    cropWidth = imgWidth;
                    cropHeight = cropWidth / targetRatio;
                    x = 0;
                    y = (imgHeight - cropHeight) / 2;
                }
                // Create an ImageView for the loaded image.
                ImageView iv = new ImageView(image);
                // Set the viewport to crop the image to a centered square.
                iv.setViewport(new Rectangle2D(x, y, cropWidth, cropHeight));
                // Fit the ImageView to the signaturePanel's preferred dimensions.
                iv.setFitWidth(signaturePanel.getPrefWidth());
                iv.setFitHeight(signaturePanel.getPrefHeight());
                iv.setPreserveRatio(true);
                // Clear any existing children and add the new ImageView.
                signaturePanel.getChildren().clear();
                signaturePanel.getChildren().add(iv);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a student.");
            return;
        }

        try {
            // Check if the student is already deleted
            String checkQuery = "SELECT is_deleted FROM student WHERE id = ?";
            PreparedStatement checkStmt = DBConnect.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedStudent.getStudID());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("is_deleted") == 1) {
                // Student is already deleted, ask if they want to restore
                Alert restoreAlert = new Alert(Alert.AlertType.CONFIRMATION);
                restoreAlert.setTitle("Restore Student");
                restoreAlert.setHeaderText("This student is already deleted.");
                restoreAlert.setContentText("Would you like to restore them?");

                ButtonType restoreButton = new ButtonType("Restore");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                restoreAlert.getButtonTypes().setAll(restoreButton, cancelButton);

                ButtonType result = restoreAlert.showAndWait().orElse(cancelButton);
                if (result == restoreButton) {
                    String restoreQuery = "UPDATE student SET is_deleted = 0 WHERE id = ?";
                    PreparedStatement restoreStmt = DBConnect.getConnection().prepareStatement(restoreQuery);
                    restoreStmt.setInt(1, selectedStudent.getStudID());
                    restoreStmt.executeUpdate();

                    loadStudents();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student restored successfully!");
                }
                return;
            }

            // Proceed with marking the student as deleted
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this student?");
            confirmationAlert.setContentText("This action can be undone by restoring the student.");

            ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);
            if (result == ButtonType.OK) {
                String deleteQuery = "UPDATE student SET is_deleted = 1 WHERE id = ?";
                PreparedStatement deleteStmt = DBConnect.getConnection().prepareStatement(deleteQuery);
                deleteStmt.setInt(1, selectedStudent.getStudID());
                deleteStmt.executeUpdate();

                loadStudents();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student marked as deleted successfully!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student status.");
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Expand animation on hover (left side moves, right side stays fixed)
    private void animateSearchTransition() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(searchField.layoutXProperty(), targetSearchLayoutX, Interpolator.EASE_BOTH),
                        new KeyValue(searchField.prefWidthProperty(), targetSearchPrefWidth, Interpolator.EASE_BOTH),
                        new KeyValue(showDeleted.layoutXProperty(), targetShowDeletedLayoutX, Interpolator.EASE_BOTH)
                )
        );
        timeline.play();
    }
    //reset hover in search
    private void animateSearchResetTransition() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(searchField.layoutXProperty(), originalSearchLayoutX, Interpolator.EASE_BOTH),
                        new KeyValue(searchField.prefWidthProperty(), originalSearchPrefWidth, Interpolator.EASE_BOTH),
                        new KeyValue(showDeleted.layoutXProperty(), originalShowDeletedLayoutX, Interpolator.EASE_BOTH)
                )
        );
        timeline.play();
    }


    // Setup hover behavior
    private void setupSearchFilter() {
        searchField.setOnMouseEntered(event -> animateSearchTransition());
        searchField.setOnMouseExited(event -> animateSearchResetTransition());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchStudentsInDatabase(newValue);
            showSuggestions(newValue);
        });
    }

    // Declare ContextMenu at class level to keep it persistent
    private ContextMenu suggestionsMenu = new ContextMenu();

    private void showSuggestions(String input) {
        if (input == null || input.isEmpty()) {
            suggestionsMenu.hide(); // Hide if input is empty
            return;
        }

        ObservableList<String> suggestions = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT first_name FROM student WHERE first_name LIKE ? " +
                "UNION SELECT DISTINCT last_name FROM student WHERE last_name LIKE ? " +
                "UNION SELECT DISTINCT middle_name FROM student WHERE middle_name LIKE ? " +
                "UNION SELECT DISTINCT address FROM student WHERE address LIKE ? " +
                "UNION SELECT DISTINCT major FROM student WHERE major LIKE ? " +
                "UNION SELECT DISTINCT program FROM student WHERE program LIKE ? " +
                "UNION SELECT DISTINCT status FROM student WHERE status LIKE ? " +
                "UNION SELECT DISTINCT year_level FROM student WHERE year_level LIKE ? " +
                "UNION SELECT DISTINCT sr_code FROM student WHERE sr_code LIKE ?";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(sql)) {
            String searchPattern = input + "%";
            for (int i = 1; i <= 9; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next() && suggestions.size() < 10) { // Limit to 10 suggestions
                suggestions.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch suggestions: " + e.getMessage());
        }

        // Remove duplicates & ensure the first suggestion doesn't disappear
        suggestions = suggestions.stream().distinct().collect(Collectors.toCollection(FXCollections::observableArrayList));

        // If there are no suggestions, hide the menu
        if (suggestions.isEmpty()) {
            suggestionsMenu.hide();
            return;
        }

        // Clear only items (not the ContextMenu itself) to keep it open
        suggestionsMenu.getItems().clear();

        // Populate new suggestions
        for (String suggestion : suggestions) {
            MenuItem item = new MenuItem(suggestion);
            item.setStyle("-fx-padding: 5px 10px; -fx-font-size: 10px; -fx-font-family: Arial;");
            item.setOnAction(e -> searchField.setText(suggestion)); // Set selected text
            suggestionsMenu.getItems().add(item);
        }

        // Position dropdown below the searchField
        Bounds boundsInScreen = searchField.localToScreen(searchField.getBoundsInLocal());
        if (boundsInScreen != null) {
            suggestionsMenu.show(searchField, boundsInScreen.getMinX(), boundsInScreen.getMaxY() + 2);
        }
    }


    private void searchStudentsInDatabase(String searchText) {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return;
        }

        ObservableList<Student> filteredStudents = FXCollections.observableArrayList();

        String sql = "SELECT * FROM student WHERE " +
                "first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ? OR " +
                "sr_code LIKE ? OR year_level LIKE ? OR program LIKE ? OR " +
                "major LIKE ? OR contact LIKE ? OR email LIKE ? OR " +
                "address LIKE ? OR status LIKE ?";


        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + searchText + "%"; // Allow partial matching with wildcards
            for (int i = 1; i <= 11; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),  // Changed "studID" to "id" (as per your table)
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("sr_code"),
                        rs.getString("year_level"),
                        rs.getString("program"),
                        rs.getString("major"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("status"),  // Ensure enum handling as needed
                        rs.getString("pic_link"),
                        rs.getString("sign_link"),
                        rs.getString("password")
                );
                filteredStudents.add(student);
            }

            // Bind the filtered list to the TableView
            studentTable.setItems(filteredStudents);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
    }


    @FXML
    private void loadStudents() {
        boolean showDeletedSelected = showDeleted.isSelected();

        // Adjust the WHERE clause based on the checkbox selection
        String query = "SELECT id, first_name, IFNULL(middle_name, '') AS middle_name, last_name, sr_code, " +
                "year_level, program, major, contact, email, address, status, pic_link, sign_link, password " +
                "FROM student WHERE is_deleted = ?";

        ObservableList<Student> studentList = FXCollections.observableArrayList();

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(query)) {
            stmt.setInt(1, showDeletedSelected ? 1 : 0); // 1 = show deleted, 0 = show active
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name"); // Ensures NULL middle names are handled
                String lastName = rs.getString("last_name");

                // Concatenate full name for display purposes
                String fullName = firstName + " " + (middleName.isEmpty() ? "" : middleName + " ") + lastName;

                studentList.add(new Student(
                        id,
                        firstName,
                        middleName,
                        lastName,
                        rs.getString("sr_code"),
                        rs.getString("year_level"),
                        rs.getString("program"),
                        rs.getString("major"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("pic_link"),
                        rs.getString("sign_link"),
                        rs.getString("password")
                ));
            }

            studentTable.setItems(studentList);
            System.out.println("Total Students Loaded: " + studentList.size());

            // Modify the TableColumn to display full name
            studentNameColumn.setCellValueFactory(cellData -> {
                Student student = cellData.getValue();
                String fullName = student.getFirstName() + " " +
                        (student.getMiddleName().isEmpty() ? "" : student.getMiddleName() + " ") +
                        student.getLastName();
                return new SimpleStringProperty(fullName);
            });

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading students: " + e.getMessage()).show();
        }
    }



    @FXML
    private void populateFieldsFromTable() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
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
            password.setText(selectedStudent.getPassword());

            // Load picture into imagePanel if available.
            if (selectedStudent.getPicLink() != null && !selectedStudent.getPicLink().isEmpty()) {
                Image image = new Image(selectedStudent.getPicLink(), true);
                ImageView iv = new ImageView(image);
                iv.setFitWidth(imagePanel.getPrefWidth());
                iv.setFitHeight(imagePanel.getPrefHeight());
                iv.setPreserveRatio(true);
                imagePanel.getChildren().clear();
                imagePanel.getChildren().add(iv);
            } else {
                imagePanel.getChildren().clear();
            }

            // Load signature into signaturePanel if available.
            if (selectedStudent.getSignLink() != null && !selectedStudent.getSignLink().isEmpty()) {
                Image image = new Image(selectedStudent.getSignLink(), true);
                ImageView iv = new ImageView(image);
                iv.setFitWidth(signaturePanel.getPrefWidth());
                iv.setFitHeight(signaturePanel.getPrefHeight());
                iv.setPreserveRatio(true);
                signaturePanel.getChildren().clear();
                signaturePanel.getChildren().add(iv);
            } else {
                signaturePanel.getChildren().clear();
            }
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
        if (isInputInvalid()) {
            return;
        }
        if (isStudentExists(srCode.getText(), email.getText(), contact.getText())) {
            new Alert(Alert.AlertType.ERROR, "Student with the same SR-Code, Email, or Contact already exists!").show();
            return;
        }

        try {
            String photoLinks = null;
            String signatureLinks = null;
            Drive driveService = getDriveService();

            if (photoFile != null) {
                System.out.println("Uploading photo...");
                photoLinks = uploadFileToDrive(driveService, photoFile, "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW", "image/jpeg");
                System.out.println("Photo uploaded to: " + photoLinks);
            } else {
                photoLinks = getImageUrlFromPane(imagePanel);
            }

            if (signatureFile != null) {
                System.out.println("Uploading signature...");
                signatureLinks = uploadFileToDrive(driveService, signatureFile, "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW", "image/png");
                System.out.println("Signature uploaded to: " + signatureLinks);
            } else {
                signatureLinks = getImageUrlFromPane(signaturePanel);
            }

            if (photoLinks == null || signatureLinks == null || photoLinks.isEmpty() || signatureLinks.isEmpty()) {
                throw new IOException("File upload failed: Photo or Signature URL is empty.");
            }

            String studentSQL = "INSERT INTO student (first_name, middle_name, last_name, pic_link, sign_link, sr_code, year_level, program, major, contact, email, password, address, status, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
            int studentID;
            try (PreparedStatement studentStmt = DBConnect.getConnection().prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS)) {
                studentStmt.setString(1, firstName.getText());
                studentStmt.setString(2, middleName.getText());
                studentStmt.setString(3, lastName.getText());
                studentStmt.setString(4, photoLinks);
                studentStmt.setString(5, signatureLinks);
                studentStmt.setString(6, srCode.getText());
                studentStmt.setString(7, yearLevel.getValue());
                studentStmt.setString(8, program.getValue());
                studentStmt.setString(9, major.getValue());
                studentStmt.setString(10, contact.getText());
                studentStmt.setString(11, email.getText());
                studentStmt.setString(12, password.getText());
                studentStmt.setString(13, address.getText());
                studentStmt.setString(14, status.getValue());
                studentStmt.executeUpdate();
                try (ResultSet rs = studentStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        studentID = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve Student ID.");
                    }
                }
            }
            kon = DBConnect.getConnection();
            loadStudents();
            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Student and Guardian inserted successfully!").show();
        } catch (Exception ex) {
            try {
                kon.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        } finally {
            try {
                kon.setAutoCommit(true);
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

        if (isInputInvalid()) {
            return; // Stop execution if validation fails
        }

        try {

            String photoLink;
            if (studID == null) {
                if (photoFile != null) {
                    String photoFolderId = "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW";
                    Drive driveService = getDriveService();
                    photoLink = uploadFileToDrive(driveService, photoFile, photoFolderId, "image/jpeg");
                    System.out.println("Photo uploaded: " + photoLink);
                } else {
                    photoLink = getImageUrlFromPane(imagePanel);
                }
            } else {
                if (photoFile != null) {
                    String photoFolderId = "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW";
                    Drive driveService = getDriveService();
                    photoLink = uploadFileToDrive(driveService, photoFile, photoFolderId, "image/jpeg");
                    System.out.println("Updated photo uploaded: " + photoLink);
                } else {
                    photoLink = getImageUrlFromPane(imagePanel);
                }
            }

            String signatureLink;
                if (signatureFile != null) {
                    String signatureFolderId = "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW";
                    Drive driveService = getDriveService();
                    signatureLink = uploadFileToDrive(driveService, signatureFile, signatureFolderId, "image/png");
                    System.out.println("Signature uploaded: " + signatureLink);
                } else {
                    signatureLink = getImageUrlFromPane(signaturePanel);
                }
                if (signatureFile != null) {
                    String signatureFolderId = "1Sb89lmsZMpicUJWEWsud9Yl_lStkWJfW";
                    Drive driveService = getDriveService();
                    signatureLink = uploadFileToDrive(driveService, signatureFile, signatureFolderId, "image/png");
                    System.out.println("Updated signature uploaded: " + signatureLink);
                } else {
                    signatureLink = getImageUrlFromPane(signaturePanel);
                }


            // Check if student exists
            String checkStudentSQL = "SELECT id FROM student WHERE sr_code = ? AND contact = ? AND email = ?";
            int studentID = -1;

            try (PreparedStatement checkStmt = DBConnect.getConnection().prepareStatement(checkStudentSQL)) {
                checkStmt.setString(1, srCode.getText());
                checkStmt.setString(2, contact.getText());
                checkStmt.setString(3, email.getText());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        studentID = rs.getInt("id");
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Student not found!").show();
                        return;
                    }
                }
            }

            // Check if a guardian exists
            String checkGuardianSQL = "SELECT id FROM guardian WHERE student_id = ?";
            boolean guardianExists = false;

            try (PreparedStatement checkGuardianStmt = DBConnect.getConnection().prepareStatement(checkGuardianSQL)) {
                checkGuardianStmt.setInt(1, studentID);
                try (ResultSet rs = checkGuardianStmt.executeQuery()) {
                    if (rs.next()) {
                        guardianExists = true;
                    }
                }
            }

            // Get guardian details
            Map<String, String> guardianDetails = getGuardianDetails();
            if (guardianDetails.isEmpty()) {
                return; // Stop update if user cancels input
            }

            if (guardianExists) {
                // Update guardian details
                String updateGuardianSQL = "UPDATE guardian SET first_name = ?, middle_name = ?, last_name = ?, address = ?, email = ?, contact_no = ?, relationship = ? WHERE student_id = ?";
                try (PreparedStatement guardianStmt = DBConnect.getConnection().prepareStatement(updateGuardianSQL)) {
                    guardianStmt.setString(1, guardianDetails.get("GFName"));
                    guardianStmt.setString(2, guardianDetails.get("GMName"));
                    guardianStmt.setString(3, guardianDetails.get("GLName"));
                    guardianStmt.setString(4, guardianDetails.get("GAddress"));
                    guardianStmt.setString(5, guardianDetails.get("GEmail"));
                    guardianStmt.setString(6, guardianDetails.get("GContactNo"));
                    guardianStmt.setString(7, guardianDetails.get("Relationship"));
                    guardianStmt.setInt(8, studentID);

                    guardianStmt.executeUpdate();
                }
            } else {
                // Insert new guardian
                String insertGuardianSQL = "INSERT INTO guardian (student_id, first_name, middle_name, last_name, address, email, contact_no, relationship) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertGuardianStmt = DBConnect.getConnection().prepareStatement(insertGuardianSQL)) {
                    insertGuardianStmt.setInt(1, studentID);
                    insertGuardianStmt.setString(2, guardianDetails.get("GFName"));
                    insertGuardianStmt.setString(3, guardianDetails.get("GMName"));
                    insertGuardianStmt.setString(4, guardianDetails.get("GLName"));
                    insertGuardianStmt.setString(5, guardianDetails.get("GAddress"));
                    insertGuardianStmt.setString(6, guardianDetails.get("GEmail"));
                    insertGuardianStmt.setString(7, guardianDetails.get("GContactNo"));
                    insertGuardianStmt.setString(8, guardianDetails.get("Relationship"));

                    insertGuardianStmt.executeUpdate();
                }
            }

            // Update student details
            String studentSQL = "UPDATE student SET first_name = ?, middle_name = ?, last_name = ?, pic_link = ?, year_level = ?, program = ?, major = ?, contact = ?, email = ?, password = ?, address = ?, status = ? WHERE id = ?";
            try (PreparedStatement studentStmt = DBConnect.getConnection().prepareStatement(studentSQL)) {
                studentStmt.setString(1, firstName.getText());
                studentStmt.setString(2, middleName.getText());
                studentStmt.setString(3, lastName.getText());
                studentStmt.setString(4, photoLink);
                studentStmt.setString(5, yearLevel.getValue());
                studentStmt.setString(6, program.getValue());
                studentStmt.setString(7, major.getValue());
                studentStmt.setString(8, contact.getText());
                studentStmt.setString(9, email.getText());
                studentStmt.setString(10, password.getText());
                studentStmt.setString(11, address.getText());
                studentStmt.setString(12, status.getValue());
                studentStmt.setInt(13, studentID);

                studentStmt.executeUpdate();
            }

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private boolean isStudentExists(String srCode, String email, String contact) {
        if (kon == null) {
            new Alert(Alert.AlertType.ERROR, "Database connection is unavailable!").show();
            return false;
        }

        String sql = "SELECT COUNT(*) FROM student WHERE sr_code = ? OR email = ? OR contact = ?";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, srCode);
            stmt.setString(2, email);
            stmt.setString(3, contact);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
        return false;
    }


    @FXML
    private Map<String, String> getGuardianDetails() {
        Map<String, String> guardianData = new HashMap<>();

        // Get selected student from TableView
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.ERROR, "No student selected! Please select a student to update.").show();
            return new HashMap<>();
        }

        int studentID = selectedStudent.getStudID(); // Get student ID from the selected row

        // Query to fetch guardian details
        String fetchGuardianSQL = "SELECT first_name, middle_name, last_name, address, email, contact_no, relationship FROM guardian WHERE student_id = ?";

        try (PreparedStatement stmt = DBConnect.getConnection().prepareStatement(fetchGuardianSQL)) {
            stmt.setInt(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    guardianData.put("GFName", rs.getString("first_name"));
                    guardianData.put("GMName", rs.getString("middle_name"));
                    guardianData.put("GLName", rs.getString("last_name"));
                    guardianData.put("GAddress", rs.getString("address"));
                    guardianData.put("GEmail", rs.getString("email"));
                    guardianData.put("GContactNo", rs.getString("contact_no"));
                    guardianData.put("Relationship", rs.getString("relationship"));
                }
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error fetching guardian details: " + ex.getMessage()).show();
            return new HashMap<>(); // Return empty map on error
        }

        // Display the input dialog with pre-filled values
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Guardian Information");
        dialog.setHeaderText("Update Guardian Details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField gfName = new TextField(guardianData.getOrDefault("GFName", ""));
        TextField gmName = new TextField(guardianData.getOrDefault("GMName", ""));
        TextField glName = new TextField(guardianData.getOrDefault("GLName", ""));
        TextField gAddress = new TextField(guardianData.getOrDefault("GAddress", ""));
        TextField gEmail = new TextField(guardianData.getOrDefault("GEmail", ""));
        TextField gContactNo = new TextField(guardianData.getOrDefault("GContactNo", ""));

        ComboBox<String> relationship = new ComboBox<>();
        relationship.getItems().addAll("Father", "Mother", "Guardian");
        relationship.setValue(guardianData.getOrDefault("Relationship", "Father"));

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
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Map<String, String> result = new HashMap<>();
                result.put("GFName", gfName.getText());
                result.put("GMName", gmName.getText());
                result.put("GLName", glName.getText());
                result.put("GAddress", gAddress.getText());
                result.put("GEmail", gEmail.getText());
                result.put("GContactNo", gContactNo.getText());
                result.put("Relationship", relationship.getValue());
                return result;
            }
            return new HashMap<>();
        });

        return dialog.showAndWait().orElse(new HashMap<>());
    }


    /**
     * Validates if any required field is empty and displays an error message.
     * @return true if input is invalid, false if all fields are filled
     */

    @FXML
    private boolean isInputInvalid() {
        StringBuilder errorMessage = new StringBuilder();

        // Check for empty fields
        if (firstName.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#ff1744;'>First Name</span> is required.<br>");
        if (lastName.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#ff1744;'>Last Name</span> is required.<br>");
        if (srCode.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#ff1744;'>SR-Code</span> is required.<br>");
        if (yearLevel.getValue() == null) errorMessage.append("• <span style='color:#ff9800;'>Year Level</span> is required.<br>");
        if (program.getValue() == null) errorMessage.append("• <span style='color:#ff9800;'>Program</span> is required.<br>");
        if (major.getValue() == null) errorMessage.append("• <span style='color:#ff9800;'>Major</span> is required.<br>");
        if (contact.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#2196f3;'>Contact</span> is required.<br>");
        if (email.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#2196f3;'>Email</span> is required.<br>");
        if (password.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#4caf50;'>Password</span> is required.<br>");
        if (address.getText().trim().isEmpty()) errorMessage.append("• <span style='color:#4caf50;'>Address</span> is required.<br>");
        if (status.getValue() == null) errorMessage.append("• <span style='color:#673ab7;'>Status</span> is required.<br>");

        // Show error message if any field is empty
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("⚠️ Validation Errors Found");

            // HTML-styled content (JavaFX Alert only supports plain text, so this will be simplified)
            String styledMessage = errorMessage.toString()
                    .replace("<br>", "\n") // Replace HTML line breaks
                    .replaceAll("<.*?>", ""); // Remove HTML tags

            Label messageLabel = new Label(styledMessage);
            messageLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setContent(messageLabel);
            dialogPane.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-width: 2px;");

            // Button style
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            alert.show();
            return true; // Input is invalid
        }

        return false; // Input is valid
    }



    @FXML
    private void clearFields() {
        Arrays.asList(firstName, middleName, lastName, password, srCode, contact, email, password, address)
                .forEach(TextField::clear);

        Arrays.asList(yearLevel, program, major, status)
                .forEach(comboBox -> comboBox.setValue(null));

        clearPanels();
    }
    private void clearPanels() {
        signaturePanel.getChildren().clear();
        imagePanel.getChildren().clear();
    }


    @FXML
    public void adjustPanelAndColumns() {
        double totalWidth = 0;
        // Iterate over each column in the TableView
        for (TableColumn<Student, ?> column : studentTable.getColumns()) {
            // Center the content in every cell of this column
            column.setStyle("-fx-alignment: CENTER;");

            double maxWidth = 0;
            // Check if this is the controls or picture column by fx:id.
            if ("colControls".equals(column.getId())) {
                maxWidth = 90;  // Fixed width for the controls column
            } else if ("picLinkColumn".equals(column.getId())) {
                maxWidth = 93;  // Fixed width for the picture column (adjust as needed)
            } else {
                String header = column.getText();
                if (header == null || header.trim().isEmpty()) {
                    maxWidth = 80;
                } else {
                    // Measure header text width.
                    Text headerText = new Text(header);
                    maxWidth = headerText.getBoundsInLocal().getWidth();
                    // Iterate over each FacultyTable item for this column.
                    for (Student item : studentTable.getItems()) {
                        Object cellData = column.getCellData(item);
                        if (cellData != null) {
                            Text cellText = new Text(cellData.toString());
                            double cellWidth = cellText.getBoundsInLocal().getWidth();
                            if (cellWidth > maxWidth) {
                                maxWidth = cellWidth;
                            }
                        }
                    }
                    // Add padding (20 pixels)
                    maxWidth += 20;
                }
            }
            // Set the column's preferred width and add to the total width.
            column.setPrefWidth(maxWidth);
            totalWidth += maxWidth;
        }
        // Adjust the facultyPane's preferred width to the total columns width plus margin.
        rightpane.setPrefWidth(totalWidth + 10);
    }

    private boolean isPaneHidden = false;

    @FXML
    public void togglePaneAndResize() {
        if (!isPaneHidden) {
            // Hide the hidePane by sliding it downward.
            TranslateTransition hideTransition = new TranslateTransition(Duration.millis(200), hidePane);
            hideTransition.setToY(230); // Adjust the value as needed.
            hideTransition.setInterpolator(Interpolator.SPLINE(0.42, 0, 0.58, 1));
            hideTransition.play();

            // Expand the TableView and its container by the height of hidePane.
            double extraHeight = 218;
            Timeline expandTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(studentTable.prefHeightProperty(), originalStudentTableHeight + extraHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1)),
                            new KeyValue(rightpane.prefHeightProperty(), originalStudentPaneHeight + extraHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1))
                    )
            );
            expandTimeline.play();

            isPaneHidden = true;
        } else {
            // Slide hidePane back up (restore to Y = 0).
            TranslateTransition showTransition = new TranslateTransition(Duration.millis(200), hidePane);
            showTransition.setToY(0);
            showTransition.setInterpolator(Interpolator.SPLINE(0.42, 0, 0.58, 1));
            showTransition.play();

            // Contract the TableView and its container back to their original heights.
            Timeline contractTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(studentTable.prefHeightProperty(), originalStudentTableHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1)),
                            new KeyValue(rightpane.prefHeightProperty(), originalStudentPaneHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1))
                    )
            );
            contractTimeline.play();

            isPaneHidden = false;
        }
    }
}

