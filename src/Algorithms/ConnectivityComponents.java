package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import Controllers.ResultsPaneController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

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
        if(G.V.isEmpty())
            return;
        DFS dfs = new DFS(G,null);
        result = dfs.FindConnectivityComponents();

    }

    public void CreateOutputGraph()
    {
        Hashtable<String, Set<String>> components = new Hashtable<>();
        for(String Hvertice : result.keySet())
        {
            components.put(Hvertice, new HashSet<>(Arrays.asList(result.get(Hvertice).split(","))));
        }
        List<Color> colors = KosarajuSharirAlgorithm.generateColors(components.size());
        this.graphResult = new Graph(this.G);
        int componentNumber = 0;
        for(String componentIndex : components.keySet())
        {
            Set<String> verticesLabels = components.get(componentIndex);
            for(String verticeLabel : verticesLabels)
            {
                Graph.GraphNode node = graphResult.VerticeIndexer.get(verticeLabel);
                node.ChangeColor(colors.get(componentNumber));
            }

            for(DirectedEdge edge : graphResult.E)
            {
                if(verticesLabels.contains(edge.getFrom().getNodeLabel()) && verticesLabels.contains(edge.getTo().getNodeLabel()))
                    edge.ChangeColor(colors.get(componentNumber));
            }

            componentNumber++;
        }
    }

    @Override
    public Boolean checkValidity() {
        return !G.isDirected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

}
