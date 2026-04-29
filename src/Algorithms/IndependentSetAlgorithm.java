package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class IndependentSetAlgorithm extends NonDeterministicAlgorithm{


    public IndependentSetAlgorithm(Graph g, int iterations,int k)
    {
        super(g, iterations, k);
        INIT(G);
    }

    @Override
    protected void INIT(Graph graph) {
        AlgorithmDescription = "This algorithm non-deterministically determines if G has an independent set of size k.";
        this.AlgorithmName = "Non-Deterministic Independent Set Algorithm";
        this.requiredInput = "undirected graph";
    }

    @JsonCreator
    public IndependentSetAlgorithm(@JsonProperty("iterations") Integer iterations, @JsonProperty("k") Integer k)
    {
        super(ControllerManager.getGraphInputController().getGraph(), iterations, k);
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    @Override
    public void Run() {

        if(this.G.V.isEmpty())
        {
            isSetFound = true;
            return;
        }
        currentSet = init();
        isSetFound = checkIfIndependentSet(currentSet);

    }

    private Set<Graph.GraphNode> init()
    {
        Set<Graph.GraphNode> nodes = new HashSet<>();
        Random rand = new Random();

        while(nodes.size() < k)
        {
            int index = rand.nextInt(1,this.G.V.size()+1);
            nodes.add(this.G.VerticeIndexer.get(Integer.toString(index)));
        }

        return nodes;
    }

    @JsonIgnore
    public boolean getIsSetFound() {
        return isSetFound;
    }

    @JsonIgnore
    public Set<Graph.GraphNode> getCurrentSet() {
        return currentSet;
    }

    private boolean checkIfIndependentSet(Set<Graph.GraphNode> kset) {
        for (DirectedEdge edge : this.G.E)
        {
            if (kset.contains(edge.getFrom()) && kset.contains(edge.getTo())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean checkValidity() {
        return !this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        LoadAndRun();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(this.G);
        if(this.G.V.isEmpty())
        {
            isSetFound = true;
            return;
        }

        // Color the nodes
        for(Graph.GraphNode node : currentSet) {
            Graph.GraphNode copyNode = this.graphResult.VerticeIndexer.get(node.getNodeLabel());
            if (copyNode != null) {
                copyNode.ChangeColor(isSetFound ? Color.LIMEGREEN : Color.PINK); // Green if found, Pink if not
            }
        }

        if(isSetFound) return; // If found, don't color edges

        // If not found, find and color bad edges
        for (DirectedEdge edge : this.G.E) {
            if (currentSet.contains(edge.getFrom()) && currentSet.contains(edge.getTo())) {
                // Get the *copy* of the edge from the new graph
                DirectedEdge edgeCopy = this.graphResult.getAdjacencyMap().get(edge.getFrom()).get(edge.getTo());
                if (edgeCopy != null) {
                    edgeCopy.ChangeColor(Color.RED);
                }
            }
        }
    }

    @Override
    public String WriteOutputToBuffer() {

        StringWriter stringWriter = new StringWriter();
        try (PrintWriter out = new PrintWriter(stringWriter)) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            for(int counter = 1; counter <= this.iterations && !isSetFound; counter++)
            {
                Run();
                out.println("Iteration #"+counter+":");
                out.print("       current set: ");
                for(Graph.GraphNode node : currentSet)
                    out.print(node.getNodeLabel()+", ");
                CreateOutputGraph();
                if(isSetFound)
                    out.println(" --> independent set of size "+this.k +" found !");
                else
                    out.println(" --> not an independent set");

            }
            if(!isSetFound)
                out.println("\n--Independent set of size "+this.k +" not found --");
            out.println("----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            return "an error occured : "+e.getMessage();
        }

        return stringWriter.toString();
    }


}
