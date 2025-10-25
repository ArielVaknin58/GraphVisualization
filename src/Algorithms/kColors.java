package Algorithms;

import GraphVisualizer.Graph;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
        colors = KosarajuSharirAlgorithm.generateColors(setSize);
        Set<Graph.GraphNode> nodes = new HashSet<>();
        Random rand = new Random();

        for(Graph.GraphNode node : initialGraph.V)
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
        this.graphResult = new Graph(this.initialGraph);
        if(this.initialGraph.V.isEmpty())
        {
            isSetFound = true;
            return;
        }

        isSetFound = true;
        Random rand = new Random();

        for(Graph.GraphNode node : this.graphResult.V) {
            Color color = colors.get(rand.nextInt(0,colors.size()));
            node.ChangeColor(color);
        }

        for(Graph.GraphNode node1 : currentSet) {
            for(Graph.GraphNode node2 : currentSet) {
                Color color1 = (Color) node1.getCircle().getFill();
                Color color2 = (Color) node2.getCircle().getFill();
                if(color1.equals(color2) && node1.getneighborEdge(node2) != null)
                {
                    this.graphResult.getAdjacencyMap().get(node1).get(node2).ChangeColor(Color.RED);
                    isSetFound = false;
                }
            }
        }

    }
}
