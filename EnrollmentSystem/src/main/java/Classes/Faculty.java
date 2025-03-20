package Classes; //Hello Everyone!

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.sql.*;
import ExtraSources.DBConnect;
import GettersSetters.FacultyTable;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Faculty {
    private Connection kon;
    private ObservableList<FacultyTable> facultyList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        kon = DBConnect.getConnection();
        colId.setVisible(false);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPicLink.setCellValueFactory(new PropertyValueFactory<>("picLink"));
        colSignLink.setCellValueFactory(new PropertyValueFactory<>("signLink"));

        loadFacultyTable();
    }

    @FXML
    private void openPictureDialog() {
        try {
            // Load the PictureDialog FXML file (adjust the resource path as needed)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/PictureDialog.fxml"));
            Parent root = loader.load();

            // Get the PictureDialog controller instance
            PictureDialog dialogController = loader.getController();

            // Create and display the dialog stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Capture Picture");
            stage.showAndWait();

// After the dialog closes, retrieve the captured image
            Image capturedImage = dialogController.getCapturedImage();
            if (capturedImage != null) {
                // Create an ImageView with the captured image
                ImageView iv = new ImageView(capturedImage);

                // Get dimensions of the captured image and the target panel
                double imageWidth = capturedImage.getWidth();
                double imageHeight = capturedImage.getHeight();
                double panelWidth = imagePanel.getPrefWidth();
                double panelHeight = imagePanel.getPrefHeight();

                // Compute target aspect ratio (we assume face is centered)
                double targetRatio = panelWidth / panelHeight;
                double imageRatio = imageWidth / imageHeight;

                double viewportX, viewportY, viewportWidth, viewportHeight;

                if (imageRatio > targetRatio) {
                    // Image is wider than target ratio: crop the left and right sides
                    viewportHeight = imageHeight;
                    viewportWidth = imageHeight * targetRatio;
                    viewportX = (imageWidth - viewportWidth) / 2;
                    viewportY = 0;
                } else {
                    // Image is taller than target ratio: crop top and bottom
                    viewportWidth = imageWidth;
                    viewportHeight = imageWidth / targetRatio;
                    viewportX = 0;
                    viewportY = (imageHeight - viewportHeight) / 2;
                }

                // Set the viewport to crop the image (center crop)
                iv.setViewport(new javafx.geometry.Rectangle2D(viewportX, viewportY, viewportWidth, viewportHeight));

                // Fit the ImageView to the panel while preserving the cropped aspect ratio
                iv.setFitWidth(panelWidth);
                iv.setFitHeight(panelHeight);
                iv.setPreserveRatio(true);

                // Clear the panel and add the cropped ImageView
                imagePanel.getChildren().clear();
                imagePanel.getChildren().add(iv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSignatureDialog() {
        try {
            // Load the signature dialog FXML
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
                signaturePanel.getChildren().clear();
                signaturePanel.getChildren().add(iv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFacultyTable() {
        String sql = "SELECT * FROM faculty";
        try {
            PreparedStatement ps = kon.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String roleStr = rs.getString("role");
                String emailStr = rs.getString("email");
                String picLink = rs.getString("pic_link");
                String signLink = rs.getString("sign_link");

                FacultyTable facultyRecord = new FacultyTable(id, firstName, middleName, lastName,
                        roleStr, emailStr, picLink, signLink);
                facultyList.add(facultyRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        facultyTable.setItems(facultyList);
    }

    @FXML private TextField FName;
    @FXML private TextField LName;
    @FXML private TextField MName;
    @FXML private Button add;
    @FXML private Button delete;
    @FXML private TextField email;
    @FXML private Pane imagePanel;
    @FXML private Button picture;
    @FXML private ComboBox<?> role;
    @FXML private Button signature;
    @FXML private Pane signaturePanel;
    @FXML private Button update;
    @FXML private ImageView upload;
    @FXML private Button uploadPic;
    @FXML private ImageView uploadSign;

    @FXML private TableView<FacultyTable> facultyTable;
    @FXML private TableColumn<FacultyTable, Integer> colId;
    @FXML private TableColumn<FacultyTable, String> colFirstName;
    @FXML private TableColumn<FacultyTable, String> colMiddleName;
    @FXML private TableColumn<FacultyTable, String> colLastName;
    @FXML private TableColumn<FacultyTable, String> colRole;
    @FXML private TableColumn<FacultyTable, String> colEmail;
    @FXML private TableColumn<FacultyTable, String> colPicLink;
    @FXML private TableColumn<FacultyTable, String> colSignLink;
}
