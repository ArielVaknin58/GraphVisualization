package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import Services.GraphData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static Controllers.Controller.AlertError;

public class FordFelkersonAlgorithm extends Algorithm{

    public static final String AlgorithmDescription = "This algorithm finds the maximum flow in graph G from s to v with given capacities..";

    private Graph.GraphNode s;
    private Graph.GraphNode t;
    private int maxFlow;
    private Map<Graph.GraphNode, Map<Graph.GraphNode, DirectedEdge>> adjMap;


    public FordFelkersonAlgorithm(Graph graph, Graph.GraphNode s, Graph.GraphNode t)
    {
        this.G = graph;
        this.s = s;
        this.t = t;
        this.AlgorithmName = "Ford-Felkerson Algorithm";
        this.requiredInput = "A directed graph G, and vertices s and t from G";
        this.adjMap = this.G.getAdjacencyMap();


    }

    // In this algorithm, weight is used as the difference between capacity and flow.
    private void init()
    {
        this.maxFlow = 0;
        for (DirectedEdge edge : G.E) {
            edge.setFlow(0);
        }
    }


    public Graph getGraph()
    {
        return G;
    }
    @Override
    public void Run() {
        init();

        Map<Graph.GraphNode, DirectedEdge> parentEdges = new HashMap<>();
        while (findAugmentingPath(parentEdges)) {
            int minFlow = Integer.MAX_VALUE;
            Graph.GraphNode curr = t;
            while (!curr.equals(s)) {
                DirectedEdge edge = parentEdges.get(curr);
                minFlow = Math.min(minFlow, edge.getWeight());
                curr = edge.getFrom();
            }

            maxFlow += minFlow;
            curr = t;
            while (!curr.equals(s)) {
                DirectedEdge residualEdge = parentEdges.get(curr);
                Graph.GraphNode prev = residualEdge.getFrom();
                DirectedEdge forwardEdge = adjMap.get(prev).get(curr);
                if (forwardEdge != null) {
                    forwardEdge.setFlow(forwardEdge.getFlow() + minFlow);
                } else {
                    DirectedEdge backwardEdge = adjMap.get(curr).get(prev);
                    backwardEdge.setFlow(backwardEdge.getFlow() - minFlow);
                }

                curr = prev;
            }
            parentEdges.clear();
        }
    }

    private boolean findAugmentingPath(Map<Graph.GraphNode, DirectedEdge> parentEdges) {
        Queue<Graph.GraphNode> queue = new LinkedList<>();
        queue.add(s);

        Map<Graph.GraphNode, Boolean> visited = new HashMap<>();
        for (Graph.GraphNode node : G.V) {
            visited.put(node, false);
        }
        visited.put(s, true);

        while (!queue.isEmpty()) {
            Graph.GraphNode u = queue.poll();
            for (DirectedEdge edge : G.E) {
                if (edge.getFrom().equals(u) && !visited.get(edge.getTo())) {
                    int residualCapacity = edge.getCapacity() - edge.getFlow();
                    if (residualCapacity > 0) {
                        visited.put(edge.getTo(), true);

                        DirectedEdge resEdge = new DirectedEdge(u, edge.getTo(), true, residualCapacity, 0, 0); // 'weight' = residual capacity
                        parentEdges.put(edge.getTo(), resEdge);

                        queue.add(edge.getTo());
                        if (edge.getTo().equals(t))
                            return true;
                    }
                }

                if (edge.getTo().equals(u) && !visited.get(edge.getFrom())) {
                    int residualCapacity = edge.getFlow();
                    if (residualCapacity > 0) {
                        visited.put(edge.getFrom(), true);

                        DirectedEdge resEdge = new DirectedEdge(u, edge.getFrom(), true, residualCapacity, 0, 0); // 'weight' = residual capacity
                        parentEdges.put(edge.getFrom(), resEdge);

                        queue.add(edge.getFrom());
                        if (edge.getFrom().equals(t)) return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        if(this.G.V.isEmpty())
            return;
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {

        this.graphResult = new Graph(this.G);
        this.graphResult.VerticeIndexer.get(s.getNodeLabel()).ChangeColor(Color.RED);
        this.graphResult.VerticeIndexer.get(t.getNodeLabel()).ChangeColor(Color.RED);

        for(DirectedEdge edge : graphResult.E)
        {
            if(edge.getFlow() > 0)
                edge.ChangeColor(Color.BLUE);
        }

    }

    @Override
    protected void WriteOutputToFile(Path fileName) {
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results from vertice "+s.getNodeLabel()+" to "+t.getNodeLabel()+" ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(this.G), out);
            out.println("\nMax Flow: "+this.maxFlow);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
