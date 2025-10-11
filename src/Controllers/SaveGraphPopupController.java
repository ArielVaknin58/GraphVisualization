package Controllers;

import GraphVisualizer.GraphSerializer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SaveGraphPopupController extends  Controller{

    @FXML
    private TextField nameInput;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText(""); // start with no error message
        ControllerManager.setSaveGraphPopupController(this);
    }

    @FXML
    private void onSaveGraphSaveClicked() {
        String filename = nameInput.getText().trim();

        if (filename.isEmpty()) {
            errorLabel.setText("Filename cannot be empty.");
            return;
        }

        // Example validation: prevent illegal filename chars
        if (!filename.matches("[A-Za-z0-9_\\-]+")) {
            errorLabel.setText("Invalid filename. Use letters, numbers, _ or - only.");
            return;
        }

        System.out.println("Saving graph as: " + filename + ".ser");

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();

        GraphSerializer.saveGraph(ControllerManager.getGraphInputController().getGraph(), filename);
    }


}
