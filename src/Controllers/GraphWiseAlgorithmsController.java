package Controllers;

import Algorithms.Algorithm;
import Algorithms.TopologicalSort;
import Exceptions.InvalidAlgorithmInputException;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import GraphVisualizer.Graph;

import javafx.scene.control.Button;


public class GraphWiseAlgorithmsController extends Controller{

    @FXML
    private AnchorPane AlgorithmsPane;

    @FXML
    private Button TopologicalSortButton;


    public void initialize()
    {
        ControllerManager.setGraphWiseAlgorithmsController(this);
    }


    public void OnTopologicalClick()
    {
        Graph G = ControllerManager.getGraphInputController().getGraph();

        Algorithm algorithm = new TopologicalSort(new Graph(G));
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm),null);
        }
        algorithm.Run();
        algorithm.DisplayResults();
    }
}
