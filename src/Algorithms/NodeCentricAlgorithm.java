package Algorithms;

import GraphVisualizer.Graph;

import java.util.Map;

public abstract class NodeCentricAlgorithm extends Algorithm {

    protected Graph.GraphNode inputNode = null;

    public NodeCentricAlgorithm(Graph.GraphNode inputNode)
    {
        this.inputNode = inputNode;
    }

    @Override
    protected String UpdateParams(Map<String, String> params)
    {
        this.inputNode = this.G.VerticeIndexer.get(params.get("inputNode"));
        if(this.inputNode == null)
            return "inputNode isn't present, or it's value isn't valid.";
        return null;
    }
}
