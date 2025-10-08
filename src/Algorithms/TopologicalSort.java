package Algorithms;

import GraphVisualizer.Graph;

import java.util.ArrayList;
import java.util.List;

public class TopologicalSort extends Algorithm {

    private List<Graph.GraphNode> result;
    public TopologicalSort(Graph G)
    {
        super();
        result = new ArrayList<>();
        this.G = G;
        this.AlgorithmName = "Topological Sort";
        this.AlgorithmDescription = "A topological sort is a linear ordering of vertices in a directed acyclic graph (DAG), where for every directed edge from vertex u to vertex v, u comes before v in the ordering.";
    }
    @Override
    public String getAlgorithmName() {
        return this.AlgorithmName;
    }

    @Override
    public String getAlgorithmDescription() {
        return this.AlgorithmDescription;
    }

    @Override
    public void Run() {


        while(true);
    }

    @Override
    public Boolean checkValidity() {
        //Run DFS
        return null;
    }
}
