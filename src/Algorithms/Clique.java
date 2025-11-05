package Algorithms;

import GraphVisualizer.Graph;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Clique extends NonDeterministicAlgorithm{


    public static final String AlgorithmDescription = "This algorithm non-deterministically determines if G has a clique of size k.";

    public Clique(Graph g, int iterations, int k) {
        super(g, iterations, k);
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

        while(nodes.size() < setSize)
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
}
