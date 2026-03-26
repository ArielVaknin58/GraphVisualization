package Services;

import GraphVisualizer.DirectedEdge;

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

    public EdgeData(String from, String to, int weight, int capacity) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.capacity = capacity;
    }
}