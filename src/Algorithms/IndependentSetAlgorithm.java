package Algorithms;

import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import javafx.scene.paint.Color;
import java.util.*;

public class IndependentSetAlgorithm extends NonDeterministicAlgorithm{

    public static final String AlgorithmDescription = "This ND algorithm guesses a set of vertices in G and determines if it's an independent set.";

    public IndependentSetAlgorithm(Graph g, int iterations,int k)
    {
        this.G = g;
        this.setSize = k;
        this.iterations = iterations;
        this.isSetFound = false;
        this.currentSet = new HashSet<>();
        this.AlgorithmName = "Non-Deterministic Independent Set Algorithm";
        this.requiredInput = "any graph";
    }
    @Override
    public void Run() {

        if(this.G.V.isEmpty())
        {
            isSetFound = true;
            return;
        }
        currentSet = init();
        isSetFound = checkIfIndependentSet(currentSet);

    }

    private Set<Graph.GraphNode> init()
    {
        Set<Graph.GraphNode> nodes = new HashSet<>();
        Random rand = new Random();

        while(nodes.size() < setSize)
        {
            int index = rand.nextInt(1,this.G.V.size()+1);
            nodes.add(this.G.VerticeIndexer.get(Integer.toString(index)));
        }

        return nodes;
    }

    private boolean checkIfIndependentSet(Set<Graph.GraphNode> kset) {
        for (DirectedEdge edge : this.G.E)
        {
            if (kset.contains(edge.getFrom()) && kset.contains(edge.getTo())) {
                return false;
            }
        }
        return true;
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

        // Color the nodes
        for(Graph.GraphNode node : currentSet) {
            Graph.GraphNode copyNode = this.graphResult.VerticeIndexer.get(node.getNodeLabel());
            if (copyNode != null) {
                copyNode.ChangeColor(isSetFound ? Color.LIMEGREEN : Color.PINK); // Green if found, Pink if not
            }
        }

        if(isSetFound) return; // If found, don't color edges

        // If not found, find and color bad edges
        for (DirectedEdge edge : this.G.E) {
            if (currentSet.contains(edge.getFrom()) && currentSet.contains(edge.getTo())) {
                // Get the *copy* of the edge from the new graph
                DirectedEdge edgeCopy = this.graphResult.getAdjacencyMap().get(edge.getFrom()).get(edge.getTo());
                if (edgeCopy != null) {
                    edgeCopy.ChangeColor(Color.RED);
                }
            }
        }
    }
}
