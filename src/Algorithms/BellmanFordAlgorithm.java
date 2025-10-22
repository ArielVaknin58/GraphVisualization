package Algorithms;

import Controllers.ControllerManager;
import Controllers.ResultsPaneController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static Controllers.Controller.AlertError;

public class BellmanFordAlgorithm extends Algorithm{

    private Graph.GraphNode inputNode;
    private HashMap<Graph.GraphNode,Integer> weightedPaths;
    private HashMap<Graph.GraphNode, Graph.GraphNode> parents;
    private boolean hasNegativeCycles = false;
    public static final String AlgorithmDescription = "This algorithm finds the lightest paths from a given vertice v to each other vertice in G.";

    public BellmanFordAlgorithm(Graph graph, Graph.GraphNode inputNode)
    {
        this.G = graph;
        this.AlgorithmName = "Bellman-Ford Algorithm";
        this.inputNode = inputNode;
        this.requiredInput = "a directed, weighted graph G=(V,E)";
        init();
    }

    private void init()
    {
        this.weightedPaths = new HashMap<>();
        this.parents = new HashMap<>();
        for(Graph.GraphNode node : this.G.V)
        {
            if(node.equals(inputNode))
                weightedPaths.put(node,0);
            else
                weightedPaths.put(node,Integer.MAX_VALUE);
            parents.put(node,null);
        }
    }


    @Override
    public void Run() {

        init();
        for(int i = 1; i < this.G.V.size(); i++)
        {
            for(DirectedEdge edge : this.G.E)
                relax(edge.getFrom(),edge.getTo(),edge);
        }

        for(DirectedEdge edge : this.G.E)
        {
            if(relax(edge.getFrom(),edge.getTo(),edge))
            {
                hasNegativeCycles = true;
            }
        }

    }

    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        if(hasNegativeCycles)
        {
            ControllerManager.getGraphInputController().infoPopup("The graph has a negative cycle !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Results_Popup_location));
            AnchorPane resultsPane = loader.load();
            ResultsPaneController controller = loader.getController();
            controller.getResultsLabel().setText("Lightest paths from vertice "+this.inputNode.getNodeLabel()+" :");
            controller.getNodeCol().setText("Node index");
            controller.getValueCol().setText("Min weight path");
            Stage popupStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);
            popupStage.setTitle(this.AlgorithmName+" results :");

            ObservableList<ResultPair<String,String>> data = FXCollections.observableArrayList();
            controller.getResultsTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);


            for (Map.Entry<Graph.GraphNode, Integer> entry : weightedPaths.entrySet()) {
                String displayWeight = (entry.getValue() == Integer.MAX_VALUE) ? "∞" : entry.getValue().toString();
                data.add(new ResultPair<String,String>(entry.getKey().getNodeLabel(), displayWeight));

            }

            ControllerManager.getResultsPaneController().getResultsTable().setItems(data);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(resultsPane);
            popupStage.setScene(scene);
            ThemeManager.getThemeManager().AddScene(scene);

            popupStage.show();

        }catch (IOException e)
        {
            AlertError(e);
        }

    }

    @Override
    public void CreateOutputGraph() {}

    private boolean relax(Graph.GraphNode source, Graph.GraphNode dest, DirectedEdge edge)
    {
        int sourceWeight = weightedPaths.get(source);
        int destWeight = weightedPaths.get(dest);
        int edgeWeight = edge.getWeight();

        if(sourceWeight != Integer.MAX_VALUE && destWeight > sourceWeight + edgeWeight)
        {
            weightedPaths.put(dest,weightedPaths.get(source) + edge.getWeight());
            parents.put(dest,source);
            return true;
        }
        return false;
    }
}
