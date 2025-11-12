package Algorithms;

import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import Services.GraphData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static Controllers.Controller.AlertError;

public class MaxCut extends NonDeterministicAlgorithm{

    public static final String AlgorithmDescription = "This algorithm non-deterministically determines if G has a cut of size at least k.";

    private DirectedEdge edge;
    public MaxCut(Graph g, int iterations, int k)
    {
        super(g,iterations,k);
        this.AlgorithmName = "Non-Deterministic Max-Cut Algorithm";
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
        int size = rand.nextInt(1,this.G.V.size()+1);

        while(nodes.size() < size)
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

        for(Graph.GraphNode node : currentSet) {
            Graph.GraphNode copyNode = this.graphResult.VerticeIndexer.get(node.getNodeLabel());
            if (copyNode != null) {
                copyNode.ChangeColor(Color.LIMEGREEN);
            }
        }

        int counter = 0;
        isSetFound = false;
        for (DirectedEdge edge : this.G.E) {
            if (currentSet.contains(edge.getFrom()) && !currentSet.contains(edge.getTo())  || (!currentSet.contains(edge.getFrom()) && currentSet.contains(edge.getTo())))
            {
                counter++;
                DirectedEdge edgeCopy = this.graphResult.getAdjacencyMap().get(edge.getFrom()).get(edge.getTo());
                if (edgeCopy != null) {
                    edgeCopy.ChangeColor(Color.RED);
                }
                if(counter == 2*setSize)
                    isSetFound = true;
            }
            else if(currentSet.contains(edge.getFrom()) && currentSet.contains(edge.getTo()))
            {
                this.edge = edge;
                isSetFound = false;
            }
        }
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
                CreateOutputGraph();
                if(isSetFound)
                    out.println(" --> Cut of size "+this.setSize+" found !");
                else
                    out.println(" --> not a Cut. "+ (this.edge != null ? "In-Cut Edge : "+edge.getFrom().getNodeLabel()+" and "+edge.getTo().getNodeLabel() : ""));

            }
            if(!isSetFound)
                out.println("\n--Cut with size "+this.setSize+" not found --");
            out.println("----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }

}
