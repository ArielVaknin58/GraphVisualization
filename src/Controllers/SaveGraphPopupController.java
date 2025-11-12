package Controllers;

import Algorithms.Algorithm;
import GraphVisualizer.Graph;
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
    private Label saveLabel;

    private Algorithm currentAlgorithm;

    @FXML
    private void initialize() {
        errorLabel.setText("");
        if(!ControllerManager.getGraphInputController().isApiMode())
            saveLabel.setText("Please enter filename to be saved (will be saved as name.ser) :");
        else
            saveLabel.setText("Please enter filename to be saved (will be saved as name.txt) :");
        ControllerManager.setSaveGraphPopupController(this);
    }

    public void setAlgorithm(Algorithm algorithm) {
        currentAlgorithm = algorithm;
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



        System.out.println("Saving graph as: " + filename + (!ControllerManager.getGraphInputController().isApiMode() ? ".ser" : ".txt"));

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();

        Graph graphToSave = ControllerManager.getGraphInputController().getGraph();
        if(graphToSave == null)
        {
            AlertError(new Exception("Graph is null !"));
            return;
        }
        for(Graph.GraphNode node : graphToSave.V)
        {
            node.xPosition = node.getCircle().getCenterX();
            node.yPosition = node.getCircle().getCenterY();
        }
        if(!ControllerManager.getGraphInputController().isApiMode() && currentAlgorithm == null)
            GraphSerializer.saveGraph(graphToSave, filename);
        else if(currentAlgorithm != null)
        {
            currentAlgorithm.PrintOutputToFile(filename);
            currentAlgorithm = null;
        }
    }


}
