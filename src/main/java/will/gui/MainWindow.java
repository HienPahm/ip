package will.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import will.Will;

/**
 * The main window's controller: forwards each line the user types to
 * {@link Will#getResponse(String)} and shows the exchange as a pair of
 * dialog boxes. Loaded from {@code MainWindow.fxml} by {@link MainApp}.
 */
public class MainWindow extends AnchorPane {
    private static final Image USER_IMAGE = new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));
    private static final Image WILL_IMAGE = new Image(MainWindow.class.getResourceAsStream("/images/DaWill.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Will will;

    /** Keeps the scroll pane pinned to the newest message as it grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the {@link Will} instance to send input to.
     *
     * @param will The chatbot backing this window.
     */
    public void setWill(Will will) {
        this.will = will;
    }

    /**
     * Sends the text in the input field to {@link Will}, shows the
     * exchange as a pair of dialog boxes, then clears the input field.
     * If that input was "bye", closes the window shortly after so the
     * user sees the goodbye message before it disappears.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = will.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                DialogBox.getWillDialog(response, WILL_IMAGE)
        );
        userInput.clear();

        if (will.isLastResponseExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
