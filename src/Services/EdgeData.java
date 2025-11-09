package Services;

import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;

public class EdgeData {
    public String from;
    public String to;
    public int weight;
    public int capacity;

    public EdgeData() {}


    public EdgeData(DirectedEdge edge) {
        this.from = edge.getFrom().getNodeLabel();
        this.to = edge.getTo().getNodeLabel();
        this.weight = edge.getWeight();
        this.capacity = edge.getCapacity();
    }
}