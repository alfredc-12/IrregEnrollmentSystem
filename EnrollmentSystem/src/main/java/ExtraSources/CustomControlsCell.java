package ExtraSources;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.util.function.Consumer;

public class CustomControlsCell<T> extends TableCell<T, Void> {
    private HBox container;
    private Button updateButton;
    private Button deleteButton;
    private final Consumer<T> updateAction;
    private final Consumer<T> deleteAction;

    public CustomControlsCell(Consumer<T> updateAction, Consumer<T> deleteAction) {
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ControlsCell.fxml"));
            container = loader.load();
            // Look up the buttons by fx:id
            updateButton = (Button) container.lookup("#updateButton");
            deleteButton = (Button) container.lookup("#deleteButton");
        } catch (IOException e) {
            e.printStackTrace();
            container = new HBox(); // Fallback if loading fails
        }
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || container == null) {
            setGraphic(null);
        } else {
            // Get the current row item
            T currentItem = getTableView().getItems().get(getIndex());
            // Attach actions to the buttons
            updateButton.setOnAction(e -> updateAction.accept(currentItem));
            deleteButton.setOnAction(e -> deleteAction.accept(currentItem));
            setGraphic(container);
        }
    }
}
