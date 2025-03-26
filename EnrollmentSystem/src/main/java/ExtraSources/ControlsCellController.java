package ExtraSources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ControlsCellController {
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    public void setUpdateAction(Runnable action) {
        updateButton.setOnAction(e -> action.run());
    }

    public void setDeleteAction(Runnable action) {
        deleteButton.setOnAction(e -> action.run());
    }
}
