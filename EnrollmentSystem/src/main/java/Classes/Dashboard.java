package Classes;

import Application.HelloApplication;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dashboard {

    @FXML private AnchorPane dashboardRoot;
    @FXML private AnchorPane leftpane;
    @FXML private AnchorPane rightpane;
    @FXML private Label adminLabel;
    @FXML private Button btnSched;
    @FXML private Button btnSection;
    @FXML private Button btnSubj;

    private Parent facultyRoot;
    private Parent currentView;
    private Map<Integer, Parent> viewMap = new HashMap<>();
    private int currentIndex = 1; // Track the currently displayed view

    private void applyEffectsToButtons(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                addHoverAndPressEffects((Button) node);
            } else if (node instanceof Parent) {
                applyEffectsToButtons((Parent) node);
            }
        }
    }

    public static void addHoverAndPressEffects(Button button) {
        // When mouse enters, scale to 1.1
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(button.scaleXProperty(), 1.1, Interpolator.EASE_BOTH),
                            new KeyValue(button.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        });

        // When mouse exits, scale back to 1.0
        button.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(button.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                            new KeyValue(button.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        });

        // When mouse is pressed, scale down slightly.
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(button.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                            new KeyValue(button.scaleYProperty(), 0.95, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        });

        // When mouse is released, return to the hovered or normal state.
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            double targetScale = button.isHover() ? 1.1 : 1.0;
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(button.scaleXProperty(), targetScale, Interpolator.EASE_BOTH),
                            new KeyValue(button.scaleYProperty(), targetScale, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        });
    }

    @FXML
    private void initialize() {
        // Apply hover/press effects to all buttons in the dashboard.
        applyEffectsToButtons(dashboardRoot);
        // Pre-load the Faculty content into rightpane.
        loadPreloadedViews();
    }

    // Call this from initialize() to pre-load all views.
    private void loadPreloadedViews() {
        try {
            FXMLLoader loader;

            // Pre-load Student view (1)
            loader = new FXMLLoader(getClass().getResource("/FXML/student-view.fxml"));
            Parent student = loader.load();
            setupPreloadedView(student);
            viewMap.put(1, student);

            // Pre-load Faculty view (2)
            loader = new FXMLLoader(getClass().getResource("/FXML/Faculty.fxml"));
            Parent faculty = loader.load();
            setupPreloadedView(faculty);
            viewMap.put(2, faculty);

            // Pre-load Section view (3)
            loader = new FXMLLoader(getClass().getResource("/FXML/section.fxml"));
            Parent section = loader.load();
            setupPreloadedView(section);
            viewMap.put(3, section);
/*
            // Pre-load Schedule view (4)
            loader = new FXMLLoader(getClass().getResource("/FXML/Scheduler.fxml"));
            Parent sched = loader.load();
            setupPreloadedView(sched);
            viewMap.put(4, sched);

            // Pre-load Schedule view (4)
            loader = new FXMLLoader(getClass().getResource("/FXML/subject-view.fxml"));
            Parent subj = loader.load();
            setupPreloadedView(subj);
            viewMap.put(5, subj);
*/
            // Initially display the student view
            currentView = viewMap.get(currentIndex);
            rightpane.getChildren().clear();
            rightpane.getChildren().add(currentView);
            AnchorPane.setTopAnchor(currentView, 0.0);
            AnchorPane.setBottomAnchor(currentView, 0.0);
            AnchorPane.setLeftAnchor(currentView, 0.0);
            AnchorPane.setRightAnchor(currentView, 0.0);

            // Animate initial view
            animateTransitionIn(currentView, true, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setupPreloadedView(Parent view) {
        // Anchor to fill rightpane:
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);

        // Set initial transformation: scale 0.95, slight downward offset, and transparent.
        view.setScaleX(0.95);
        view.setScaleY(0.95);
        view.setOpacity(0.0);
        view.setTranslateY(20);
    }

    // Updated animateTransitionIn method with control over scaling
    private void animateTransitionIn(Parent view, boolean slideDown, boolean expandToFull) {
        view.setTranslateY(slideDown ? -50 : 50); // Start from off-screen position
        view.setOpacity(0.0);

        Timeline slideIn = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(view.translateYProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(view.opacityProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(view.scaleXProperty(), expandToFull ? 1.0 : 0.95, Interpolator.EASE_BOTH),
                        new KeyValue(view.scaleYProperty(), expandToFull ? 1.0 : 0.95, Interpolator.EASE_BOTH)
                )
        );
        slideIn.play();
    }

    private void animateTransitionOut(Parent view, boolean slideUp, Runnable onFinished) {
        Timeline slideOut = new Timeline(
                new KeyFrame(Duration.millis(150), // First shrink slightly
                        new KeyValue(view.scaleXProperty(), 0.95, Interpolator.EASE_BOTH),
                        new KeyValue(view.scaleYProperty(), 0.95, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(300), // Then fade out and slide away
                        new KeyValue(view.translateYProperty(), slideUp ? -50 : 50, Interpolator.EASE_BOTH),
                        new KeyValue(view.opacityProperty(), 0.0, Interpolator.EASE_BOTH)
                )
        );

        slideOut.setOnFinished(event -> onFinished.run());
        slideOut.play();
    }

    public void transitionTo(int newIndex) {
        if (newIndex == currentIndex || !viewMap.containsKey(newIndex)) {
            return; // Nothing to do.
        }

        int step = (newIndex > currentIndex) ? 1 : -1; // Determine forward or backward transition
        cycleThroughViews(currentIndex, newIndex, step);
    }

    private void cycleThroughViews(int current, int target, int step) {
        int nextIndex = current + step;

        if (!viewMap.containsKey(nextIndex)) {
            return; // Prevent invalid view transitions
        }

        Parent nextView = viewMap.get(nextIndex);
        boolean slideUp = step > 0; // Move up if going forward, down if going backward

        if (!rightpane.getChildren().contains(nextView)) {
            rightpane.getChildren().add(nextView);
            AnchorPane.setTopAnchor(nextView, 0.0);
            AnchorPane.setBottomAnchor(nextView, 0.0);
            AnchorPane.setLeftAnchor(nextView, 0.0);
            AnchorPane.setRightAnchor(nextView, 0.0);
        }

        // Ensure intermediate views stay at 0.95 scale
        nextView.setScaleX(0.95);
        nextView.setScaleY(0.95);

        // Delay before transitioning to the next step
        PauseTransition pause = new PauseTransition(Duration.millis(200));
        pause.setOnFinished(event -> {
            animateTransitionOut(currentView, slideUp, () -> {
                rightpane.getChildren().remove(currentView);
                currentView = nextView;

                // Expand only if it's the final target view
                if (nextIndex == target) {
                    animateTransitionIn(nextView, !slideUp, true); // true = expand to 1.0
                } else {
                    animateTransitionIn(nextView, !slideUp, false); // false = stay at 0.95
                }

                // Move to the next view in sequence after animation completes
                if (nextIndex != target) {
                    cycleThroughViews(nextIndex, target, step);
                } else {
                    currentIndex = target; // Final update
                }
            });
        });

        pause.play();
    }

    // Now your button handlers simply call:
    @FXML
    private void openStudent() {
        transitionTo(1);
    }

    @FXML
    private void openFaculty() {
        transitionTo(2);
    }

    @FXML
    private void openSection() {
        transitionTo(3);
    }

    @FXML
    private void openSched() {
        transitionTo(4);
    }

    @FXML
    private void openSubj() {
        transitionTo(5);
    }
}