package Controllers;

import Algorithms.Algorithm;
import Algorithms.EulerCircuit;
import Algorithms.HamiltonianCircuit;
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
    @FXML
    private Button eulerCircuitButton;
    @FXML
    private Button hamiltonButton;


    public void initialize()
    {
        ControllerManager.setGraphWiseAlgorithmsController(this);
    }

    private void run(Algorithm algorithm)
    {
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm));
        }
        else
        {
            algorithm.Run();
            algorithm.DisplayResults();
        }

    }
    public void OnTopologicalClick()
    {
        run(new TopologicalSort(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnEulerCircuitClick()
    {
        run(new EulerCircuit(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void onHamiltonianCircuitClicked()
    {
        run(new HamiltonianCircuit(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }
}

