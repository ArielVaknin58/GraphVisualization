package Algorithms;

import Controllers.Controller;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.ArrowEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Objects;

public class ShortestPathsTree extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm returns a tree that presents all the shortest paths from vertice s in graph G to all the vertices";
    private Graph.GraphNode inputNode;
    private Graph result;


    public ShortestPathsTree(Graph graph, Graph.GraphNode node)
    {
        this.G = graph;
        this.inputNode = node;
        this.AlgorithmName = "Shortest Paths Algorithm";
        this.requiredInput = "A graph G and a vertice v from G.";
        result = new Graph(false);
    }


    @Override
    public void Run() {

        for(Integer i = 1 ; i <= this.G.V.size() ; i++)
        {
            this.result.createNode(String.valueOf(i));
        }

        BFS bfs = new BFS(G,inputNode);
        bfs.Run();
        HashMap<String,Integer> bfsResult = bfs.getDistancesResults();

        for(ArrowEdge edge : G.E)
        {
            int fromValue = bfsResult.get(edge.getFrom().getNodeLabel());
            int toValue = bfsResult.get(edge.getTo().getNodeLabel());
            if(toValue == fromValue + 1)
                result.createEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel(),0);
        }

    }



    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_results_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            GraphResultController controller = loader.getController();
            controller.displayGraph(this.result);

            Stage resultStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            resultStage.getIcons().add(icon);
            resultStage.initModality(Modality.APPLICATION_MODAL);
            resultStage.setTitle("Graph result");

            resultStage.setScene(scene);
            resultStage.show();
        }
        catch (Exception e)
        {
            Controller.AlertError(e);
        }
    }
}
