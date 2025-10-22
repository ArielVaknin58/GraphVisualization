package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TopologicalSort extends Algorithm {

    private List<Graph.GraphNode> result;
    public static final String AlgorithmDescription = "A topological sort is a linear ordering of vertices in a directed acyclic graph (DAG), where for every directed edge from vertex u to vertex v, u comes before v in the ordering.";

    public TopologicalSort(Graph G)
    {
        super();
        result = new ArrayList<>();
        this.G = G;
        requiredInput = "Acyclic directed graph";
        this.AlgorithmName = "Topological Sort";

    }

    @Override
    public void Run() {

        Graph.GraphNode source = checkForSources().getFirst();
        while(source != null)
        {
            result.add(source);
            G.RemoveVertice(source);
            List<Graph.GraphNode> sources = checkForSources();
            if(sources.isEmpty())
                source = null;
            else
                source = sources.getFirst();
        }

    }

    public boolean isSingularSort()
    {
        if(this.G.V.isEmpty())
            return true;

        result = new ArrayList<>();
        List<Graph.GraphNode> sources;
        Graph.GraphNode source;

        while (!this.G.getNodes().isEmpty())
        {
            sources = checkForSources();
            if (sources.size() != 1) {
                return false;
            }
            source = sources.getFirst();
            result.add(source);
            G.RemoveVertice(source);
        }

        return true;
    }


    public List<Graph.GraphNode> getResult()
    {
        return result;
    }

    @Override
    public Boolean checkValidity() {
        if(G.V.isEmpty())
            return false;
        DFS dfs = new DFS(G,G.V.getFirst());
        return dfs.isAcyclic() && this.G.isDirected();
    }

    @Override
    public void DisplayResults() {

        if(this.result.isEmpty())
        {
            ControllerManager.getGraphWiseAlgorithmsController().infoPopup("The graph doesn't have a topological sort.");
        }
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(true);
        for(Graph.GraphNode node : this.result)
        {
            this.graphResult.createNode(node.getNodeLabel());
        }

        for(int i = 0; i < result.size() - 1 ; i++)
        {
            Graph.GraphNode current = result.get(i);
            Graph.GraphNode next = result.get(i+1);
            this.graphResult.createEdge(current.getNodeLabel(), next.getNodeLabel(),0);
        }
    }


    private List<Graph.GraphNode> checkForSources()
    {
        List<Graph.GraphNode> result = new ArrayList<>();
        for(Graph.GraphNode node : G.getNodes())
        {
            if (node.inDegree == 0)
                result.add(node);
        }

        return result;
    }
}
