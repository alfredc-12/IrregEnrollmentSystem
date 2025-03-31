package Classes; //Hello Everyone!

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;
import java.sql.*;
import ExtraSources.*;
import GettersSetters.FacultyTable;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import java.io.InputStream;
import java.io.FileNotFoundException;
import com.google.api.client.json.gson.GsonFactory;
import javax.imageio.ImageIO;

public class Faculty {
    private Connection conn;
    private Connection kon;
    private ObservableList<FacultyTable> facultyList = FXCollections.observableArrayList();
    private double originalFacultyTableHeight;
    private double originalFacultyPaneHeight;
    private java.io.File photoFile;
    private java.io.File signatureFile;
    private Integer currentUpdateFacultyId = null;

    private final double originalSearchLayoutX = 588.0;
    private final double originalSearchPrefWidth = 110.0;
    private final double originalShowDeletedLayoutX = 476.0;

    private final double targetSearchLayoutX = 498.0;
    private final double targetSearchPrefWidth = 200.0;
    private final double targetShowDeletedLayoutX = 385.0;

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

    private void showConfirmationDialog(String message,
                                        String confirmText, Runnable onConfirm,
                                        String cancelText, Runnable onCancel,
                                        String iconType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Notification.fxml"));
            Parent root = loader.load();
            ExtraSources.Notification notification = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            notification.setStage(dialogStage);
            notification.setMessage(message);
            notification.setButtons(confirmText, () -> {
                dialogStage.close();
                onConfirm.run();
            }, cancelText, () -> {
                dialogStage.close();
                onCancel.run();
            });
            notification.setIcon(iconType);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeEvents() {
        // Add a listener to the search field.
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                searching.hide();
            } else {
                loadSuggestions(newValue.trim());
            }
        });

        search.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { // Focus lost
                String text = search.getText().trim();
                if (text.isEmpty() || text.equals("(Search Name)")) {
                    animateSearchResetTransition();
                }
            }
        });

        // Optional: Add key event handling to support arrow key navigation
        search.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (searching.isShowing()) {
                if (event.getCode() == KeyCode.DOWN) {
                    // Transfer focus to the first suggestion
                    searching.getSkin().getNode().requestFocus();
                    event.consume();
                }
            }
        });

        showSearch.setOnMouseEntered(event -> animateSearchTransition());
        showSearch.setOnMouseExited(event -> animateSearchResetTransition());

        search.textProperty().addListener((observable, oldValue, newValue) -> loadFacultyTable());
    }

    private void initSearchField() {
        // Set the initial style to center and light gray text.
        search.setText("(Search Name)");
        search.setStyle("-fx-text-fill: lightgray; -fx-alignment: center;");

        search.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // When the field gains focus:
                if (search.getText().equals("(Search Name)")) {
                    search.setText("");
                    // Change text fill to black (or any other color you want for user input).
                    search.setStyle("-fx-text-fill: black; -fx-alignment: center-left;");
                }
            } else {
                // When the field loses focus:
                if (search.getText().trim().isEmpty()) {
                    search.setText("(Search Name)");
                    search.setStyle("-fx-text-fill: lightgray; -fx-alignment: center;");
                }
            }
        });
    }

    private void animateSearchTransition() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(search.layoutXProperty(), targetSearchLayoutX, Interpolator.EASE_BOTH),
                        new KeyValue(search.prefWidthProperty(), targetSearchPrefWidth, Interpolator.EASE_BOTH),
                        new KeyValue(showDeleted.layoutXProperty(), targetShowDeletedLayoutX, Interpolator.EASE_BOTH)
                )
        );
        timeline.play();
    }

    private void animateSearchResetTransition() {
        String currentText = search.getText();
        if (currentText.isEmpty() || currentText.equals("(Search Name)")) {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(search.layoutXProperty(), originalSearchLayoutX, Interpolator.EASE_BOTH),
                            new KeyValue(search.prefWidthProperty(), originalSearchPrefWidth, Interpolator.EASE_BOTH),
                            new KeyValue(showDeleted.layoutXProperty(), originalShowDeletedLayoutX, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        }
    }

    private void loadSuggestions(String query) {
        ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
        String sql = "SELECT CONCAT(first_name, ' ', IFNULL(middle_name, ''), ' ', last_name) AS fullname " +
                "FROM faculty " +
                "WHERE isDeleted = 0 " +
                "AND CONCAT(first_name, ' ', middle_name, ' ', last_name) LIKE ? " +
                "LIMIT 10";
        try (PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql)) {
            // Use '%' wildcards to match any characters before/after the query.
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fullName = rs.getString("fullname").trim();
                MenuItem item = new MenuItem(fullName);
                // When a suggestion is clicked, update the search field and hide the context menu.
                item.setOnAction(e -> {
                    search.setText(fullName);
                    searching.hide();
                });
                menuItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If there are suggestions, show the context menu under the text field.
        if (!menuItems.isEmpty()) {
            searching.getItems().setAll(menuItems);
            if (!searching.isShowing()) {
                searching.show(search, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        } else {
            searching.hide();
        }
    }

    private void performTogglePaneAndResize() {
        if (!isPaneHidden) {
            // Hide the hidePane by sliding it downward.
            TranslateTransition hideTransition = new TranslateTransition(Duration.millis(200), hidePane);
            hideTransition.setToY(213); // Adjust as needed
            hideTransition.setInterpolator(Interpolator.SPLINE(0.42, 0, 0.58, 1));
            hideTransition.play();

            // Expand the TableView and its container.
            double extraHeight = 218;
            Timeline expandTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(facultyTable.prefHeightProperty(), originalFacultyTableHeight + extraHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1)),
                            new KeyValue(facultyPane.prefHeightProperty(), originalFacultyPaneHeight + extraHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1))
                    )
            );
            expandTimeline.play();

            isPaneHidden = true;
        } else {
            // Slide hidePane back up.
            TranslateTransition showTransition = new TranslateTransition(Duration.millis(200), hidePane);
            showTransition.setToY(0);
            showTransition.setInterpolator(Interpolator.SPLINE(0.42, 0, 0.58, 1));
            showTransition.play();

            // Contract the TableView and container back to original heights.
            Timeline contractTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(facultyTable.prefHeightProperty(), originalFacultyTableHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1)),
                            new KeyValue(facultyPane.prefHeightProperty(), originalFacultyPaneHeight, Interpolator.SPLINE(0.42, 0, 0.58, 1))
                    )
            );
            contractTimeline.play();

            isPaneHidden = false;
        }
    }

    private void clearAllFields() {
        FName.clear();
        MName.clear();
        LName.clear();
        contantno.clear();
        email.clear();
        role.setValue(null);
        imagePanel.getChildren().clear();
        signaturePanel.getChildren().clear();
    }

    @FXML
    public void addFacultyRecord() {
        // Check required fields.
        if (FName.getText().trim().isEmpty() ||
                MName.getText().trim().isEmpty() ||
                LName.getText().trim().isEmpty() ||
                contantno.getText().trim().isEmpty() ||
                email.getText().trim().isEmpty() ||
                role.getValue() == null ||
                role.getValue().toString().trim().isEmpty()) {

            // Show a warning dialog with only one button ("Ok").
            showConfirmationDialog(
                    "Warning: Required fields are empty. Please fill in all required fields.",
                    "Ok",    // Confirm button text (only one button will be shown)
                    () -> { /* No action on confirm */ },
                    "",      // Cancel button text is empty so it won't show
                    () -> { /* Do nothing on cancel */ },
                    "warning"
            );
            return;
        }

        // Determine the confirmation message based on add vs. update.
        String confirmMessage = (currentUpdateFacultyId == null)
                ? "Are you sure you want to add this faculty record?"
                : "Are you sure you want to update this faculty record?";

        // Show confirmation dialog with "Confirm" and "Cancel" buttons.
        showConfirmationDialog(
                confirmMessage,
                "Confirm", // Confirm button text
                () -> {
                    // Run database operations on a background thread.
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                // Retrieve values from UI fields.
                                String firstName = FName.getText().trim();
                                String middleName = MName.getText().trim();
                                String lastName = LName.getText().trim();
                                String roleStr = role.getValue() != null ? role.getValue().toString() : "";
                                String contactNo = contantno.getText().trim();
                                String personalEmail = email.getText().trim();
                                String cleanFirstName = firstName.replaceAll("\\s+", "");
                                String cleanLastName = lastName.replaceAll("\\s+", "");
                                String bsuEmail = (cleanFirstName + "." + cleanLastName).toLowerCase() + "@g.batstate-u.edu.ph";

                                // For new records, use default (or null) for password and maxSubjects.
                                String defaultPassword = null;
                                Integer defaultMaxSubjects = null;

                                // --- Prepare the photo link ---
                                String photoLink;
                                if (currentUpdateFacultyId == null) {
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
                                        String photoFolderId = "1oailnJ6skGtrF2jL9eme4OskQ9XXbnag";
                                        Drive driveService = getDriveService();
                                        photoLink = uploadFileToDrive(driveService, photoFile, photoFolderId, "image/jpeg");
                                        System.out.println("Updated photo uploaded: " + photoLink);
                                    } else {
                                        photoLink = getImageUrlFromPane(imagePanel);
                                    }
                                }

                                // --- Prepare the signature link ---
                                String signatureLink;
                                if (currentUpdateFacultyId == null) {
                                    if (signatureFile != null) {
                                        String signatureFolderId = "1oailnJ6skGtrF2jL9eme4OskQ9XXbnag";
                                        Drive driveService = getDriveService();
                                        signatureLink = uploadFileToDrive(driveService, signatureFile, signatureFolderId, "image/png");
                                        System.out.println("Signature uploaded: " + signatureLink);
                                    } else {
                                        signatureLink = getImageUrlFromPane(signaturePanel);
                                    }
                                } else {
                                    if (signatureFile != null) {
                                        String signatureFolderId = "1oailnJ6skGtrF2jL9eme4OskQ9XXbnag";
                                        Drive driveService = getDriveService();
                                        signatureLink = uploadFileToDrive(driveService, signatureFile, signatureFolderId, "image/png");
                                        System.out.println("Updated signature uploaded: " + signatureLink);
                                    } else {
                                        signatureLink = getImageUrlFromPane(signaturePanel);
                                    }
                                }

                                if (currentUpdateFacultyId == null) {
                                    Connection connect = DBConnect.getConnection();
                                    // --- INSERT new record ---
                                    String sql = "INSERT INTO faculty (first_name, middle_name, last_name, role, contact_no, personal_email, bsu_email, password, pic_link, sign_link, max_subjects) " +
                                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                    PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql);
                                    ps.setString(1, firstName);
                                    ps.setString(2, middleName);
                                    ps.setString(3, lastName);
                                    ps.setString(4, roleStr);
                                    ps.setString(5, contactNo);
                                    ps.setString(6, personalEmail);
                                    ps.setString(7, bsuEmail);
                                    if (defaultPassword == null) {
                                        ps.setNull(8, Types.VARCHAR);
                                    } else {
                                        ps.setString(8, defaultPassword);
                                    }
                                    ps.setString(9, photoLink);
                                    ps.setString(10, signatureLink);
                                    if (defaultMaxSubjects == null) {
                                        ps.setNull(11, Types.INTEGER);
                                    } else {
                                        ps.setInt(11, defaultMaxSubjects);
                                    }

                                    int affectedRows = ps.executeUpdate();
                                    if (affectedRows > 0) {
                                        System.out.println("Faculty record added successfully!");
                                    } else {
                                        System.err.println("Failed to add faculty record.");
                                    }
                                } else {
                                    // --- UPDATE existing record ---
                                    String sql = "UPDATE faculty SET first_name=?, middle_name=?, last_name=?, role=?, contact_no=?, personal_email=?, bsu_email=?, pic_link=?, sign_link=? " +
                                            "WHERE faculty_id=?";
                                    PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql);
                                    ps.setString(1, firstName);
                                    ps.setString(2, middleName);
                                    ps.setString(3, lastName);
                                    ps.setString(4, roleStr);
                                    ps.setString(5, contactNo);
                                    ps.setString(6, personalEmail);
                                    ps.setString(7, bsuEmail);
                                    ps.setString(8, photoLink);
                                    ps.setString(9, signatureLink);
                                    ps.setInt(10, currentUpdateFacultyId);
                                    int affectedRows = ps.executeUpdate();
                                    if (affectedRows > 0) {
                                        System.out.println("Faculty record updated successfully!");
                                    } else {
                                        System.err.println("Failed to update faculty record.");
                                    }
                                    currentUpdateFacultyId = null;
                                    Platform.runLater(() -> add.setText("Add"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return null;
                        }
                    };

                    task.setOnSucceeded(event -> {
                        FName.clear();
                        MName.clear();
                        LName.clear();
                        contantno.clear();
                        email.clear();
                        role.setValue(null);
                        imagePanel.getChildren().clear();
                        signaturePanel.getChildren().clear();
                        loadFacultyTable();
                    });

                    task.setOnFailed(event -> {
                        task.getException().printStackTrace();
                    });

                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                },
                "Cancel",        // Cancel button text.
                () -> { /* Cancel action: do nothing */ },
                "confirm"        // Icon type.
        );
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
    private void initialize() {
        hidePane.setTranslateY(213);
        searching.setStyle("-fx-pref-width: 200px;");
        //DBConnectInitialize();
        kon = DBConnect.getConnection();
        colId.setVisible(false);
        initializeTableColumns();
        initSearchField();
        loadFacultyTable();
        adjustPanelAndColumns();
        initializeEvents();
        originalFacultyPaneHeight = facultyPane.getPrefHeight();
        originalFacultyTableHeight = facultyTable.getPrefHeight();
        role.setItems(FXCollections.observableArrayList(
                "Professor",
                "Assistant Professor",
                "Lecturer",
                "Program Chair",
                "Guest Lecturer",
                "Dean"
        ));
        togglePaneAndResize();
    }

   /* @FXML
    private void DBConnectInitialize() {
        // In your initialize() method or wherever you need the connection:
        Task<Connection> connectionTask = new Task<Connection>() {
            @Override
            protected Connection call() throws Exception {
                // This call runs on a background thread
                return DBConnect2.getConnection();
            }
        };

        connectionTask.setOnSucceeded(event -> {
            // Update the UI thread with the connection
            conn = connectionTask.getValue();
            System.out.println("Database connection established successfully!");
            // Optionally, call further UI updates or methods that need the connection.
        });

        connectionTask.setOnFailed(event -> {
            // Handle errors; you might show an error dialog or log the exception.
            Throwable ex = connectionTask.getException();
            ex.printStackTrace();
            System.err.println("Failed to establish database connection.");
        });

        // Start the task on a new daemon thread so it doesn't block the UI.
        Thread connectionThread = new Thread(connectionTask);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }*/

    @FXML
    private void handleUpdate(FacultyTable faculty) {
        // Open hidePane if it is currently hidden.
        if (isPaneHidden) {
            togglePaneAndResize();
        }
        // Store the current record id for update mode.
        currentUpdateFacultyId = faculty.getId();

        // Populate text fields from the faculty object.
        FName.setText(faculty.getFirstName());
        MName.setText(faculty.getMiddleName());
        LName.setText(faculty.getLastName());
        contantno.setText(faculty.getContactNo());
        email.setText(faculty.getPersonalEmail());
        role.setValue(faculty.getRole());

        // Load picture into imagePanel if available.
        if (faculty.getPicLink() != null && !faculty.getPicLink().isEmpty()) {
            Image image = new Image(faculty.getPicLink(), true);
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
        if (faculty.getSignLink() != null && !faculty.getSignLink().isEmpty()) {
            Image image = new Image(faculty.getSignLink(), true);
            ImageView iv = new ImageView(image);
            iv.setFitWidth(signaturePanel.getPrefWidth());
            iv.setFitHeight(signaturePanel.getPrefHeight());
            iv.setPreserveRatio(true);
            signaturePanel.getChildren().clear();
            signaturePanel.getChildren().add(iv);
        } else {
            signaturePanel.getChildren().clear();
        }

        // Repurpose the add button to "Confirm Update"
        add.setText("Confirm Update");
    }

    @FXML
    private void handleDelete(FacultyTable faculty) {
        // If "Show Deleted" is selected, then this button acts as a "restore" button.
        if (showDeleted.isSelected()) {
            showConfirmationDialog(
                    "Are you sure you want to restore this faculty record?",
                    "Restore", // Confirm button label
                    () -> {    // onConfirm: restore record
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                String sql = "UPDATE faculty SET isDeleted = ? WHERE faculty_id = ?";
                                try (PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql)) {
                                    ps.setBoolean(1, false); // restore record
                                    ps.setInt(2, faculty.getId());
                                    int affectedRows = ps.executeUpdate();
                                    if (affectedRows > 0) {
                                        System.out.println("Faculty record restored successfully!");
                                    } else {
                                        System.err.println("Failed to restore faculty record.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        };
                        task.setOnSucceeded(event -> loadFacultyTable());
                        task.setOnFailed(event -> task.getException().printStackTrace());
                        new Thread(task).start();
                    },
                    "Cancel", // Cancel button label
                    () -> {    // onCancel: no action
                        // Optionally, perform any cancel action here.
                    },
                    "confirm" // Icon type (set to "confirm")
            );
        } else {
            // Otherwise, perform deletion (set isDeleted = true)
            showConfirmationDialog(
                    "Are you sure you want to delete this faculty record?",
                    "Delete", // Confirm button label
                    () -> {   // onConfirm: delete record
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                String sql = "UPDATE faculty SET isDeleted = ? WHERE faculty_id = ?";
                                try (PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql)) {
                                    ps.setBoolean(1, true); // mark as deleted
                                    ps.setInt(2, faculty.getId());
                                    int affectedRows = ps.executeUpdate();
                                    if (affectedRows > 0) {
                                        System.out.println("Faculty record deleted successfully!");
                                    } else {
                                        System.err.println("Failed to delete faculty record.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        };
                        task.setOnSucceeded(event -> loadFacultyTable());
                        task.setOnFailed(event -> task.getException().printStackTrace());
                        new Thread(task).start();
                    },
                    "Cancel",  // Cancel button label
                    () -> {    // onCancel: no action
                        // Optionally do something on cancel
                    },
                    "warning"  // Icon type
            );
        }
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

    @FXML
    private void initializeTableColumns() {
        colControls.setCellFactory(col -> new CustomControlsCell<FacultyTable>(
                faculty -> handleUpdate(faculty),
                faculty -> handleDelete(faculty)
        ));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        colContactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPersonalEmail.setCellValueFactory(new PropertyValueFactory<>("personalEmail"));
        colBSUEmail.setCellValueFactory(new PropertyValueFactory<>("bsuEmail"));
        colPic.setCellValueFactory(new PropertyValueFactory<>("picLink"));
        colPic.setCellFactory(column -> new TableCell<FacultyTable, String>() {
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

    @FXML
    private boolean isPaneHidden = false;

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
    public void adjustPanelAndColumns() {
        double totalWidth = 0;
        // Iterate over each column in the TableView
        for (TableColumn<FacultyTable, ?> column : facultyTable.getColumns()) {
            // Center the content in every cell of this column
            column.setStyle("-fx-alignment: CENTER;");

            double maxWidth = 0;
            // Check if this is the controls or picture column by fx:id.
            if ("colControls".equals(column.getId())) {
                maxWidth = 90;  // Fixed width for the controls column
            } else if ("colPic".equals(column.getId())) {
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
                    for (FacultyTable item : facultyTable.getItems()) {
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
        facultyPane.setPrefWidth(totalWidth + 10);
    }

    @FXML
    public void togglePaneAndResize() {
        // Check for unsaved changes.
        boolean unsaved = !FName.getText().trim().isEmpty() ||
                !MName.getText().trim().isEmpty() ||
                !LName.getText().trim().isEmpty() ||
                !contantno.getText().trim().isEmpty() ||
                !email.getText().trim().isEmpty() ||
                (role.getValue() != null && !role.getValue().toString().trim().isEmpty()) ||
                !imagePanel.getChildren().isEmpty() ||
                !signaturePanel.getChildren().isEmpty();

        if (unsaved) {
            // Use different messages based on mode.
            String msg = (currentUpdateFacultyId == null)
                    ? "You have unsaved changes in Add mode. Do you want to discard them and hide the pane?"
                    : "You have unsaved update changes. Do you want to discard them and hide the pane?";
            showConfirmationDialog(
                    msg,
                    "Discard",
                    () -> {
                        clearAllFields();
                        performTogglePaneAndResize();
                        // Reset update mode if applicable.
                        currentUpdateFacultyId = null;
                        add.setText("Add Faculty");
                    },
                    "Cancel",
                    () -> { /* No action on cancel */ },
                    "warning"
            );
            return;
        }
        // No unsaved changes – simply toggle.
        performTogglePaneAndResize();
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
    private void loadFacultyTable() {
        String sql;
        String searchText = search.getText().trim();
        boolean applySearch = !searchText.equals("(Search Name)") && !searchText.isEmpty();
        // Determine isDeleted condition based on the radio button
        String isDeletedCondition = showDeleted.isSelected() ? "isDeleted = 1" : "isDeleted = 0";

        if (applySearch) {
            // Use CONCAT with spaces; adjust for possible NULL middle names.
            sql = "SELECT faculty_id, first_name, middle_name, last_name, role, contact_no, personal_email, bsu_email, pic_link, sign_link " +
                    "FROM faculty " +
                    "WHERE " + isDeletedCondition + " AND CONCAT(first_name, ' ', IFNULL(middle_name, ''), ' ', last_name) LIKE ?";
        } else {
            sql = "SELECT faculty_id, first_name, middle_name, last_name, role, contact_no, personal_email, bsu_email, pic_link, sign_link " +
                    "FROM faculty " +
                    "WHERE " + isDeletedCondition;
        }

        facultyList.clear();

        try {
            PreparedStatement ps = DBConnect.getConnection().prepareStatement(sql);
            if (applySearch) {
                ps.setString(1, "%" + searchText + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("faculty_id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String roleStr = rs.getString("role");
                String contactNo = rs.getString("contact_no");
                String personalEmail = rs.getString("personal_email");
                String bsuEmail = rs.getString("bsu_email");
                String picLink = rs.getString("pic_link");
                String signLink = rs.getString("sign_link");

                // Dummy values for the unused fields.
                String password = "";
                int maxSubjects = 0;

                FacultyTable facultyRecord = new FacultyTable(id, firstName, middleName, lastName,
                        roleStr, contactNo, personalEmail, bsuEmail, password, picLink, signLink, maxSubjects);
                facultyList.add(facultyRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        facultyTable.setItems(facultyList);
        adjustPanelAndColumns();
    }

    @FXML private TextField FName;
    @FXML private TextField LName;
    @FXML private TextField MName;
    @FXML private Button add;
    @FXML private Button delete;
    @FXML private TextField email;
    @FXML private Pane imagePanel;
    @FXML private Button picture;
    @FXML private ComboBox<String> role;
    @FXML private Button signature;
    @FXML private Pane signaturePanel;
    @FXML private Button update;
    @FXML private ImageView upload;
    @FXML private Button uploadPic;
    @FXML private ImageView uploadSign;

    @FXML private Button btnHide;
    @FXML private TextField contantno;
    @FXML private Pane hidePane;
    @FXML private AnchorPane facultyPane;
    @FXML private RadioButton showDeleted;
    @FXML private Pane showSearch;
    @FXML private TextField search;
    @FXML private MenuItem searcher;
    @FXML private ContextMenu searching;
    @FXML private AnchorPane paneFaculty;

    @FXML private TableView<FacultyTable> facultyTable;
    @FXML private TableColumn<FacultyTable, Integer> colId;
    @FXML private TableColumn<FacultyTable, String> colFullname;
    @FXML private TableColumn<FacultyTable, String> colContactNumber;
    @FXML private TableColumn<FacultyTable, String> colPersonalEmail;
    @FXML private TableColumn<FacultyTable, String> colRole;
    @FXML private TableColumn<FacultyTable, String> colBSUEmail;
    @FXML private TableColumn<FacultyTable, String> colPic;
    @FXML private TableColumn<FacultyTable, Void> colControls;
}
