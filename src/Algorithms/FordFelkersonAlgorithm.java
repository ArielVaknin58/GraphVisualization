package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import Services.GraphData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static Controllers.Controller.AlertError;

public class FordFelkersonAlgorithm extends Algorithm{

    private Graph.GraphNode s;
    private Graph.GraphNode t;
    private int maxFlow;
    private Map<Graph.GraphNode, Map<Graph.GraphNode, DirectedEdge>> adjMap;


    public FordFelkersonAlgorithm(Graph graph, Graph.GraphNode s, Graph.GraphNode t)
    {
        this.G = graph;
        this.s = s;
        this.t = t;
        INIT(graph);

    }

    // In this algorithm, weight is used as the difference between capacity and flow.
    private void init()
    {
        this.maxFlow = 0;
        for (DirectedEdge edge : G.E) {
            edge.setFlow(0);
        }
    }

    @JsonCreator
    public FordFelkersonAlgorithm(@JsonProperty("s") int start, @JsonProperty("t") int finish)
    {
        this.G = ControllerManager.getGraphInputController().getGraph();
        this.s = G.V.get(start);
        this.t = G.V.get(finish);
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    public Graph getGraph()
    {
        return G;
    }

    @Override
    protected void INIT(Graph graph) {
        this.AlgorithmName = "Ford-Felkerson Algorithm";
        AlgorithmDescription = "This algorithm finds the maximum flow in graph G from s to v with given capacities..";
        this.requiredInput = "A directed graph G, and vertices s and t from G";
        this.adjMap = this.G.getAdjacencyMap();
    }

    @Override
    protected String UpdateParams(Map<String, String> params) {
        try{
            this.s = G.VerticeIndexer.get(params.get(AppSettings.API_MODE_S_STRING));
            this.t = G.VerticeIndexer.get(params.get(AppSettings.API_MODE_T_STRING));
            if(this.s == null || this.t == null)
                return "%s or %s values aren't valid. \nvalues : %s and %s respectively".formatted(AppSettings.API_MODE_S_STRING, AppSettings.API_MODE_T_STRING, params.get(AppSettings.API_MODE_S_STRING),params.get(AppSettings.API_MODE_T_STRING));
            return null;
        }catch (Exception e){
            return "an error has occured : "+e.getMessage();
        }

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
    public String WriteOutputToBuffer() {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter out = new PrintWriter(stringWriter)) {
            out.println("--- "+this.AlgorithmName+" Results from vertice "+s.getNodeLabel()+" to "+t.getNodeLabel()+" ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(this.G), out);
            out.println("\nMax Flow: "+this.maxFlow);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            return "an error occured : "+e.getMessage();
        }

        return stringWriter.toString();
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
