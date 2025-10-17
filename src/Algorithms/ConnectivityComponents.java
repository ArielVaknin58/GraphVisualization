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
import java.util.Map;

import static Controllers.Controller.AlertError;

public class ConnectivityComponents extends Algorithm{


    public static final String AlgorithmDescription = "An algorithm that finds Connectivity components in an undirected graph.";
    private Hashtable<String,String> result;


    public ConnectivityComponents(Graph G)
    {
        this.G = G;
        this.AlgorithmName = "Undirected Connectivity Components";
        this.requiredInput = "An undirected Graph G = (V,E)";
        this.result = new Hashtable<>();
    }

    @Override
    public void Run() {
        DFS dfs = new DFS(G,null);
        result = dfs.FindConnectivityComponents();

    }

    @Override
    public Boolean checkValidity() {
        return !G.isDirected();
    }

    @Override
    public void DisplayResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Results_Popup_location));
            AnchorPane resultsPane = loader.load();
            ResultsPaneController controller = loader.getController();
            controller.getResultsLabel().setText("Connectivity components for the graph are :");
            controller.getNodeCol().setText("Node index");
            controller.getValueCol().setText("Connectivity Component");
            Stage popupStage = new Stage();
            popupStage.setTitle("Algorithm Results");

            ObservableList<ResultPair<String,String>> data = FXCollections.observableArrayList();

            for (Map.Entry<String, String> entry : result.entrySet()) {
                //data.add(new NodeResult(entry.getKey(), entry.getValue()));
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
