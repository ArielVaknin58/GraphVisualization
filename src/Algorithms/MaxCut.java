package Algorithms;

import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MaxCut extends NonDeterministicAlgorithm{

    public static final String AlgorithmDescription = "This algorithm non-deterministically determines if G has a cut of size k.";

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
        }
    }
}
