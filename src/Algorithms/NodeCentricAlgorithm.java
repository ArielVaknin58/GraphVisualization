package Algorithms;

import GraphVisualizer.AppSettings;
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
        this.inputNode = this.G.VerticeIndexer.get(params.get(AppSettings.API_MODE_INPUT_NODE_STRING));
        if(this.inputNode == null)
            return "%s key isn't present, or it's value isn't valid.".formatted(AppSettings.API_MODE_INPUT_NODE_STRING);
        return null;
    }
}
