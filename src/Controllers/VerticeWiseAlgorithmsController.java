package Controllers;

import Algorithms.Algorithm;
import Algorithms.BFS;
import Algorithms.DFS;
import Algorithms.ShortestPathsTree;
import Exceptions.InvalidAlgorithmInputException;
import GraphVisualizer.AppSettings;
import GraphVisualizer.CurrentlyPressedNodeHelper;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import GraphVisualizer.Graph;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class VerticeWiseAlgorithmsController extends Controller{

    @FXML
    private AnchorPane VerticeAlgorithmsPane;
    @FXML
    private Button BFSButton;
    @FXML
    private Button DFSButton;
    @FXML
    private Label verticeLabel;
    @FXML
    private Label inDegree;
    @FXML
    private Label outDegree;
    @FXML
    private Label outLabel;
    @FXML
    private Label inLabel;
    @FXML
    private Button shortestPathsButton;

    private Graph.GraphNode currentNode;

    @FXML
    public void initialize() {
        ControllerManager.setVerticeWiseAlgorithmsController(this);
        BFSButton.setTooltip(new Tooltip(BFS.AlgorithmDescription));
        DFSButton.setTooltip(new Tooltip(DFS.AlgorithmDescription));
        shortestPathsButton.setTooltip(new Tooltip(ShortestPathsTree.AlgorithmDescription));
        currentNode = CurrentlyPressedNodeHelper.getCurrentNode();
    }

    public void updateCurrentNodeLabels()
    {
        currentNode = CurrentlyPressedNodeHelper.getCurrentNode();
        if (currentNode != null) {
            verticeLabel.setText(currentNode.getNodeLabel());
            if (ControllerManager.getGraphInputController().getGraph().isDirected())
            {
                inLabel.setText("in Degree :");
                inDegree.setText(Integer.toString(currentNode.inDegree));
                outLabel.setText("out Degree :");
                outDegree.setText(Integer.toString(currentNode.outDegree));
            }
            else {
                inLabel.setText("Degree :");
                inDegree.setText(Integer.toString(currentNode.inDegree));
                outDegree.setText("");
                outLabel.setText("");
            }

        }
    }

    private void run(Algorithm algorithm)  {
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm));
        }
        algorithm.Run();
        algorithm.DisplayResults();
    }
    public void OnBFSClick()
    {
        run(new BFS(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }

    public void OnDFSClick()
    {
        run(new DFS((ControllerManager.getGraphInputController().getGraph()), currentNode));
    }

    public void OnShortestPathsTreeClick()
    {
        run(new ShortestPathsTree(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }
}
