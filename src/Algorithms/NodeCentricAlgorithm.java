package Algorithms;

import GraphVisualizer.Graph;

public abstract class NodeCentricAlgorithm extends Algorithm {

    protected Graph.GraphNode inputNode = null;

    public NodeCentricAlgorithm(Graph.GraphNode inputNode)
    {
        this.inputNode = inputNode;
    }
}
