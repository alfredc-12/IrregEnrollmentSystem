package Classes;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SignatureDialog {

    @FXML private Pane signaturePanel;  // The pane in which the user will draw the signature.
    @FXML private Button takeSignature;   // Button to confirm the signature

    private Canvas canvas;
    private GraphicsContext gc;
    private Image signatureImage;  // The captured signature image

    @FXML
    private void initialize() {
        // Create a canvas with the same size as the signature panel.
        canvas = new Canvas(signaturePanel.getPrefWidth(), signaturePanel.getPrefHeight());
        gc = canvas.getGraphicsContext2D();
        signaturePanel.getChildren().add(canvas);

        // Set up mouse event handlers for drawing
        canvas.setOnMousePressed((MouseEvent e) -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged((MouseEvent e) -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
    }

    @FXML
    private void handleTakeSignature() {
        // Capture the drawn signature as an image.
        WritableImage snapshot = canvas.snapshot(null, null);
        signatureImage = snapshot;

        // Close the dialog.
        Stage stage = (Stage) takeSignature.getScene().getWindow();
        stage.close();
    }

    public Image getSignatureImage() {
        return signatureImage;
    }
}
