package Controllers;

import GraphVisualizer.Graph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;  // <-- ADD THIS IMPORT
import javafx.scene.control.TextField; // <-- ADD THIS IMPORT
import javafx.scene.layout.AnchorPane;

public class MaxFlowPopupController extends Controller{

    @FXML
    private AnchorPane popupPane;
    @FXML
    private TextField destField;
    @FXML
    private Button enterButton;

    private Graph.GraphNode sourceVertice;

    @FXML
    private void initialize()
    {
        ControllerManager.setMaxFlowPopupController(this);
    }

    public void setSourceVertice(Graph.GraphNode sourceVertice)
    {
        this.sourceVertice = sourceVertice;
    }

    @FXML
    private void OnEnterClicked()
    {
        try {
            String dest = destField.getText();
            int parsedDestination = Integer.parseInt(dest);
            Graph.GraphNode destination = ControllerManager.getGraphInputController().getGraph().VerticeIndexer.get(dest);
            if(destination == null)
            {
                AlertError(new Exception("destination is null"));
                return;
            }

            ControllerManager.getGraphInputController().runFordFelkerson(sourceVertice,destination);
            popupPane.getScene().getWindow().hide();
        }catch (NumberFormatException e)
        {
            AlertError(new Exception("destination is not a number"));
        }
    }

}
