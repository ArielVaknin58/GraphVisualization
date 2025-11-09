package Algorithms;

import Controllers.Controller;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import Services.GraphData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import static Controllers.Controller.AlertError;

public class ShortestPathsTree extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm returns a tree that presents all the shortest paths from vertice s in graph G to all the vertices";
    private Graph.GraphNode inputNode;
    private HashMap<String,String> bfsResult;

    public ShortestPathsTree(Graph graph, Graph.GraphNode node)
    {
        this.G = graph;
        this.inputNode = node;
        this.AlgorithmName = "Shortest Paths Algorithm";
        this.requiredInput = "A graph G and a vertice v from G.";
        this.bfsResult = new HashMap<>();
    }


    @Override
    public void Run() {

        BFS bfs = new BFS(G,inputNode);
        bfs.Run();
        bfsResult = bfs.getParents();

    }



    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(this.G);
        for(Graph.GraphNode currentNode : this.G.V)
        {
            this.graphResult.createNodeWithCoordinates(currentNode.xPosition, currentNode.yPosition, currentNode.getNodeLabel());
        }

        for(String nodeLabel : bfsResult.keySet())
        {
            if(bfsResult.get(nodeLabel) != null)
            {
                Graph.GraphNode parent = this.G.VerticeIndexer.get(bfsResult.get(nodeLabel));
                DirectedEdge edge = graphResult.createEdge(parent.getNodeLabel(),nodeLabel);
                edge.ChangeColor(Color.RED);
            }
        }
    }

    @Override
    protected void WriteOutputToFile(Path fileName) {
        Graph graph = new Graph(false);
        for(Graph.GraphNode currentNode : this.G.V)
        {
            graph.createNodeWithCoordinates(currentNode.xPosition, currentNode.yPosition, currentNode.getNodeLabel());
        }
        for(String nodeLabel : bfsResult.keySet())
        {
            if(bfsResult.get(nodeLabel) != null)
            {
                Graph.GraphNode parent = this.G.VerticeIndexer.get(bfsResult.get(nodeLabel));
                graph.createEdge(parent.getNodeLabel(),nodeLabel);
            }
        }

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(graph), out);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
