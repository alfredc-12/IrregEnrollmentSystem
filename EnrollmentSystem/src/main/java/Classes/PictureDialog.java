package Classes;

import com.github.sarxos.webcam.Webcam;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class PictureDialog {

    // These fields must match the fx:id in the FXML.
    @FXML
    private Pane picturePanel;

    @FXML
    private Button takePicture;

    private Webcam webcam;
    private AnimationTimer timer;
    private Image capturedImage;
    private ImageView imageView; // This will be created and added to picturePanel.

    // Store the fixed dimensions from the FXML.
    private double panelWidth;
    private double panelHeight;

    // Flag to ensure we set the viewport only once.
    private boolean viewportSet = false;

    @FXML
    private void initialize() {
        // Retrieve fixed dimensions from the Pane's preferred size.
        panelWidth = picturePanel.getPrefWidth();
        panelHeight = picturePanel.getPrefHeight();

        // Create an ImageView with the fixed dimensions.
        imageView = new ImageView();
        imageView.setFitWidth(panelWidth);
        imageView.setFitHeight(panelHeight);
        // We keep preserveRatio true so the image isn't squished,
        // and then we will crop it via the viewport.
        imageView.setPreserveRatio(true);

        // Add the ImageView to the Pane. The Pane size remains fixed.
        picturePanel.getChildren().add(imageView);

        // Get the default webcam.
        webcam = Webcam.getDefault();
        if (webcam != null) {
            // Set the webcam resolution to a supported size that best matches the panel dimensions.
            updateWebcamResolution(panelWidth, panelHeight);
            webcam.open();

            // Start an AnimationTimer to update the ImageView with the webcam feed.
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    BufferedImage frame = webcam.getImage();
                    if (frame != null) {
                        // On the first frame, compute and set a viewport that crops the image
                        // to the desired aspect ratio (panelWidth/panelHeight) centered in the frame.
                        if (!viewportSet) {
                            int imgWidth = frame.getWidth();
                            int imgHeight = frame.getHeight();
                            double targetRatio = panelWidth / panelHeight;
                            double imgRatio = (double) imgWidth / imgHeight;
                            double cropWidth, cropHeight, x, y;
                            if (imgRatio > targetRatio) {
                                // The frame is wider than needed. Crop horizontally.
                                cropHeight = imgHeight;
                                cropWidth = cropHeight * targetRatio;
                                x = (imgWidth - cropWidth) / 2;
                                y = 0;
                            } else {
                                // The frame is taller than needed. Crop vertically.
                                cropWidth = imgWidth;
                                cropHeight = cropWidth / targetRatio;
                                x = 0;
                                y = (imgHeight - cropHeight) / 2;
                            }
                            imageView.setViewport(new Rectangle2D(x, y, cropWidth, cropHeight));
                            viewportSet = true;
                        }
                        // Convert the captured frame to a JavaFX Image.
                        Image fxImage = SwingFXUtils.toFXImage(frame, null);
                        imageView.setImage(fxImage);
                    }
                }
            };
            timer.start();
        }
    }

    /**
     * Sets the webcam resolution to a supported size that best matches the target dimensions.
     */
    private void updateWebcamResolution(double targetWidth, double targetHeight) {
        Dimension[] supportedSizes = webcam.getViewSizes();
        if (supportedSizes != null && supportedSizes.length > 0) {
            Dimension bestFit = null;
            double minDiff = Double.MAX_VALUE;
            for (Dimension d : supportedSizes) {
                double diff = Math.abs(d.getWidth() - targetWidth) + Math.abs(d.getHeight() - targetHeight);
                if (diff < minDiff) {
                    minDiff = diff;
                    bestFit = d;
                }
            }
            if (bestFit != null) {
                webcam.setViewSize(bestFit);
                System.out.println("Webcam resolution set to: " + bestFit.width + "x" + bestFit.height);
            }
        }
    }

    /**
     * Called when the "Take Picture" button is pressed.
     * Captures the current image from the ImageView, stops the webcam feed, and closes the window.
     */
    @FXML
    private void handleTakePicture() {
        capturedImage = imageView.getImage();
        stopCamera();
        Stage stage = (Stage) takePicture.getScene().getWindow();
        stage.close();
    }


    void stopCamera() {
        if (timer != null) {
            timer.stop();
        }
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
            System.out.println("Camera closed.");
        }
    }

    /**
     * Returns the captured image so that the parent view can retrieve the photo.
     */
    public Image getCapturedImage() {
        return capturedImage;
    }
}
