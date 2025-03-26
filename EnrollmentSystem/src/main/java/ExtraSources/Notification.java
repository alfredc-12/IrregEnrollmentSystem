package ExtraSources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.InputStream;

public class Notification {

    @FXML
    private ImageView icon;

    @FXML
    private TextArea text;

    @FXML
    private Button btn1;

    @FXML
    private Button btn2;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMessage(String message) {
        text.setText(message);
    }

    /**
     * Sets up the buttons. If the second button's text is null or empty,
     * only the first button is displayed.
     */
    public void setButtons(String btn1Text, Runnable btn1Action, String btn2Text, Runnable btn2Action) {
        btn1.setText(btn1Text);
        btn1.setOnAction(e -> {
            btn1Action.run();
            stage.close();
        });

        if (btn2Text == null || btn2Text.trim().isEmpty()) {
            // Hide the second button if no text is provided.
            btn2.setVisible(false);
            btn2.setManaged(false);
        } else {
            btn2.setText(btn2Text);
            btn2.setOnAction(e -> {
                btn2Action.run();
                stage.close();
            });
            btn2.setVisible(true);
            btn2.setManaged(true);
        }
    }

    public void setIcon(String type) {
        String iconPath;
        switch (type.toLowerCase()) {
            case "warning":
                iconPath = "/Images/exclamation.png";
                break;
            case "confirm":
                iconPath = "/Images/right.png";
                break;
            default:
                iconPath = "/Images/information.png";
                break;
        }
        InputStream is = getClass().getResourceAsStream(iconPath);
        if (is != null) {
            icon.setImage(new Image(is));
        } else {
            System.err.println("Icon not found at path: " + iconPath);
        }
    }
}
