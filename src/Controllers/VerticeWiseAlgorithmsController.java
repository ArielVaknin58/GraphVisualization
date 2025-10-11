package Controllers;

import Algorithms.Algorithm;
import Algorithms.BFS;
import Exceptions.InvalidAlgorithmInputException;
import GraphVisualizer.AppSettings;
import GraphVisualizer.CurrentlyPressedNodeHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import GraphVisualizer.Graph;

import javafx.scene.control.Button;

import java.io.IOException;


public class VerticeWiseAlgorithmsController extends Controller{

    @FXML
    private AnchorPane VerticeAlgorithmsPane;

    @FXML
    private Button BFSButton;

    @FXML
    private Label verticeLabel;

    @FXML
    private Label verticeWeight;

    @FXML
    private Label inDegree;

    @FXML
    private Label outDegree;

    private Graph.GraphNode currentNode;

    @FXML
    public void initialize() {
        ControllerManager.setVerticeWiseAlgorithmsController(this);

        currentNode = CurrentlyPressedNodeHelper.getCurrentNode();

    }

    public void updateCurrentNodeLabels()
    {
        currentNode = CurrentlyPressedNodeHelper.getCurrentNode();
        if (currentNode != null) {
            verticeLabel.setText(currentNode.getNodeLabel());
            verticeWeight.setText("0");
            inDegree.setText(Integer.toString(currentNode.inDegree));
            outDegree.setText(Integer.toString(currentNode.outDegree));
        }
    }

    private void run(Algorithm algorithm)
    {
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm),null);
        }
        algorithm.Run();
        algorithm.DisplayResults();
    }
    public void OnBFSClick()
    {
        run(new BFS(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }
}
