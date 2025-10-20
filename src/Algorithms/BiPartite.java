package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.ArrowEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Objects;

public class BiPartite extends Algorithm{

    public static final String AlgorithmDescription = "This algorithm returns a division of graph G into 2 cut (U,V), where every edge - if exists - crosses one to the other.";
    private Graph result;
    private boolean isBipartite;
    private HashMap<String,Integer> bfsRESULT;

    public BiPartite(Graph graph)
    {
        this.G = graph;
        this.requiredInput = "Undirected, fully connected graph.";
        this.AlgorithmName = "Bipartite graph algorithm";
        this.result = new Graph(false);
        this.isBipartite = false;
        this.bfsRESULT = new HashMap<>();
    }
    @Override
    public void Run()
    {
        if(this.G.V.isEmpty())
            return;

        BFS bfs = new BFS(this.G,this.G.V.getFirst());
        bfs.Run();
        bfsRESULT = bfs.getDistancesResults();
        for(ArrowEdge edge : this.G.E)
        {
            if(Objects.equals(bfsRESULT.get(edge.getFrom().getNodeLabel()), bfsRESULT.get(edge.getTo().getNodeLabel())))
                return;
        }

        isBipartite = true;
        BuildBipartiteColoredGraph();

    }

    private void BuildBipartiteColoredGraph()
    {
        this.result = new Graph(this.G);
        for(Graph.GraphNode node : result.V)
        {
            if(bfsRESULT.get(node.getNodeLabel()) % 2 == 0)
            {
                node.ChangeColor(Color.RED);
            }
            else
            {
                node.ChangeColor(Color.BLUEVIOLET);
            }
        }
    }

    @Override
    public Boolean checkValidity()
    {
        if(this.G.V.isEmpty())
        {
            return true;
        }
        DFS dfs = new DFS(this.G,this.G.V.getFirst());
        return !this.G.isDirected() && dfs.isConnected();
    }

    @Override
    public void DisplayResults()
    {
        if(!isBipartite)
        {
            ControllerManager.getGraphInputController().infoPopup("The given graph isn't bipartite.");
            return;
        }

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_results_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            BuildBipartiteColoredGraph();
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
