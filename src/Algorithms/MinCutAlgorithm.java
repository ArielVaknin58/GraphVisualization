package Algorithms;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static Controllers.Controller.AlertError;

public class MinCutAlgorithm extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm finds a minimal cut, smallest group of edges that removing them disconnects the graph into 2 components.";
    private Graph flowingGraph;


    public MinCutAlgorithm(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Min-Cut Algorithm";
        this.requiredInput = "A directed graph G";
    }

    @Override
    public void Run() {
        FordFelkersonAlgorithm ffa = new FordFelkersonAlgorithm(this.G,this.G.V.getFirst(),this.G.V.getLast());
        ffa.Run();
        flowingGraph = ffa.getGraph();

    }

    @Override
    public Boolean checkValidity() {
        if(this.G.V.isEmpty())
            return false;
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(true);
        for(Graph.GraphNode node : this.flowingGraph.V)
        {
            this.graphResult.createNodeWithCoordinates(node.getxPosition(),node.yPosition,node.getNodeLabel());
        }
        for(DirectedEdge edge : this.flowingGraph.E)
        {
            if(edge.getCapacity() - edge.getFlow() > 0)
                this.graphResult.createEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel());
        }


        BFS bfs = new BFS(this.graphResult,this.graphResult.V.getFirst());
        bfs.Run();
        HashMap<String,Integer> bfsResults = bfs.getDistancesResults();
        Set<Graph.GraphNode> S = new HashSet<Graph.GraphNode>();
        for(Graph.GraphNode node : this.graphResult.V)
        {
            if(bfsResults.get(node.getNodeLabel()) == Integer.MAX_VALUE)
                node.ChangeColor(Color.BISQUE);
            else
            {
                node.ChangeColor(Color.PINK);
                S.add(node);
            }
        }

        for(DirectedEdge edge : this.G.E)
        {
            this.graphResult.createEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel());
            if(S.contains(edge.getFrom()) && !S.contains(edge.getTo()) || !S.contains(edge.getFrom()) && S.contains(edge.getTo()))
            {
                DirectedEdge newEdge = this.graphResult.VerticeIndexer.get(edge.getFrom().getNodeLabel()).getneighborEdge(edge.getTo());
                newEdge.ChangeColor(Color.BLUE);
            }
        }

    }

    @Override
    protected void WriteOutputToFile(Path fileName) {
        Graph graph = new Graph(true);
        for(Graph.GraphNode node : this.flowingGraph.V)
        {
            graph.createNodeWithCoordinates(node.getxPosition(),node.yPosition,node.getNodeLabel());
        }
        for(DirectedEdge edge : this.flowingGraph.E)
        {
            if(edge.getCapacity() - edge.getFlow() > 0)
                graph.createEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel());
        }
        BFS bfs = new BFS(graph,graph.V.getFirst());
        bfs.Run();
        HashMap<String,Integer> bfsResults = bfs.getDistancesResults();
        Set<Graph.GraphNode> S = new HashSet<Graph.GraphNode>();
        for(Graph.GraphNode node : graph.V)
        {
            if(bfsResults.get(node.getNodeLabel()) != Integer.MAX_VALUE)
                S.add(node);
        }

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            out.print("S : ");
            for(Graph.GraphNode node : S)
            {
                out.print(node.getNodeLabel()+", ");
            }
            out.print("\nV\\S : ");
            for(Graph.GraphNode node : G.V)
            {
                if(!S.contains(node))
                    out.print(node.getNodeLabel()+", ");
            }
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
