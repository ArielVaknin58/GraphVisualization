package Services;

import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import java.util.ArrayList;
import java.util.List;

public class GraphData {
    public boolean isDirected;
    public List<String> nodes;
    public List<EdgeData> edges;

    public GraphData() {}

    public GraphData(Graph graph) {
        this.isDirected = graph.isDirected();
        this.nodes = new ArrayList<>();
        for(Graph.GraphNode node : graph.getNodes()) {
            this.nodes.add(node.getNodeLabel());
        }
        this.edges = new ArrayList<>();
        for(DirectedEdge edge : graph.getEdges()) {
            this.edges.add(new EdgeData(edge));
        }

    }
}