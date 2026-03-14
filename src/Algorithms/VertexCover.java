package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;

public class VertexCover extends NonDeterministicAlgorithm{


    public VertexCover(Graph g, int iterations, int k)
    {
        super(g, iterations, k);
        INIT(g);
    }

    @JsonCreator
    public VertexCover(@JsonProperty("iterations") Integer iterations, @JsonProperty("k") Integer k)
    {
        super(ControllerManager.getGraphInputController().getGraph(), iterations, k);
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    @Override
    protected void INIT(Graph graph) {
        AlgorithmDescription = "This algorithm non-deterministically determines if G has a vertex cover of size k.";
        this.AlgorithmName = "Non-Deterministic Vertex Cover Algorithm";
        this.requiredInput = "undirected graph";
    }

    @Override
    public void Run() {
        int independentSetSize = this.G.V.size() - this.k;

        if (independentSetSize < 0) {
            isSetFound = false;
            currentSet = new HashSet<>();
            return;
        }

        IndependentSetAlgorithm is = new IndependentSetAlgorithm(this.G, iterations, independentSetSize);
        is.Run();

        isSetFound = is.getIsSetFound();
        Set<Graph.GraphNode> independentSet = is.getCurrentSet();
        this.currentSet.clear(); // Clear the set to store the VC

        for (Graph.GraphNode node : this.G.V) {
            if (!independentSet.contains(node)) {
                this.currentSet.add(node);
            }
        }
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
                copyNode.ChangeColor(isSetFound ? Color.LIMEGREEN : Color.PINK);
            }
        }

        for (DirectedEdge edge : this.G.E) {
            if (currentSet.contains(edge.getFrom()) || currentSet.contains(edge.getTo())) {
                DirectedEdge edgeCopy = this.graphResult.getAdjacencyMap().get(edge.getFrom()).get(edge.getTo());
                if (edgeCopy != null) {
                    edgeCopy.ChangeColor(Color.RED);
                }
            }
        }
    }
}
