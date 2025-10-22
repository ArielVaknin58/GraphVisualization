package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
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
    private boolean isBipartite;
    private HashMap<String,Integer> bfsRESULT;

    public BiPartite(Graph graph)
    {
        this.G = graph;
        this.requiredInput = "Undirected, fully connected graph.";
        this.AlgorithmName = "Bipartite graph algorithm";
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
        for(DirectedEdge edge : this.G.E)
        {
            if(Objects.equals(bfsRESULT.get(edge.getFrom().getNodeLabel()), bfsRESULT.get(edge.getTo().getNodeLabel())))
                return;
        }

        isBipartite = true;

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
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph()
    {
        this.graphResult = new Graph(this.G);
        for(Graph.GraphNode node : graphResult.V)
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
}
