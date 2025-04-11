package ExtraSources;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Loading {

    @FXML
    private ImageView spartan;

    public void initialize() {
        // Create a FadeTransition that will make the ImageView fade in and out
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), spartan);
        fadeTransition.setFromValue(0.0); // Start from transparent
        fadeTransition.setToValue(1.0);   // Fade to fully visible
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE); // Loop the fade indefinitely
        fadeTransition.setAutoReverse(true); // Make the fade go back and forth (fade in, fade out)

        fadeTransition.play(); // Start the fade animation
    }
}
