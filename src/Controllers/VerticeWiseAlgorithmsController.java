package Controllers;

import Algorithms.*;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import GraphVisualizer.Graph;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class VerticeWiseAlgorithmsController extends Controller{

    @FXML
    private AnchorPane VerticeAlgorithmsPane;
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
    private Button BFSButton;
    @FXML
    private Button DFSButton;
    @FXML
    private Button shortestPathsButton;
    @FXML
    private Button bellmanFordButton;
    @FXML
    private Button maxflowButton;

    private Graph.GraphNode currentNode;

    @FXML
    public void initialize() {
        ControllerManager.setVerticeWiseAlgorithmsController(this);
        BFSButton.setTooltip(new Tooltip(BFS.AlgorithmDescription));
        DFSButton.setTooltip(new Tooltip(DFS.AlgorithmDescription));
        shortestPathsButton.setTooltip(new Tooltip(ShortestPathsTree.AlgorithmDescription));
        bellmanFordButton.setTooltip(new Tooltip(BellmanFordAlgorithm.AlgorithmDescription));
        maxflowButton.setTooltip(new Tooltip(FordFelkersonAlgorithm.AlgorithmDescription));
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

    @FXML
    private void OnBFSClick()
    {
        run(new BFS(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }

    @FXML
    private void OnDFSClick()
    {
        run(new DFS((ControllerManager.getGraphInputController().getGraph()), currentNode));
    }
    @FXML
    private void OnShortestPathsTreeClick()
    {
        run(new ShortestPathsTree(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }
    @FXML
    private void OnLightestPathsClick()
    {
        run(new BellmanFordAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode));
    }

    @FXML
    private void OnfordFelkersonClicked()
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.Max_Flow_Popup_Location));
            AnchorPane saveGraphPane = saveGraphLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Enter Destination vertice");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);

            Scene popupScene = new Scene(saveGraphPane);
            popupStage.setScene(popupScene);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.showAndWait();
        }
        catch (IOException e) {
            AlertError(e);
        }
    }

    public void runFordFelkerson(Graph.GraphNode destination)
    {
        run(new FordFelkersonAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()), currentNode,destination));
    }

}
