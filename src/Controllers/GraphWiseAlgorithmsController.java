package Controllers;

import Algorithms.*;
import Exceptions.InvalidAlgorithmInputException;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
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
    @FXML
    private Button eulerPathButton;
    @FXML
    private Button connectivityButton;
    @FXML
    private Button KosarajuButton;
    @FXML
    private Button superGraphButton;
    @FXML
    private Button bipartiteButton;


    public void initialize()
    {
        ControllerManager.setGraphWiseAlgorithmsController(this);
        TopologicalSortButton.setTooltip(new Tooltip(TopologicalSort.AlgorithmDescription));
        eulerCircuitButton.setTooltip(new Tooltip(EulerCircuit.AlgorithmDescription));
        hamiltonButton.setTooltip(new Tooltip(HamiltonianPath.AlgorithmDescription));
        eulerPathButton.setTooltip(new Tooltip(EulerPath.AlgorithmDescription));
        connectivityButton.setTooltip(new Tooltip(ConnectivityComponents.AlgorithmDescription));
        KosarajuButton.setTooltip((new Tooltip(KosarajuSharirAlgorithm.AlgorithmDescription)));
        superGraphButton.setTooltip(new Tooltip(SuperGraph.AlgorithmDescription));
        bipartiteButton.setTooltip(new Tooltip(BiPartite.AlgorithmDescription));
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

    public void onHamiltonianPathClicked()
    {
        run(new HamiltonianPath(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }


    public void onEulerPathClicked()
    {
        run(new EulerPath(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnConnectivityClicked()
    {
        run(new ConnectivityComponents(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnKosarajuClick()
    {
        run(new KosarajuSharirAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnSuperGraphClicked()
    {
        run(new SuperGraph(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnBipartiteClicked()
    {
        run(new BiPartite(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }
}

