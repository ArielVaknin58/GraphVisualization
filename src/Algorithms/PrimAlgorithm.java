package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import Services.GraphData;
import com.fasterxml.jackson.annotation.JsonCreator;
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

public class PrimAlgorithm extends Algorithm {

    private HashMap<Graph.GraphNode,Boolean> isInTree;
    private HashMap<Graph.GraphNode,Integer> weightsToConnect;
    private HashMap<Graph.GraphNode, Graph.GraphNode> parents;;

    public PrimAlgorithm(Graph graph)
    {
        INIT(graph);
    }

    @JsonCreator
    public PrimAlgorithm()
    {
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    private void init()
    {
        this.isInTree = new HashMap<>();
        this.weightsToConnect = new HashMap<>();
        this.parents = new HashMap<>();
        for(Graph.GraphNode node : G.V)
        {
            isInTree.put(node,false);
            weightsToConnect.put(node,Integer.MAX_VALUE);
            parents.put(node,null);
        }
    }

    @Override
    protected void INIT(Graph graph) {
        this.G = graph;
        AlgorithmDescription = "The algorithm finds minimal spanning tree in a given undirected graph.";
        this.AlgorithmName = "Prim's Algorithm";
        this.requiredInput = "A weighted, undirected and fully connected graph";
        this.graphResult = new Graph(false);
        init();
    }

    @Override
    protected String UpdateParams(Map<String, String> params) {
        return null;
    }

    @Override
    public void Run() {
        if (G.V.isEmpty()) {
            return;
        }
        init();

        PriorityQueue<Graph.GraphNode> Q = new PriorityQueue<>(
                Comparator.comparingInt(weightsToConnect::get)
        );

        Graph.GraphNode startNode = G.V.getFirst();
        weightsToConnect.put(startNode, 0);
        Q.addAll(G.V);

        while (!Q.isEmpty()) {
            Graph.GraphNode currentNode = Q.poll();

            if (weightsToConnect.get(currentNode) == Integer.MAX_VALUE) {
                continue;
            }

            isInTree.put(currentNode, true);

            for (DirectedEdge edge : currentNode.connectedEdges) {

                Graph.GraphNode neighborNode;
                if (edge.getFrom().equals(currentNode)) {
                    neighborNode = edge.getTo();
                } else {
                    neighborNode = edge.getFrom();
                }

                if (!isInTree.get(neighborNode)) {

                    boolean relaxed = relax(currentNode, neighborNode, edge);

                    if (relaxed) {

                        Q.remove(neighborNode);
                        Q.add(neighborNode);
                    }
                }
            }
        }
    }

    private boolean relax(Graph.GraphNode source, Graph.GraphNode dest, DirectedEdge edge)
    {
        int destWeight = weightsToConnect.get(dest);
        int edgeWeight = edge.getWeight();

        if (destWeight > edgeWeight)
        {
            weightsToConnect.put(dest, edgeWeight);
            parents.put(dest, source);
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkValidity() {
        if(G.V.isEmpty())
        {
            return true;
        }
        DFS dfs = new DFS(this.G,this.G.V.getFirst());
        return !this.G.isDirected() && dfs.isConnected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(this.G);
        for(Graph.GraphNode node : parents.keySet())
        {
            Graph.GraphNode parent = parents.get(node);
            if(parent != null)
            {
                DirectedEdge edge = graphResult.getAdjacencyMap().get(parent).get(node);
                edge.ChangeColor(Color.RED);
                DirectedEdge otherEdge = graphResult.getAdjacencyMap().get(node).get(parent);
                otherEdge.ChangeColor(Color.RED);
            }
        }
    }

    @Override
    public String WriteOutputToBuffer() {
        Graph graph = new Graph(false);
        int TreeWeight = 0;
        for(Graph.GraphNode node : G.V)
        {
            graph.createNode(node.getNodeLabel());
        }
        for(Graph.GraphNode node : parents.keySet())
        {
            Graph.GraphNode parent = parents.get(node);
            if(parent != null)
            {
                DirectedEdge edge = G.getAdjacencyMap().get(parent).get(node);
                graph.createEdge(node.getNodeLabel(), parent.getNodeLabel(), edge.getWeight(),0,0);
                TreeWeight += edge.getWeight();
            }
        }


        StringWriter stringWriter = new StringWriter();
        try (PrintWriter out = new PrintWriter(stringWriter)) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(graph), out);
            out.println("\nTree Weight: "+TreeWeight);
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
        Graph graph = new Graph(false);
        int TreeWeight = 0;
        for(Graph.GraphNode node : G.V)
        {
            graph.createNode(node.getNodeLabel());
        }
        for(Graph.GraphNode node : parents.keySet())
        {
            Graph.GraphNode parent = parents.get(node);
            if(parent != null)
            {
                DirectedEdge edge = G.getAdjacencyMap().get(parent).get(node);
                graph.createEdge(node.getNodeLabel(), parent.getNodeLabel(), edge.getWeight(),0,0);
                TreeWeight += edge.getWeight();
            }
        }


        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(graph), out);
            out.println("\nTree Weight: "+TreeWeight);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
