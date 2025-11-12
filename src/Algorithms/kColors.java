package Algorithms;

import GraphVisualizer.Graph;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static Controllers.Controller.AlertError;

public class kColors extends NonDeterministicAlgorithm
{

    public static final String AlgorithmDescription = "This algorithm non-deterministically determines if G has a valid k-colors coloring of vertices.";
    private List<Color> colors;
    private Graph initialGraph;

    public kColors(Graph g, int iterations, int k) {
        super(g, iterations, k);
        this.initialGraph = new Graph(g);
        this.AlgorithmName = "k-colors Algorithm";
        this.requiredInput = "undirected graph";
    }


    @Override
    public void Run() {

        currentSet = init();
    }

    private Set<Graph.GraphNode> init()
    {
        this.graphResult = new Graph(this.initialGraph);
        colors = KosarajuSharirAlgorithm.generateColors(setSize);
        Set<Graph.GraphNode> nodes = new HashSet<>();
        Random rand = new Random();

        for(Graph.GraphNode node : graphResult.V)
        {
            int index = rand.nextInt(0,colors.size());
            Color color = colors.get(index);
            node.ChangeColor(color);
            nodes.add(node);
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
        if(this.initialGraph.V.isEmpty())
        {
            isSetFound = true;
            return;
        }

        isSetFound = true;


        for(Graph.GraphNode node1 : this.graphResult.V) {
            for(Graph.GraphNode node2 : this.graphResult.V) {
                Color color1 = node1.getVerticeColor();
                Color color2 = node2.getVerticeColor();
                if(color1.equals(color2) && node1.getneighborEdge(node2) != null)
                {
                    this.graphResult.getAdjacencyMap().get(node1).get(node2).ChangeColor(Color.RED);
                    isSetFound = false;
                }
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
                out.println("       current set: ");
                for(Graph.GraphNode node : this.graphResult.V)
                    out.println("              "+node.getNodeLabel()+" : "+node.getVerticeColor());
                CreateOutputGraph();
                if(isSetFound)
                    out.println(" --> Coloring of size "+this.setSize+" found !");
                else
                    out.println(" --> not a valid coloring..");

            }
            if(!isSetFound)
                out.println("\n--Coloring with "+this.setSize+" colors not found --");
            out.println("----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
