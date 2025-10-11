package Algorithms;

import Controllers.ControllerManager;
import Controllers.ResultsPaneController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static Controllers.Controller.AlertError;

public class BFS extends Algorithm{

    private Graph.GraphNode inputNode;
    private HashMap<String, Integer> result = new HashMap();

    public BFS(Graph G, Graph.GraphNode inputNode)
    {
        this.G = G;
        this.inputNode = inputNode;
        result.put(inputNode.getNodeLabel(),0);
        for(Graph.GraphNode node : G.V)
        {
            if(!Objects.equals(node.getNodeLabel(), inputNode.getNodeLabel()))
                result.put(node.getNodeLabel(),Integer.MAX_VALUE);
        }
        this.AlgorithmName = "BFS";
        this.requiredInput = "A Graph G = (V,E) and a node u from V";
        this.AlgorithmDescription = "The Breadth First Search Algorithm gets a given graph and a vertice v, and returns a list of the lengths of the shortest paths from v to each vertice in G. (can also produce the paths themselves).";
    }
    @Override
    public String getAlgorithmName() {
        return this.AlgorithmName;
    }

    @Override
    public String getAlgorithmDescription() {
        return this.AlgorithmDescription;
    }

    @Override
    public String getRequiredInputDescription() {
        return this.requiredInput;
    }

    @Override
    public void Run() {
        Queue<Graph.GraphNode> Q = new LinkedList<>();
        Q.add(inputNode);

        while (!Q.isEmpty()) {
            Graph.GraphNode current = Q.remove();
            int currentDistance = result.get(current.getNodeLabel());

            for (Graph.GraphNode node : current.neighborsList) {

                Integer value = result.get(node.getNodeLabel());
                if (currentDistance + 1 < value) {
                    result.put(node.getNodeLabel(), currentDistance + 1);
                    Q.add(node);
                }

            }
        }
    }


    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Results_Popup_location));
            AnchorPane resultsPane = loader.load();
            ResultsPaneController controller = loader.getController();
            controller.getResultsLabel().setText("Shortest distances from vertice "+this.inputNode.getNodeLabel()+" :");
            controller.getNodeCol().setText("Node index");
            controller.getValueCol().setText("distance");
            Stage popupStage = new Stage();
            popupStage.setTitle("Algorithm Results");

            ObservableList<NodeResult> data = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                data.add(new NodeResult(entry.getKey(), entry.getValue()));
            }

            ControllerManager.getResultsPaneController().getResultsTable().setItems(data);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(resultsPane);
            popupStage.setScene(scene);
            ThemeManager.getThemeManager().AddScene(scene);

            popupStage.show();

        }catch (IOException e)
        {
            AlertError(e,null);
        }

    }
}
