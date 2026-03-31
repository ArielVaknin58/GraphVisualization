package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static Controllers.Controller.AlertError;

public class Clique extends NonDeterministicAlgorithm{


    public Clique(Graph g, int iterations, int k) {
        super(g, iterations, k);
        INIT(g);
    }

    @JsonCreator
    public Clique(@JsonProperty("iterations") Integer iterations, @JsonProperty("k") Integer k)
    {
        super(ControllerManager.getGraphInputController().getGraph(),iterations , k);
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    @Override
    protected void INIT(Graph graph) {
        AlgorithmDescription = "This algorithm non-deterministically determines if G has a clique of size k.";
        this.AlgorithmName = "Non-Deterministic Clique Algorithm";
        this.requiredInput = "undirected graph";
    }

    @Override
    public void Run() {

        currentSet = init();

    }

    private Set<Graph.GraphNode> init()
    {
        Set<Graph.GraphNode> nodes = new HashSet<>();
        Random rand = new Random();

        while(nodes.size() < k)
        {
            int index = rand.nextInt(1,this.G.V.size()+1);
            nodes.add(this.G.VerticeIndexer.get(Integer.toString(index)));
        }

        return nodes;
    }
    @Override
    public Boolean checkValidity() {
        return !this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        LoadAndRun();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(this.G);
        if(this.G.V.isEmpty())
        {
            isSetFound = true;
            return;
        }

        isSetFound = true;
        for(Graph.GraphNode node : currentSet) {
            Graph.GraphNode copyNode = this.graphResult.VerticeIndexer.get(node.getNodeLabel());
            if (copyNode != null) {
                copyNode.ChangeColor(Color.LIMEGREEN);
            }
        }


        for(Graph.GraphNode node1 : currentSet)
        {
            for(Graph.GraphNode node2 : currentSet)
            {
                if(!node1.equals(node2))
                {
                    if (node1.neighborsList.contains(node2))
                    {
                        this.graphResult.getAdjacencyMap().get(node1).get(node2).ChangeColor(Color.RED);
                    }
                    else
                        isSetFound = false;
                }
            }
        }
    }

    @Override
    public String WriteOutputToBuffer() {
        return "";
    }


    @Override
    protected void WriteOutputToFile(Path fileName) {
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            for(int counter = 1; counter <= this.iterations && !isSetFound; counter++)
            {
                Run();
                out.println("Iteration #"+counter+":");
                out.print("       current set: ");
                for(Graph.GraphNode node : currentSet)
                    out.print(node.getNodeLabel()+", ");
                out.println(" --> not a clique");
                CreateOutputGraph();
                if(isSetFound)
                    out.println(" --> Clique of size "+this.k +" found !");
            }
            if(!isSetFound)
                out.println("\n--Clique of size "+this.k +" not found --");
            out.println("----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
