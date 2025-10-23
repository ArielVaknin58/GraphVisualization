package Algorithms;

import Controllers.Controller;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class SuperGraph extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm find Super graph H of graph G, where every connected component in G is a vertice in H";
    private Hashtable<String, Set<String>> components;
    private Hashtable<String,String> ksaResult;


    public SuperGraph(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Super Graph Algorithm";
        this.requiredInput = "A directed graph";
        this.components = new Hashtable<>();
        this.ksaResult = new Hashtable<>();
    }

    @Override
    public void Run() {

        KosarajuSharirAlgorithm ksa = new KosarajuSharirAlgorithm(G);
        ksa.Run();
        this.ksaResult = ksa.getResult();
    }


    public Hashtable<String, Set<String>> getComponents()
    {
        return components;
    }

    private String findComponent(String Nodelabel)
    {
        for(String componentKey : components.keySet())
        {
            if(components.get(componentKey).contains(Nodelabel))
                return ksaResult.get(componentKey);
        }
        return null;
    }
    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(true);
        for(String Hvertice : ksaResult.keySet())
        {
            this.graphResult.createNode(ksaResult.get(Hvertice));
            components.put(Hvertice, new HashSet<>(Arrays.asList(ksaResult.get(Hvertice).split(","))));
        }
        for(DirectedEdge edge : G.E)
        {
            String fromLabel = edge.getFrom().getNodeLabel();
            String toLabel = edge.getTo().getNodeLabel();
            String fromComponent = findComponent(fromLabel);
            String toComponent = findComponent(toLabel);

            assert fromComponent != null;
            if(!fromComponent.equals(toComponent))
            {
                this.graphResult.createEdge(fromComponent,toComponent);
            }

        }
    }
}
