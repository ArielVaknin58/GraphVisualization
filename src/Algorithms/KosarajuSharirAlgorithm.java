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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static Controllers.Controller.AlertError;

public class KosarajuSharirAlgorithm extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm find strongly connected components in a given directed graph.";
    private Hashtable<String,String> result;


    public KosarajuSharirAlgorithm(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Kosaraju Sharir Algorithm";
        this.requiredInput = "A directed graph";
        this.result = new Hashtable<>();
    }


    @Override
    public void Run() {

        if(this.G.V.isEmpty())
            return;
        DFS dfs = new DFS(this.G,this.G.V.getFirst());
        List<Graph.GraphNode> finishTimesList = dfs.DFSWithEndTimeList();
        Graph transpose = this.G.Transpose();
        DFS transposeDFS = new DFS(transpose,null);
        for(Graph.GraphNode node : finishTimesList.reversed())
        {
            String component = transposeDFS.FindDirectedComponent(transpose.VerticeIndexer.get(node.getNodeLabel()));
            if(!component.isEmpty())
                result.put(node.getNodeLabel(),component);
        }
    }

    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Results_Popup_location));
            AnchorPane resultsPane = loader.load();
            ResultsPaneController controller = loader.getController();
            controller.getResultsLabel().setText("Strong Connectivity components for the graph are :");
            controller.getNodeCol().setText("Node index");
            controller.getValueCol().setText("Strong Connectivity Component");
            Stage popupStage = new Stage();
            popupStage.setTitle("Algorithm Results");

            ObservableList<ResultPair<String,String>> data = FXCollections.observableArrayList();

            for (Map.Entry<String, String> entry : result.entrySet()) {
                data.add(new ResultPair<String,String>(entry.getKey(), entry.getValue()));
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
}
